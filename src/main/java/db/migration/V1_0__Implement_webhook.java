package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;


public class V1_0__Implement_webhook implements SpringJdbcMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param jdbcTemplate The jdbcTemplate to use to execute statements.
     * @throws Exception when the migration failed.
     */
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.execute("ALTER TABLE public.event ADD COLUMN short_description VARCHAR(255)");

        jdbcTemplate.execute("CREATE TABLE public.webhook (" +
                "    id integer NOT NULL, " +
                "    active boolean NOT NULL, " +
                "    key character varying(255), " +
                "    ldap_group integer, " +
                "    payload_url character varying(255), " +
                "    secret character varying(255) " +
                ");"
        );

        jdbcTemplate.execute("CREATE TABLE public.webhook_webhook_triggers (" +
                "    webhook_id integer NOT NULL," +
                "    webhook_triggers integer" +
                ");"
        );
    }
}
