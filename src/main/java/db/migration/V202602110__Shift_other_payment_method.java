package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * DB migration to shift OTHER payment method after introducing CHPAY.
 */
public class V202602110__Shift_other_payment_method extends BaseJavaMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param context of type Context
     * @throws Exception when something is wrong
     */
    public void migrate(Context context) throws Exception {
        try (Statement statement = context.getConnection().createStatement()) {
            statement.execute("ALTER TABLE public.orders DROP CONSTRAINT IF EXISTS orders_payment_method_check");
            statement.execute("ALTER TABLE public.orders ADD CONSTRAINT orders_payment_method_check CHECK (payment_method BETWEEN 0 AND 6)");
            statement.execute("UPDATE public.orders SET payment_method = 6 WHERE payment_method = 5");
        }
    }
}
