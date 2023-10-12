package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmSaveException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.repository.EventRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {
    private final JdbcTemplate jdbcTemplate;


    @Override
    public Event save(Event event) {
        if (findById(event.getId()).isPresent()) {
            return update(event);
        }
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withSchemaName("public")
                .withTableName("event")
                .usingColumns("event_id", "timestamp", "user_id", "event_type", "operation", "entity_id")
                .usingGeneratedKeyColumns("event_id");
        insert.compile();

        int id = (int) insert.executeAndReturnKey(Map.of(
                "timestamp", Instant.now().toEpochMilli(),
                "user_id", event.getUserId(),
                "event_type", event.getType(),
                "operation", event.getOperation(),
                "entity_id", event.getEntityId()
        ));
        event.setId(id);
        Event savedEvent = findById(id)
                .orElseThrow(() -> new FilmSaveException("Произошла ошибка при сохранении события"));
        return savedEvent;
    }

    @Override
    public Optional<Event> findById(int id) {
        String sqlQuery = "SELECT * FROM event WHERE event_id = ?";
        EventMapper mapper = new EventMapper();
        Event event;
        try {
            event = jdbcTemplate.queryForObject(
                    sqlQuery,
                    mapper,
                    id
            );
        } catch (DataAccessException e) {
            return Optional.empty();
        }
        if (event == null) {
            return Optional.empty();
        }
        return Optional.of(event);
    }

    @Override
    public List<Event> findByUserId(int id) {
        String sqlQuery = "SELECT * FROM event WHERE user_id = ?";
        EventMapper mapper = new EventMapper();
        List<Event> events = jdbcTemplate.query(
                sqlQuery,
                mapper,
                id
        );
        return events;
    }

    @Override
    public List<Event> findAll() {
        String sqlQuery = "SELECT * FROM event";
        EventMapper mapper = new EventMapper();
        List<Event> events = jdbcTemplate.query(
                sqlQuery,
                mapper
        );
        return events;
    }

    @Override
    public void delete(Event film) {
        final int filmId = film.getId();
        String sqlQuery = "DELETE FROM event WHERE event_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }


    private Event update(Event event) {
        final int eventId = event.getId();
        String sqlQuery = "UPDATE event SET timestamp = ?,user_id = ?,event_type = ?,operation = ?, entity_id = ? WHERE eventId = ?";
        jdbcTemplate.update(
                sqlQuery,
                event.getTimestamp(),
                event.getUserId(),
                event.getType(),
                event.getOperation(),
                event.getEntityId(),
                eventId
        );
        Event updatedEvent = findById(eventId)
                .orElseThrow(() -> new FilmSaveException("Произошла ошибка при обновлении события"));
        return updatedEvent;
    }

    private static class EventMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Event.builder()
                    .id(rs.getInt("event_id"))
                    .timestamp(rs.getLong("timestamp"))
                    .userId(rs.getInt("user_id"))
                    .type(EventType.valueOf(rs.getString("event_type")))
                    .operation(EventOperation.valueOf(rs.getString("operation")))
                    .entityId(rs.getInt("entity_id")).build();
        }
    }
}
