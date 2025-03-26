package ru.boraldan.aop.taskaop.domen.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Модель задачи")
public class TaskDto {

    @Schema(description = "Уникальный идентификатор задачи", example = "1")
    @Positive(message = "taskId должен быть > 0")
    private Long taskId;

    @Schema(description = "Заголовок задачи", example = "Добавить новую задачу")
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 100, message = "title должен быть до 100 символов длиной")
    private String title;

    @Schema(description = "Описание задачи", example = "Подробное описание новой задачи")
    @Size(max = 500, message = "description должен быть до 500 символов длиной")
    private String description;

    @Schema(description = "Идентификатор пользователя задачи", example = "1")
    @Positive(message = "userId должен быть > 0")
    private Long userId;

    @Schema(description = "Статус активности задачи. Если false, задача считается завершённой", example = "true")
    private Boolean isActive;
}
