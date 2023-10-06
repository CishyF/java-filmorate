package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Event {
    private int id;
    private Instant timestamp;
    private int userId;
    private EventType type;
    private EventOperation operation;
    private int entityId;
}
