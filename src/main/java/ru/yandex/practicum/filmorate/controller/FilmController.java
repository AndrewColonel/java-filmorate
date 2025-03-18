package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    // получение всех фильмов
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    // добавление фильма
    public Film create(@RequestBody Film film) {
        return film;
    }

    @PutMapping
    // обновление фильма
    public Film update(@RequestBody Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        return oldFilm;
    }

    // вспомогательный метод получения следующего значения id
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
