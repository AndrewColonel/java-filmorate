package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    // получение списка всех пользователей
    Collection<User> findAll();

    // поиск пользователя по ID
    public Optional<User> findUserById(long id);

    // создание нового пользователя
    User create(User user);

    // обновление имеющегося пользователя
    User update(User newUser);
}
