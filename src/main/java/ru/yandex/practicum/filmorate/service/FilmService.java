package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

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

    public Collection<FilmDto> findAll() {
        return filmStorage.findAll();
    }

    public Film findFilmById(long id) {
        return filmStorage.findFilmById(id);
    }


    public FilmDto create(Film film) {
        return filmStorage.create(film);
    }

    public FilmDto update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    // пользователь ставит лайк фильму
    public FilmDto addLikes(long filmId, long userId) {
        log.trace("Вызван метод добавления Лайка для фильма с ID {} от пользователя с ID {}", filmId, userId);
        // успешный вызов метода поиска фильма и пользователя по ID гарантирует их существование
        FilmDto filmDto = filmStorage.findFilmDtoById(filmId);
        User user = userStorage.findUserById(userId);
        if (filmDto.getLikes().add(userId)) {
            log.debug("Для фильма {} добавлен лайк от пользователя {}", filmId, userId);
        } else {
            log.debug("Поставить лайк для фильма {} от пользователя {} не удалось}", filmId, userId);
        }
        filmStorage.addLikes(filmId, userId);
        return filmDto;
    }

    // пользователь удаляет лайк.
    public FilmDto delLikes(long filmId, long userId) {
        log.trace("Вызван метод удаления  Лайка для фильма с ID {} от пользователя с ID {}", filmId, userId);
        // успешный вызов метода поиска фильма и пользователя по ID гарантирует их существование
        FilmDto filmDto = filmStorage.findFilmDtoById(filmId);
        User user = userStorage.findUserById(userId);
        if (filmDto.getLikes().remove(userId)) {
            log.debug("Удален лайк для фильма {} от пользователя {}", filmId, userId);
        } else {
            log.debug("Для фильма {} не удален лайк пользователя {}", filmId, userId);
        }
        filmStorage.delLikes(filmId, userId);
        return filmDto;
    }

    // возвращает список из первых `count` фильмов по количеству лайков.
    // предварительно весь срисок фильмов был помещен в TreeSet с компоратором для получения чарта
    public Collection<FilmDto> topChart(long count) {
        log.trace("Вызван метод вывод чарт списка для {} фильмов", count);
       return filmStorage.findFilmTopChart(count).stream()
               .map(FilmMapper::mapToFilmDto)
               .toList();
    }

}
