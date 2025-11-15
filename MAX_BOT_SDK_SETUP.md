# Max Bot SDK Setup Instructions

## ⚠️ ВАЖНО: Требуется установка Max Bot SDK

Проект требует локально установленный **Max Bot SDK версии 0.0.6-SNAPSHOT**.

## Статус зависимостей

Проект использует следующие зависимости от Max Bot SDK:

- `bot-gateway` → `ru.max:max-bot-sdk:0.0.6-SNAPSHOT`
- `notification-service` → `ru.max:max-bot-api:0.0.6-SNAPSHOT`
- `application` → зависит от `bot-gateway` и `notification-service`

## Варианты установки

### Вариант 1: Установка из локального файла JAR (рекомендуется)

Если у вас есть JAR-файлы SDK:

```bash
# Для max-bot-sdk
mvn install:install-file \
  -Dfile=path/to/max-bot-sdk-0.0.6-SNAPSHOT.jar \
  -DgroupId=ru.max \
  -DartifactId=max-bot-sdk \
  -Dversion=0.0.6-SNAPSHOT \
  -Dpackaging=jar

# Для max-bot-api
mvn install:install-file \
  -Dfile=path/to/max-bot-api-0.0.6-SNAPSHOT.jar \
  -DgroupId=ru.max \
  -DartifactId=max-bot-api \
  -Dversion=0.0.6-SNAPSHOT \
  -Dpackaging=jar
```

### Вариант 2: Сборка из исходников

Если у вас есть доступ к репозиторию Max Bot SDK:

```bash
git clone <URL репозитория max-bot-sdk>
cd max-bot-sdk
mvn clean install
```

### Вариант 3: Временная сборка без bot-модулей

Если SDK недоступен, можно собрать только основные модули:

```bash
# Сборка без модулей, требующих SDK
mvn clean install -pl common,identity-service,task-service

# Или использовать профиль (если настроен)
mvn clean install -P without-bot
```

## Структура проекта

✅ **Модули без зависимости от SDK (всегда собираются):**
- `common` - общие DTO и исключения
- `identity-service` - управление пользователями (8 тестов)
- `task-service` - управление задачами (15 тестов)

⚠️ **Модули, требующие Max Bot SDK:**
- `bot-gateway` - интеграция с Max Bot (контроллеры, обработчики команд)
- `notification-service` - отправка уведомлений через бота
- `application` - главное приложение, объединяющее все модули

## Настроенные репозитории Maven

В родительском `pom.xml` уже добавлены репозитории:

- `https://oss.sonatype.org/content/repositories/snapshots` (для SNAPSHOT версий)
- `https://oss.sonatype.org/content/repositories/releases` (для release версий)

## Следующие шаги

1. **Получите Max Bot SDK** от разработчиков или из корпоративного репозитория
2. **Установите SDK** в локальный Maven репозиторий (см. Вариант 1 выше)
3. **Запустите полную сборку:**
   ```bash
   mvn clean install
   ```
4. **Запустите приложение:**
   ```bash
   cd application
   mvn spring-boot:run
   ```

## Проверка установки

После установки SDK проверьте, что зависимости разрешены:

```bash
mvn dependency:tree -pl bot-gateway
mvn dependency:tree -pl notification-service
```

## Контакты

Для получения доступа к Max Bot SDK обратитесь к команде разработки Max Bot или вашему тимлиду.


