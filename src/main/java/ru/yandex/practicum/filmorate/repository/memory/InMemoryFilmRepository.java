package ru.yandex.practicum.filmorate.repository.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryFilmRepository implements FilmRepository {

    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 0;

    @Override
    public Film save(Film film) {
        final int filmId = film.getId();
        if (filmId != 0 && filmId <= idCounter && films.containsKey(filmId)) {
            films.put(filmId, film);
            return film;
        }

        final int newId = ++idCounter;
        film.setId(newId);
        films.put(newId, film);

        return film;
    }

    @Override
    public Optional<Film> findById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> findAll() {
        if (films.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(films.values());
    }

    public List<Film> foundCommonFilms(int userId, int friendId) {
        throw new RuntimeException("Checkpoint not supported");
    }

    @Override
    public List<Film> findTopFilmsByLikes(int count) {
        return findAll().stream()
                .sorted(Comparator.comparingInt(Film::getAmountOfLikes).reversed())
                .limit(count).collect(Collectors.toList());
    }

    @Override
    public List<Film> findTopFilmsByLikesAndGenre(int genreId, int count) {
        return null;
    }

    @Override
    public List<Film> findTopFilmsByLikesAndYear(int year, int count) {
        return null;
    }

    @Override
    public List<Film> findTopFilmsByLikesAndGenreAndYear(int genreId, int year, int count) {
        return null;
    }

    @Override
    public void delete(Film film) {
        films.remove(film.getId());
    }
}
