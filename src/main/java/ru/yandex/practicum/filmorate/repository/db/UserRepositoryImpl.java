package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserSaveException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {


    private final JdbcTemplate jdbcTemplate;

    @Override
    public User save(User user) {
        if (findById(user.getId()).isPresent()) {
            return update(user);
        }
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withSchemaName("public")
                .withTableName("\"user\"")
                .usingColumns("email", "login", "name", "birthday")
                .usingGeneratedKeyColumns("id");
        insert.compile();

        int id = (int) insert.executeAndReturnKey(Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday()
        ));

        User savedUser = findById(id)
                .orElseThrow(() -> new UserSaveException("Произошла ошибка при сохранении пользователя"));
        return savedUser;
    }

    private User update(User user) {
        final int userId = user.getId();
        String sqlQuery = "UPDATE \"user\" SET email = ?, login = ?, name = ?, " +
                "birthday = ? WHERE id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                userId
        );
        User updatedUser = findById(userId)
                .orElseThrow(() -> new UserSaveException("Произошла ошибка при обновлении пользователя"));
        return updatedUser;
    }

    @Override
    public Optional<User> findById(int id) {
        String sqlQuery = "SELECT * FROM \"user\" WHERE id = ?;";

        UserMapper mapper = new UserMapper();
        User user;
        try {
            user = jdbcTemplate.queryForObject(
                    sqlQuery,
                    mapper,
                    id
            );
        } catch (DataAccessException e) {
            return Optional.empty();
        }
        if (user == null) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT * FROM \"user\";";

        UserMapper mapper = new UserMapper();
        List<User> users = jdbcTemplate.query(
                sqlQuery,
                mapper
        );
        return users;
    }

    @Override
    public void delete(User user) {
        final int userId = user.getId();
        String sqlQuery = "DELETE FROM \"user\" WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, userId);
    }

    private static class UserMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return User.builder()
                    .id(rs.getInt("id"))
                    .email(rs.getString("email"))
                    .login(rs.getString("login"))
                    .birthday(rs.getDate("birthday").toLocalDate())
                    .name(rs.getString("name"))
                    .build();
        }
    }
}
