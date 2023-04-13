CREATE TABLE movie
(
    id              SERIAL primary key,
    name_ukrainian  VARCHAR(255),
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

CREATE TABLE movie_genre
(
    movie_id SERIAL REFERENCES movie (id) ON UPDATE CASCADE ON DELETE CASCADE,
    genre_id SERIAL REFERENCES genre (id) ON UPDATE CASCADE,
    CONSTRAINT movie_genre_key PRIMARY KEY (movie_id, genre_id)
);
