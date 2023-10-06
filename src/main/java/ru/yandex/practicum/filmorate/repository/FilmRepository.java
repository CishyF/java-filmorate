package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {

    Film save(Film film);

    Optional<Film> findById(int id);

    List<Film> findAll();

    List<Film> findTopFilmsByLikes(int count);

    List<Film> findTopFilmsByLikesAndGenre(int genreId, int count);

    List<Film> findTopFilmsByLikesAndYear(int year, int count);

    List<Film> findTopFilmsByLikesAndGenreAndYear(int genreId, int year, int count);

    void delete(Film film);
}
