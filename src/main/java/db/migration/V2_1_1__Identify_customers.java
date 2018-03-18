package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;


public class V2_1_1__Identify_customers implements SpringJdbcMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param jdbcTemplate The jdbcTemplate to use to execute statements.
     * @throws Exception when the migration failed.
     */
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // Make username, sub and email unique
        jdbcTemplate.execute("ALTER TABLE public.customer ADD CONSTRAINT uni_sub UNIQUE (sub);");
        jdbcTemplate.execute("ALTER TABLE public.customer ADD CONSTRAINT uni_ch_username UNIQUE (ch_username);");
        jdbcTemplate.execute("ALTER TABLE public.customer ADD CONSTRAINT uni_email UNIQUE (email);");
    }
}
