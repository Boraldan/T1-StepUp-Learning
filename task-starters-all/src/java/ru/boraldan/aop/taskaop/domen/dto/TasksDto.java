package ru.boraldan.aop.taskaop.domen.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.boraldan.aop.taskaop.domen.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TasksDto {

    private UUID tasksId;
    private String title;
    private String description;
    private UUID userId;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
}
