package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;


public class V1_8__Webhook_task_overview implements SpringJdbcMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param jdbcTemplate The jdbcTemplate to use to execute statements.
     * @throws Exception when the migration failed.
     */
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        //Create webhook_task table.
        jdbcTemplate.execute("ALTER TABLE public.webhook_task ADD COLUMN webhook_task_status INTEGER");
        jdbcTemplate.execute("ALTER TABLE public.webhook_task ADD COLUMN webhook_error TEXT");
    }
}
