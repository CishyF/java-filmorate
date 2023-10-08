package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        log.info("Пришел GET-запрос /films");

        List<Film> films = filmService.findAll();
        log.info("Ответ на GET-запрос /films с телом={}", films);
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        log.info("Пришел GET-запрос /films/{id={}}", id);

        Film film = filmService.findById(id);
        log.info("Ответ на GET-запрос /films/{id={}} с телом={}", id, film);
        return film;
    }

    @GetMapping("/popular")
    public List<Film> getTopFilmsByLikes(
        @RequestParam(value = "count", defaultValue = "10", required = false) int count
    ) {
        log.info("Пришел GET-запрос /films/popular?count={}", count);

        List<Film> popularFilms = filmService.getFilmsByLikes(count);
        log.info("Ответ на GET-запрос /films/popular?count={} с телом={}", count, popularFilms);
        return popularFilms;
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        log.info("Пришел GET-запрос /films/common?userId={}&friendId={}", userId, friendId);

        List<Film> commonFilms = filmService.getCommonFilms(userId, friendId);
        log.info("Ответ на GET-запрос /films/common?userId={}&friendId={} с телом={}", userId,
            friendId, commonFilms);
        return commonFilms;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Пришел POST-запрос /films с телом={}", film);

        Film savedFilm = filmService.create(film);
        log.info("Фильм film={} успешно создан", savedFilm);
        return savedFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Пришел PUT-запрос /films с телом={}", film);

        Film updatedFilm = filmService.update(film);
        log.info("Фильм film={} успешно обновлен", updatedFilm);
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLikeToFilm(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Пришел PUT-запрос /films/{id={}}/like/{userId={}}", filmId, userId);

        Film likedFilm = filmService.addLikeToFilm(filmId, userId);
        log.info("Лайк фильму film={} от пользователя с id={} поставлен", likedFilm, userId);
        return likedFilm;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromFilm(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Пришел DELETE-запрос /films/{id={}}/like/{userId={}}", filmId, userId);

        filmService.removeLikeFromFilm(filmId, userId);
        log.info("Лайк у фильма id={} от пользователя id={} удален", filmId, userId);
    }
}
