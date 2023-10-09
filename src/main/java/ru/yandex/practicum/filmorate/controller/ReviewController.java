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
    public Review addLikeToReview(@PathVariable("id") int reviewId, @PathVariable int userId) {
        log.info("Пришел PUT-запрос /reviews/{id={}}/like/{userId={}}", reviewId, userId);

        Review likedReview = reviewService.addLikeToReview(reviewId, userId, true);
        log.info("Лайк отзыву review={} от пользователя с id={} поставлен", likedReview, userId);
        return likedReview;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromReview(@PathVariable("id") int reviewId, @PathVariable int userId) {
        log.info("Пришел DELETE-запрос /reviews/{id={}}/like/{userId={}}", reviewId, userId);

        reviewService.removeLikeFromReview(reviewId, userId);
        log.info("Лайк у отзыва id={} от пользователя id={} удален", reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislikeToFilm(@PathVariable("id") int reviewId, @PathVariable int userId) {
        log.info("Пришел PUT-запрос /reviews/{id={}}/dislike/{userId={}}", reviewId, userId);

        Review likedReview = reviewService.addLikeToReview(reviewId, userId, false);
        log.info("Дизлайк отзыву film={} от пользователя с id={} поставлен", likedReview, userId);
        return likedReview;
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislikeFromReview(@PathVariable("id") int reviewId, @PathVariable int userId) {
        log.info("Пришел DELETE-запрос /reviews/{id={}}/dislike/{userId={}}", reviewId, userId);

        reviewService.removeLikeFromReview(reviewId, userId);
        log.info("Дизлайк у отзыва id={} от пользователя id={} удален", reviewId, userId);
    }

}
