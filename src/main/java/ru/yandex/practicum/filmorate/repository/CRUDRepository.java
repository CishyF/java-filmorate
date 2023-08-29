package ru.yandex.practicum.filmorate.repository;

import java.util.List;
import java.util.Optional;

public interface CRUDRepository<T> {

     T save(T t);

     Optional<T> findById(int id);

     List<T> findAll();

     void delete(T t);
}
