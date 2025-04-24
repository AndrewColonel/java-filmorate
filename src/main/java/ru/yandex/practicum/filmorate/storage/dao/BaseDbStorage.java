package ru.yandex.practicum.filmorate.storage.dao;


import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class BaseDbStorage<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    // получаем список объектов из БД, используем мотод query
    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    // получаем объект из БД, используем мотод queryForObject
    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
//        log.error("SQL запрос {}", query);
        Long id = keyHolder.getKeyAs(Long.class);
        // Возвращаем id нового пользователя
        if (id != null) {
            return id;
        } else {
            throw new RuntimeException("Не удалось сохранить данные");
        }
    }

    public void delete(String query, long id) {
//        int rowsDeleted =
                jdbc.update(query, id);
//        return rowsDeleted > 0;
    }

    protected void update(String query, Object... params) {
//        return jdbc.update(query, params);
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
//            throw new RuntimeException("Не далось обновить данные");
            throw new NotFoundException("Запись с id " + Arrays.toString(params) + " не найдена");
        }
    }
}
