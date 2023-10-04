package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.EventRepository;
import ru.yandex.practicum.filmorate.repository.FriendRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Repository
@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepository {

    private final JdbcTemplate jdbcTemplate;
    private final EventRepository eventRepository;

    @Override
    public void saveFriends(User user) {
        final int userId = user.getId();
        for (int friendId : user.getFriends()) {
            saveFriend(userId, friendId);
        }
    }

    private void saveFriend(int userId, int friendId) {
        String sqlQuery = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?);";
        jdbcTemplate.update(
                sqlQuery,
                userId,
                friendId
        );
        eventRepository.save(
                Event.builder()
                        .dateTime(LocalDateTime.now())
                        .userId(userId).eventType("FRIEND")
                        .operation("ADD")
                        .entityId(friendId).build()
        );
    }

    @Override
    public void loadFriends(List<User> users) {
        String inSql = String.join(", ", Collections.nCopies(users.size(), "?"));
        String sqlQuery = String.format("SELECT f.user_id, f.friend_id FROM friendship as f " +
                "LEFT JOIN \"user\" AS u ON f.friend_id = u.id " +
                "WHERE u.login IS NOT NULL AND f.user_id IN (%s);", inSql);
        Map<Integer, User> userById = users.stream().collect(toMap(User::getId, identity()));
        jdbcTemplate.query(
                sqlQuery,
                (rs) -> {
                    final int userId = rs.getInt("user_id");
                    final int friendId = rs.getInt("friend_id");
                    User user = userById.get(userId);
                    if (user != null) {
                        user.addFriend(friendId);
                    }
                },
                userById.keySet().toArray()
        );
    }

    @Override
    public List<Integer> findFriendsByUserId(int userId) {
        String sqlQuery = "SELECT friend_id FROM friendship WHERE user_id = ?;";
        List<Integer> friends = jdbcTemplate.query(
                sqlQuery,
                (rs, rowNum) -> rs.getInt("friend_id"),
                userId
        );
        return friends;
    }

    @Override
    public void deleteFriend(User user, User friend) {
        final int userId = user.getId();
        final int friendId = friend.getId();
        String sqlQuery = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                userId,
                friendId
        );
        eventRepository.save(
                Event.builder()
                        .dateTime(LocalDateTime.now())
                        .userId(userId).eventType("FRIEND")
                        .operation("REMOVE")
                        .entityId(friendId).build()
        );
    }

    @Override
    public void deleteFriends(User user) {
        final int userId = user.getId();
        String sqlQuery = "DELETE FROM friendship WHERE user_id = ?;";
        jdbcTemplate.update(sqlQuery, userId);
    }

    @Override
    public void deleteFriendFromUsers(User friend) {
        final int friendId = friend.getId();
        String sqlQuery = "DELETE FROM friendship WHERE friend_id = ?;";
        jdbcTemplate.update(sqlQuery, friendId);
    }
}
