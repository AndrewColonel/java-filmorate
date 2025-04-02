package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    // получение всех фильмов
    Collection<Film> findAll();

    // поиск фильмов по ID
    public Optional<Film> findFilmById(long id);

    // добавление фильма
    Film create(Film film);

    // обновление фильма
    Film update(Film newFilm);
}
