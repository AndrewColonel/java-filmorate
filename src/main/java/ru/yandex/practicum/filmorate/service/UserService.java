package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    // добавляем друзей
    // TODO подумать про Stream API
    public User addFriends(long userId, long frientId) {
        Optional<User> mayBeUser = userStorage.findUserById(userId);
        Optional<User> mayBeFriend = userStorage.findUserById(frientId);
        if (mayBeUser.isEmpty()) {
            throw new NotFoundException("Пользователя с ID " + userId + "не существует.");
        }
        if (mayBeFriend.isEmpty()) {
            throw new NotFoundException("Пользователя с ID " + frientId + "не существует.");
        }
        mayBeUser.get().setFriend(frientId);
        mayBeFriend.get().setFriend(userId);
        return mayBeUser.get();
    }

    // удаляем bp друзей
    // TODO подумать про Stream API
    public User delFriends(long userId, long frientId) {
        Optional<User> mayBeUser = userStorage.findUserById(userId);
        Optional<User> mayBeFriend = userStorage.findUserById(frientId);
        if (mayBeUser.isEmpty()) {
            throw new NotFoundException("Пользователя с ID " + userId + "не существует.");
        }
        if (mayBeFriend.isEmpty()) {
            throw new NotFoundException("Пользователя с ID " + frientId + "не существует.");
        }
        mayBeUser.get().delFriend(frientId);
        mayBeFriend.get().delFriend(userId);
        return mayBeUser.get();
    }

    // возвращает списко друзей
    // TODO подумать про Stream API
    public Set<Long> getFrientsList(long id) {
        Optional<User> mayBeUser = userStorage.findUserById(id);
        if (mayBeUser.isEmpty()) {
            throw new  NotFoundException("Пользователя с ID " + id + "не существует.");
        }
        return mayBeUser.get().getFriends();
    }

}
