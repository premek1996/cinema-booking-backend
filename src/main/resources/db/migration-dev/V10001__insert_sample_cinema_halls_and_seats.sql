-- ============================================
-- INSERT sample cinema halls
-- ============================================

INSERT INTO cinema_halls (name, rows, seats_per_row)
VALUES
    ('Sala 1', 5, 10),
    ('Sala 2', 6, 12),
    ('Sala VIP', 4, 8);

-- ============================================
-- INSERT seats for each hall using generate_series()
-- ============================================

-- Sala 1 (5 x 10 = 50 seats)
INSERT INTO seats (hall_id, row_number, seat_number)
SELECT ch.id, r AS row_number, s AS seat_number
FROM cinema_halls ch
JOIN generate_series(1, 5) r ON TRUE
JOIN generate_series(1, 10) s ON TRUE
WHERE ch.name = 'Sala 1';

-- Sala 2 (6 x 12 = 72 seats)
INSERT INTO seats (hall_id, row_number, seat_number)
SELECT ch.id, r AS row_number, s AS seat_number
FROM cinema_halls ch
JOIN generate_series(1, 6) r ON TRUE
JOIN generate_series(1, 12) s ON TRUE
WHERE ch.name = 'Sala 2';

-- Sala VIP (4 x 8 = 32 seats)
INSERT INTO seats (hall_id, row_number, seat_number)
SELECT ch.id, r AS row_number, s AS seat_number
FROM cinema_halls ch
JOIN generate_series(1, 4) r ON TRUE
JOIN generate_series(1, 8) s ON TRUE
WHERE ch.name = 'Sala VIP';
