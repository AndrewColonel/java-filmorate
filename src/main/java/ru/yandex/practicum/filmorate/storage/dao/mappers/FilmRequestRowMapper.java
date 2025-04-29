package ru.yandex.practicum.filmorate.storage.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRequestRowMapper implements RowMapper<FilmRequest> {
    @Override
    public FilmRequest mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return FilmRequest.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .duration(resultSet.getInt("duration"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .ratingId(resultSet.getInt("rating_id"))
                .build();
    }
}
