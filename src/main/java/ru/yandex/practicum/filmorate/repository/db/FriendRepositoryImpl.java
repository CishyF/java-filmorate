package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FriendRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepository {

    private final JdbcTemplate jdbcTemplate;

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
