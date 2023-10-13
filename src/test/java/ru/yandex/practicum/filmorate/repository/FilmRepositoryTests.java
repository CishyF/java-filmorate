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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

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
    private final FilmGenreRepository filmGenreRepository;
    private final UserService userService;
    private static final AtomicInteger expectedId = new AtomicInteger(0);

    @Autowired
    public FilmRepositoryTests(@Qualifier("filmRepositoryImpl")
    FilmRepository filmRepository, FilmService filmService, UserService userService,
        @Qualifier("filmGenreRepositoryImpl") FilmGenreRepository filmGenreRepository) {
            this.filmRepository = filmRepository;
            this.filmService = filmService;
            this.userService = userService;
            this.filmGenreRepository = filmGenreRepository;
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
    public void shouldGetFilmsCommonTest() {
        final int id;

        Film film2 = Film.builder()
            .mpa(new RatingMPA(1, "G"))
            .name("name1")
            .description("desc1")
            .releaseDate(LocalDate.of(1990, 1, 1))
            .duration(101)
            .build();
        Film film3 = Film.builder()
            .mpa(new RatingMPA(2, "PG"))
            .name("name2")
            .description("desc2")
            .releaseDate(LocalDate.of(1990, 1, 2))
            .duration(102)
            .build();
        Film film4 = Film.builder()
            .mpa(new RatingMPA(3, "PG-13"))
            .name("name3")
            .description("desc3")
            .releaseDate(LocalDate.of(1990, 1, 3))
            .duration(103)
            .build();
        Film film5 = Film.builder()
            .mpa(new RatingMPA(3, "PG-13"))
            .name("name4")
            .description("desc4")
            .releaseDate(LocalDate.of(1990, 1, 4))
            .duration(104)
            .build();
        Film film6 = Film.builder()
            .mpa(new RatingMPA(4, "R"))
            .name("name5")
            .description("desc5")
            .releaseDate(LocalDate.of(1990, 1, 5))
            .duration(105)
            .build();

        filmRepository.save(film2);
        filmRepository.save(film3);
        filmRepository.save(film4);
        filmRepository.save(film5);
        filmRepository.save(film6);

        User user1 = User.builder()
            .email("1@yandex.ru")
            .login("login1")
            .name("name1")
            .birthday(LocalDate.of(1990, 1, 1))
            .build();
        User user2 = User.builder()
            .email("2@yandex.ru")
            .login("login2")
            .name("name2")
            .birthday(LocalDate.of(1990, 1, 2))
            .build();
        User user3 = User.builder()
            .email("3@yandex.ru")
            .login("login3")
            .name("name3")
            .birthday(LocalDate.of(1990, 1, 3))
            .build();
        User user4 = User.builder()
            .email("4@yandex.ru")
            .login("login4")
            .name("name4")
            .birthday(LocalDate.of(1990, 1, 4))
            .build();
        User user5 = User.builder()
            .email("5@yandex.ru")
            .login("login5")
            .name("name5")
            .birthday(LocalDate.of(1990, 1, 5))
            .build();

        userService.create(user1);
        userService.create(user2);
        userService.create(user3);
        userService.create(user4);
        userService.create(user5);

        film2.addGenre(new Genre(1, "Комедия"));
        film3.addGenre(new Genre(2, "Драма"));
        film2.addGenre(new Genre(1, "Комедия"));

        filmGenreRepository.saveGenres(film2);
        filmGenreRepository.saveGenres(film3);

        expectedId.incrementAndGet();
        id = expectedId.incrementAndGet();
        expectedId.incrementAndGet();
        expectedId.incrementAndGet();
        expectedId.incrementAndGet();
        expectedId.incrementAndGet();

        filmService.addLikeToFilm(id, 1);
        filmService.addLikeToFilm(id, 2);
        filmService.addLikeToFilm(id, 3);

        filmService.addLikeToFilm(id, 4);
        filmService.addLikeToFilm(id + 1, 1);
        filmService.addLikeToFilm(id + 1, 2);
        filmService.addLikeToFilm(id + 2, 1);
        filmService.addLikeToFilm(id + 3, 2);

        List<Film> sharedFilms = filmService.getCommonFilms(1, 2);

        assertEquals(2, sharedFilms.size());

        assertEquals(4, sharedFilms.get(0)
            .getId());
        assertEquals(5, sharedFilms.get(1)
            .getId());
        assertEquals(id, sharedFilms.get(0).getId());
        assertEquals(id + 1, sharedFilms.get(1).getId());

    }

    @Test
    public void shouldDeleteFilmAfterSave() {
        filmRepository.delete(savedFilm);

        final int id = expectedId.incrementAndGet();
        Optional<Film> optionalFilm = filmRepository.findById(id);

        assertTrue(optionalFilm.isEmpty());
    }
}
