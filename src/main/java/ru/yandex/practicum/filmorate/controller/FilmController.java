package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    // получение всех фильмов
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping
    // добавление фильма
    public Film create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping
    // обновление фильма
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmStorage.update(newFilm);
    }

    // пользователь ставит лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public Film addLikes(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        return filmService.addLikes(filmId, userId);
    }

    // пользователь удаляет лайк
    @DeleteMapping("/{id}/like/{userId}")
    public Film delLikes(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        return filmService.delLikes(filmId, userId);
    }

    // возвращает список из первых `count` фильмов по количеству лайков.
    // Если значение параметра `count` не задано, верните первые 10.
    @GetMapping("/popular")
    public Collection<Film> topChart(@RequestParam(value = "count", defaultValue = "10", required = false) Long count) {
        return filmService.topChart(count);
    }
}
