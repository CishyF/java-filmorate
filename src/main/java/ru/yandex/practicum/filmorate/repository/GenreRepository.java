package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    void saveGenres(Film film);

    List<Genre> findGenresByFilmId(int filmId);

    Optional<Genre> findById(int id);

    List<Genre> findAll();

    void deleteGenres(Film film);
}
