// Добавьте unit-тесты для валидации моделей. Убедитесь, что она работает на граничных условиях.
// Проверьте, что валидация не пропускает пустые или неверно заполненные поля.
// Посмотрите, как контроллер реагирует на пустой запрос.
package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Определим атрибут webEnvironment как RANDOM_PORT,
// который сообщает Spring запустить полностью работающий веб-сервер на случайном порту.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmorateApplicationTests {

    // Добавление аннотации @SpringBootTest к классу
    // зарегистрирует bean-компонент TestRestTemplate и @Autowired в  тестовом классе
    @Autowired
    TestRestTemplate template;

    @Test
    void shouldCreateNewFilm() {
        // Создаем запись о яильме, котроая пройдет валидацию
        Film film = new Film(1, "nisi eiusmod", "adipisicing",
                LocalDate.parse("1967-03-25"), 100);
        // используем  метод postForEntity() TestRestTemplate, чтобы сделать запрос POST к эндпоинту /films
        ResponseEntity<Film> entity = template.postForEntity("/films", film, Film.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        Film postedFilm = entity.getBody();
        assert postedFilm != null;
        assertEquals(1, postedFilm.getId());
        assertEquals("nisi eiusmod", postedFilm.getName());
        assertEquals("adipisicing", postedFilm.getDescription());
        assertEquals("1967-03-25", postedFilm.getReleaseDate().toString());
        assertEquals(100, postedFilm.getDuration());

        // создаем запись о филмме с ошибкой
        // название не может быть пустым и null
        Film notValidName = new Film(1, "", "adipisicing",
                LocalDate.parse("1967-03-25"), 100);
        ResponseEntity<Film> entity1 = template.postForEntity("/films", notValidName,
                Film.class);
        assertEquals(HttpStatus.BAD_REQUEST, entity1.getStatusCode());

        // максимальная длина описания — 200 символов;
        Film notValidDescription = new Film(1, "nisi eiusmod",
                "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                        "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                        "а именно 20 миллионов. о Куглов, который за время «своего отсутствия», " +
                        "стал кандидатом Коломбани.",
                LocalDate.parse("1967-03-25"), 100);
        ResponseEntity<Film> entity2 = template.postForEntity("/films", notValidDescription,
                Film.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity2.getStatusCode());

        // дата релиза — не раньше 28 декабря 1895 года;
        Film notValidRealeasedDate = new Film(1, "nisi eiusmod", "adipisicing",
                LocalDate.parse("1890-03-25"), 100);
        ResponseEntity<Film> entity3 = template.postForEntity("/films", notValidRealeasedDate,
                Film.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity3.getStatusCode());

        // продолжительность фильма должна быть положительным числом.
        Film notValidDuration = new Film(1, "nisi eiusmod", "adipisicing",
                LocalDate.parse("1967-03-25"), -100);
        ResponseEntity<Film> entity4 = template.postForEntity("/films", notValidDuration,
                Film.class);
        assertEquals(HttpStatus.BAD_REQUEST, entity4.getStatusCode());
    }

    @Test
    void shouldCreateNewUser() {
        // Создаем запись о пользователе, котроая пройдет валидацию
        User user = new User(1L, Set.of(), "mail@mail.ru","dolore","Nick Name",
                LocalDate.parse("1946-08-20"));
        // используем  метод postForEntity() TestRestTemplate, чтобы сделать запрос POST к эндпоинту /users
        ResponseEntity<User> entity = template.postForEntity("/users", user, User.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        User postedUser = entity.getBody();
        assert postedUser != null;
        assertEquals(1, postedUser.getId());
        assertEquals("Nick Name", postedUser.getName());
        assertEquals("dolore", postedUser.getLogin());
        assertEquals("1946-08-20", postedUser.getBirthday().toString());
        assertEquals("mail@mail.ru", postedUser.getEmail());

        // создаем запись о пользователе с ошибкой
        // логин не может быть пустым - проверено через аннотации и содержать пробелы
        User notValidLogin = new User(1L, Set.of(),"mail@mail.ru","dolore ullamco","Nick Name",
                LocalDate.parse("1946-08-20"));
        ResponseEntity<Film> entity1 = template.postForEntity("/users", notValidLogin,
                Film.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity1.getStatusCode());

        // электронная почта не может быть пустой и должна содержать символ `@`
        User notValidEmail = new User(1L, Set.of(), "mail&mail.ru","dolore","Nick Name",
                LocalDate.parse("1946-08-20"));
        ResponseEntity<Film> entity2 = template.postForEntity("/users", notValidEmail,
                Film.class);
        assertEquals(HttpStatus.BAD_REQUEST, entity2.getStatusCode());

        // дата рождения не может быть в будущем и пустым.
        User notValidBirthday = new User(1L, Set.of(), "mail@mail.ru","dolore","Nick Name",
                LocalDate.parse("2030-08-20"));
        ResponseEntity<Film> entity3 = template.postForEntity("/users", notValidBirthday,
                Film.class);
        assertEquals(HttpStatus.BAD_REQUEST, entity3.getStatusCode());

    }

}
