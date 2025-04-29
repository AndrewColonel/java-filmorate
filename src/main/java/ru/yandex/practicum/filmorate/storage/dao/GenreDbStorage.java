package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;

@Slf4j
@Repository
public class GenreDbStorage extends BaseDbStorage<Genres> {

    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_ID_GENRES_QUERY = "SELECT * FROM genre WHERE genre_id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genres> mapper) {
        super(jdbc, mapper);
    }

    public List<Genres> findAll() {
        return findMany(FIND_ALL_GENRES_QUERY);
    }

    public Genres findGenresById(int id) {
        return findOne(FIND_BY_ID_GENRES_QUERY,id).orElseThrow(
                () -> new NotFoundException(String.format("Жанр с ID %d не найдено", id))
        );
    }


}
