package ru.yandex.practicum.filmorate.storage.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friends;


import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendsRowMapper implements RowMapper<Friends> {

    @Override
    public Friends mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Friends.builder()
                .id(resultSet.getLong("friend_id"))
                .userId(resultSet.getLong("user1_id"))
                .friendId(resultSet.getLong("user2_id"))
                .build();
    }
}
