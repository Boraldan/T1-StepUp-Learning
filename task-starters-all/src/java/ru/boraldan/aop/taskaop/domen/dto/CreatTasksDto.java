package ru.boraldan.aop.taskaop.domen.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.boraldan.aop.taskaop.domen.Status;

import java.util.UUID;

@Data
@NoArgsConstructor
@Schema(description = "Модель задачи")
public class CreatTasksDto {

    @Schema(description = "Уникальный идентификатор задачи", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID tasksId;

    @Schema(description = "Заголовок задачи", example = "Добавить новую задачу")
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 100, message = "title должен быть до 100 символов длиной")
    private String title;

    @Schema(description = "Описание задачи", example = "Подробное описание новой задачи")
    @Size(max = 500, message = "description должен быть до 500 символов длиной")
    private String description;

    @Schema(description = "Уникальный идентификатор пользователя", example = "33333333-3333-3333-3333-333333333333")
    private UUID userId;

    @Schema(description = "Идентификатор статуса задачи", example = "PENDING")
    private Status status;

    @Schema(description = "Статус активности задачи. Если false, задача считается завершённой", example = "true")
    private Boolean isActive;
}
