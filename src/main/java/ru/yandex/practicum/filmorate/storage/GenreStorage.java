package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;

public interface GenreStorage {
    List<Genres> findAll();

    Genres findGenresById(int id);
}
