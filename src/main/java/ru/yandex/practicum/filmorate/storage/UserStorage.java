package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    // получение списка всех пользователей
    Collection<User> findAll();

    // получение всех фильмов
    User create(User user);

    // обновление пользователя
    User update(User newUser);
}
