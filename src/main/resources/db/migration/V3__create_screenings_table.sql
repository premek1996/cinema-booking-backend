CREATE TABLE IF NOT EXISTS screenings (
    id BIGSERIAL PRIMARY KEY,
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
