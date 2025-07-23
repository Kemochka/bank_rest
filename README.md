# Система управления банковскими картами
## Описание 
- REST API для управления банковскими картами с поддержкой ролей ADMIN и USER. Реализованы функции создания, блокировки, удаления карт, переводы между своими картами, просмотр баланса и управление пользователями.
## Функциональность
- Пользователь: 
  - Просмотр собственных карт 
  - Просмотр баланса карты 
  - Переводы между своими картами
- Админ
  - Создание и удаление карт 
  - Изменение статуса карты 
  - Просмотр всех пользователей и всех карт
- Замаскированный номер карты
- Ролевой доступ ADMIN/USER (доступ к эндпоинтам ограничен @PreAuthorize)
# Стек технологий 
- Java 17
- SpringBoot 3.x
- Maven
- Spring Security, JWT
- Spring Data JPA
- Swagger
- PostgreSql, Liquibase
- Docker Compose
# Атрибуты карты
	•	Номер карты — зашифрован, отображается в виде маски: **** **** **** 1234
	•	Владелец
	•	Срок действия
	•	Статус — ACTIVE, BLOCKED, EXPIRED
	•	Баланс
# Развертывание с Docker
- Склонируйте репозиторий
- Создайте файл .env на основе`.env.example` в корне проекта со следующими переменными:
  - `POSTGRES_DB=bank_db`
  - `POSTGRES_USER=your_db_user`
  - `POSTGRES_PASSWORD=your_password`
  - `SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/bank_db`
  - `SPRING_DATASOURCE_USERNAME=your_db_user`
  - `SPRING_DATASOURCE_PASSWORD=your_password`
- Запуск с dev-среды:
  - `docker-compose up --build'`
- Приложение будет доступно на: `http://localhost:8081`
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- PostgreSQL: порт 5433, пользователь и пароль указаны в .env или docker-compose.yml
  - Liquibase миграции находятся в: `src/main/resources/db/migration`
  - Может понадобиться создание базы данных bank_db
# Локальный запуск 
- Убедитесь, что PostgreSQL запущен и настроена база данных с нужными параметрами.
- В файле application.yaml установите параметры подключения к БД.
- Выполните: `mvn spring-boot:run`
# Эндпоинты
- Зарегистрироваться: `/api/auth/signup`
- Выполнить вход: `/api/auth/signin` - *для тестирования в Postman необходимо после регистрации пользователя скопировать JWT токен и использовать его*
- Работа с картами `/api/card`
  - GET `/api/card/{id}` Получить карту по ID (включая данные пользователя и шифрованный пароль)
  - POST `/api/card/create` Создать новую карту (ввод: ID пользователя, баланс)
  - PUT `/api/card/{id}/{status}` Обновить статус карты (BLOCKED, ACTIVE)
  - DELETE `/api/card/{cardId}` Удалить карту по ID 
  - POST `/api/card/transfers/{id}` Перевод между своими картами 
  - GET `/api/card/{id}/balance` Получить баланс по ID карты 
  - GET `/api/card/{userId}/cards` Получить список карт пользователя с пагинацией
- `/api/cards` Получить список всех карт
- Работа с пользователем `/api/user`
  - GET `/api/user/{id}` Получить пользователя по ID 
  - POST `/api/user` Создать нового пользователя 
  - PUT `/api/user` Обновить данные пользователя (логин, имя)
  - DELETE `/api/user/{id}`Удалить пользователя по ID
- `/api/users` Получить список всех пользователей
