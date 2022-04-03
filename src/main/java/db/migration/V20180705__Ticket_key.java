package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * DB migration which adds a key to a ticket.
 */
public class V20180705__Ticket_key extends BaseJavaMigration {
    /**
     * Migration V20180705__Ticket_key.
     *
     * @param context of type Context
     * @throws Exception when something is wrong
     */
    @Override
    public void migrate(Context context) throws Exception {
        try (Statement select = context.getConnection().createStatement()) {
            select.execute("ALTER TABLE ticket ADD COLUMN key CHARACTER VARYING(255)");
            select.execute("UPDATE ticket SET key = uuid_in(md5(random()::text || now()::text)::cstring)");
        }
    }
}
