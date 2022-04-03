package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * DB migration.
 */
public class V20180928__Order_ref_ticket extends BaseJavaMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param context of type Context
     * @throws Exception when something is wrong
     */
    public void migrate(Context context) throws Exception {
        try (Statement select = context.getConnection().createStatement()) {
            select.execute("ALTER TABLE public.ticket ADD COLUMN order_id INTEGER");
            select.execute("ALTER TABLE ONLY public.ticket ADD CONSTRAINT fk6pbiwpljchpolob40s7di41y4 FOREIGN KEY (order_id) REFERENCES "
                    + "public.orders(id);");
        }
    }

}

