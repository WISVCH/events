package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;


public class V1_7__Add_webhook_task implements SpringJdbcMigration {

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
        jdbcTemplate.execute("CREATE TABLE public.webhook_task (\n" +
                "   id integer NOT NULL,\n" +
                "   created_at timestamp without time zone,\n" +
                "   object bytea,\n" +
                "   trigger integer,\n" +
                "   webhook_id integer,\n" +
                "   CONSTRAINT webhook_task_pkey PRIMARY KEY (id)\n" +
                ")" +
                "WITH (OIDS = FALSE)\n" +
                "TABLESPACE pg_default;");
    }
}
