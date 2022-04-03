package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * DB migration which adds a document table
 */
public class V20180818__Event_image extends BaseJavaMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param context of type Context
     * @throws Exception when something is wrong
     */
    public void migrate(Context context) throws Exception {
        try (Statement select = context.getConnection().createStatement()) {
            // Create address table
            String addressTable = "CREATE TABLE public.document (\n"
                    + "     id bigint NOT NULL CONSTRAINT document_pkey PRIMARY KEY,\n"
                    + "     file bytea,\n"
                    + "     file_name character varying (255),\n"
                    + "     full_name character varying (255),\n"
                    + "     type character varying (255)\n"
                    + ");\n"
                    + "ALTER TABLE public.document OWNER TO events;";
            select.execute(addressTable);
        }
    }

}
