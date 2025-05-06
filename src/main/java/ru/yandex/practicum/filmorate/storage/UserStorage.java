package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    // получение списка всех пользователей
    Collection<User> findAll();

    // поиск пользователя по ID
    public User findUserById(long id);

    // создание нового пользователя
    User create(User user);

    // обновление имеющегося пользователя
    User update(User newUser);

    void addFriend(long userId, long friendId);

    void delFriend(long userId, long friendId);


}
