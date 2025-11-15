# Productivity Service

Многомодульное Spring Boot приложение для управления задачами и уведомлениями.

## Структура проекта

```
productivity-service/
├── pom.xml                   # Родительский POM-файл
├── common/                   # Общий модуль
│   └── src/main/java/com/max/productivity/common/
│       ├── dto/TaskDto.java
│       └── exception/TaskNotFoundException.java
├── bot-gateway/              # Модуль шлюза бота
│   └── src/main/java/com/max/productivity/gateway/
│       └── MaxBotController.java
├── identity-service/         # Модуль управления пользователями
│   └── src/main/java/com/max/productivity/identity/
│       ├── domain/User.java
│       ├── service/IdentityService.java
│       └── repo/UserRepository.java
├── task-service/             # Модуль управления задачами
│   └── src/main/java/com/max/productivity/task/
│       ├── domain/Task.java
│       ├── service/TaskService.java
│       ├── service/TaskServiceImpl.java
│       ├── repo/TaskRepository.java
│       └── event/TaskCreatedEvent.java
├── notification-service/     # Модуль уведомлений
│   └── src/main/java/com/max/productivity/notification/
│       └── service/NotificationEventListener.java
└── application/              # Модуль запуска Spring Boot
    └── src/main/java/com/max/productivity/
        └── ProductivityApplication.java
```

## Описание модулей

### common
Общий модуль, содержащий DTO и общие исключения, используемые всеми другими модулями.

### bot-gateway
REST API контроллер для взаимодействия с ботом. Предоставляет endpoints для работы с задачами.

### identity-service
Сервис управления пользователями. Содержит сущность User, репозиторий и сервис для работы с пользователями.

### task-service
Сервис управления задачами. Содержит бизнес-логику для работы с задачами, публикует события при создании задач.

### notification-service
Сервис уведомлений. Подписывается на события создания задач и отправляет уведомления.

### application
Главный модуль приложения, который собирает все модули вместе и запускает Spring Boot приложение.

## Запуск приложения

1. Соберите проект:
```bash
mvn clean install
```

2. Запустите приложение:
```bash
mvn spring-boot:run -pl application
```

Приложение будет доступно по адресу: http://localhost:8080

## API Endpoints

- `GET /api/bot/tasks?userId={userId}` - Получить все задачи пользователя
- `POST /api/bot/tasks` - Создать новую задачу
- `GET /api/bot/tasks/{id}` - Получить задачу по ID
- `PUT /api/bot/tasks/{id}` - Обновить задачу
- `DELETE /api/bot/tasks/{id}` - Удалить задачу

## База данных

Приложение использует встроенную базу данных H2. 

H2 Console доступна по адресу: http://localhost:8080/h2-console

Параметры подключения:
- JDBC URL: `jdbc:h2:mem:productivitydb`
- Username: `sa`
- Password: (пусто)

## Технологии

- Java 25
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database
- Maven

