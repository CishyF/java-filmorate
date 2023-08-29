package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.CRUDRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final CRUDRepository<Film> filmRepository;
    private final UserService userService;

    public Film create(Film film) {
        return filmRepository.save(film);
    }

    public Film findById(int id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка получить несуществующий фильм"));
    }

    public List<Film> findAll() {
        return filmRepository.findAll();
    }

    public Film update(Film film) {
        final int filmId = film.getId();
        filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка обновить несуществующий фильм"));
        return filmRepository.save(film);
    }

    public Film addLikeToFilm(int filmId, int userId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка поставить лайк несуществующему фильму"));
        User user = userService.findById(userId);

        film.addLike(user);
        return film;
    }

    public void removeLikeFromFilm(int filmId, int userId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка убрать лайк у несуществующего фильма"));
        User user = userService.findById(userId);

        film.removeLike(user);
    }

    public List<Film> getFilmsByLikes(int count) {
        return filmRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Film::getAmountOfLikes).reversed())
                .limit(count).collect(Collectors.toList());
    }
}
