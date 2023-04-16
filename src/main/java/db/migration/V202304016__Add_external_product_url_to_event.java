package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;


/**
 * DB migration which adds an external product URL to an event.
 * If it is present, the event will have a button to this URL instead of normal prducts.
 */
public class V202304016__Add_external_product_url_to_event extends BaseJavaMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param context of type Context
     * @throws Exception when something is wrong
     */
    public void migrate(Context context) throws Exception {
        try (Statement select = context.getConnection().createStatement()) {
            select.execute("ALTER TABLE public.event ADD COLUMN external_product_url varchar(255)");
            select.execute("UPDATE public.event SET external_product_url = NULL");
        }
    }

}
