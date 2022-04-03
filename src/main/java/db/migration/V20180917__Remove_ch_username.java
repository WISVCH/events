package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * DB migration which adds a document table
 */
public class V20180917__Remove_ch_username extends BaseJavaMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param context of type Context
     * @throws Exception when something is wrong
     */
    public void migrate(Context context) throws Exception {
        try (Statement select = context.getConnection().createStatement()) {
            // Remove column ch_username.
            select.execute("ALTER TABLE public.customer DROP COLUMN ch_username");
        }
    }

}
