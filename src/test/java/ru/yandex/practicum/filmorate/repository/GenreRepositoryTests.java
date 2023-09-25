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
public class GenreRepositoryTests {

    private final GenreRepository genreRepository;

    @Test
    public void shouldReturnAllGenres() {
        List<Film.Genre> expectedGenres = List.of(
                new Film.Genre(1, "Комедия"),
                new Film.Genre(2, "Драма"),
                new Film.Genre(3, "Мультфильм"),
                new Film.Genre(4, "Триллер"),
                new Film.Genre(5, "Документальный"),
                new Film.Genre(6, "Боевик")
        );
        List<Film.Genre> actualGenres = genreRepository.findAll();
        assertEquals(expectedGenres, actualGenres);
    }

    @Test
    public void shouldReturnComedyGenre() {
        final int idComedy = 1;
        Film.Genre expectedGenre = new Film.Genre(1, "Комедия");

        Optional<Film.Genre> optionalGenre = genreRepository.findById(idComedy);
        assertTrue(optionalGenre.isPresent());

        Film.Genre actualGenre = optionalGenre.get();
        assertEquals(expectedGenre, actualGenre);
    }
}
