package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    // получение всех фильмов
    Collection<FilmDto> findAll();

    // поиск фильмов по ID
    public FilmDto findFilmById(long id);


    // добавление фильма
    FilmDto create(Film film);

    // обновление фильма
    FilmDto update(Film newFilm);

    void addLikes(long filmId, long userId);

    void delLikes(long filmId, long userId);

}
