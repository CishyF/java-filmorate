package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreRepositoryTests {

    private final GenreRepository genreRepository;

    @Test
    public void shouldReturnAllGenres() {
        List<Genre> expectedGenres = List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик")
        );
        List<Genre> actualGenres = genreRepository.findAll();
        assertEquals(expectedGenres, actualGenres);
    }

    @Test
    public void shouldReturnComedyGenre() {
        final int idComedy = 1;
        Genre expectedGenre = new Genre(1, "Комедия");

        Optional<Genre> optionalGenre = genreRepository.findById(idComedy);
        assertTrue(optionalGenre.isPresent());

        Genre actualGenre = optionalGenre.get();
        assertEquals(expectedGenre, actualGenre);
    }
}
