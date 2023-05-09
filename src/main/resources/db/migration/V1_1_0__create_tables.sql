CREATE TABLE movie
(
    id              SERIAL primary key,
    name_ukrainian  VARCHAR(255),
    name_native     VARCHAR(255),
    price           double precision,
    rating          double precision,
    year_of_release integer,
    picture_path    VARCHAR(512)
);

CREATE TABLE genre
(
    id   SERIAL primary key,
    name VARCHAR(255)
);

CREATE TABLE country
(
    id   SERIAL primary key,
    name VARCHAR(255)
);

CREATE TABLE users
(
    id       SERIAL primary key,
    email    VARCHAR(100),
    userName VARCHAR(100),
    nickName VARCHAR(100),
    encrypted_password VARCHAR(100)
);

CREATE TABLE movie_genre
(
    movie_id SERIAL REFERENCES movie (id) ON UPDATE CASCADE ON DELETE CASCADE,
    genre_id SERIAL REFERENCES genre (id) ON UPDATE CASCADE,
    CONSTRAINT movie_genre_key PRIMARY KEY (movie_id, genre_id)
);

CREATE TABLE movie_country
(
    movie_id   SERIAL REFERENCES movie (id) ON UPDATE CASCADE ON DELETE CASCADE,
    country_id SERIAL REFERENCES country (id) ON UPDATE CASCADE,
    CONSTRAINT movie_country_key PRIMARY KEY (movie_id, country_id)
);

CREATE TABLE review
(
    id      SERIAL primary key,
    user_id SERIAL REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    text    VARCHAR(1000)
);

CREATE TABLE movie_review
(
    movie_id  SERIAL REFERENCES movie (id) ON UPDATE CASCADE ON DELETE CASCADE,
    review_id SERIAL REFERENCES genre (id) ON UPDATE CASCADE,
    CONSTRAINT movie_review_key PRIMARY KEY (movie_id, review_id)
);
