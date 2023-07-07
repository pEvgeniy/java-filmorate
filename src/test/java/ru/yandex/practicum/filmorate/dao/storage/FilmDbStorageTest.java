package ru.yandex.practicum.filmorate.dao.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private Film film;

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
    }

    @Test
    public void createFilm() {
        Optional<Film> createdFilm = Optional.of(filmDbStorage.create(film));

        assertThat(createdFilm)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(f).hasFieldOrPropertyWithValue("name", "Dune");
                });
    }

    @Test
    public void deleteFilm() {
        Film createdFilm = filmDbStorage.create(film);

        Optional<Film> deletedFilm = Optional.of(filmDbStorage.delete(createdFilm));
        assertThat(deletedFilm)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("id", 1)
                );

        List<Film> films = filmDbStorage.findAll();
        assertThat(films)
                .isEmpty();
    }

    @Test
    public void updateFilm() {
        filmDbStorage.create(film);
        film.setName("Dune 2");
        LocalDate newDate = LocalDate.of(2023, 9, 20);
        film.setReleaseDate(newDate);

        Optional<Film> updatedFilm = Optional.of(filmDbStorage.update(film));
        assertThat(updatedFilm)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("name", "Dune 2");
                    assertThat(f).hasFieldOrPropertyWithValue("releaseDate", newDate);
                });
    }

    @Test
    public void findAll() {
        Film createdFilm = filmDbStorage.create(film);

        List<Film> films = filmDbStorage.findAll();
        assertThat(films)
                .isNotEmpty()
                .hasSize(1);
        assertThat(films.get(0))
                .hasFieldOrPropertyWithValue("name", createdFilm.getName());
    }

    @Test
    public void findFilmById() {
        Film createdFilm = filmDbStorage.create(film);
        int id = createdFilm.getId();

        Optional<Film> foundFilm = Optional.of(filmDbStorage.findFilmById(id));
        assertThat(foundFilm)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("id", id);
                    assertThat(f).hasFieldOrPropertyWithValue("name", createdFilm.getName());
                });
    }
}
