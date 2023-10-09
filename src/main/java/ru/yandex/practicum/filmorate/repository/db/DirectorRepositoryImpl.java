package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DirectorSaveException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DirectorRepositoryImpl implements DirectorRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director save(Director director) {
        final int directorId = director.getId();
        if (findById(directorId).isPresent()) {
            return update(director);
        }

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withSchemaName("public")
                .withTableName("director")
                .usingColumns("name")
                .usingGeneratedKeyColumns("id");
        insert.compile();

        int id = (int) insert.executeAndReturnKey(Map.of(
                "name", director.getName()
        ));
        director.setId(id);

        Director savedDirector = findById(id)
                .orElseThrow(() -> new DirectorSaveException("Произошла ошибка при сохранении режиссера"));
        return savedDirector;
    }

    private Director update(Director director) {
        final int directorId = director.getId();
        String sqlQuery = "UPDATE director SET name = ? WHERE id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                director.getName(),
                directorId
        );

        Director updatedDirector = findById(directorId)
                .orElseThrow(() -> new DirectorSaveException("Произошла ошибка при обновлении режиссера"));
        return updatedDirector;
    }

    @Override
    public Optional<Director> findById(int id) {
        String sqlQuery = "SELECT id, name FROM director WHERE id = ?;";

        DirectorMapper mapper = new DirectorMapper();
        Director director;
        try {
            director = jdbcTemplate.queryForObject(
                    sqlQuery,
                    mapper,
                    id
            );
        } catch (DataAccessException e) {
            return Optional.empty();
        }
        if (director == null) {
            return Optional.empty();
        }

        return Optional.of(director);
    }

    @Override
    public List<Director> findAll() {
        String sqlQuery = "SELECT id, name FROM director;";

        DirectorMapper mapper = new DirectorMapper();
        List<Director> directors = jdbcTemplate.query(
                sqlQuery,
                mapper
        );
        return directors;
    }

    @Override
    public void deleteDirectorById(int id) {
        String sqlQuery = "DELETE FROM director WHERE id = ?;";
        jdbcTemplate.update(
                sqlQuery,
                id
        );
    }

    private static class DirectorMapper implements RowMapper<Director> {

        @Override
        public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Director.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .build();
        }
    }
}
