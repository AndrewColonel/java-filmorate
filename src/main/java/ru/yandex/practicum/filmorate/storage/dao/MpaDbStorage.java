package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {

    private static final String FIND_ALL_MPA_QUERY = "SELECT * FROM rating";
    private static final String FIND_BY_ID_MPA_QUERY = "SELECT * FROM rating WHERE rating_id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Mpa> findAll() {
        log.trace("Обработка запроса на получение списка всех рейтингов MPA");
        log.debug("выполнен запрос на получение списка рейтинга MPA {}",FIND_ALL_MPA_QUERY);
        return findMany(FIND_ALL_MPA_QUERY);
    }

    @Override
    public Mpa findMpaById(int id) {
        log.trace("Обработка запроса на получение MPA {}", id);
        log.debug("выполнен запрос нп получение рейтинга MPA {} с id {}",FIND_BY_ID_MPA_QUERY, id);
        return findOne(FIND_BY_ID_MPA_QUERY,id).orElseThrow(
                () -> new NotFoundException(String.format("MPA с ID %d не найдено", id))
        );
    }

}
