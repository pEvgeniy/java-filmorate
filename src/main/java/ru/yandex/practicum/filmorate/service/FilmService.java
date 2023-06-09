package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.LikeNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.findFilmById(filmId);
        film.getLikes().add(userId);
        log.info("Added like from user with id={} to film {}", userId, film.getName());
        return film;
    }

    public Film deleteLike(int filmId, int userId) {
        Film film = filmStorage.findFilmById(filmId);
        Set<Integer> likes = film.getLikes();
        if (likes.remove(userId)) {
            log.info("Deleted user's with id={} like from film {}", userId, film.getName());
            return film;
        }
        log.warn("Like from user with id={} to film {} not found", userId, film.getName());
        throw new LikeNotFoundException("Like not found");
    }

    public List<Film> findPopularFilms(int count) {
        List<Film> popularFilms = filmStorage.findAll().stream()
                .sorted((f1, f2) -> compare(f1, f2))
                .limit(count)
                .collect(Collectors.toList());
        log.info("found popular films {}", popularFilms);
        return popularFilms;
    }

    private int compare(Film film1, Film film2) {
        return film2.getLikes().size() - film1.getLikes().size();
    }
}
