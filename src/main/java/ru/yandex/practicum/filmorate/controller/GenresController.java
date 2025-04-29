package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genres;

import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
public class GenresController {

    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenresController(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    @GetMapping
    public Collection<Genres> findAll() {
        return  genreDbStorage.findAll();
    }

    @GetMapping("/{id}")
    public Genres findMpaById(@PathVariable("id") int id) {
        return  genreDbStorage.findGenresById(id);
    }

}
