package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // добавляем друзей
    public User addFriends(long userId, long friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", userId)));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", friendId)));
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
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
        user.getFriends().remove(frientId);
        friend.getFriends().remove(userId);
        return user;
    }

    // возвращает список друзей
    public Collection<Long> getFriendsList(long id) {
        return userStorage.findUserById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", id)))
                .getFriends().stream().toList();
    }

    // возвращаем список друзей, общих с другим пользователем
    public Collection<Long> getCommonFriendsList(long userId, long otherId) {
        return getFriendsList(userId).stream()
                .filter(id -> getFriendsList(otherId).contains(id))
                .toList();
    }
}
