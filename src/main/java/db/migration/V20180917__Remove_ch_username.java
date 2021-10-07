package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * DB migration which adds a document table
 */
public class V20180917__Remove_ch_username implements SpringJdbcMigration {

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
        jdbcTemplate.execute("ALTER TABLE public.customer DROP COLUMN ch_username");
    }

}
