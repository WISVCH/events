package db.migration2;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * DB migration which adds a key to a ticket.
 */
public class V20180705__Ticket_key implements SpringJdbcMigration {

    /**
     * Migration V20180705__Ticket_key.
     *
     * @param jdbcTemplate of type JdbcTemplate
     *
     * @throws Exception when something is wrong
     */
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // Update table
        jdbcTemplate.execute("ALTER TABLE ticket ADD COLUMN key CHARACTER VARYING(255)");

        // Set value for all the column in the table
        jdbcTemplate.execute("UPDATE ticket SET key = uuid_in(md5(random()::text || now()::text)::cstring)");
    }
}
