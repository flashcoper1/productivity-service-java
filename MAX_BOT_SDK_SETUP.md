# Max Bot SDK Setup Instructions

## Проблема
Зависимость `ru.max:max-bot-sdk:0.0.6-SNAPSHOT` не найдена в публичных Maven репозиториях.

## Решения

### Вариант 1: Установка из локального файла (если у вас есть JAR)
```bash
mvn install:install-file \
  -Dfile=path/to/max-bot-sdk-0.0.6-SNAPSHOT.jar \
  -DgroupId=ru.max \
  -DartifactId=max-bot-sdk \
  -Dversion=0.0.6-SNAPSHOT \
  -Dpackaging=jar
```

### Вариант 2: Сборка из исходников (если есть доступ к GitHub)
```bash
git clone https://github.com/maxbot/max-bot-sdk.git
cd max-bot-sdk
mvn clean install
```

### Вариант 3: Использование release версии вместо SNAPSHOT
Измените версию в `pom.xml` на release версию (если доступна):
```xml
<dependency>
    <groupId>ru.max</groupId>
    <artifactId>max-bot-sdk</artifactId>
    <version>0.0.5</version> <!-- или другая доступная версия -->
</dependency>
```

### Вариант 4: Временное исключение модулей с зависимостью
Собирайте проект без модулей, использующих max-bot-sdk:
```bash
mvn clean install -pl common,identity-service,task-service
```

## Текущий статус сборки

✅ **Успешно собраны модули:**
- common
- identity-service (8 тестов пройдено)
- task-service (15 тестов пройдено)

⚠️ **Требуют max-bot-sdk:**
- bot-gateway
- notification-service
- application

## Добавленные репозитории
В `pom.xml` уже добавлены следующие репозитории:
- https://s01.oss.sonatype.org/content/repositories/snapshots/
- https://oss.sonatype.org/content/repositories/snapshots/
- https://jitpack.io

## Рекомендация
Свяжитесь с авторами Max Bot SDK для получения информации о корректном репозитории или актуальной версии библиотеки.

