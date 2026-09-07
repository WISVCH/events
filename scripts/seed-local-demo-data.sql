-- Deterministic demo data for local comparison environments only.
-- This removes all existing application data before inserting the fixture.

BEGIN;

TRUNCATE TABLE
    customer,
    document,
    event,
    order_product,
    orders,
    product,
    ticket,
    webhook,
    webhook_task
RESTART IDENTITY CASCADE;

INSERT INTO customer (id, verified_ch_member, created_at, email, key, name, rfid_token, sub) VALUES
    (1, true,  '2026-08-20 09:00:00', 'alex.devries@example.test',  'a1000000-0000-4000-8000-000000000001', 'Alex de Vries',  '0000000001', 'wisvch.demo.alex'),
    (2, false, '2026-08-21 10:15:00', 'sam.jansen@example.test',   'a1000000-0000-4000-8000-000000000002', 'Sam Jansen',     '0000000002', 'wisvch.demo.sam'),
    (3, true,  '2026-08-22 13:30:00', 'riley.bakker@example.test', 'a1000000-0000-4000-8000-000000000003', 'Riley Bakker',   '0000000003', 'wisvch.demo.riley'),
    (4, false, '2026-08-23 16:45:00', 'jordan.smit@example.test',  'a1000000-0000-4000-8000-000000000004', 'Jordan Smit',    '0000000004', 'wisvch.demo.jordan'),
    (5, true,  '2026-08-24 11:00:00', 'taylor.visser@example.test','a1000000-0000-4000-8000-000000000005', 'Taylor Visser',  '0000000005', 'wisvch.demo.taylor'),
    (6, false, '2026-08-25 14:20:00', 'morgan.meijer@example.test','a1000000-0000-4000-8000-000000000006', 'Morgan Meijer',  '0000000006', 'wisvch.demo.morgan'),
    (7, true,  '2026-08-26 09:10:00', 'casey.mulder@example.test','a1000000-0000-4000-8000-000000000007', 'Casey Mulder',  '0000000007', 'wisvch.demo.casey'),
    (8, false, '2026-08-27 12:25:00', 'jamie.boer@example.test', 'a1000000-0000-4000-8000-000000000008', 'Jamie Boer',    '0000000008', 'wisvch.demo.jamie'),
    (9, true,  '2026-08-28 15:40:00', 'drew.koning@example.test','a1000000-0000-4000-8000-000000000009', 'Drew Koning',  '0000000009', 'wisvch.demo.drew'),
    (10, false,'2026-08-29 10:50:00', 'robin.prins@example.test','a1000000-0000-4000-8000-000000000010', 'Robin Prins',  '0000000010', 'wisvch.demo.robin'),
    (11, true, '2026-08-30 13:05:00', 'lee.hendriks@example.test','a1000000-0000-4000-8000-000000000011', 'Lee Hendriks', '0000000011', 'wisvch.demo.lee'),
    (12, false,'2026-08-31 16:15:00', 'noa.kuiper@example.test','a1000000-0000-4000-8000-000000000012', 'Noa Kuiper',  '0000000012', 'wisvch.demo.noa');

INSERT INTO customer_ldap_groups (customer_id, ldap_groups) VALUES
    (1, 27),
    (3, 0),
    (5, 11),
    (7, 27),
    (9, 1),
    (11, 24);

INSERT INTO event (id, max_sold, organized_by, published, target, ending, start, description, external_product_url, image_url, key, location, short_description, title) VALUES
    (1, 120, 27, 0, 80, '2026-09-10 19:30:00', '2026-09-10 18:00:00',
        '<p>Meet fellow members over pizza, drinks, and board games.</p>', NULL,
        '/images/events_header.jpg', 'b1000000-0000-4000-8000-000000000001', 'Mekelzaal',
        'An evening of food and games for CH members.', 'CH Community Night'),
    (2, 60, 11, 0, 45, '2026-09-17 20:30:00', '2026-09-17 19:00:00',
        '<p>A practical introduction to modern web security, followed by questions.</p>', NULL,
        '/images/events_header.jpg', 'b1000000-0000-4000-8000-000000000002', 'Lecture hall E',
        'An accessible talk on web security.', 'T.U.E.S.Day: Web Security'),
    (3, NULL, 0, 1, 100, '2026-09-24 21:00:00', '2026-09-24 18:30:00',
        '<p>This event uses an external ticket provider.</p>', 'https://tickets.example.test/retro-night',
        '/images/events_header.jpg', 'b1000000-0000-4000-8000-000000000003', 'X',
        'A concept event with external ticket sales.', 'Retro Arcade Night'),
    (4, 30, 27, 0, 30, '2026-08-12 19:30:00', '2026-08-12 18:00:00',
        '<p>A completed event kept for reporting and order-history views.</p>', NULL,
        '/images/events_header.jpg', 'b1000000-0000-4000-8000-000000000004', 'Mekelzaal',
        'A past event for testing historical data.', 'Summer Barbecue'),
    (5, 75, 27, 0, 55, '2026-09-06 18:00:00', '2026-09-06 12:00:00',
        '<p>A session that is in progress while this local test fixture is used.</p>', NULL,
        '/images/events_header.jpg', 'b1000000-0000-4000-8000-000000000005', 'X',
        'A currently running event for time-sensitive views.', 'Live Coding Sprint'),
    (6, 200, 22, 0, 150, '2026-10-08 22:00:00', '2026-10-08 18:00:00',
        '<p>Our annual symposium with workshops, food, and networking.</p>', NULL,
        '/images/events_header.jpg', 'b1000000-0000-4000-8000-000000000006', 'Aula',
        'A large future event with several ticket types.', 'CH Symposium 2026'),
    (7, 40, 35, 0, 35, '2026-07-15 23:30:00', '2026-07-15 20:00:00',
        '<p>A completed event with scanned and open tickets.</p>', NULL,
        '/images/events_header.jpg', 'b1000000-0000-4000-8000-000000000007', 'X',
        'A past event for ticket and sales reporting.', 'Summer LAN Party'),
    (8, 80, 27, 2, 80, '2026-11-05 21:00:00', '2026-11-05 18:00:00',
        '<p>An unpublished event kept as an administration draft.</p>', NULL,
        '/images/events_header.jpg', 'b1000000-0000-4000-8000-000000000008', 'X',
        'A future concept event.', 'Winter Board Games');

INSERT INTO event_categories (event_id, categories) VALUES
    (1, 1),
    (2, 2),
    (3, 1),
    (4, 1),
    (5, 2),
    (6, 0),
    (6, 2),
    (7, 1),
    (8, 1);

INSERT INTO product (id, ch_only, cost, linked, max_sold, max_sold_per_customer, parent_product_id, reservable, reserved, sold, sell_end, sell_start, description, key, redirect_url, title, vat_rate) VALUES
    (1, false, 12.50, true,  100, 4, NULL, false, 0, 12, '2026-09-10 17:30:00', '2026-08-15 09:00:00', '<p>Admission including two drink tokens.</p>', 'c1000000-0000-4000-8000-000000000001', NULL, 'Community Night ticket', 'VAT_LOW'),
    (2, true,   4.50, true,   40, 2, NULL, false, 0,  8, '2026-09-10 17:30:00', '2026-08-15 09:00:00', '<p>Discounted ticket for verified CH members.</p>', 'c1000000-0000-4000-8000-000000000002', NULL, 'Member ticket', 'VAT_LOW'),
    (3, false,  0.00, true,   60, 1, NULL, true,  3, 19, '2026-09-17 18:30:00', '2026-08-20 09:00:00', '<p>Free admission for the lecture.</p>', 'c1000000-0000-4000-8000-000000000003', NULL, 'Lecture ticket', 'VAT_FREE'),
    (4, false,  8.00, true,   50, 3, NULL, false, 1, 22, '2026-09-17 18:30:00', '2026-08-20 09:00:00', '<p>Pizza selection; choose a variant below.</p>', 'c1000000-0000-4000-8000-000000000004', NULL, 'Pizza', 'VAT_LOW'),
    (5, false,  0.00, true, NULL, NULL,    4, false, 0,  7, '2026-09-17 18:30:00', '2026-08-20 09:00:00', '<p>Vegetarian pizza variant.</p>', 'c1000000-0000-4000-8000-000000000005', NULL, 'Pizza: vegetarian', 'VAT_FREE'),
    (6, false, 10.00, true,   30, 2, NULL, false, 0, 30, '2026-08-12 17:30:00', '2026-07-01 09:00:00', '<p>Ticket for a completed event.</p>', 'c1000000-0000-4000-8000-000000000006', NULL, 'Barbecue ticket', 'VAT_LOW'),
    (7, false,  0.00, true,   75, 1, NULL, false, 0, 31, '2026-09-06 17:30:00', '2026-08-28 09:00:00', '<p>Admission to the live coding sprint.</p>', 'c1000000-0000-4000-8000-000000000007', NULL, 'Coding Sprint ticket', 'VAT_FREE'),
    (8, false,  2.50, true,  100, 4, NULL, false, 0, 42, '2026-09-06 17:30:00', '2026-08-28 09:00:00', '<p>A fresh coffee during the coding sprint.</p>', 'c1000000-0000-4000-8000-000000000008', NULL, 'Coffee token', 'VAT_LOW'),
    (9, true,   5.00, true,   25, 1, NULL, true,  4, 12, '2026-09-06 17:30:00', '2026-08-28 09:00:00', '<p>Reserve a workstation as a CH member.</p>', 'c1000000-0000-4000-8000-000000000009', NULL, 'Reserved workstation', 'VAT_LOW'),
    (10, false, 25.00, true, 150, 4, NULL, false, 0, 41, '2026-10-08 17:30:00', '2026-08-25 09:00:00', '<p>Full access to the symposium and workshops.</p>', 'c1000000-0000-4000-8000-000000000010', NULL, 'Symposium ticket', 'VAT_LOW'),
    (11, true,  15.00, true,  50, 2, NULL, false, 0, 19, '2026-10-08 17:30:00', '2026-08-25 09:00:00', '<p>Discounted symposium ticket for members.</p>', 'c1000000-0000-4000-8000-000000000011', NULL, 'Symposium member ticket', 'VAT_LOW'),
    (12, false,  7.50, true, 100, 3, NULL, false, 0, 14, '2026-10-08 17:30:00', '2026-08-25 09:00:00', '<p>Optional dinner at the symposium.</p>', 'c1000000-0000-4000-8000-000000000012', NULL, 'Symposium dinner', 'VAT_LOW'),
    (13, false,  6.00, true,  40, 2, NULL, false, 0, 35, '2026-07-15 19:30:00', '2026-06-01 09:00:00', '<p>LAN party admission.</p>', 'c1000000-0000-4000-8000-000000000013', NULL, 'LAN party ticket', 'VAT_LOW'),
    (14, false,  3.00, true, 100, 5, NULL, false, 0, 24, '2026-07-15 19:30:00', '2026-06-01 09:00:00', '<p>Snack token for the LAN party.</p>', 'c1000000-0000-4000-8000-000000000014', NULL, 'Snack token', 'VAT_LOW'),
    (15, false,  0.00, false, NULL, NULL, NULL, false, 0, 0, NULL, NULL, '<p>An unlinked product for administration search.</p>', 'c1000000-0000-4000-8000-000000000015', 'https://ch.tudelft.nl', 'Standalone information product', 'VAT_FREE');

INSERT INTO event_products (event_id, products_id) VALUES
    (1, 1), (1, 2), (2, 3), (2, 4), (2, 5), (4, 6), (5, 7), (5, 8), (5, 9),
    (6, 10), (6, 11), (6, 12), (7, 13), (7, 14);

INSERT INTO orders (id, administration_costs, amount, owner_id, payment_method, status, ticket_created, vat, created_at, paid_at, ch_payments_reference, created_by, public_reference) VALUES
    (1, 0.00, 25.00, 1, 5, 5, true, 2.06, '2026-09-01 14:05:00', '2026-09-01 14:06:00', 'demo-chpay-paid-001', 'wisvch.demo.alex', 'DEMO-PAID-001'),
    (2, 0.00,  4.50, 2, 2, 3, false, 0.37, '2026-09-05 09:15:00', NULL,                  'demo-chpay-pending-002', 'wisvch.demo.sam', 'DEMO-PENDING-002'),
    (3, 0.00,  8.00, 3, 6, 4, false, 0.66, '2026-09-05 10:30:00', NULL,                  NULL, 'wisvch.demo.riley', 'DEMO-RESERVATION-003'),
    (4, 0.00, 10.00, 5, 0, 2, false, 0.83, '2026-08-10 17:00:00', NULL,                  NULL, 'wisvch.demo.taylor', 'DEMO-CANCELLED-004'),
    (5, 0.00,  0.00, 7, 5, 5, true,  0.00, '2026-09-06 12:15:00', '2026-09-06 12:15:30', 'demo-chpay-paid-005', 'wisvch.demo.casey', 'DEMO-PAID-005'),
    (6, 0.00,  2.50, 8, 0, 5, true,  0.21, '2026-09-06 12:20:00', '2026-09-06 12:21:00', NULL, 'wisvch.demo.jamie', 'DEMO-PAID-006'),
    (7, 0.00,  5.00, 9, 6, 4, false, 0.41, '2026-09-06 12:35:00', NULL,                  NULL, 'wisvch.demo.drew', 'DEMO-RESERVATION-007'),
    (8, 0.00, 25.00,10, 2, 3, false, 2.06, '2026-09-05 17:10:00', NULL,                  'demo-chpay-pending-008', 'wisvch.demo.robin', 'DEMO-PENDING-008'),
    (9, 0.00, 15.00,11, 1, 5, true,  1.24, '2026-08-30 09:20:00', '2026-08-30 09:22:00', NULL, 'wisvch.demo.lee', 'DEMO-PAID-009'),
    (10,0.00,  6.00,12, 0, 8, false, 0.50, '2026-07-15 20:00:00', NULL,                  NULL, 'wisvch.demo.noa', 'DEMO-EXPIRED-010');

INSERT INTO order_product (id, price, product_id, vat, amount, vat_rate) VALUES
    (1, 12.50, 1, 1.03, 2, 'VAT_LOW'),
    (2,  4.50, 2, 0.37, 1, 'VAT_LOW'),
    (3,  8.00, 4, 0.66, 1, 'VAT_LOW'),
    (4, 10.00, 6, 0.83, 1, 'VAT_LOW'),
    (5,  0.00, 7, 0.00, 1, 'VAT_FREE'),
    (6,  2.50, 8, 0.21, 1, 'VAT_LOW'),
    (7,  5.00, 9, 0.41, 1, 'VAT_LOW'),
    (8, 25.00,10, 2.06, 1, 'VAT_LOW'),
    (9, 15.00,11, 1.24, 1, 'VAT_LOW'),
    (10, 6.00,13, 0.50, 1, 'VAT_LOW');

INSERT INTO orders_order_products (order_id, order_products_id) VALUES
    (1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7), (8, 8), (9, 9), (10, 10);

INSERT INTO ticket (id, order_id, owner_id, product_id, status, valid, key, unique_code) VALUES
    (1, 1, 1, 1, 0, true,  'd1000000-0000-4000-8000-000000000001', '100001'),
    (2, 1, 1, 1, 1, true,  'd1000000-0000-4000-8000-000000000002', '100002'),
    (3, 5, 7, 7, 0, true,  'd1000000-0000-4000-8000-000000000003', '100003'),
    (4, 6, 8, 8, 1, true,  'd1000000-0000-4000-8000-000000000004', '100004'),
    (5, 9,11,11, 0, true,  'd1000000-0000-4000-8000-000000000005', '100005'),
    (6, 9,11,11, 1, false, 'd1000000-0000-4000-8000-000000000006', '100006');

INSERT INTO webhook (id, active, ldap_group, key, payload_url, secret) VALUES
    (1, true, 27, 'e1000000-0000-4000-8000-000000000001', 'https://webhook.example.test/events', 'demo-events-secret'),
    (2, true, 11, 'e1000000-0000-4000-8000-000000000002', 'https://webhook.example.test/products', 'demo-products-secret'),
    (3, false, 0, 'e1000000-0000-4000-8000-000000000003', 'https://webhook.example.test/disabled', 'demo-disabled-secret');

INSERT INTO webhook_webhook_triggers (webhook_id, webhook_triggers) VALUES
    (1, 0), (1, 1), (2, 2), (2, 3), (3, 0);

INSERT INTO webhook_task (id, trigger, webhook_id, webhook_task_status, created_at, webhook_error, object) VALUES
    (1, 0, 1, 1, '2026-09-01 10:00:00', NULL,
        '{"key":"b1000000-0000-4000-8000-000000000001","title":"CH Community Night","trigger":"EVENT_CREATE_UPDATE"}'::jsonb),
    (2, 2, 2, 1, '2026-09-02 11:30:00', NULL,
        '{"key":"c1000000-0000-4000-8000-000000000001","title":"Community Night ticket","trigger":"PRODUCT_CREATE_UPDATE"}'::jsonb),
    (3, 0, 1, 2, '2026-09-03 09:15:00', 'HTTP 500: demonstration failure',
        '{"key":"b1000000-0000-4000-8000-000000000002","title":"T.U.E.S.Day: Web Security","trigger":"EVENT_CREATE_UPDATE"}'::jsonb),
    (4, 3, 2, 2, '2026-09-04 16:45:00', 'Connect to webhook.example.test:443 failed: Unknown host',
        '{"key":"c1000000-0000-4000-8000-000000000015","title":"Standalone information product","trigger":"PRODUCT_DELETE"}'::jsonb),
    (5, 0, 3, 1, '2026-09-05 14:00:00', NULL,
        '{"key":"b1000000-0000-4000-8000-000000000008","title":"Winter Board Games","trigger":"EVENT_CREATE_UPDATE"}'::jsonb);

SELECT setval('customer_seq', 12, true);
SELECT setval('event_seq', 8, true);
SELECT setval('product_seq', 15, true);
SELECT setval('order_seq', 10, true);
SELECT setval('order_product_seq', 10, true);
SELECT setval('ticket_seq', 6, true);
SELECT setval('webhook_seq', 3, true);
SELECT setval('webhook_task_seq', 5, true);

COMMIT;
