package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;


public class V1_5__Add_Event_category implements SpringJdbcMigration {

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
        jdbcTemplate.execute("CREATE TABLE public.event_categories (" +
                "    event_id integer NOT NULL, " +
                "    categories integer, " +
                "    CONSTRAINT fkg0l4lu4fo069a5tefj56q3l12 FOREIGN KEY (event_id) " +
                "        REFERENCES public.event (id) MATCH SIMPLE " +
                "        ON UPDATE NO ACTION " +
                "        ON DELETE NO ACTION " +
                ")" +
                " WITH (" +
                "    OIDS = FALSE\n" +
                ")" +
                "TABLESPACE pg_default;"
        );
    }
}
