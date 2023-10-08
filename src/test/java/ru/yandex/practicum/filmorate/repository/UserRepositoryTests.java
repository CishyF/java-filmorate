package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserRepositoryTests {

    private final UserRepository userRepository;
    private final UserService userService;
    private User savedUser;
    private static final AtomicInteger expectedId = new AtomicInteger(0);

    @Autowired
    public UserRepositoryTests(
        @Qualifier("userRepositoryImpl") UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @BeforeEach
    public void saveUser() {
        savedUser = userRepository.save(User.builder()
            .name("fkdsl")
            .login("fdsf")
            .email("fedorovn@yandex.ru")
            .birthday(LocalDate.now())
            .build());
    }

    @AfterEach
    public void afterEach() {
        userRepository.findAll()
            .forEach(userService::delete);
    }

    @Test
    public void shouldSaveAndReturnUserWithExpectedId() {
        assertNotNull(savedUser);

        final int id = expectedId.incrementAndGet();
        final int userId = savedUser.getId();
        assertEquals(id, userId);
    }

    @Test
    public void shouldFindByExpectedIdAfterSave() {
        final int id = expectedId.incrementAndGet();
        Optional<User> optionalUser = userRepository.findById(id);
        assertTrue(optionalUser.isPresent());

        User foundUser = optionalUser.get();
        assertEquals(savedUser, foundUser);
    }

    @Test
    public void shouldFindEmptyIfIdIsIncorrect() {
        final int incorrectId = expectedId.incrementAndGet() * -1;
        Optional<User> optionalUser = userRepository.findById(incorrectId);

        assertTrue(optionalUser.isEmpty());
    }

    @Test
    public void shouldFindUsersWithExpectedIds() {
        User user2 = User.builder()
            .name("fkds")
            .login("fdsfsdf")
            .email("fedorov@yandex.ru")
            .birthday(LocalDate.now())
            .build();
        User savedUser2 = userRepository.save(user2);

        final int expectedId1 = expectedId.incrementAndGet();
        final int expectedId2 = expectedId.incrementAndGet();

        List<User> users = userRepository.findAll();

        final int expectedSize = 2;
        assertEquals(expectedSize, users.size());

        User actualUser1 = users.get(0);
        User actualUser2 = users.get(1);

        assertEquals(savedUser, actualUser1);
        assertEquals(savedUser2, actualUser2);
        assertEquals(expectedId1, actualUser1.getId());
        assertEquals(expectedId2, actualUser2.getId());
    }

    @Test
    public void shouldDeleteUserById() {
        User user2 = User.builder()
            .name("fkds")
            .login("fdsfsdf")
            .email("fedorov@yandex.ru")
            .birthday(LocalDate.now())
            .build();
        User savedUser2 = userRepository.save(user2);

        User user = userService.deleteById(savedUser2.getId());

        assertEquals(5, user.getId());

        UserDoesNotExistException exc = assertThrows(UserDoesNotExistException.class,
            () -> userService.findById(savedUser2.getId()));
        assertEquals("Попытка получить несуществующего пользователя", exc.getMessage());

        expectedId.incrementAndGet();
        expectedId.incrementAndGet();
    }

    @Test
    public void shouldDeleteUserAfterSave() {
        userRepository.delete(savedUser);

        final int id = expectedId.incrementAndGet();
        Optional<User> optionalUser = userRepository.findById(id);

        assertTrue(optionalUser.isEmpty());
    }
}
