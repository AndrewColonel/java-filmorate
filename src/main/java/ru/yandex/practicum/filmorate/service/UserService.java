package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // добавляем друзей
    public User addFriends(long userId, long frientId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", userId)));
        User friend = userStorage.findUserById(frientId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", frientId)));
        user.setFriend(frientId);
        friend.setFriend(userId);
        return user;
    }

    // удаляем друзей
    public User delFriends(long userId, long frientId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", userId)));
        User friend = userStorage.findUserById(frientId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", frientId)));
        user.delFriend(frientId);
        friend.delFriend(userId);
        return user;
    }

    // возвращает список друзей
    public Set<Long> getFriendsList(long id) {
        return userStorage.findUserById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", id)))
                .getFriends();

    }

    // возвращаем список друзей, общих с другим пользователем
    public Set<Long> getCommonFriendsList(long userId, long otherId) {
        return getFriendsList(userId).stream()
                .filter(id -> getFriendsList(otherId).contains(id))
                .collect(Collectors.toSet());
    }

}
