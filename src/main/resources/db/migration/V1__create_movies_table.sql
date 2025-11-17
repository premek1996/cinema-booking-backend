CREATE TABLE IF NOT EXISTS movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(2000) NOT NULL,
    genre VARCHAR(255) NOT NULL,
    duration_minutes INT NOT NULL,
    release_date DATE NOT NULL,
    age_rating VARCHAR(50) NOT NULL
);