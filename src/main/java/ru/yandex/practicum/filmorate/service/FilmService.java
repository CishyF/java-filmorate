package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmGenreRepository;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.LikeRepository;

import java.util.Collections;
import java.util.List;

@Service
public class FilmService {

    private final FilmRepository filmRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;

    @Autowired
    public FilmService(
            @Qualifier("filmRepositoryImpl") FilmRepository filmRepository,
            FilmGenreRepository filmGenreRepository,
            LikeRepository likeRepository,
            UserService userService
    ) {
        this.filmRepository = filmRepository;
        this.filmGenreRepository = filmGenreRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    public Film create(Film film) {
        Film savedFilm = filmRepository.save(film);
        filmGenreRepository.saveGenres(film);

        List<Film> singletonListForLoad = Collections.singletonList(savedFilm);
        filmGenreRepository.loadGenres(singletonListForLoad);
        likeRepository.loadLikes(singletonListForLoad);

        return savedFilm;
    }

    public Film findById(int id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка получить несуществующий фильм"));

        List<Film> singletonListForLoad = Collections.singletonList(film);
        filmGenreRepository.loadGenres(singletonListForLoad);
        likeRepository.loadLikes(singletonListForLoad);

        return film;
    }

    public List<Film> findAll() {
        List<Film> films = filmRepository.findAll();
        filmGenreRepository.loadGenres(films);
        likeRepository.loadLikes(films);
        return films;
    }

    public Film update(Film film) {
        final int filmId = film.getId();
        filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка обновить несуществующий фильм"));
        filmGenreRepository.deleteGenres(film);
        return create(film);
    }

    public Film addLikeToFilm(int filmId, int userId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка поставить лайк несуществующему фильму"));

        List<Film> singletonListForLoad = Collections.singletonList(film);
        filmGenreRepository.loadGenres(singletonListForLoad);
        likeRepository.loadLikes(singletonListForLoad);

        User user = userService.findById(userId);

        film.addLike(user);
        likeRepository.deleteLikes(film);
        likeRepository.saveLikes(film);
        return film;
    }

    public void removeLikeFromFilm(int filmId, int userId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new FilmDoesNotExistException("Попытка убрать лайк у несуществующего фильма"));
        User user = userService.findById(userId);

        likeRepository.deleteLike(film, user);
    }

    public List<Film> findTopFilmsByLikesOrGenreAndYear(int genreId, int year, int count) {
        List<Film> films;

        if (genreId > 0 && year == 0) {
            films = filmRepository.findTopFilmsByLikesAndGenre(genreId, count);

            filmGenreRepository.loadGenres(films);
            likeRepository.loadLikes(films);

            return films;
        } else if (genreId == 0 && year > 0) {
            films = filmRepository.findTopFilmsByLikesAndYear(year, count);

            filmGenreRepository.loadGenres(films);
            likeRepository.loadLikes(films);

            return films;
        } else if (genreId > 0 && year > 0) {
            films = filmRepository.findTopFilmsByLikesAndGenreAndYear(genreId, year, count);

            filmGenreRepository.loadGenres(films);
            likeRepository.loadLikes(films);

            return films;
        } else {
            films = filmRepository.findTopFilmsByLikes(count);

            filmGenreRepository.loadGenres(films);
            likeRepository.loadLikes(films);

            return films;
        }
    }

    public void delete(Film film) {
        filmRepository.delete(film);
        filmGenreRepository.deleteGenres(film);
        likeRepository.deleteLikes(film);
    }
}
