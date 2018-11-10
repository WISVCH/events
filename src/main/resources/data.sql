
-- Insert events.
INSERT INTO public.event (item_id, created_at, public_reference, updated_at, description, ending, image, location, organized_by, short_description, starting, status, ticket_limit, ticket_target, title) VALUES
 (1, '2018-11-07T21:19:43.792+01:00[Europe/Amsterdam]', '10146f0b-fbb9-4287-b34c-f44fedcae812', '2018-11-07T21:19:43.792+01:00[Europe/Amsterdam]', 'Ask all your questions about graduating during this panel.', '2019-01-01T21:00Z', 'http://localhost:8080/events/uploads/photo-on-07-11-2018-at-21.39.jpg', 'T.B.A.', 34, 'Ask all your questions about graduating during this panel.', '2019-01-01T17:00Z', 0, 0, 50, 'Career College 2.2 Graduation Panel');

INSERT INTO public.event_categories (event_item_id, categories) VALUES
 (1, 0);


-- Insert products
INSERT INTO public.product (item_id, created_at, public_reference, updated_at, ch_only, description, mandatory_product_option, max_number_of_ticket_per_user, price, ticket_limit, title) VALUES
 (4, '2018-11-08T21:21:22.539+01:00[Europe/Amsterdam]', '3d45c4b8-1dc5-4822-8741-65a9b7a83059', '2018-11-08T21:21:22.539+01:00[Europe/Amsterdam]', false, 'Career College 2.2 Graduation Panel', true, 10, 0, 0, 'Career College 2.2 Graduation Panel');

INSERT INTO public.product_option (item_id, created_at, public_reference, updated_at, additional_price, title) VALUES
 (1, '2018-11-08T21:21:22.539+01:00[Europe/Amsterdam]', '14bb7762-ef5b-400c-8e4b-d4ad3ebd0ba5', '2018-11-08T21:21:22.539+01:00[Europe/Amsterdam]', 0, 'Without food'),
 (2, '2018-11-08T21:21:22.540+01:00[Europe/Amsterdam]', '6a3eca90-2a45-4c92-aaba-e03c51b554d4', '2018-11-08T21:21:22.540+01:00[Europe/Amsterdam]', 5, 'Pizza Margarita'),
 (3, '2018-11-08T21:21:22.540+01:00[Europe/Amsterdam]', 'a0e63859-abb4-4605-968e-e7c49fa8bd4a', '2018-11-08T21:21:22.540+01:00[Europe/Amsterdam]', 5, 'Pizza Calzone');

INSERT INTO public.product_product_options (product_item_id, product_options_item_id) VALUES
 (4, 1),
 (4, 2),
 (4, 3);


-- Link product to event
INSERT INTO public.event_products (event_item_id, products_item_id) VALUES
 (1, 4);

-- Insert users
INSERT INTO public."user" (item_id, created_at, public_reference, updated_at, email, name, sub) VALUES
 (5, '2018-11-09T12:14:46.701+01:00[Europe/Amsterdam]', 'f30d05d6-6345-45be-a6c6-e2b29eb9843b', '2018-11-09T12:14:46.708+01:00[Europe/Amsterdam]', 'svenp@ch.tudelft.nl', 'Sven Popping', 'WISVCH.3806');

-- Set hibernate sequence
SELECT pg_catalog.setval('public.hibernate_sequence', 5, true);