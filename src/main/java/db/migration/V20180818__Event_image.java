package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * DB migration which adds a document table
 */
public class V20180818__Event_image implements SpringJdbcMigration {

    /**
     * Executes this migration. The execution will automatically take place within a transaction, when the underlying
     * database supports it.
     *
     * @param jdbcTemplate The jdbcTemplate to use to execute statements.
     *
     * @throws Exception when the migration failed.
     */
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // Create address table
        String addressTable = "CREATE TABLE public.document (\n"
                + "     id bigint NOT NULL CONSTRAINT document_pkey PRIMARY KEY,\n"
                + "     file bytea,\n"
                + "     file_name character varying (255),\n"
                + "     full_name character varying (255),\n"
                + "     type character varying (255)\n"
                + ");\n"
                + "ALTER TABLE public.document OWNER TO events;";
        jdbcTemplate.execute(addressTable);
    }

}
