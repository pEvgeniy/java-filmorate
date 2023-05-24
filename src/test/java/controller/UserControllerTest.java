package controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.InvalidUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
    }

    @Test
    public void create() {
        User user = new User(0, "login", "name", "mymail@mail.ru",
                LocalDate.of(2000, 5, 20));
        userController.create(user);

        User expectedUser = new User(1, "login", "name", "mymail@mail.ru",
                LocalDate.of(2000, 5, 20));
        Assertions.assertEquals(expectedUser, userController.getUsers().get(1));
    }

    @Test
    public void createUserWithEmptyName() {
        User user = new User(0, "login", "", "mymail@mail.ru",
                LocalDate.of(2000, 5, 20));
        userController.create(user);

        User expectedUser = new User(1, "login", "login", "mymail@mail.ru",
                LocalDate.of(2000, 5, 20));
        Assertions.assertEquals(expectedUser, userController.getUsers().get(1));
    }

    @Test
    public void createUserWithBadEmail() {
        User user = new User(0, "login", "name", "",
                LocalDate.of(2000, 5, 20));

        final InvalidUserException exception = Assertions.assertThrows(
                InvalidUserException.class,
                () -> userController.create(user)
        );

        Assertions.assertEquals("User email must not be empty!", exception.getMessage());
    }

    @Test
    public void createUserWithoutAt() {
        User user = new User(0, "login", "name", "myemail",
                LocalDate.of(2000, 5, 20));

        final InvalidUserException exception = Assertions.assertThrows(
                InvalidUserException.class,
                () -> userController.create(user)
        );

        Assertions.assertEquals("User email must contain @!", exception.getMessage());
    }

    @Test
    public void createUserWithNullLogin() {
        User user = new User(0, null, "name", "myemail@mail.ru",
                LocalDate.of(2000, 5, 20));

        final InvalidUserException exception = Assertions.assertThrows(
                InvalidUserException.class,
                () -> userController.create(user)
        );

        Assertions.assertEquals("User login must not be empty!", exception.getMessage());
    }

    @Test
    public void createUserWithEmptyLogin() {
        User user = new User(0, "", "name", "myemail@mail.ru",
                LocalDate.of(2000, 5, 20));

        final InvalidUserException exception = Assertions.assertThrows(
                InvalidUserException.class,
                () -> userController.create(user)
        );

        Assertions.assertEquals("User login must not be empty!", exception.getMessage());
    }

    @Test
    public void createUserWithWhitespacesInLogin() {
        User user = new User(0, " lo g in ", "name", "myemail@mail.ru",
                LocalDate.of(2000, 5, 20));

        final InvalidUserException exception = Assertions.assertThrows(
                InvalidUserException.class,
                () -> userController.create(user)
        );

        Assertions.assertEquals("User login must not contain whitespaces!", exception.getMessage());
    }

    @Test
    public void createUserWithNullBirthday() {
        User user = new User(0, "login", "name", "myemail@mail.ru",
                null);

        final InvalidUserException exception = Assertions.assertThrows(
                InvalidUserException.class,
                () -> userController.create(user)
        );

        Assertions.assertEquals("User birthday must be not null!", exception.getMessage());
    }

    @Test
    public void createUserWithBirthdayInTheFuture() {
        User user = new User(0, "login", "name", "myemail@mail.ru",
                LocalDate.now().plusDays(1));

        final InvalidUserException exception = Assertions.assertThrows(
                InvalidUserException.class,
                () -> userController.create(user)
        );

        Assertions.assertEquals("User birthday must be not in the future!", exception.getMessage());
    }
}
