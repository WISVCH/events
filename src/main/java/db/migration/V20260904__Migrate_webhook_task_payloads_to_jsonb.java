package db.migration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.json.simple.JSONObject;

/**
 * Converts the legacy Java-serialized webhook payload column to PostgreSQL JSONB.
 */
public class V20260904__Migrate_webhook_task_payloads_to_jsonb extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        if (!webhookTaskTableExists(connection) || payloadIsAlreadyJson(connection)) {
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE webhook_task ADD COLUMN object_json jsonb");
        }

        try (PreparedStatement select = connection.prepareStatement(
                "SELECT id, object FROM webhook_task WHERE object IS NOT NULL");
             PreparedStatement update = connection.prepareStatement(
                "UPDATE webhook_task SET object_json = ?::jsonb WHERE id = ?");
             ResultSet rows = select.executeQuery()) {
            while (rows.next()) {
                update.setString(1, deserializePayload(rows.getBytes("object"), rows.getInt("id")));
                update.setInt(2, rows.getInt("id"));
                update.addBatch();
            }
            update.executeBatch();
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE webhook_task DROP COLUMN object");
            statement.execute("ALTER TABLE webhook_task RENAME COLUMN object_json TO object");
        }
    }

    private boolean webhookTaskTableExists(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT to_regclass('public.webhook_task')");
             ResultSet result = statement.executeQuery()) {
            return result.next() && result.getString(1) != null;
        }
    }

    private boolean payloadIsAlreadyJson(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT data_type = 'jsonb'
                FROM information_schema.columns
                WHERE table_schema = 'public' AND table_name = 'webhook_task' AND column_name = 'object'
                """);
             ResultSet result = statement.executeQuery()) {
            return result.next() && result.getBoolean(1);
        }
    }

    private String deserializePayload(byte[] bytes, int taskId) {
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            Object payload = input.readObject();
            if (payload instanceof JSONObject jsonObject) {
                return jsonObject.toJSONString();
            }
            throw new IllegalStateException("Webhook task " + taskId + " does not contain a JSON object");
        } catch (IOException | ClassNotFoundException exception) {
            throw new IllegalStateException("Unable to deserialize webhook task " + taskId, exception);
        }
    }
}
