package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public List<Film.RatingMPA> getRatings() {
        log.info("Пришел GET-запрос /mpa");

        List<Film.RatingMPA> ratings = ratingService.findAll();
        log.info("Ответ на GET-запрос /mpa с телом={}", ratings);
        return ratings;
    }

    @GetMapping("/{id}")
    public Film.RatingMPA getRating(@PathVariable int id) {
        log.info("Пришел GET-запрос /mpa/{id={}}", id);

        Film.RatingMPA rating = ratingService.findById(id);
        log.info("Ответ на GET-запрос /mpa/{id={}} с телом={}", id, rating);
        return rating;
    }
}
