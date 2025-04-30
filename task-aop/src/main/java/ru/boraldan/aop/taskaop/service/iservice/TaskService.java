package ru.boraldan.aop.taskaop.service.iservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.boraldan.aop.taskaop.domen.dto.CreatTasksDto;
import ru.boraldan.aop.taskaop.domen.dto.TasksDto;

import java.util.UUID;

public interface TaskService {

    Page<TasksDto> getTasks(Pageable pageable);

    TasksDto getTaskById(UUID id);

    TasksDto createTask(CreatTasksDto creatTasksDto);

    TasksDto updateTask(UUID id, CreatTasksDto creatTasksDto);

    void deleteTask(UUID id);
}
