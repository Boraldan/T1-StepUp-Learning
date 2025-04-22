package ru.boraldan.aop.taskaop.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.boraldan.aop.taskaop.domen.Status;
import ru.boraldan.aop.taskaop.domen.Tasks;
import ru.boraldan.aop.taskaop.domen.dto.CreatTasksDto;
import ru.boraldan.aop.taskaop.domen.dto.TasksDto;
import ru.boraldan.aop.taskaop.kafka.KafkaTasksStatusProducer;
import ru.boraldan.aop.taskaop.repository.TaskRepository;
import ru.boraldan.aop.taskaop.container.TaskTestContainers;
import ru.boraldan.aop.taskaop.tool.TaskMapper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@SpringBootTest
class TaskServiceSpringBootTest extends TaskTestContainers {

    @Autowired
    private KafkaTasksStatusProducer kafkaTasksStatusProducer;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskMapper taskMapper;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        taskService = new TaskService(kafkaTasksStatusProducer, taskRepository, taskMapper);
    }

    @Test
    @DisplayName("getTasks: получение всех задач")
    void getTasks() {
        Tasks task = creatTask();
        task.setTasksId(null);
        task = taskRepository.save(task);

        Page<TasksDto> tasksDtoPage = taskService.getTasks(PageRequest.of(0, 10));

        assertNotNull(tasksDtoPage);
        assertEquals(1, tasksDtoPage.getContent().size());
        assertEquals(task.getTasksId(), tasksDtoPage.getContent().get(0).getTasksId());
        assertEquals(task.getTitle(), tasksDtoPage.getContent().get(0).getTitle());
        assertEquals(task.getDescription(), tasksDtoPage.getContent().get(0).getDescription());
        assertEquals(task.getUserId(), tasksDtoPage.getContent().get(0).getUserId());
        assertEquals(task.getStatus(), tasksDtoPage.getContent().get(0).getStatus());
        assertEquals(task.getCreatedAt(), tasksDtoPage.getContent().get(0).getCreatedAt());
        assertEquals(task.getUpdatedAt(), tasksDtoPage.getContent().get(0).getUpdatedAt());
        assertEquals(task.getIsActive(), tasksDtoPage.getContent().get(0).getIsActive());
    }

    @Test
    @DisplayName("getTaskById: получение задачи по id")
    void getTaskById() {
        Tasks task = creatTask();
        task.setTasksId(null);
        task = taskRepository.save(task);

        TasksDto testTasksDto = taskService.getTaskById(task.getTasksId());

        assertNotNull(testTasksDto);
        assertEquals(task.getTasksId(), testTasksDto.getTasksId());
        assertEquals(task.getTitle(), testTasksDto.getTitle());
        assertEquals(task.getDescription(), testTasksDto.getDescription());
        assertEquals(task.getStatus(), testTasksDto.getStatus());
        assertEquals(task.getIsActive(), testTasksDto.getIsActive());
        assertNotNull(testTasksDto.getUserId());
        assertNotNull(testTasksDto.getStatus());
        assertNotNull(testTasksDto.getCreatedAt());
        assertNotNull(testTasksDto.getUpdatedAt());
        assertNotNull(testTasksDto.getIsActive());
        assertThrows(EntityNotFoundException.class, () -> taskService.getTaskById(UUID.randomUUID()));
    }

    @Test
    @DisplayName("createTask: создание новой задачи")
    void createTask() {
        CreatTasksDto creatTasksDto = creatTaskDto();
        Tasks tasks = taskMapper.creatTasksFromDto(creatTasksDto);
        TasksDto tasksDto = taskService.createTask(creatTasksDto);

        assertNotNull(tasksDto);
        assertNotNull(tasksDto.getTasksId());
        assertEquals(tasks.getTitle(), tasksDto.getTitle());
        assertEquals(tasks.getDescription(), tasksDto.getDescription());
        assertNotNull(tasksDto.getUserId());
        assertNotNull(tasksDto.getStatus());
        assertNotNull(tasksDto.getCreatedAt());
        assertNotNull(tasksDto.getUpdatedAt());
        assertNotNull(tasksDto.getIsActive());

        Optional<Tasks> savedTasks = taskRepository.findById(tasksDto.getTasksId());
        assertTrue(savedTasks.isPresent());
        assertEquals(tasksDto.getTasksId(), savedTasks.get().getTasksId());
    }

    @Test
    @DisplayName("updateTaskWithoutStatusUpdate: обновление задачи по id - Status не меняется, в Kafka не отправляем ")
    void updateTaskWithoutStatusUpdate() {
        Tasks oldTask = creatTask();
        oldTask.setTasksId(null);
        oldTask.setStatus(Status.PENDING);
        oldTask = taskRepository.save(oldTask);

        UUID taskId = oldTask.getTasksId();

        CreatTasksDto updateTasks = this.creatTaskDto();
        updateTasks.setTasksId(taskId);
        updateTasks.setStatus(Status.PENDING);

        TasksDto updatedTasksDto = taskService.updateTask(taskId, updateTasks);

        assertNotNull(updatedTasksDto);
        assertEquals(oldTask.getStatus(), updatedTasksDto.getStatus());
        assertEquals(oldTask.getTasksId(), updatedTasksDto.getTasksId());
        assertEquals(updateTasks.getStatus(), updatedTasksDto.getStatus());
        assertEquals(updateTasks.getTasksId(), updatedTasksDto.getTasksId());
        assertNotNull(updatedTasksDto.getUserId());
        assertNotNull(updatedTasksDto.getStatus());
        assertNotNull(updatedTasksDto.getCreatedAt());
        assertNotNull(updatedTasksDto.getUpdatedAt());
        assertNotNull(updatedTasksDto.getIsActive());

    }

    @Test
    @DisplayName("updateNewStatusTask: обновление задачи по id - новый Status, отправка в Kafka ")
    void updateNewStatusTask() {
        Tasks oldTask = creatTask();
        oldTask.setTasksId(null);
        oldTask.setStatus(Status.PENDING);
        oldTask = taskRepository.save(oldTask);

        UUID taskId = oldTask.getTasksId();

        CreatTasksDto updateTasks = this.creatTaskDto();
        updateTasks.setTasksId(taskId);
        updateTasks.setStatus(Status.IN_PROGRESS);

        TasksDto updatedTasksDto = taskService.updateTask(taskId, updateTasks);

        assertNotNull(updatedTasksDto);
        assertEquals(oldTask.getTasksId(), updatedTasksDto.getTasksId());
        assertNotEquals(oldTask.getStatus(), updatedTasksDto.getStatus());
        assertEquals(updateTasks.getStatus(), updatedTasksDto.getStatus());
        assertNotNull(updatedTasksDto.getUserId());
        assertNotNull(updatedTasksDto.getStatus());
        assertNotNull(updatedTasksDto.getCreatedAt());
        assertNotNull(updatedTasksDto.getUpdatedAt());
        assertNotNull(updatedTasksDto.getIsActive());
    }

    @Test
    @DisplayName("deleteTask: удаление задачи по id")
    void deleteTask() {
        Tasks task = creatTask();
        task.setTasksId(null);
        task = taskRepository.save(task);

        TasksDto testTasksDto = taskService.getTaskById(task.getTasksId());

        assertNotNull(testTasksDto);
        assertEquals(task.getTasksId(), testTasksDto.getTasksId());

        UUID taskId = task.getTasksId();
        taskService.deleteTask(task.getTasksId());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            taskService.getTaskById(taskId);
        });

        assertEquals("Task with id %s not found".formatted(taskId), exception.getMessage());
    }

    private Tasks creatTask() {
        Tasks tasks = new Tasks();
        tasks.setTasksId(UUID.randomUUID());
        tasks.setTitle("MockTask1");
        tasks.setDescription("Описание задания 1");
        tasks.setUserId(UUID.randomUUID());
        tasks.setStatus(Status.PENDING);
        tasks.setCreatedAt(LocalDateTime.now());
        tasks.setUpdatedAt(LocalDateTime.now());
        tasks.setIsActive(true);
        return tasks;
    }

    private CreatTasksDto creatTaskDto() {
        CreatTasksDto creatTasksDto = new CreatTasksDto();
        creatTasksDto.setTasksId(UUID.randomUUID());
        creatTasksDto.setTitle("MockTaskDto_2");
        creatTasksDto.setDescription("MockTaskDto_2");
        creatTasksDto.setUserId(UUID.randomUUID());
        creatTasksDto.setStatus(Status.PENDING);
        creatTasksDto.setIsActive(true);
        return creatTasksDto;
    }
}