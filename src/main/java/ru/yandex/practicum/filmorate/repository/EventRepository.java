package ru.yandex.practicum.filmorate.repository;


import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event save(Event event);

    Optional<Event> findById(int id);

    List<Event> findByUserId(int id);

    List<Event> findAll();

    void delete(Event event);
}
