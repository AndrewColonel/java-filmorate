package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.GenreList;

@Slf4j
@Repository
public class GenreListDbStorage extends BaseDbStorage<GenreList>{

    public GenreListDbStorage(JdbcTemplate jdbc, RowMapper<GenreList> mapper) {
        super(jdbc, mapper);
    }


}
