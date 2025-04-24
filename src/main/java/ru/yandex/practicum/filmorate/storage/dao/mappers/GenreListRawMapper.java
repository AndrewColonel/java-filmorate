package ru.yandex.practicum.filmorate.storage.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.GenreList;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreListRawMapper implements RowMapper<GenreList> {
    @Override
    public GenreList mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return GenreList.builder()
                .id(resultSet.getLong("genre_list_id"))
                .filmId(resultSet.getLong("film_id"))
                .genreId(resultSet.getLong("genre_id"))
                .build();
    }
}
