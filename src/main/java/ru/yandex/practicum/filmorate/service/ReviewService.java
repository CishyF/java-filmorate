package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ReviewDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.ReviewLikeRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;

import java.util.Collections;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserService userService;

    public ReviewService(ReviewRepository reviewRepository, ReviewLikeRepository reviewLikeRepository, UserService userService) {
        this.reviewRepository = reviewRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.userService = userService;
    }

    public Review create(Review review) {
        Review savedReview = reviewRepository.save(review);
        List<Review> singletonListForLoad = Collections.singletonList(review);
        reviewLikeRepository.loadLikes(singletonListForLoad);

        return savedReview;
    }

    public Review findById(int id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewDoesNotExistException("Попытка получить несуществующий отзыв"));

        List<Review> singletonListForLoad = Collections.singletonList(review);

        reviewLikeRepository.loadLikes(singletonListForLoad);

        return review;
    }

    public List<Review> findReviewsByFilmId(int filmId, int count) {
        return reviewRepository.findReviewsByFilmId(filmId, count);
    }

    public List<Review> findAll() {
        List<Review> reviews = reviewRepository.findAll();
        reviewLikeRepository.loadLikes(reviews);
        return reviews;
    }

    public Review update(Review review) {
        final int reviewId = review.getId();
        reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewDoesNotExistException("Попытка обновить несуществующий обзор"));
        return create(review);
    }

    public Review addLikeToReview(int filmId, int userId, boolean like) {
        Review review = reviewRepository.findById(filmId)
                .orElseThrow(() -> new ReviewDoesNotExistException("Попытка поставить лайк/дизлайк несуществующему обзору"));

        List<Review> singletonListForLoad = Collections.singletonList(review);

        reviewLikeRepository.loadLikes(singletonListForLoad);

        User user = userService.findById(userId);
        if (like)
            review.addLike(user);
        else
            review.addDislike(user);

        reviewLikeRepository.deleteLikes(review);
        reviewLikeRepository.saveLikes(review);
        return review;
    }

    public void removeLikeFromReview(int reviewId, int userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewDoesNotExistException("Попытка убрать лайк/дизлайк у несуществующего обзора"));
        User user = userService.findById(userId);

        reviewLikeRepository.deleteLike(review, user);
    }


    public void delete(Review review) {
        reviewLikeRepository.deleteLikes(review);
        reviewRepository.delete(review);
    }

}
