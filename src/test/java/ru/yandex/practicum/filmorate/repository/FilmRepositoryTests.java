package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class FilmRepositoryTests {

    private final FilmRepository filmRepository;
    private final FilmService filmService;
    private Film savedFilm;
    private static final AtomicInteger expectedId = new AtomicInteger(0);

    @Autowired
    public FilmRepositoryTests(
            @Qualifier("filmRepositoryImpl") FilmRepository filmRepository,
            FilmService filmService
    ) {
        this.filmRepository = filmRepository;
        this.filmService = filmService;
    }

    @BeforeEach
    public void saveFilm() {
        savedFilm = filmRepository.save(
            Film.builder()
                    .name("nisi eiusmod")
                    .description("adipisicing")
                    .releaseDate(LocalDate.now())
                    .duration(100)
                    .mpa(new RatingMPA(1, "G"))
                    .build()
        );
    }

    @AfterEach
    public void afterEach() {
        filmRepository.findAll().forEach(filmService::delete);
    }

    @Test
    public void shouldSaveAndReturnFilmWithExpectedId() {
        assertNotNull(savedFilm);

        final int id = expectedId.incrementAndGet();
        final int filmId = savedFilm.getId();
        assertEquals(id, filmId);
    }

    @Test
    public void shouldFindByExpectedIdAfterSave() {
        final int id = expectedId.incrementAndGet();
        Optional<Film> optionalFilm = filmRepository.findById(id);
        assertTrue(optionalFilm.isPresent());

        Film foundFilm = optionalFilm.get();
        assertEquals(savedFilm, foundFilm);
    }

    @Test
    public void shouldFindEmptyIfIdIsIncorrect() {
        final int incorrectId = expectedId.incrementAndGet() * -1;
        Optional<Film> optionalFilm = filmRepository.findById(incorrectId);

        assertTrue(optionalFilm.isEmpty());
    }

    @Test
    public void shouldFindFilmsWithExpectedIds() {
        Film film2 = Film.builder()
                .name("vfdvdfvd")
                .description("dadidacing")
                .releaseDate(LocalDate.now())
                .duration(100)
                .mpa(new RatingMPA(1, "G"))
                .build();
        Film savedFilm2 = filmRepository.save(film2);

        final int expectedId1 = expectedId.incrementAndGet();
        final int expectedId2 = expectedId.incrementAndGet();

        List<Film> films = filmRepository.findAll();

        final int expectedSize = 2;
        assertEquals(expectedSize, films.size());

        Film actualFilm1 = films.get(0);
        Film actualFilm2 = films.get(1);

        assertEquals(savedFilm, actualFilm1);
        assertEquals(savedFilm2, actualFilm2);
        assertEquals(expectedId1, actualFilm1.getId());
        assertEquals(expectedId2, actualFilm2.getId());
    }

    @Test
    public void shouldDeleteFilmById() {
        Film film2 = Film.builder()
            .name("vfdvdfvd")
            .description("dadidacing")
            .releaseDate(LocalDate.now())
            .duration(100)
            .mpa(new RatingMPA(1, "G"))
            .build();
        Film savedFilm2 = filmRepository.save(film2);

        expectedId.incrementAndGet();
        expectedId.incrementAndGet();

        filmService.deleteFilmById(savedFilm2.getId());

        FilmDoesNotExistException exc = assertThrows(FilmDoesNotExistException.class,
            () -> filmService.deleteFilmById(savedFilm2.getId()));
        assertEquals("Попытка получить несуществующий фильм", exc.getMessage());
    }

    @Test
    public void shouldDeleteFilmAfterSave() {
        filmRepository.delete(savedFilm);

        final int id = expectedId.incrementAndGet();
        Optional<Film> optionalFilm = filmRepository.findById(id);

        assertTrue(optionalFilm.isEmpty());
    }
}
