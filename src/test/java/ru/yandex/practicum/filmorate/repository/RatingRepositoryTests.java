package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.RatingMPA;

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
        List<RatingMPA> expectedRatings = List.of(
                new RatingMPA(1, "G"),
                new RatingMPA(2, "PG"),
                new RatingMPA(3, "PG-13"),
                new RatingMPA(4, "R"),
                new RatingMPA(5, "NC-17")
        );
        List<RatingMPA> actualRatings = ratingRepository.findAll();
        assertEquals(expectedRatings, actualRatings);
    }

    @Test
    public void shouldReturnGRating() {
        final int idG = 1;
        RatingMPA expectedRating = new RatingMPA(idG, "G");
        Optional<RatingMPA> optionalRating = ratingRepository.findById(idG);
        assertTrue(optionalRating.isPresent());

        RatingMPA actualRating = optionalRating.get();
        assertEquals(expectedRating, actualRating);
    }
}
