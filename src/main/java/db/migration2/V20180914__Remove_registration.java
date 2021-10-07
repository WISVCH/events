package db.migration2;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * DB migration which adds a document table
 */
public class V20180914__Remove_registration implements SpringJdbcMigration {

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
        try {
            // Begin transaction
            jdbcTemplate.execute("BEGIN;");

            // Remove constraints
            String constraints = "ALTER TABLE ONLY public.address DROP CONSTRAINT address_pkey CASCADE;\n"
                    + "ALTER TABLE ONLY public.permissions DROP CONSTRAINT permissions_pkey CASCADE;\n"
                    + "ALTER TABLE ONLY public.profile DROP CONSTRAINT profile_pkey CASCADE;\n"
                    + "ALTER TABLE ONLY public.registration DROP CONSTRAINT registration_pkey CASCADE;\n"
                    + "ALTER TABLE ONLY public.study_details DROP CONSTRAINT study_details_pkey CASCADE;\n"
                    + "ALTER TABLE ONLY public.profile DROP CONSTRAINT fk2hsdsntwy25qr73fsvd7l3wu7;\n"
                    + "ALTER TABLE ONLY public.registration DROP CONSTRAINT fk6pbiwpljchpolob40s7di41y4 CASCADE;\n"
                    + "ALTER TABLE ONLY public.registration DROP CONSTRAINT fkko3c91odb9f49kc3visnv21be CASCADE;\n"
                    + "ALTER TABLE ONLY public.registration DROP CONSTRAINT fkthagckur8igeragp1vx3qewlx CASCADE;\n";
            jdbcTemplate.execute(constraints);

            // Remove tables
            String removeTables = "DROP TABLE public.address;\n"
                    + "DROP TABLE public.permissions;\n"
                    + "DROP TABLE public.profile;\n"
                    + "DROP TABLE public.registration;\n"
                    + "DROP TABLE public.study_details;\n";
            jdbcTemplate.execute(removeTables);

            // Remove columns
            String removeColumns = "ALTER TABLE public.product DROP COLUMN includes_registration;\n"
                    + "ALTER TABLE public.event DROP COLUMN ch_only;\n"
                    + "ALTER TABLE public.orders DROP COLUMN ch_only;\n";
            jdbcTemplate.execute(removeColumns);

            // Commit
            jdbcTemplate.execute("COMMIT;");
        } catch (Exception e) {
            jdbcTemplate.execute("ROLLBACK;");

            throw e;
        }
    }

}
