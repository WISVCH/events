package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;


/**
 * DB migration which adds a redirect link to a product. If it is present it will send the user to that link
 * after payment is complete.
 */
public class V20220402__Add_redirect_to_product extends BaseJavaMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param context of type Context
     * @throws Exception when something is wrong
     */
    public void migrate(Context context) throws Exception {
        try (Statement select = context.getConnection().createStatement()) {
            select.execute("ALTER TABLE public.product ADD COLUMN redirect_url varchar(255)");
            select.execute("UPDATE public.product SET redirect_url = NULL");
        }
    }

}
