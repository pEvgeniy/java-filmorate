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
    private Film film;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        film = new Film();
        film.setId(0);
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(date);
        film.setDuration(100L);
    }

    @Test
    public void create() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void createFilmWhenNameIsNull() {
        film.setName(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createFilmWhenNameIsEmpty() {
        film.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createWhenFilmDescriptionIsNull() {
        film.setDescription(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createFilmWhenDescriptionIsEmpty() {
        film.setDescription("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createWhenFilmDurationIsNull() {
        film.setDuration(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createWhenFilmDurationIsZero() {
        film.setDuration(0L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createWhenFilmDurationIsNegative() {
        film.setDuration(-100L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createWhenFilmReleaseDateIsBefore19852() {
        film.setReleaseDate(LocalDate.of(1895, 11, 27));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}
