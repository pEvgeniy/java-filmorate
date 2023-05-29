package controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class FimControllerTest {

    private Validator validator;
    private final LocalDate date = LocalDate.of(1967, 3, 25);

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void create() {
        Film film = new Film(0, "name", "description", date, 100L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void createFilmWhenNameIsNull() {
        Film film = new Film(0, null, "description", date, 100L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createFilmWhenNameIsEmpty() {
        Film film = new Film(0, "", "description", date, 100L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createWhenFilmDescriptionIsNull() {
        Film film = new Film(0, "name", null, date, 100L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createFilmWhenDescriptionIsEmpty() {
        Film film = new Film(0, "name", "", date, 100L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createWhenFilmDurationIsNull() {
        Film film = new Film(0, "name", "description", date, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createWhenFilmDurationIsZero() {
        Film film = new Film(0, "name", "description", date, 0L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createWhenFilmDurationIsNegative() {
        Film film = new Film(0, "name", "description", date, -100L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createWhenFilmReleaseDateIsBefore19852() {
        Film film = new Film(0, "name", "description",
                LocalDate.of(1895, 11, 27), 100L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}
