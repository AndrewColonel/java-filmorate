package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.Friends;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FriendsDbStorage extends BaseDbStorage<Friends> {

    private static final String FIND_ALL_FRIENDS_QUERY = "SELECT * FROM friends WHERE user1_id = ?";
    private static final String CREATE_FRIENDS_ID_QUERY = "INSERT INTO friends (user1_id, user2_id) VALUES (?, ?)";
    private static final String DELETE_FRIENDS_ID_QUERY = "DELETE FROM friends WHERE user1_id = ?";

    public FriendsDbStorage(JdbcTemplate jdbc, RowMapper<Friends> mapper) {
        super(jdbc, mapper);
    }

    // получаем множество уникальных id друзей для избранного пользователя
    public Set<Long> findAlLfriends(long id) {
        log.debug("Получение списка всех друзей пользователей {}", id);
        return findMany(FIND_ALL_FRIENDS_QUERY, id).stream()
                .map(Friends::getFriendId)
                .collect(Collectors.toSet());
    }

    // обновляем списко друзей пользователя - сначала зачищаем весь список друзей данного пользователя
    // и вне зависимости от рзультата записываем id друзей
    public void updateFriends(long userId, Set<Long> friends) {
        delete(DELETE_FRIENDS_ID_QUERY, userId);
        friends.forEach(id -> insert(CREATE_FRIENDS_ID_QUERY, userId, id));
    }
}
