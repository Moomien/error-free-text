# error-free-text

Тестовое задание. Сервис принимает текст, исправляет опечатки через Яндекс Спеллер и отдаёт результат по id задачи.

Стек: Java 21, Spring Boot 3.5, Gradle, PostgreSQL 16, Flyway, Docker, Testcontainers.

## Запуск

Нужен только Docker:

```
docker compose up --build
```

Настройки базы лежат в `.env` (пример в `.env.example`). Без него поднимется с дефолтами.

Приложение будет на `localhost:8080`, Postgres снаружи на `:5433` (чтобы не конфликтовать с локальным).

Локально без докера: поднять только базу `docker compose up postgres` и запустить `.\gradlew bootRun`

Тесты: `.\gradlew test`

## API

Создать задачу:

```
curl -i -X POST http://localhost:8080/tasks \
    -H "Content-Type: application/json" \
    -d `{"text": "Превет мир", "language": "RU"}`
``` 

Ответ `201`:
````
{"id": "af226892-cb49-4a7a-b11b-93996fea7805"}
````

Получить результат:
```
curl -i http://localhost:8080/tasks{id]
```

- `DONE` - статус + `correctedText`
- `NEW` / `IN_PROGRESS` - только статус
- `ERROR` - статус + описание ошибки

Ошибки все в одном формате:
```
{"errorMessage": "Task with id: ... not found", "errorCode": 40401, "timestamp":"2026-10-04T15:24:22.461", "path": "/tasks/..."}
```

| errorCode | когда |
|---|---|
| 40001 | не прошла валидация текста (меньше 3 символов, нет ни одной буквы) |
| 40002 | кривой json или язык не EN/RU |
| 40003 | id не UUID |
| 40401 | задачи нет |