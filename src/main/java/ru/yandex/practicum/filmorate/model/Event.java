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
    private String eventType;
    private String operation;
    private int entityId;
}
