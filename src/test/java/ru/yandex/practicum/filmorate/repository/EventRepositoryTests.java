package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
 class EventRepositoryTests {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final FilmService filmService;


    private Film savedFilm;
    private User savedUser;
    private User savedFriend;


    @Autowired
    EventRepositoryTests(
            EventRepository eventRepository,
            UserService userService,
            FilmService filmService
    ) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.filmService = filmService;

    }

    @BeforeEach
    void saveEntities() {
        savedFilm = filmService.create(
                Film.builder()
                        .name("nisi eiusmod")
                        .description("adipisicing")
                        .releaseDate(LocalDate.now())
                        .duration(100)
                        .mpa(new RatingMPA(1, "G"))
                        .build()
        );
        savedUser = userService.create(
                User.builder()
                        .name("fkdsl")
                        .login("fdsf")
                        .email("fedorovn@yandex.ru")
                        .birthday(LocalDate.now())
                        .build()
        );
        savedFriend = userService.create(
                User.builder()
                        .name("Mark")
                        .login("Kostrykin")
                        .email("kostrykinmark@gmail.com")
                        .birthday(LocalDate.now())
                        .build()
        );

    }

    @AfterEach
    void afterEach() {

        eventRepository.findAll().forEach(eventRepository::delete);
        filmService.delete(savedFilm);
        userService.delete(savedUser);
        userService.delete(savedFriend);
    }

    @Test
    void shouldAddFriendToEventWithAddStatus() {
        userService.addFriendToUser(savedUser.getId(), savedFriend.getId());
        userService.addFriendToUser(savedFriend.getId(), savedUser.getId());
        List<Event> events = userService.getUserFeed(savedFriend.getId());
        assertEquals(1, events.size());
        Event event = events.get(0);
        assertEquals("ADD", event.getOperation());
        assertEquals("FRIEND", event.getEventType());
        assertEquals(savedUser.getId(), event.getUserId());
        assertEquals(savedFriend.getId(), event.getEntityId());
        userService.removeFriendOfUser(savedUser.getId(), savedFriend.getId());
        userService.removeFriendOfUser(savedFriend.getId(), savedUser.getId());
    }

    @Test
    void shouldRemoveFriendToEventWithRemoveStatus() {
        userService.addFriendToUser(savedUser.getId(), savedFriend.getId());
        userService.addFriendToUser(savedFriend.getId(), savedUser.getId());
        userService.removeFriendOfUser(savedFriend.getId(), savedUser.getId());
        List<Event> events = userService.getUserFeed(savedUser.getId());
        assertEquals(2, events.size());
        Event event = events.get(1);
        assertEquals("REMOVE", event.getOperation());
        assertEquals("FRIEND", event.getEventType());
        assertEquals(savedFriend.getId(), event.getUserId());
        assertEquals(savedUser.getId(), event.getEntityId());
        userService.removeFriendOfUser(savedUser.getId(), savedFriend.getId());
    }

    @Test
    void shouldAddLikeToEventWithAddStatus() {
        userService.addFriendToUser(savedUser.getId(), savedFriend.getId());
        userService.addFriendToUser(savedFriend.getId(), savedUser.getId());
        filmService.addLikeToFilm(savedFilm.getId(), savedUser.getId());
        List<Event> events = userService.getUserFeed(savedFriend.getId());
        assertEquals(2, events.size());
        Event event = events.get(1);
        assertEquals("ADD", event.getOperation());
        assertEquals("LIKE", event.getEventType());
        assertEquals(savedUser.getId(), event.getUserId());
        assertEquals(savedFilm.getId(), event.getEntityId());
        userService.removeFriendOfUser(savedUser.getId(), savedFriend.getId());
        userService.removeFriendOfUser(savedFriend.getId(), savedUser.getId());
        filmService.removeLikeFromFilm(savedFilm.getId(), savedUser.getId());
    }

    @Test
    void shouldRemoveLikeToEventWithRemoveStatus() {
        userService.addFriendToUser(savedUser.getId(), savedFriend.getId());
        userService.addFriendToUser(savedFriend.getId(), savedUser.getId());
        filmService.addLikeToFilm(savedFilm.getId(), savedUser.getId());
        filmService.removeLikeFromFilm(savedFilm.getId(), savedUser.getId());
        List<Event> events = userService.getUserFeed(savedFriend.getId());
        assertEquals(3, events.size());
        Event event = events.get(2);
        assertEquals("REMOVE", event.getOperation());
        assertEquals("LIKE", event.getEventType());
        assertEquals(savedUser.getId(), event.getUserId());
        assertEquals(savedFilm.getId(), event.getEntityId());
        userService.removeFriendOfUser(savedUser.getId(), savedFriend.getId());
        userService.removeFriendOfUser(savedFriend.getId(), savedUser.getId());
    }

    @Test
    void shouldThrowExceptionByGetByWrongId() {
        assertThrows(UserDoesNotExistException.class, () -> userService.getUserFeed(666));
    }
}
