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
import ru.boraldan.aop.taskaop.domen.Status;
import ru.boraldan.aop.taskaop.domen.Tasks;
import ru.boraldan.aop.taskaop.domen.dto.CreatTasksDto;
import ru.boraldan.aop.taskaop.domen.dto.TasksDto;
import ru.boraldan.aop.taskaop.kafka.KafkaTasksStatusProducer;
import ru.boraldan.aop.taskaop.repository.TaskRepository;
import ru.boraldan.aop.taskaop.tool.TaskMapper;

import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TaskService {

    private final KafkaTasksStatusProducer kafkaTasksStatusProducer;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @LogBefore
    public Page<TasksDto> getTasks(Pageable pageable) {
        return taskMapper.toTasksDtoPage(taskRepository.findAll(pageable));
    }

    @LogAfterThrowing
    public TasksDto getTaskById(UUID id) {
        System.out.println(id);
        Tasks tasks = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %s not found".formatted(id)));
        return taskMapper.toDtoFromTasks(tasks);
    }

    @LogAfterReturning
    @Transactional
    public TasksDto createTask(CreatTasksDto creatTasksDto) {
        creatTasksDto.setStatus(Status.PENDING);
        Tasks tasks = taskRepository.save(taskMapper.creatTasksFromDto(creatTasksDto));
        TasksDto tasksDto = taskMapper.toDtoFromTasks(tasks);
        kafkaTasksStatusProducer.sendToUpdateStatus(tasksDto);
        return tasksDto;
    }

    @LogAround
    @Transactional
    public TasksDto updateTask(UUID id, CreatTasksDto creatTasksDto) {
        Tasks tasks = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %s not found".formatted(id)));
        tasks = taskRepository.save(taskMapper.updateTasksFromDto(creatTasksDto, tasks));
        TasksDto tasksDto = taskMapper.toDtoFromTasks(tasks);
        kafkaTasksStatusProducer.sendToUpdateStatus(tasksDto);
        return tasksDto;
    }

    @LogAfterThrowing
    @Transactional
    public void deleteTask(UUID id) {
        Tasks tasks = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %s not found".formatted(id)));
        taskRepository.delete(tasks);
    }
}
