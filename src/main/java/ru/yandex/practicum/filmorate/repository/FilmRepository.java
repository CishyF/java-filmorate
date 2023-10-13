package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {

    Film save(Film film);

    Optional<Film> findById(int id);

    List<Film> findAll();

    default List<Film> findTopFilmsByName(String searchQuery) {
        throw new RuntimeException("Not Implemented");
    }

    default List<Film> findTopFilmsByDirector(String searchQuery) {
        throw new RuntimeException("Not Implemented");
    }

    default List<Film> findTopFilmsByLikes(int count) {
        throw new RuntimeException("Not Implemented");
    }

    default List<Film> findTopFilmsByLikesAndGenre(int genreId, int count) {
        throw new RuntimeException("Not Implemented");
    }

    default List<Film> findTopFilmsByLikesAndYear(int year, int count) {
        throw new RuntimeException("Not Implemented");
    }

    default List<Film> findTopFilmsByLikesAndGenreAndYear(int genreId, int year, int count) {
        throw new RuntimeException("Not Emplemented");
    }

    void delete(Film film);
}
