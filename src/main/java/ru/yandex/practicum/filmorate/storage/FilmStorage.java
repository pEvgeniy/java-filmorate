package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film delete(Film film);

    Film update(Film film);

    List<Film> findAll();

    Film findFilmById(int filmId);
}
