package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;


public class V2_0__Online_ticket_sales implements SpringJdbcMigration {

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
        jdbcTemplate.execute("CREATE TABLE public.order_product\n" +
                "(\n" +
                "    id INTEGER NOT NULL,\n" +
                "    amount BIGINT,\n" +
                "    price DOUBLE PRECISION,\n" +
                "    product_id INTEGER,\n" +
                "    CONSTRAINT order_product_pkey PRIMARY KEY (id),\n" +
                "    CONSTRAINT fkhnfgqyjx3i80qoymrssls3kno FOREIGN KEY (product_id)\n" +
                "        REFERENCES public.product (id) MATCH SIMPLE\n" +
                "        ON UPDATE NO ACTION\n" +
                "        ON DELETE NO ACTION\n" +
                ")\n" +
                "WITH (\n" +
                "    OIDS = FALSE\n" +
                ")\n" +
                "TABLESPACE pg_default;");
        jdbcTemplate.execute("CREATE TABLE public.order_order_products\n" +
                "(\n" +
                "    order_id INTEGER NOT NULL,\n" +
                "    order_products_id INTEGER NOT NULL,\n" +
                "    CONSTRAINT fkayr8yu5y1evasjya9rdeeh5hs FOREIGN KEY (order_products_id)\n" +
                "        REFERENCES public.order_product (id) MATCH SIMPLE\n" +
                "        ON UPDATE NO ACTION\n" +
                "        ON DELETE NO ACTION,\n" +
                "    CONSTRAINT fki75xwo4rxfs4akg5c4s29qig1 FOREIGN KEY (order_id)\n" +
                "        REFERENCES public.\"order\" (id) MATCH SIMPLE\n" +
                "        ON UPDATE NO ACTION\n" +
                "        ON DELETE NO ACTION\n" +
                ")\n" +
                "WITH (\n" +
                "    OIDS = FALSE\n" +
                ")\n" +
                "TABLESPACE pg_default;");
    }
}
