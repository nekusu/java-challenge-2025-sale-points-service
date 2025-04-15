INSERT INTO
    sale_points (id, name)
VALUES
    (1, 'CABA'),
    (2, 'GBA_1'),
    (3, 'GBA_2'),
    (4, 'Santa Fe'),
    (5, 'Córdoba'),
    (6, 'Misiones'),
    (7, 'Salta'),
    (8, 'Chubut'),
    (9, 'Santa Cruz'),
    (10, 'Catamarca') ON CONFLICT (id) DO NOTHING;

INSERT INTO
    paths (id_a, id_b, cost)
VALUES
    (1, 2, 2),
    (1, 3, 3),
    (2, 3, 5),
    (2, 4, 10),
    (1, 4, 11),
    (4, 5, 5),
    (2, 5, 14),
    (6, 7, 32),
    (8, 9, 11),
    (10, 7, 5),
    (3, 8, 10),
    (5, 8, 30),
    (10, 5, 5),
    (4, 6, 6) ON CONFLICT (id_a, id_b) DO NOTHING;
