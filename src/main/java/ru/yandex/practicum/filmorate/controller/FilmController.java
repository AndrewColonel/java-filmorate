package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    // получение всех фильмов
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    // получение всех фильмов
    public Film findFilmById(@PathVariable("id") long id) {
        return filmService.findFilmById(id);
    }

    @PostMapping
    // добавление фильма
    public FilmDto create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    // обновление фильма
    public FilmDto update(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    // пользователь ставит лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public FilmDto addLikes(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        return filmService.addLikes(filmId, userId);
    }

    // пользователь удаляет лайк
    @DeleteMapping("/{id}/like/{userId}")
    public FilmDto delLikes(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        return filmService.delLikes(filmId, userId);
    }

    // возвращает список из первых `count` фильмов по количеству лайков.
    // Если значение параметра `count` не задано, верните первые 10.
    @GetMapping("/popular")
    public Collection<FilmDto> topChart(@RequestParam(value = "count", defaultValue = "10", required = false) Long count) {
        return filmService.topChart(count);
    }
}
