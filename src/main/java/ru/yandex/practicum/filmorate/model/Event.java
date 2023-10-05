package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    private int id;
    private Long timestamp;
    private Integer userId;
    private String eventType;
    private String operation;
    private Integer entityId;
}
