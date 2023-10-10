package ru.yandex.practicum.filmorate.repository.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.*;

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

    @Override
    public void delete(Film film) {
        films.remove(film.getId());
    }
}
