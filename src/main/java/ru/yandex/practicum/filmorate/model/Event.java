package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Event {
    private int id;
    private LocalDateTime dateTime;
    private Integer userId;
    private String eventType;
    private String operation;
    private Integer entityId;
}
