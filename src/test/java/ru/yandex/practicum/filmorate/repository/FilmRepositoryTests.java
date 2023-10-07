package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmRepositoryTests(@Qualifier("filmRepositoryImpl")
    FilmRepository filmRepository, FilmService filmService, JdbcTemplate jdbcTemplate) {
        this.filmRepository = filmRepository;
        this.filmService = filmService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    public void saveFilm() {
        savedFilm = filmRepository.save(Film.builder()
            .name("nisi eiusmod")
            .description("adipisicing")
            .releaseDate(LocalDate.now())
            .duration(100)
            .mpa(new RatingMPA(1, "G"))
            .build());
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
    public void shouldGetFilmsSharedTest() {
        jdbcTemplate.update(
            "INSERT INTO film (rating_mpa_id, name, description, release_date, " + "duration)\n" +
                "VALUES (1, 'name1', 'description1', '1990-02-01', 101),\n" +
                "       (2, 'name2', 'description2', '1990-02-02', 102),\n" +
                "       (3, 'name3', 'description3', '1990-02-03', 103),\n" +
                "       (3, 'name4', 'description4', '1990-02-04', 104),\n" +
                "       (4, 'name5', 'description5', '1990-02-05', 105)");

        jdbcTemplate.update("INSERT INTO \"user\" (email, login, name, birthday)\n" +
            "VALUES ('email1', 'login1', 'name1', '1990-01-01'),\n" +
            "       ('email2', 'login2', 'name2', '1990-01-02'),\n" +
            "       ('email3', 'login3', 'name3', '1990-01-03'),\n" +
            "       ('email4', 'login4', 'name4', '1990-01-04'),\n" +
            "       ('email5', 'login5', 'name5', '1990-01-05')");

        jdbcTemplate.update(
            "INSERT INTO film_genre\n" + "VALUES (1, 1),\n" + "       (2, 2),\n" + "       (1, 3)");

        expectedId.incrementAndGet();
        expectedId.incrementAndGet();
        expectedId.incrementAndGet();
        expectedId.incrementAndGet();
        expectedId.incrementAndGet();
        expectedId.incrementAndGet();

        jdbcTemplate.update("INSERT INTO film_like\n" + "                    VALUES (1, 1),\n" +
            "                           (1, 2),\n" + "                           (1, 3),\n" +
            "                           (1, 4),\n" + "                           (2, 1),\n" +
            "                           (2, 2),\n" + "                           (4, 1),\n" +
            "                           (5, 2)");

        List<Film> sharedFilms = filmService.getCommonFilms(1, 2);

        assertEquals(2, sharedFilms.size());

        assertEquals(1, sharedFilms.get(0)
            .getId());
        assertEquals(2, sharedFilms.get(1)
            .getId());

    }

    @Test
    public void shouldDeleteFilmAfterSave() {
        filmRepository.delete(savedFilm);

        final int id = expectedId.incrementAndGet();
        Optional<Film> optionalFilm = filmRepository.findById(id);

        assertTrue(optionalFilm.isEmpty());
    }
}
