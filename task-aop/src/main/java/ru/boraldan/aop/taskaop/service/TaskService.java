package ru.boraldan.aop.taskaop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.boraldan.aop.taskaop.aspect.annotation.LogAfterReturning;
import ru.boraldan.aop.taskaop.aspect.annotation.LogAfterThrowing;
import ru.boraldan.aop.taskaop.aspect.annotation.LogAround;
import ru.boraldan.aop.taskaop.aspect.annotation.LogBefore;
import ru.boraldan.aop.taskaop.domen.Task;
import ru.boraldan.aop.taskaop.domen.dto.TaskDto;
import ru.boraldan.aop.taskaop.repository.TaskRepository;
import ru.boraldan.aop.taskaop.tool.TaskMapper;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @LogBefore
    public Page<Task> getTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @LogAfterThrowing
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %d not found".formatted(id)));
    }

    @LogAfterReturning
    @Transactional
    public Task createTask(TaskDto taskDto) {
        Task task = taskMapper.toTaskFromDto(taskDto);
        return taskRepository.save(task);
    }

    @LogAround
    @Transactional
    public Task updateTask(Long id, TaskDto taskDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %d not found".formatted(id)));
        task = taskMapper.updateTaskFromDto(taskDto, task);
        return taskRepository.save(task);
    }

    @LogAfterThrowing
    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %d not found".formatted(id)));
        taskRepository.delete(task);
    }
}
