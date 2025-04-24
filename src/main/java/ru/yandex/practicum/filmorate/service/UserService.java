package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
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

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    // добавляем друзей
    public User addFriends(long userId, long friendId) {
        log.trace("Для пользователя с ID {} вызван метод по добавлению друга ID {}", userId, friendId);
        if (userId == friendId) {
            throw new DuplicatedDataException(String.format("Пользователь %s добавляет сам себя в друзья", userId));
        }
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        if ((user.getFriends().add(friendId)) && (friend.getFriends().add(userId))) {
            log.debug("Для пользователя с ID {} добавлен друг с ID {}", userId, friendId);
        } else {
            log.debug("Для пользователя с ID {} не удалось добавить в друзья ID {}", userId, friendId);
        }
        // в другой реализации можно\нужно обновлять только таблицу друзей. а не всего пользователя
        return userStorage.update(user);
    }

    // удаляем друзей
    public User delFriends(long userId, long friendId) {
        log.trace("Для пользователя с ID {} вызван метод по удалению друга ID {}", userId, friendId);
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        if ((user.getFriends().remove(friendId)) && (friend.getFriends().remove(userId))) {
            log.debug("Для пользователя с ID {} удален друг с ID {}", userId, friendId);
        } else {
            log.debug("Для пользователя с ID {} не удалось удалить из друзей {}", userId, friendId);
        }
        // в другой реализации можно\нужно обновлять только таблицу друзей. а не всего пользователя
        return userStorage.update(user);
    }

    // возвращает список друзей
    public Collection<User> getFriends(long id) {
        log.trace("Вызван метод по вормированию списка друзей для пользователя с ID {}", id);
        return getFriendsList(id).stream()
                .map(userStorage::findUserById)
                .toList();
    }

    // возвращаем список друзей, общих с другим пользователем
    public Collection<User> getCommonFriends(long userId, long otherId) {
        log.trace("Вызван метод по вормированию списка общих друзей для пользователей с ID {} и {}", userId, otherId);
        Collection<Long> otherUserFriendList = getFriendsList(otherId);
        return getFriendsList(userId).stream()
                .filter(otherUserFriendList::contains)
                .map(userStorage::findUserById)
                .toList();
    }

    // вспомогательный метод - выдает коллекцию друзей пользователя
    public Collection<Long> getFriendsList(long id) {
        return userStorage.findUserById(id).getFriends();
    }
}
