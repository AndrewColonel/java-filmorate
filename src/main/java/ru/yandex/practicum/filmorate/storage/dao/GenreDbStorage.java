package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Repository
public class GenreDbStorage extends BaseDbStorage<Genres> implements GenreStorage {

    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_ID_GENRES_QUERY = "SELECT * FROM genre WHERE genre_id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genres> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genres> findAll() {
        log.trace("Обработка запроса на получение списка всех жанров");
        log.debug("выполнен запрос на получение списка жанров{}",FIND_ALL_GENRES_QUERY);
        return findMany(FIND_ALL_GENRES_QUERY);
    }

    @Override
    public Genres findGenresById(int id) {
        log.trace("Обработка запроса на получение жанра {}", id);
        log.debug("выполнен запрос нп получение жанра {} с id {}",FIND_BY_ID_GENRES_QUERY, id);
        return findOne(FIND_BY_ID_GENRES_QUERY,id).orElseThrow(
                () -> new NotFoundException(String.format("Жанр с ID %d не найдено", id))
        );
    }


}
