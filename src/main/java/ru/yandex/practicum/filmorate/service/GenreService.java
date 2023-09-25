package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.GenreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public Film.Genre findById(int id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new GenreDoesNotExistException("Попытка получить несуществующий жанр фильма"));
    }

    public List<Film.Genre> findAll() {
        return genreRepository.findAll();
    }
}
