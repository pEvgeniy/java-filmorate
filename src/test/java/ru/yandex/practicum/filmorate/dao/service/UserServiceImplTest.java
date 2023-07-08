package ru.yandex.practicum.filmorate.dao.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.dao.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.dao.UserStorageImpl;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceImplTest {

    private final UserStorageImpl userDbStorage;

    private final UserServiceImpl userDbService;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp() {
        user1 = User.builder()
                .login("login")
                .name("name")
                .email("mymail@mail.com")
                .birthday(LocalDate.of(2000, 5, 20))
                .build();
        user1.setId(userDbStorage.create(user1).getId());
        user2 = User.builder()
                .login("login2")
                .name("name2")
                .email("mymail2@mail.com")
                .birthday(LocalDate.of(2000, 5, 20))
                .build();
        user2.setId(userDbStorage.create(user2).getId());
        user3 = User.builder()
                .login("login3")
                .name("name3")
                .email("mymail3@mail.com")
                .birthday(LocalDate.of(2000, 5, 20))
                .build();
        user3.setId(userDbStorage.create(user3).getId());
    }

    @Test
    public void addFriend() {
        userDbService.addFriend(user1.getId(), user2.getId());

        List<User> friends = userDbService.findFriends(user1.getId());
        assertThat(friends)
                .isNotEmpty()
                .hasSize(1);
        assertThat(friends.get(0))
                .hasFieldOrPropertyWithValue("name", "name2");
    }

    @Test
    public void deleteFriend() {
        userDbService.addFriend(user1.getId(), user2.getId());

        userDbService.deleteFriend(user1.getId(), user2.getId());
        List<User> friends = userDbService.findFriends(user1.getId());
        assertThat(friends)
                .isEmpty();
    }

    @Test
    public void findFriends() {
        userDbService.addFriend(user1.getId(), user2.getId());
        userDbService.addFriend(user1.getId(), user3.getId());

        List<User> friends = userDbService.findFriends(user1.getId());
        assertThat(friends)
                .isNotEmpty()
                .hasSize(2);
    }

    @Test
    public void findCommonFriends() {
        userDbService.addFriend(user1.getId(), user3.getId());
        userDbService.addFriend(user2.getId(), user3.getId());

        List<User> commonFriends = userDbService.findCommonFriends(user1.getId(), user2.getId());
        assertThat(commonFriends)
                .isNotEmpty()
                .hasSize(1);
        assertThat(commonFriends.get(0))
                .hasFieldOrPropertyWithValue("name", "name3");
    }
}