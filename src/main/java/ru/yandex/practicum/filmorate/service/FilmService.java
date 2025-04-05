package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    // пользователь ставит лайк фильму
    public Film addLikes(long filmId, long userId) {
        log.trace("Вызван метод добавления Лайка для фильма с ID {} от пользователя с ID {}", filmId, userId);
        // успешный вызов метода поиска фильма и пользователя по ID гарантирует их существование
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.findUserById(userId);
        if (film.getLikes().add(userId)) {
            log.debug("Для фильма {} добавлен лайк от пользователя {}", filmId, userId);
        } else {
            log.debug("Поставить лайк для фильма {} от пользователя {} не удалось}", filmId, userId);
        }
        return film;
    }

    // пользователь удаляет лайк.
    public Film delLikes(long filmId, long userId) {
        log.trace("Вызван метод удаления  Лайка для фильма с ID {} от пользователя с ID {}", filmId, userId);
        // успешный вызов метода поиска фильма и пользователя по ID гарантирует их существование
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.findUserById(userId);
        if (film.getLikes().remove(userId)) {
            log.debug("Удален лайк для фильма {} от пользователя {}", filmId, userId);
        } else {
            log.debug("Для фильма {} не удален лайк пользователя {}", filmId, userId);
        }
        return film;
    }

    // возвращает список из первых `count` фильмов по количеству лайков.
    // предварительно весь срисок фильмов был помещен в TreeSet с компоратором для получения чарта
    public Collection<Film> topChart(long count) {
        log.trace("Вызван метод вывод чарт списка для {} фильмов", count);
        Set<Film> chartSet =
                new TreeSet<>(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed());
        chartSet.addAll(filmStorage.findAll());
        return chartSet.stream().limit(count).toList();
    }
}
