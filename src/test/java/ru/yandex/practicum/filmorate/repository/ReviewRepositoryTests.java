package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
 class ReviewRepositoryTests {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final FilmService filmService;

    private Film savedFilm;
    private User savedUser;

    @Autowired
    ReviewRepositoryTests(
            ReviewRepository reviewRepository,
            UserService userService,
            FilmService filmService
    ) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.filmService = filmService;
    }

    @BeforeEach
    void saveEntities() {
        savedFilm = filmService.create(
                Film.builder()
                        .name("nisi eiusmod")
                        .description("adipisicing")
                        .releaseDate(LocalDate.now())
                        .duration(100)
                        .mpa(new RatingMPA(1, "G"))
                        .build()
        );
        savedUser = userService.create(
                User.builder()
                        .name("fkdsl")
                        .login("fdsf")
                        .email("fedorovn@yandex.ru")
                        .birthday(LocalDate.now())
                        .build()
        );
    }

    @AfterEach
    void afterEach() {
        reviewRepository.findAll().forEach(reviewRepository::delete);
        filmService.delete(savedFilm);
        userService.delete(savedUser);
    }

    @Test
    void shouldSaveReviewToFilm() {
        Review expectedReview = Review.builder().content("This film is so bad").isPositive(false).userId(1).filmId(1).build();
        Review actualReview = reviewRepository.save(expectedReview);
        assertEquals(1, actualReview.getId());
        assertEquals("This film is so bad", actualReview.getContent());
        assertEquals(false, actualReview.isPositive());
        assertEquals(1, actualReview.getUserId());
        assertEquals(1, actualReview.getFilmId());
        assertEquals(0, actualReview.getUseful());
    }

    @Test
    void shouldRemoveReviewFromFilm() {
        Review expectedReview = Review.builder().content("This film is so bad").isPositive(false).userId(1).filmId(1).build();
        Review actualReview = reviewRepository.save(expectedReview);
        reviewRepository.delete(actualReview);
        List<Review> reviews = reviewRepository.findAll();
        assertEquals(0, reviews.size());
    }

    @Test
    void shouldFindReviewById() {
        Review expectedReview = Review.builder().content("This film is so bad").isPositive(false).userId(1).filmId(1).build();
        reviewRepository.save(expectedReview);
        Optional<Review> optionalReview = reviewRepository.findById(1);
        assertTrue(optionalReview.isPresent());
        Review actualReview = optionalReview.get();
        assertEquals("This film is so bad", actualReview.getContent());
        assertEquals(false, actualReview.isPositive());
        assertEquals(1, actualReview.getUserId());
        assertEquals(1, actualReview.getFilmId());
        assertEquals(0, actualReview.getUseful());
    }

    @Test
    void shouldUpdateReview() {
        Review expectedReview = Review.builder().content("This film is so bad").isPositive(false).userId(1).filmId(1).build();
        reviewRepository.save(expectedReview);
        expectedReview.setContent("This film is so good");
        expectedReview.setPositive(true);
        reviewRepository.save(expectedReview);
        Optional<Review> optionalReview = reviewRepository.findById(1);
        Review actualReview = optionalReview.get();
        assertEquals("This film is so good", actualReview.getContent());
        assertEquals(true, actualReview.isPositive());
    }

    @Test
    void shouldBeEmptyWithWrongId() {
        Optional<Review> optionalReview = reviewRepository.findById(666);
        assertEquals(false, optionalReview.isPresent());
    }

}
