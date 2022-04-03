package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * DB migration which adds a redirect link to a product. If it is present it will send the user to that link
 * after payment is complete.
 */
public class V20220402__Add_redirect_to_product implements SpringJdbcMigration {

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
        jdbcTemplate.execute("ALTER TABLE public.product ADD COLUMN redirect_url varchar(255)");
        jdbcTemplate.execute("UPDATE public.product SET varchar = NULL");
    }

}
