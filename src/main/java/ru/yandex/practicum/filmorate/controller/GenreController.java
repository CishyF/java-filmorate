package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Film.Genre> getGenres() {
        log.info("Пришел GET-запрос /genres");

        List<Film.Genre> genres = genreService.findAll();
        log.info("Ответ на GET-запрос /genres с телом={}", genres);
        return genres;
    }

    @GetMapping("/{id}")
    public Film.Genre getGenre(@PathVariable int id) {
        log.info("Пришел GET-запрос /genres/{id={}}", id);

        Film.Genre genre = genreService.findById(id);
        log.info("Ответ на GET-запрос /genres/{id={}} с телом={}", id, genre);
        return genre;
    }
}
