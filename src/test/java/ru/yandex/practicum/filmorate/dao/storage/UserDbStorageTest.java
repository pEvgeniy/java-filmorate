package ru.yandex.practicum.filmorate.dao.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .login("login")
                .name("name")
                .email("mymail@mail.com")
                .birthday(LocalDate.of(2000, 5, 20))
                .build();
    }

    @Test
    public void createUser() {
        Optional<User> createdUser = Optional.of(userDbStorage.create(user));
        assertThat(createdUser)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(u).hasFieldOrPropertyWithValue("name", "name");
                });
    }

    @Test
    public void deleteUser() {
        User createdUser = userDbStorage.create(user);

        Optional<User> deletedUser = Optional.of(userDbStorage.delete(createdUser));
        assertThat(deletedUser)
                .isPresent()
                .hasValueSatisfying(u ->
                    assertThat(u).hasFieldOrPropertyWithValue("id", createdUser.getId())
                );

        List<User> users = userDbStorage.findAll();
        assertThat(users)
                .isEmpty();
    }

    @Test
    public void updateUser() {
        User createdUser = userDbStorage.create(user);
        createdUser.setName("who am i");

        Optional<User> updatedUser = Optional.of(userDbStorage.update(createdUser));
        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(u).hasFieldOrPropertyWithValue("name", "who am i");
                });
    }

    @Test
    public void findAllUsers() {
        User createdUser = userDbStorage.create(user);

        List<User> foundUsers = userDbStorage.findAll();
        assertThat(foundUsers)
                .isNotEmpty()
                .hasSize(1);
        assertThat(foundUsers.get(0))
                .hasFieldOrPropertyWithValue("id", createdUser.getId());
    }

    @Test
    public void findUserById() {
        User createdUser = userDbStorage.create(user);

        Optional<User> foundUser = Optional.of(userDbStorage.findUserById(createdUser.getId()));
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("id", createdUser.getId());
                    assertThat(u).hasFieldOrPropertyWithValue("name", createdUser.getName());
                });
    }
}
