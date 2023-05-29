package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    @Getter
    protected final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(filmId++);
        films.put(film.getId(), film);
        log.info("Film {} added", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return Optional.ofNullable(films.get(film.getId()))
                .map(existingFilm -> {
                    films.put(film.getId(), film);
                    log.info("Updated film {}", film.getName());
                    return film;
                })
                .orElseThrow(() -> {
                    log.warn("Film {} to update not found!", film.getName());
                    return new NoSuchElementException("Film to be updated not found!");
                });
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Total films: {}", films.size());
        return new ArrayList<>(films.values());
    }
}
