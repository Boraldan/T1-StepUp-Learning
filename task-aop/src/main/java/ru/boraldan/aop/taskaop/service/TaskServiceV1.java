package ru.boraldan.aop.taskaop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.boraldan.aop.taskaop.domen.Status;
import ru.boraldan.aop.taskaop.domen.Tasks;
import ru.boraldan.aop.taskaop.domen.dto.CreatTasksDto;
import ru.boraldan.aop.taskaop.domen.dto.TasksDto;
import ru.boraldan.aop.taskaop.kafka.KafkaTasksStatusProducer;
import ru.boraldan.aop.taskaop.repository.TaskRepository;
import ru.boraldan.aop.taskaop.service.iservice.TaskService;
import ru.boraldan.aop.taskaop.tool.TaskMapper;
import ru.boraldan.logaopstarter.starter.aspect.annotation.LogAfterReturning;
import ru.boraldan.logaopstarter.starter.aspect.annotation.LogAfterThrowing;
import ru.boraldan.logaopstarter.starter.aspect.annotation.LogAround;
import ru.boraldan.logaopstarter.starter.aspect.annotation.LogBefore;

import java.util.UUID;

@LogAfterThrowing
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TaskServiceV1 implements TaskService {

    private final KafkaTasksStatusProducer kafkaTasksStatusProducer;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @LogBefore
    public Page<TasksDto> getTasks(Pageable pageable) {
        return taskMapper.toTasksDtoPage(taskRepository.findAll(pageable));
    }

    @LogAfterReturning
    public TasksDto getTaskById(UUID id) {
        Tasks tasks = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %s not found".formatted(id)));
        return taskMapper.toDtoFromTasks(tasks);
    }

    @LogAfterReturning
    @Transactional
    public TasksDto createTask(CreatTasksDto creatTasksDto) {
        if (creatTasksDto.getStatus() == null) creatTasksDto.setStatus(Status.PENDING);
        if (creatTasksDto.getIsActive() == null) creatTasksDto.setIsActive(true);
        Tasks tasks = taskRepository.save(taskMapper.creatTasksFromDto(creatTasksDto));
        return taskMapper.toDtoFromTasks(tasks);
    }

    @LogAround
    @Transactional
    public TasksDto updateTask(UUID id, CreatTasksDto creatTasksDto) {
        Tasks tasks = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %s not found".formatted(id)));
        boolean statusFlag = creatTasksDto.getStatus().equals(tasks.getStatus());
        tasks = taskRepository.save(taskMapper.updateTasksFromDto(creatTasksDto, tasks));
        TasksDto tasksDto = taskMapper.toDtoFromTasks(tasks);
        if (!statusFlag) {
            kafkaTasksStatusProducer.sendToUpdateStatus(tasksDto);
        }
        return tasksDto;
    }

    @LogBefore
    @Transactional
    public void deleteTask(UUID id) {
        Tasks tasks = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %s not found".formatted(id)));
        taskRepository.delete(tasks);
    }

}
