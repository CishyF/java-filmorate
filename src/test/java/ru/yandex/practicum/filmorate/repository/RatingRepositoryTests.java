package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingRepositoryTests {

    private final RatingRepository ratingRepository;

    @Test
    public void shouldReturnAllRatings() {
        List<Film.RatingMPA> expectedRatings = List.of(
                new Film.RatingMPA(1, "G"),
                new Film.RatingMPA(2, "PG"),
                new Film.RatingMPA(3, "PG-13"),
                new Film.RatingMPA(4, "R"),
                new Film.RatingMPA(5, "NC-17")
        );
        List<Film.RatingMPA> actualRatings = ratingRepository.findAll();
        assertEquals(expectedRatings, actualRatings);
    }

    @Test
    public void shouldReturnGRating() {
        final int idG = 1;
        Film.RatingMPA expectedRating = new Film.RatingMPA(idG, "G");
        Optional<Film.RatingMPA> optionalRating = ratingRepository.findById(idG);
        assertTrue(optionalRating.isPresent());

        Film.RatingMPA actualRating = optionalRating.get();
        assertEquals(expectedRating, actualRating);
    }
}
