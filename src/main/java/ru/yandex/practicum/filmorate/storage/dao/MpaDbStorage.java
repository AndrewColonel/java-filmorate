package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> {

    private static final String FIND_ALL_MPA_QUERY = "SELECT * FROM rating";
    private static final String FIND_BY_ID_MPA_QUERY = "SELECT * FROM rating WHERE rating_id = ?";
    private static final String CREATE_MPA_QUERY = "INSERT INTO (rating_id, rating_name)" +
            "VALUES (?, ?)";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public List<Mpa> findAll() {
        return findMany(FIND_ALL_MPA_QUERY);
    }

    public Mpa findMpaById(int id) {
        return findOne(FIND_BY_ID_MPA_QUERY,id).orElseThrow(
                () -> new NotFoundException(String.format("MPA с ID %d не найдено", id))
        );
    }

//    public void create(int ratingId, String ratingName) {
//        insert(CREATE_MPA_QUERY,ratingId,ratingName);
//    }
//
//    public void update(int ratingId, String ratingName) {
//        update();
//    }
}
