
-- Insert events.
INSERT INTO public.event (item_id, created_at, public_reference, updated_at, description, ending, image, location, organized_by, short_description, starting, status, ticket_limit, ticket_target, title)
VALUES (1, '2018-11-07T21:19:43.792+01:00[Europe/Amsterdam]', '10146f0b-fbb9-4287-b34c-f44fedcae812', '2018-11-07T21:19:43.792+01:00[Europe/Amsterdam]', 'Ask all your questions about graduating during this panel.', '2019-01-01T17:00Z', 'http://localhost:8080/events/uploads/photo-on-07-11-2018-at-21.39.jpg', 'T.B.A.', 34, 'Ask all your questions about graduating during this panel.', '2019-01-01T21:00Z', 0, 0, 50, 'Career College 2.2 Graduation Panel');

-- Insert categories for the events.
INSERT INTO public.event_categories (event_item_id, categories) VALUES (1, 0);