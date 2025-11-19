CREATE TABLE IF NOT EXISTS cinema_halls (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    rows INT NOT NULL,
    seats_per_row INT NOT NULL
);

CREATE TABLE IF NOT EXISTS seats (
    id BIGSERIAL PRIMARY KEY,
    hall_id BIGINT NOT NULL,
    row_number INT NOT NULL,
    seat_number INT NOT NULL,

    CONSTRAINT fk_seat_hall
        FOREIGN KEY (hall_id)
        REFERENCES cinema_halls (id)
        ON DELETE CASCADE,

    CONSTRAINT uq_seat_unique
        UNIQUE (hall_id, row_number, seat_number)
);
