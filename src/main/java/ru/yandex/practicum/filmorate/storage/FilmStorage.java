package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    // получение всех фильмов
    Collection<Film> findAll();

    // получение списка из первых `count` фильмов по количеству лайков из БД
    Collection<Film> findFilmTopChart(long count);

    // поиск фильмов по ID
    public Film findFilmDtoById(long id);

    public Film findFilmById(long id);

    // добавление фильма
    Film create(Film film);

    // обновление фильма
    Film update(Film newFilm);

    void addLikes(long filmId, long userId);

    void delLikes(long filmId, long userId);

}
