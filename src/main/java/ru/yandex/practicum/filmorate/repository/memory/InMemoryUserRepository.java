package ru.yandex.practicum.filmorate.repository.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 0;

    @Override
    public User save(User user) {
        final int userId = user.getId();
        if (userId != 0 && userId <= idCounter && users.containsKey(userId)) {
            users.put(userId, user);
            return user;
        }

        final int newId = ++idCounter;
        user.setId(newId);
        users.put(newId, user);

        return user;
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }
}
