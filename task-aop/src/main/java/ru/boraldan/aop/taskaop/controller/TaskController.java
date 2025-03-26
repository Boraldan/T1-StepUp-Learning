package ru.boraldan.aop.taskaop.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.boraldan.aop.taskaop.domen.Task;
import ru.boraldan.aop.taskaop.domen.dto.TaskDto;
import ru.boraldan.aop.taskaop.service.TaskService;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Получить все задачи", description = "Возвращает все задачи с поддержкой пагинации.")
    @GetMapping
    public ResponseEntity<Page<Task>> getTasks(@RequestParam(required = false, defaultValue = "0") int page,
                                               @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks = taskService.getTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Получить задачи id", description = "Возвращает задачи по id.")
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable @Positive Long id) {
        Task myTask = taskService.getTaskById(id);
        return ResponseEntity.ok(myTask);
    }

    @Operation(summary = "Создать новую задачу", description = "Создает новую задачу в системе.")
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody @Valid TaskDto taskDto) {
        Task myTask = taskService.createTask(taskDto);
        return ResponseEntity.ok(myTask);
    }

    @Operation(summary = "Обновить задачу по id", description = "Обновляет информацию о задаче.")
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable @Positive Long id,
                                           @RequestBody @Valid TaskDto taskDto) {
        Task myTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(myTask);
    }

    @Operation(summary = "Удалить задачу по id", description = "Удаляет задачу из системы.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable @Positive Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
