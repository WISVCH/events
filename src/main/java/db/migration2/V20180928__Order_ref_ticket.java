package db.migration2;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * DB migration.
 */
public class V20180928__Order_ref_ticket implements SpringJdbcMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param jdbcTemplate The jdbcTemplate to use to execute statements.
     */
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("ALTER TABLE public.ticket ADD COLUMN order_id INTEGER");
        jdbcTemplate.execute("ALTER TABLE ONLY public.ticket ADD CONSTRAINT fk6pbiwpljchpolob40s7di41y4 FOREIGN KEY (order_id) REFERENCES "
                                     + "public.orders(id);");
    }

}

