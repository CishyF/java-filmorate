package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false, defaultValue = "0") int id, @RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Пришел GET-запрос /reviews с параметром id={} и параметром count={}", id, count);
        List<Review> reviews;
        if (id == 0) {
            reviews = reviewService.findAll().stream().limit(count).collect(Collectors.toList());
        } else {
            reviews = reviewService.findReviewsByFilmId(id, count);
        }
        log.info("Ответ на GET-запрос /reviews с телом={}", reviews);
        return reviews;
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable int id) {
        log.info("Пришел GET-запрос /review/{id={}}", id);

        Review review = reviewService.findById(id);
        log.info("Ответ на GET-запрос /reviews/{id={}} с телом={}", id, review);
        return review;
    }


    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        log.info("Пришел POST-запрос /reviews с телом={}", review);

        Review savedReview = reviewService.create(review);
        log.info("Отзыв review={} успешно создан", review);
        return savedReview;
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Пришел PUT-запрос /films с телом={}", review);

        Review updatedReview = reviewService.update(review);
        log.info("Отзыв review={} успешно обновлен", updatedReview);
        return updatedReview;
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

    @PutMapping("/{id}/dislike/{userId}")
    public Film addDislikeToFilm(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Пришел PUT-запрос /films/{id={}}/like/{userId={}}", filmId, userId);

        Film likedFilm = filmService.addLikeToFilm(filmId, userId);
        log.info("Лайк фильму film={} от пользователя с id={} поставлен", likedFilm, userId);
        return likedFilm;
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislikeFromFilm(@PathVariable("id") int filmId, @PathVariable int userId) {
        log.info("Пришел DELETE-запрос /films/{id={}}/like/{userId={}}", filmId, userId);

        filmService.removeLikeFromFilm(filmId, userId);
        log.info("Лайк у фильма id={} от пользователя id={} удален", filmId, userId);
    }

}
