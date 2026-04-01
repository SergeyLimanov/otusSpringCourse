# Используем официальный образ OpenJDK 17
FROM eclipse-temurin:17-jre-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем jar файл приложения
COPY target/library-app-0.0.1-SNAPSHOT.jar app.jar

# Создаем директорию для логов
RUN mkdir -p /app/logs

# Открываем порт приложения
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
