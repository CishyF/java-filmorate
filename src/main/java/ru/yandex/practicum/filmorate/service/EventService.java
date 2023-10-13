package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EventDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.repository.EventRepository;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event create(Event event) {
        return eventRepository.save(event);
    }

    public Event findById(int id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventDoesNotExistException("Попытка получить несуществующее событие"));
        return event;
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event update(Event event) {
        final int eventId = event.getId();
        eventRepository.findById(eventId)
                .orElseThrow(() -> new EventDoesNotExistException("Попытка обновить несуществующее событие"));
        return create(event);
    }


}
