package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // добавляем друзей
    public User addFriends(long userId, long friendId) {
        log.trace("Для пользователя с ID {} вызван метод по добавлению друга ID {}", userId, friendId);
        User user = userStorage.findUserById(userId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", userId)));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", friendId)));
        if ((user.getFriends().add(friendId)) && (friend.getFriends().add(userId))) {
            log.debug("Для пользователя с ID {} добавлен друг с ID {}", userId, friendId);
        } else {
            log.debug("Для пользователя с ID {} не удалось добавить в друзья ID {}", userId, friendId);
        }
        return user;
    }

    // удаляем друзей
    public User delFriends(long userId, long friendId) {
        log.trace("Для пользователя с ID {} вызван метод по удалению друга ID {}", userId, friendId);
        User user = userStorage.findUserById(userId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", userId)));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", friendId)));
        if ((user.getFriends().remove(friendId)) && (friend.getFriends().remove(userId))) {
            log.debug("Для пользователя с ID {} удален друг с ID {}", userId, friendId);
        } else {
            log.debug("Для пользователя с ID {} не удалось удалить из друзей {}", userId, friendId);
        }
        return user;
    }

    // возвращает список друзей
    public Collection<Long> getFriendsList(long id) {
        log.trace("Вызван метод по вормированию списка друзей для пользователя с ID {}", id);
        return userStorage.findUserById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Пользователя с ID %d не существует.", id)))
                .getFriends().stream().toList();
    }

    // возвращаем список друзей, общих с другим пользователем
    public Collection<Long> getCommonFriendsList(long userId, long otherId) {
        log.trace("Вызван метод по вормированию списка общих друзей для пользователей с ID {} и {}", userId, otherId);
        return getFriendsList(userId).stream()
                .filter(id -> getFriendsList(otherId).contains(id))
                .toList();
    }
}
