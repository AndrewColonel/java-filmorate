package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FriendsDbStorage extends BaseDbStorage<Friends> {

    private static final String FIND_ALL_FRIENDS_QUERY = "SELECT * FROM friends WHERE user1_id = ?";
    private static final String CREATE_FRIENDS_ID_QUERY = "INSERT INTO friends (user1_id, user2_id) VALUES (?, ?)";
    private static final String DELETE_FRIENDS_ID_QUERY = "DELETE FROM friends WHERE user1_id = ? AND user2_id = ?";

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

    public void addFriend(long userId, long friendId) {
        log.debug("добавляем друга {} для пользователя {} ",friendId,userId);
        insert(CREATE_FRIENDS_ID_QUERY, userId, friendId);
    }

    public void delFriend(long userId, long friendId) {
        log.debug("elfkztv друга {} для пользователя {} ",friendId,userId);
        delete(DELETE_FRIENDS_ID_QUERY,userId,friendId);
    }


    // обновляем список друзей пользователя - необходимо определить
    // как изменился список друзей- друзья могли удалиться и добавиться
    public void updateFriends(User user) {
        log.trace("обновление друзей для пользователя {}", user.getId());
        // при отказе пользователя от друга, друг также теряет из друзей этого пользователя
        // собираем список друзей от которых "отказался" пользователь, т.е дружеские связи которые надо будет удалить
        // это "старый" список друзей, до обновления
        List<Friends> oldFriends = findMany(FIND_ALL_FRIENDS_QUERY, user.getId());

        // старый список id  друзей
        Set<Long> oldFriendsId = oldFriends.stream()
                .map(Friends::getFriendId)
                .collect(Collectors.toSet());

        // собираем списко id друзей которых надо будет убрать
        // собираем ассиметричную разницу между старым и новым списками id друзей
        Set<Long> lostFriendsId = oldFriendsId.stream()
                .filter(e -> !user.getFriends().contains(e))
                .collect(Collectors.toSet());

        // удаляем друзей из списка пользователя
        lostFriendsId.forEach(id -> delete(DELETE_FRIENDS_ID_QUERY, user.getId(),id));
        // удаляю из талицы все расторгнутье связи
        lostFriendsId.forEach(id -> delete(DELETE_FRIENDS_ID_QUERY, id,user.getId()));

        // собираем списко id друзей которых надо будет добавить
        // собираем ассиметричную разницу между новым и старым списками id друзей
        Set<Long> newFriendsId = user.getFriends().stream()
                .filter(e -> !oldFriendsId.contains(e))
                .collect(Collectors.toSet());

        // добавляем новых друзей пользователя в таблицу
        newFriendsId.forEach(id -> insert(CREATE_FRIENDS_ID_QUERY, user.getId(), id));
        // добавляем симметричные записи для друзей пользователя - автопринятие дружбы
        newFriendsId.forEach(id -> insert(CREATE_FRIENDS_ID_QUERY, id, user.getId()));

    }

}
