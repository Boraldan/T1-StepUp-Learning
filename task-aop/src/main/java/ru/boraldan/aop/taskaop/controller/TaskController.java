package ru.boraldan.aop.taskaop.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.boraldan.aop.taskaop.domen.dto.CreatTasksDto;
import ru.boraldan.aop.taskaop.domen.dto.TasksDto;
import ru.boraldan.aop.taskaop.service.TaskService;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Получить все задачи", description = "Возвращает все задачи с поддержкой пагинации.")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<TasksDto> getTasks(@RequestParam(required = false, defaultValue = "0") int page,
                                @RequestParam(required = false, defaultValue = "10") int size) {
        return taskService.getTasks(PageRequest.of(page, size));
    }

    @Operation(summary = "Получить задачи id", description = "Возвращает задачи по id.")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TasksDto getTaskById(@PathVariable @Positive Long id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Создать новую задачу", description = "Создает новую задачу в системе.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TasksDto createTask(@RequestBody @Valid CreatTasksDto creatTasksDto) {
        return taskService.createTask(creatTasksDto);
    }

    @Operation(summary = "Обновить задачу по id", description = "Обновляет информацию о задаче.")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TasksDto updateTask(@PathVariable @Positive Long id,
                               @RequestBody @Valid CreatTasksDto creatTasksDto) {
        return taskService.updateTask(id, creatTasksDto);
    }

    @Operation(summary = "Удалить задачу по id", description = "Удаляет задачу из системы.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable @Positive Long id) {
        taskService.deleteTask(id);
    }

}
