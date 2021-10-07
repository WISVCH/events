package db.migration2;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;



/**
 * DB migration which adds a reservable column to products, setting the already existing products to reservable = true
 */
public class V20190129__Add_reservable_to_product implements SpringJdbcMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param jdbcTemplate The jdbcTemplate to use to execute statements.
     *
     */
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) {
        // Remove column ch_username.
        jdbcTemplate.execute("ALTER TABLE public.product ADD COLUMN reservable BOOLEAN");
        jdbcTemplate.execute("UPDATE public.product SET reservable = TRUE");
        jdbcTemplate.execute("ALTER TABLE public.product ALTER COLUMN reservable SET NOT NULL");
    }

}
