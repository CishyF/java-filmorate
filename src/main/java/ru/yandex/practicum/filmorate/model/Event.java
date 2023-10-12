package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Event {
    private int id;
    private Long timestamp;
    private int userId;
    @JsonProperty("eventType")
    private EventType type;
    private EventOperation operation;
    private int entityId;
}
