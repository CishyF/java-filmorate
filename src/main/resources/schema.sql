CREATE TABLE IF NOT EXISTS genre (
    id integer PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS rating_mpa (
    id integer PRIMARY KEY,
    name varchar(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS film (
    id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    rating_mpa_id integer NOT NULL REFERENCES rating_mpa (id),
    name varchar(64),
    description varchar(200),
    release_date date,
    duration integer
    CONSTRAINT duration_is_positive CHECK (duration > 0)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id integer NOT NULL REFERENCES film (id),
    genre_id integer NOT NULL REFERENCES genre (id),
    UNIQUE (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS "user" (
    id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar(32) NOT NULL,
    login varchar(32) NOT NULL,
    name varchar(64),
    birthday date
);

CREATE TABLE IF NOT EXISTS film_like (
    film_id integer NOT NULL REFERENCES film (id),
    user_id integer NOT NULL REFERENCES "user" (id),
    UNIQUE (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id integer NOT NULL REFERENCES "user" (id),
    friend_id integer NOT NULL REFERENCES "user" (id),
    UNIQUE (user_id, friend_id),
    CONSTRAINT different_users CHECK (user_id != friend_id)
);

CREATE TABLE IF NOT EXISTS director (
    id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(64)
);

CREATE TABLE IF NOT EXISTS film_director (
    film_id integer NOT NULL REFERENCES film (id),
    director_id integer NOT NULL REFERENCES director (id),
    UNIQUE (film_id, director_id)
);