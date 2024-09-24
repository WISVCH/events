package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;


/**
 * DB migration which adds parent_product_id to products
 */
public class V202409024__Add_product_parent_id extends BaseJavaMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param context of type Context
     * @throws Exception when something is wrong
     */
    public void migrate(Context context) throws Exception {
        try (Statement select = context.getConnection().createStatement()) {
            select.execute("ALTER TABLE public.product ADD parent_product_id INTEGER");

            select.execute("ALTER TABLE public.product ADD CONSTRAINT FK_PRODUCT_ON_PARENT_PRODUCT FOREIGN KEY " +
                    "(parent_product_id) REFERENCES public.product (id)");
        }
    }

}
