-- --------- Тип для report.type ---------
CREATE TYPE IF NOT EXISTS report_type_enum AS ENUM (
    'popular_places',
    'top_ratings'
);

-- --------- Користувачі (Users) ---------
CREATE TABLE IF NOT EXISTS users (
                                     id       SERIAL PRIMARY KEY,
                                     username VARCHAR(50)  NOT NULL UNIQUE,
    email    VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(64)  NOT NULL,  -- SHA-256 hex
    role     VARCHAR(20)  NOT NULL
    );

-- --------- Історичні місця (Place) ---------
CREATE TABLE IF NOT EXISTS place (
                                     id          SERIAL PRIMARY KEY,
                                     name        VARCHAR(255) NOT NULL,
    country     VARCHAR(100) NOT NULL,
    era         VARCHAR(100) NOT NULL,
    description TEXT,
    image_url   TEXT
    );

-- --------- Відгуки (Review) ---------
CREATE TABLE IF NOT EXISTS review (
                                      id          SERIAL PRIMARY KEY,
                                      place_id    INT NOT NULL REFERENCES place(id)   ON DELETE CASCADE,
    user_id     INT NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    text        TEXT,
    rating      INT  CHECK (rating BETWEEN 1 AND 5),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
    );

-- --------- Звіти (Report) ---------
CREATE TABLE IF NOT EXISTS report (
                                      id           SERIAL PRIMARY KEY,
                                      type         report_type_enum NOT NULL,
                                      generated_at TIMESTAMP         NOT NULL DEFAULT NOW(),
    content      TEXT
    );

-- --------- Улюблені місця (Favorite) ---------
CREATE TABLE IF NOT EXISTS favorite (
                                        id          SERIAL PRIMARY KEY,
                                        user_id     INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    place_id    INT NOT NULL REFERENCES place(id) ON DELETE CASCADE,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, place_id)
    );

-- --------- Індекси для швидших пошуків ---------
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email    ON users(email);
CREATE INDEX IF NOT EXISTS idx_place_name      ON place(name);
CREATE INDEX IF NOT EXISTS idx_place_country   ON place(country);
CREATE INDEX IF NOT EXISTS idx_review_place    ON review(place_id);
CREATE INDEX IF NOT EXISTS idx_review_user     ON review(user_id);