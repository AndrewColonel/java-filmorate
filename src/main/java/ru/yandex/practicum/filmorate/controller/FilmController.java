package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    // получение всех фильмов
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    // добавление фильма
    public Film create(@RequestBody Film film) {
        if (isNotValid(film)) {
            throw new ValidationException("Неверные данные о фильме");
        }
        film.setId(getNextId());

        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    // обновление фильма
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            if (isNotValid(newFilm)) {
                throw new ValidationException("Неверные данные о фильме");
            }
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    // вспомогательный метод валидации экземпляра фильма
    private boolean isNotValid(Film film) {
        return film.getName().isBlank()
                || film.getDescription().length() > 200
                || film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))
                || film.getDuration() <= 0;
    }

    // вспомогательный метод получения следующего значения id
    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
