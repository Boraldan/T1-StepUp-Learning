package ru.boraldan.aop.taskaop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * Метод для настройки документации OpenAPI для системы управления задачами.
     *
     * Этот метод создает объект OpenAPI, который описывает API системы управления задачами.
     * Включает в себя основную информацию о версии, названии и описании API.
     * Конфигурирует Swagger для автоматической генерации документации для всех доступных эндпоинтов.
     *
     * @return Возвращает объект OpenAPI, содержащий описание и метаданные для API.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Management System API")
                        .version("1.0.0")
                        .description("Документация для API системы управления задачами"));
    }
}