package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;


/**
 * DB migration which adds a reservable column to products, setting the already existing products to reservable = true
 */
public class V20190129__Add_reservable_to_product extends BaseJavaMigration {

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
            select.execute("ALTER TABLE public.product ADD COLUMN reservable BOOLEAN");
            select.execute("UPDATE public.product SET reservable = TRUE");
            select.execute("ALTER TABLE public.product ALTER COLUMN reservable SET NOT NULL");
        }
    }

}
