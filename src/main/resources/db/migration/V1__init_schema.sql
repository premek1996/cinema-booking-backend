-- ============================================
-- Enable pgcrypto (required for gen_random_uuid)
-- ============================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================
-- Create movies table
-- ============================================
CREATE TABLE IF NOT EXISTS movies (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(2000) NOT NULL,
    genre VARCHAR(255) NOT NULL,
    duration_minutes INT NOT NULL,
    release_date DATE NOT NULL,
    age_rating VARCHAR(50) NOT NULL
);

-- ============================================
-- Create cinema halls table
-- ============================================
CREATE TABLE IF NOT EXISTS cinema_halls (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    rows INT NOT NULL,
    seats_per_row INT NOT NULL
);

-- ============================================
-- Create seats table
-- ============================================
CREATE TABLE IF NOT EXISTS seats (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
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

-- ============================================
-- Create screenings table
-- ============================================
CREATE TABLE IF NOT EXISTS screenings (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    movie_id BIGINT NOT NULL,
    hall_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_screenings_movie
        FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CONSTRAINT fk_screenings_hall
        FOREIGN KEY (hall_id) REFERENCES cinema_halls(id) ON DELETE CASCADE,
    CONSTRAINT unique_screening_per_hall
        UNIQUE (hall_id, start_time)
);
