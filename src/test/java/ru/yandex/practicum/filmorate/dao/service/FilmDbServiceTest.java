package ru.yandex.practicum.filmorate.dao.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.dao.FilmDbService;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbServiceTest {

    private final FilmDbStorage filmDbStorage;

    private final UserDbStorage userDbStorage;

    private final FilmDbService filmDbService;
    private Film film;
    private User user;

    @BeforeEach
    public void startUp() {
        film = Film.builder()
                .name("Dune")
                .description("Some film description")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(180L)
                .mpa(Mpa.builder()
                        .id(1)
                        .build())
                .build();
        filmDbStorage.create(film);
        user = User.builder()
                .login("login")
                .name("name")
                .email("mymail@mail.com")
                .birthday(LocalDate.of(2000, 5, 20))
                .build();
        userDbStorage.create(user);
    }

    @Test
    public void addLike() {
        Optional<Film> filmWithLike = Optional.of(filmDbService.addLike(film.getId(), user.getId()));
        assertThat(filmWithLike)
                .isPresent()
                .hasValueSatisfying(f ->
                    assertThat(f).hasFieldOrPropertyWithValue("likes", Set.of(1))
                );
    }

    @Test
    public void deleteLike() {
        filmDbService.addLike(film.getId(), user.getId());

        Optional<Film> deletedFilm = Optional.of(filmDbService.deleteLike(film.getId(), user.getId()));
        assertThat(deletedFilm)
                .isPresent()
                .hasValueSatisfying(f ->
                    assertThat(f).hasFieldOrPropertyWithValue("likes", Set.of())
                );
    }

    @Test
    public void findPopularFilms() {
        Film film2 = Film.builder()
                .name("Dune 2")
                .description("Some film description")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(180L)
                .mpa(Mpa.builder()
                        .id(1)
                        .build())
                .build();
        filmDbStorage.create(film2);
        User user2 = User.builder()
                .login("login2")
                .name("name2")
                .email("mymail@mail.com")
                .birthday(LocalDate.of(2000, 5, 20))
                .build();
        userDbStorage.create(user2);

        filmDbService.addLike(film.getId(), user.getId());
        filmDbService.addLike(film2.getId(), user.getId());
        filmDbService.addLike(film2.getId(), user2.getId());

        List<Film> popularFilms = filmDbService.findPopularFilms(10);
        assertThat(popularFilms)
                .isNotEmpty()
                .hasSize(2);
        assertThat(popularFilms.get(0))
                .hasFieldOrPropertyWithValue("name", "Dune 2");
        assertThat(popularFilms.get(1))
                .hasFieldOrPropertyWithValue("name", "Dune");
    }
}
