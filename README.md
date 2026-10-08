# Лабораторная работа №1. Высокопроизводительные системы. Монолитная информационная система для покупки/продажи билетов на события

Написать монолит с использованием Spring Boot.

Разрешены языки: Java, Kotlin.

Разрешены системы сборки: Maven, Gradle, Bazel.

Использовать стабильные версии фреймворков, библиотек, языков. При написании придерживаться языковым конвенциям Java, Kotlin.

Следовать принципам разработки ПО, таким к SOLID, DRY, KISS и др.

При разработке использовать git feature branching strategy. При выборе имен для коммитов необходимо придерживаться conventional commits.

Код должен быть покрыт модульными и интеграционными тестами с помощью testcontainers и junit-jupiter-api, берём лучше отсюда, ещё можно зайти сюда. Минимальный общий процент покрытия кода тестами 70%.

Всё должно запускаться через docker compose.

## Основные требования

  - Основные сущности должны иметь осмысленный CRUD интерфейс с использованием REST API.
  - Использовать правильные http статусы для ответов.
  - Для запросов к БД использовать Spring Data JPA/JDBC.
  - Должна быть реализована валидация полей на уровне контроллера и на уровне Entity.
  - Структура БД должна создаваться через Liquibase/Flyway миграции.
  - Для проверки бизнес-логики должны быть написаны интеграционные тесты с использованием testcontainers и junit-jupiter-api.
  - Использовать переменные среды для конфигурации приложения. При развертывании их можно задавать через поле enviroments: в docker-compose.yml.
  - Приложение должно собираться с использованием docker и запускаться через docker-compose.
  - Использовать любую реляционную/нереляционную БД (зависит от информационной системы), запущенную через docker.
  - Каждый findAll должен иметь пагинацию. Нельзя отдавать больше 50 записей за один запрос.
  - Должен быть минимум один запрос, который вернет findAll в виде бесконечной прокрутки без указания общего количества записей.
  - Должен быть минимум один запрос, который вернет findAll с пагинацией и с указанием общего количества записей в http хедере.
  - На сложных запросах должны использоваться транзакции. Должно быть не меньше двух подобных запросов. Обосновать почему там нужны транзакции.
  - Разделять модели Entity и Dto.
  - Приложение должно иметь чистый код и архитектуру с разделением по сервисам, репозиториям, контроллерам, моделям и т.д.
  - Все enums в БД должны сериализоваться как строки.
  - Ошибки (Exception) из контроллеров нужно хендлить и отдавать человеко читаемую ошибку в теле ответа.
  - Продумать и согласовать архитектуру БД с преподавателем.
  - Должны быть реализованы связи между сущностями каждого типа: Many to Many, One to Many/Many to One, Many to Many с дополнительным полем.
  - Добавить интерактивную документацию с помощью OpenApi 3, развернуть Swagger (он должен быть общий) и дальше его поддерживать.

## Запуск приложения

### Требования

- Docker + Docker Compose
- JDK 21 (для локального запуска и тестов)
- файл `.env` в корне репозитория (скопируйте из `.env.example`)

```bash
cp .env.example .env
```

Переменные в `.env`:

| Переменная | Назначение |
|---|---|
| `POSTGRES_USER` | пользователь PostgreSQL |
| `POSTGRES_PASSWORD` | пароль PostgreSQL |
| `POSTGRES_DB` | имя БД |

### Запуск через Docker Compose

Сборка образа приложения и подъём PostgreSQL + API:

```bash
docker compose up --build
```

Приложение: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui/index.html`  
OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Остановка:

```bash
docker compose down
```

Данные БД сохраняются в volume `poster-db-data`. Чтобы сбросить БД:

```bash
docker compose down -v
```

### Защищённые эндпоинты

Большинство `/api/**` требуют query-параметры `userId` и `role`, совпадающие с пользователем в БД.  
После миграций доступен seed-admin: `userId=1`, `role=ADMIN` (логин `admin`).

Пример:

```bash
curl "http://localhost:8080/api/users?userId=1&role=ADMIN&size=10"
```

### Локальный запуск (без Docker-образа приложения)

Поднять только БД:

```bash
docker compose up -d db
```

Затем:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/poster
export SPRING_DATASOURCE_USERNAME=poster
export SPRING_DATASOURCE_PASSWORD=poster
./gradlew bootRun
```

На Windows (PowerShell):

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/poster"
$env:SPRING_DATASOURCE_USERNAME = "poster"
$env:SPRING_DATASOURCE_PASSWORD = "poster"
./gradlew bootRun
```

### Тесты и покрытие

Нужны JDK 21 и запущенный Docker (Testcontainers):

```bash
./gradlew test jacocoTestReport jacocoTestCoverageVerification
```

HTML-отчёт покрытия: `build/reports/jacoco/test/html/index.html`  
Отчёт по тестам: `build/reports/tests/test/index.html`
