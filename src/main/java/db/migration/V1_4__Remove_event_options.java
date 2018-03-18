package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;


public class V1_4__Remove_event_options implements SpringJdbcMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param jdbcTemplate The jdbcTemplate to use to execute statements.
     * @throws Exception when the migration failed.
     */
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // Change events table
        jdbcTemplate.execute("ALTER TABLE public.event ADD COLUMN organized_by INTEGER");
        jdbcTemplate.execute("ALTER TABLE public.event ADD COLUMN published INTEGER");
        jdbcTemplate.execute("ALTER TABLE public.event DROP COLUMN options");

        // Drop vendor table
        jdbcTemplate.execute("DROP TABLE vendor_events");
        jdbcTemplate.execute("DROP TABLE vendor");
    }
}
