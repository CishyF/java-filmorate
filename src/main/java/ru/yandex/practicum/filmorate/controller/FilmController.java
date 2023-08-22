package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
public class FilmController {

    private Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 0;

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Пришел GET-запрос /films без тела");

        if (films.isEmpty()) {
            log.info("Ответ на GET-запрос /films с пустым списком фильмов");
            return Collections.emptyList();
        }

        log.info("Ответ на GET-запрос /films с телом={}", films.values());
        return new ArrayList<>(films.values());
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Пришел POST-запрос /films с телом={}", film);

        final int id = ++idCounter;
        film.setId(id);

        films.put(id, film);
        log.info("Фильм film={} успешно создан", film);
        return film;
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Пришел PUT-запрос /films с телом={}", film);

        final int id = film.getId();

        if (!films.containsKey(id)) {
            log.warn("Попытка обновить несуществующий фильм film={}", film);
            return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
        }

        films.put(id, film);
        log.info("Фильм film={} успешно обновлен", film);
        return ResponseEntity.ok(film);
    }
}
