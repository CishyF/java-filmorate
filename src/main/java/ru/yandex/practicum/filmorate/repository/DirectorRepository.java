package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorRepository {

    Director save(Director director);

    Optional<Director> findById(int id);

    List<Director> findAll();

    void deleteDirectorById(int id);
}
