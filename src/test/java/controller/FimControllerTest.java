package controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FimControllerTest {

    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    public void create() {
        Film film = new Film(0, "name", "description",
                LocalDate.of(1967, 3, 25), 100L);
        filmController.create(film);

        Film expectedFilm = new Film(1, "name", "description",
                LocalDate.of(1967, 3, 25), 100L);
        Assertions.assertEquals(expectedFilm, filmController.getFilms().get(1));
    }

    @Test
    public void createWhenFilmNameIsNull() {
        Film film = new Film(0, null, "description",
                LocalDate.of(1967, 3, 25), 100L);

        final InvalidFilmException exception = Assertions.assertThrows(
                InvalidFilmException.class,
                () -> filmController.create(film)
        );
        Assertions.assertEquals("Film name must not be empty!", exception.getMessage());
    }

    @Test
    public void createWhenFilmNameIsEmpty() {
        Film film = new Film(0, "", "description",
                LocalDate.of(1967, 3, 25), 100L);

        final InvalidFilmException exception = Assertions.assertThrows(
                InvalidFilmException.class,
                () -> filmController.create(film)
        );
        Assertions.assertEquals("Film name must not be empty!", exception.getMessage());
    }

    @Test
    public void createWhenFilmDescriptionIsNull() {
        Film film = new Film(0, "name", null,
                LocalDate.of(1967, 3, 25), 100L);

        final InvalidFilmException exception = Assertions.assertThrows(
                InvalidFilmException.class,
                () -> filmController.create(film)
        );
        Assertions.assertEquals("Film description must not be empty!", exception.getMessage());
    }

    @Test
    public void createWhenFilmDescriptionIsEmpty() {
        Film film = new Film(0, "name", "",
                LocalDate.of(1967, 3, 25), 100L);

        final InvalidFilmException exception = Assertions.assertThrows(
                InvalidFilmException.class,
                () -> filmController.create(film)
        );
        Assertions.assertEquals("Film description must not be empty!", exception.getMessage());
    }

    @Test
    public void createWhenFilmDurationIsNull() {
        Film film = new Film(0, "name", "description",
                LocalDate.of(1967, 3, 25), null);

        final InvalidFilmException exception = Assertions.assertThrows(
                InvalidFilmException.class,
                () -> filmController.create(film)
        );
        Assertions.assertEquals("Film duration must be positive!", exception.getMessage());
    }

    @Test
    public void createWhenFilmDurationIsZero() {
        Film film = new Film(0, "name", "description",
                LocalDate.of(1967, 3, 25), 0L);

        final InvalidFilmException exception = Assertions.assertThrows(
                InvalidFilmException.class,
                () -> filmController.create(film)
        );
        Assertions.assertEquals("Film duration must be positive!", exception.getMessage());
    }

    @Test
    public void createWhenFilmDurationIsNegative() {
        Film film = new Film(0, "name", "description",
                LocalDate.of(1967, 3, 25), -100L);

        final InvalidFilmException exception = Assertions.assertThrows(
                InvalidFilmException.class,
                () -> filmController.create(film)
        );
        Assertions.assertEquals("Film duration must be positive!", exception.getMessage());
    }

    @Test
    public void createWhenFilmReleaseDateIsBefore1985() {
        Film film = new Film(0, "name", "description",
                LocalDate.of(1895, 11, 27), 100L);

        final InvalidFilmException exception = Assertions.assertThrows(
                InvalidFilmException.class,
                () -> filmController.create(film)
        );
        Assertions.assertEquals("Film release date must be not older than 28.11.1895!", exception.getMessage());
    }
}
