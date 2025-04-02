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
import ru.boraldan.aop.taskaop.domen.Tasks;
import ru.boraldan.aop.taskaop.domen.dto.CreatTasksDto;
import ru.boraldan.aop.taskaop.domen.dto.TasksDto;
import ru.boraldan.aop.taskaop.repository.TaskRepository;
import ru.boraldan.aop.taskaop.tool.TaskMapper;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @LogBefore
    public Page<TasksDto> getTasks(Pageable pageable) {
        return taskMapper.toTasksDtoPage( taskRepository.findAll(pageable));
    }

    @LogAfterThrowing
    public TasksDto getTaskById(Long id) {
        Tasks tasks = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %d not found".formatted(id)));
        return taskMapper.toDtoFromTasks(tasks);
    }

    @LogAfterReturning
    @Transactional
    public TasksDto createTask(CreatTasksDto creatTasksDto) {
        Tasks tasks = taskRepository.save(taskMapper.creatTasksFromDto(creatTasksDto));
        return taskMapper.toDtoFromTasks(tasks);
    }

    @LogAround
    @Transactional
    public TasksDto updateTask(Long id, CreatTasksDto creatTasksDto) {
        Tasks tasks = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %d not found".formatted(id)));
        tasks = taskRepository.save(taskMapper.updateTasksFromDto(creatTasksDto, tasks));
        return taskMapper.toDtoFromTasks(tasks);
    }

    @LogAfterThrowing
    @Transactional
    public void deleteTask(Long id) {
        Tasks tasks = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %d not found".formatted(id)));
        taskRepository.delete(tasks);
    }
}
