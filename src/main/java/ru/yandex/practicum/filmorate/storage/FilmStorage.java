package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    // получение всех фильмов
    Collection<Film> findAll();

    // добавление фильма
    Film create(Film film);

    // обновление фильма
    Film update(Film newFilm);
}
