package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;


/**
 * DB migration which adds VAT fields to product, orderproduct and order.
 */
public class V202306030__Add_vat_fields extends BaseJavaMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param context of type Context
     * @throws Exception when something is wrong
     */
    public void migrate(Context context) throws Exception {
        try (Statement select = context.getConnection().createStatement()) {
            select.execute("ALTER TABLE public.products ADD COLUMN vat_rate varchar(255) NOT NULL DEFAULT 'VAT_FREE'");

            select.execute("ALTER TABLE public.order_product ADD COLUMN vat_rate varchar(255) NOT NULL DEFAULT 'VAT_FREE'");
            select.execute("ALTER TABLE public.order_product ADD COLUMN vat DOUBLE PRECISION NOT NULL DEFAULT 0");

            select.execute("ALTER TABLE public.orders ADD COLUMN vat DOUBLE PRECISION NOT NULL DEFAULT 0");
        }
    }

}
