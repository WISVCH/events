package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

/**
 * DB migration which adds a key to a ticket.
 */
public class V20180802__Registration_form extends BaseJavaMigration {

    /**
     * Migration V20180705__Ticket_key.
     *
     * @param context of type Context
     * @throws Exception when something is wrong
     */
    public void migrate(Context context) throws Exception {
        try (Statement select = context.getConnection().createStatement()) {
            // Create address table
            String addressTable = "CREATE TABLE public.address (\n"
                    + "    id integer NOT NULL,\n"
                    + "    city character varying(255),\n"
                    + "    house_number character varying(255),\n"
                    + "    street_name character varying(255),\n"
                    + "    zip_code character varying(255)\n"
                    + ");\n"
                    + "ALTER TABLE public.address OWNER TO events;";
            select.execute(addressTable);

            // Create permissions table
            String permissionsTable = "CREATE TABLE public.permissions (\n"
                    + "    id integer NOT NULL,\n"
                    + "    career_mailing boolean NOT NULL,\n"
                    + "    education_mailing boolean NOT NULL,\n"
                    + "    general_mailing boolean NOT NULL,\n"
                    + "    machazine boolean NOT NULL\n"
                    + ");\n"
                    + "ALTER TABLE public.permissions OWNER TO events;";
            select.execute(permissionsTable);

            // Create profile table
            String profileTable = "CREATE TABLE public.profile (\n"
                    + "    id integer NOT NULL,\n"
                    + "    date_of_birth timestamp without time zone,\n"
                    + "    email character varying(255),\n"
                    + "    first_name character varying(255),\n"
                    + "    gender integer,\n"
                    + "    ice_contact_name character varying(255),\n"
                    + "    ice_contact_phone character varying(255),\n"
                    + "    initials character varying(255),\n"
                    + "    phone_number character varying(255),\n"
                    + "    surname character varying(255),\n"
                    + "    surname_prefix character varying(255),\n"
                    + "    address_id integer\n"
                    + ");\n"
                    + "ALTER TABLE public.profile OWNER TO events;";
            select.execute(profileTable);

            // Create registration table
            String registrationTable = "CREATE TABLE public.registration (\n"
                    + "    id integer NOT NULL,\n"
                    + "    created_at timestamp without time zone,\n"
                    + "    date_of_signing timestamp without time zone,\n"
                    + "    signed boolean NOT NULL,\n"
                    + "    permissions_id integer,\n"
                    + "    profile_id integer,\n"
                    + "    study_details_id integer\n"
                    + ");\n"
                    + "ALTER TABLE public.registration OWNER TO events;";
            select.execute(registrationTable);

            // Create study details table
            String studyDetailsTable = "CREATE TABLE public.study_details (\n"
                    + "    id integer NOT NULL,\n"
                    + "    first_study_year integer NOT NULL,\n"
                    + "    net_id character varying(255),\n"
                    + "    student_number character varying(255),\n"
                    + "    study integer\n"
                    + ");\n"
                    + "ALTER TABLE public.study_details OWNER TO events;";
            select.execute(studyDetailsTable);

            // Add constraints
            String constraints = "ALTER TABLE ONLY public.address ADD CONSTRAINT address_pkey PRIMARY KEY (id);\n"
                    + "ALTER TABLE ONLY public.permissions ADD CONSTRAINT permissions_pkey PRIMARY KEY (id);\n"
                    + "ALTER TABLE ONLY public.profile ADD CONSTRAINT profile_pkey PRIMARY KEY (id);\n"
                    + "ALTER TABLE ONLY public.registration ADD CONSTRAINT registration_pkey PRIMARY KEY (id);\n"
                    + "ALTER TABLE ONLY public.study_details ADD CONSTRAINT study_details_pkey PRIMARY KEY (id);\n"
                    + "ALTER TABLE ONLY public.profile ADD CONSTRAINT fk2hsdsntwy25qr73fsvd7l3wu7 FOREIGN KEY (address_id) REFERENCES public.address"
                    + "(id);\n"
                    + "ALTER TABLE ONLY public.registration ADD CONSTRAINT fk6pbiwpljchpolob40s7di41y4 FOREIGN KEY (profile_id) REFERENCES public"
                    + ".profile(id);\n"
                    + "ALTER TABLE ONLY public.registration ADD CONSTRAINT fkko3c91odb9f49kc3visnv21be FOREIGN KEY (study_details_id) REFERENCES public"
                    + ".study_details(id);\n"
                    + "ALTER TABLE ONLY public.registration ADD CONSTRAINT fkthagckur8igeragp1vx3qewlx FOREIGN KEY (permissions_id) REFERENCES public"
                    + ".permissions(id);";
            select.execute(constraints);

            // Update products table
            select.execute("ALTER TABLE public.product ADD ch_only boolean DEFAULT false NOT NULL;");
            select.execute("ALTER TABLE public.product ADD includes_registration boolean DEFAULT false NOT NULL;");
        }
    }
}
