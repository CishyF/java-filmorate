package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {

    void saveGenres(Film film);

    List<Film.Genre> findGenresByFilmId(int filmId);

    Optional<Film.Genre> findById(int id);

    List<Film.Genre> findAll();

    void deleteGenres(Film film);
}
