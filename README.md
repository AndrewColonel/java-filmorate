# ER-диаграмма проекта Filmorate

![Filmorate.png](Filmorate.png)

### Описание таблиц.
### `users`
Содержит данные о пользователях.
Таблица включает такие поля:
- первичный ключ `user_id`- идентификатор пользователя;
- `login` - логин пользователя, длина 50 символов, NOT NULL ;
- 'email' - адрес электронной почты, длина 50 символов, NOT NULL;
- `name` - имя пользователя, длина 50 символов, может быть пустым;
- `birthdate`- дата рождения пользователя, тип данных date, NOT NULL.

### `films`
Содержит информацию о фильмах.
Таблица включает такие поля:
- первичный ключ `film_id` - идентификатор фильма;
- `name`- название фильма, NOT NULL;
- 'duration'  - продолжительность фильма, NOT NULL;
- 'description' - описание фильма, длина 100 символов, может быть пустым;
- 'release_date' - дата выпуска фильма, NOT NULL;
- внешний ключ `rating_id` (ссылается на таблицу `rating`) - идентификатор рейтинга фильма.

### `friends`
Содержит информацию о дружеских отношениях пользователей.
Таблица включает такие поля:
- первичный ключ `friend_id` - идентификатор списка друзей;
- внешние ключи `user1_id` и 'user2_id' (ссылается на таблицу `users`) - идентификаторы пользователя.

### `likes`
Содержит информацию об отметках "нравится" (лайк) для каждого фильма.
Таблица включает такие поля:
- первичный ключ `like_id` - идентификатор "лайка";
- внешний ключ`user_id`  (ссылается на таблицу `users`)  - идентификатор пользователя;
- внешний ключ `artist_id` (ссылается на таблицу `films') - идентификатор фильма.

### `genre`
Содержит информацию о жанрах фильмах. У фильма может быть сразу несколько жанров.
Таблица включает такие поля:
- первичный ключ `genre_id` - идентификатор фильма;
- `rating_name` - текстовое название жанра, NOT NULL;

### `genre_list`
Содержит информацию о присвоенных значениях жанра для каждого фильма.
Таблица включает такие поля:
- первичный ключ `genre_list_id` - идентификатор фильма;
- внешний ключ `film_id` (ссылается на таблицу `films`) - идентификатор фильма.
- внешний ключ `genre_id` (ссылается на таблицу `genre`) - идентификатор списка жанров фильма.

### `rating`
Содержит информацию о рейтингах Ассоциации кинокомпаний (англ. _Motion Picture Association,_ сокращённо _МРА_), определяющих возрастное ограничение для фильма. .
Таблица включает такие поля:
- первичный ключ `rating_id`  идентификатор списка рейтингов;
- `rating_name` - текстовый код рейтинга;


### Примеры запросов
##### Получаем таблицу с пользователями и друзьями
```
SELECT u.user_id,
	   u.login,
	   u.email,
	   u.name,
	   u.birthday,
	   f.user2_id
FROM users AS u
LEFT OUTER JOIN friends AS f ON u.user_id = f.user1_id
ORDER BY u.user_id
LIMIT 10

```

[//]: # (##### Результат)

[//]: # ("user_id"	"login"	"email"	"name"	"birthday"	"user2_id")

[//]: # (1	"Jwl3Nb31Vc"	"Maye38@gmail.com"	"Sylvester Quigley"	"1966-01-09"	5)

[//]: # (1	"Jwl3Nb31Vc"	"Maye38@gmail.com"	"Sylvester Quigley"	"1966-01-09"	6)

[//]: # (1	"Jwl3Nb31Vc"	"Maye38@gmail.com"	"Sylvester Quigley"	"1966-01-09"	2)

[//]: # (2	"doloreUpdate"	"mail@yandex.ru"	"est adipisicing"	"1976-09-20"	1)

[//]: # (2	"doloreUpdate"	"mail@yandex.ru"	"est adipisicing"	"1976-09-20"	10)

[//]: # (2	"doloreUpdate"	"mail@yandex.ru"	"est adipisicing"	"1976-09-20"	3)

[//]: # (2	"doloreUpdate"	"mail@yandex.ru"	"est adipisicing"	"1976-09-20"	5)

[//]: # (2	"doloreUpdate"	"mail@yandex.ru"	"est adipisicing"	"1976-09-20"	9)

[//]: # (3	"pSI8KqmSDZ"	"Jodie.Frami26@hotmail.com"	"Marie Dare"	"1996-03-05")

[//]: # (4	"AyVzfCn5bF"	"Laisha.Stark90@yahoo.com"	"Charles Stamm"	"1967-04-22")

##### Получаем таблицу только с комбинациями друзей для избранных пользователей
```
SELECT * 
FROM (
SELECT u.user_id,
	   f.user2_id
FROM users AS u
LEFT OUTER JOIN friends AS f ON u.user_id = f.user1_id
ORDER BY u.user_id) AS friends_list
WHERE friends_list.user_id IN (1,2,3,4)

```

[//]: # (##### Результат)

[//]: # ("user_id"	"user2_id")

[//]: # (1	2)

[//]: # (1	5)

[//]: # (1	6)

[//]: # (2	9)

[//]: # (2	10)

[//]: # (2	5)

[//]: # (2	1)

[//]: # (2	3)

[//]: # (3	null)

[//]: # (4	null)


##### Получаем таблицу с фильмами и рейтингами
```
SELECT f.film_id,
       f.name,
	   f.Description,
	   f.duration,
	   f.release_date,
	   r.rating_name
FROM films AS f
LEFT OUTER JOIN rating AS r ON f.rating_id = r.rating_id
LIMIT 10
```

[//]: # (##### Результат)

[//]: # ("film_id"	"name"	"description"	"duration"	"release_date"	"rating_name")

[//]: # (1	"H8Tqlv7yysKvTow"	"m4S2ELAjp9BWiq8APfzlsIlAqpRC1Qt6SWf45CVJjYeUsJqWmT"	63	"1997-09-29"	"G")

[//]: # (2	"Film Updated"	"New film update decription"	190	"1989-04-17"	"G")

[//]: # (3	"NvkrQ4yEET7OM8i"	"tq92p7TvgJDa6n9SmoojFdOuSmwh60A0WlL40BYhbUHTZC7hM6"	116	"1987-03-09"	"R")

[//]: # (4	"ssBC7N8danBjGRt"	"EiVhM2XwrN169bu5oQo3LYCSg2oqfdyCtRMZmMypFa93UrrCUs"	110	"1981-05-25"	"PG-13")

[//]: # (5	"peIEKaaQtt6ei1z"	"5ythbqD3OXeKtEzVNCc2W4Y8fO6wo5bAjX3EPkgJEOY57FY2JP"	175	"1988-11-26"	"NC-17")

[//]: # (6	"ixMO3faQlknEOav"	"jXoA1SLQ2Ld9s9chCX6dr6IVBmf4ocBQY6LruJbotE6KZugFtq"	126	"2000-12-04"	"PG")

[//]: # (7	"WTzwZeu9RkCJ4KH"	"XHzifbxR9y0JFkHfNdbXEe3NBOSNWX9bu0vEpP3l2P2z3OaFwa"	144	"1993-06-10"	"PG")

[//]: # (8	"BQGWSd6aEt3wJ88"	"xEIXaNuQAivFRzD29yc0SX3D4FG72kyJ0Y3yVysNqHv9KFEE1U"	159	"1979-10-28"	"NC-17")

[//]: # (9	"cgFUwhoHcun7UW2"	"ghTnIzJgY7RKNTMzYauODMz6uGDcy2v0TK9SX0H8kc8jqPe8Dl"	60	"1975-08-02"	"G")

[//]: # (10	"Kcl0Y2iee1A5P4o"	"RHsE7xKiLbDqM8lLS2ErCavh92hlG7nWabeZYJwmcIUfLaBpfw"	60	"1985-11-29"	"G")

##### Получаем таблицу с фильмами, категориями и рейтингом
```
SELECT f.film_id,
       f.name,
	   f.duration,
	   f.release_date,
	   r.rating_name,
	   g.genre_name
FROM films AS f
LEFT OUTER JOIN rating AS r ON f.rating_id = r.rating_id
LEFT OUTER JOIN genre_list AS gl ON f.film_id = gl.film_id
LEFT OUTER JOIN genre AS g ON gl.genre_id = g.genre_id
LIMIT 10
```

[//]: # (##### результат)

[//]: # ("film_id"	"name"	"duration"	"release_date"	"rating_name"	"genre_name")

[//]: # (1	"H8Tqlv7yysKvTow"	63	"1997-09-29"	"G"	"Комедия")

[//]: # (1	"H8Tqlv7yysKvTow"	63	"1997-09-29"	"G"	"Драма")

[//]: # (2	"Film Updated"	190	"1989-04-17"	"G"	"Триллер")

[//]: # (3	"NvkrQ4yEET7OM8i"	116	"1987-03-09"	"R"	"Документальный")

[//]: # (3	"NvkrQ4yEET7OM8i"	116	"1987-03-09"	"R"	"Драма")

[//]: # (4	"ssBC7N8danBjGRt"	110	"1981-05-25"	"PG-13"	"Комедия")

[//]: # (4	"ssBC7N8danBjGRt"	110	"1981-05-25"	"PG-13"	"Мультфильм")

[//]: # (4	"ssBC7N8danBjGRt"	110	"1981-05-25"	"PG-13"	"Документальный")

[//]: # (5	"peIEKaaQtt6ei1z"	175	"1988-11-26"	"NC-17"	"Комедия")

[//]: # (6	"ixMO3faQlknEOav"	126	"2000-12-04"	"PG"	"Мультфильм")