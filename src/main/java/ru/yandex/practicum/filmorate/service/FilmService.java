package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // пользователь ставит лайк фильму
    public Film addLikes(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Фильма с ID %d не найдено", filmId)));
        User user = userStorage.findUserById(userId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", userId)));
        film.getLikes().add(userId);
        return film;
    }

    // пользователь удаляет лайк.
    public Film delLikes(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Фильма с ID %d не найдено", filmId)));
        User user = userStorage.findUserById(userId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", userId)));
        film.getLikes().remove(userId);
        return film;
    }

    // возвращает список из первых `count` фильмов по количеству лайков.
    // Если значение параметра `count` не задано, верните первые 10
    public Set<Film> topChart(int count) {
        return filmStorage.findAll().stream()
                .limit(count)
                .collect(Collectors.toSet());
    }
}
