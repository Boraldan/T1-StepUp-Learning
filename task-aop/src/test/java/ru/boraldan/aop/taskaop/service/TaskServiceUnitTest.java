package ru.boraldan.aop.taskaop.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.boraldan.aop.taskaop.domen.Status;
import ru.boraldan.aop.taskaop.domen.Tasks;
import ru.boraldan.aop.taskaop.domen.dto.CreatTasksDto;
import ru.boraldan.aop.taskaop.domen.dto.TasksDto;
import ru.boraldan.aop.taskaop.kafka.KafkaTasksStatusProducer;
import ru.boraldan.aop.taskaop.repository.TaskRepository;
import ru.boraldan.aop.taskaop.tool.TaskMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceUnitTest {

    @Mock
    private KafkaTasksStatusProducer kafkaTasksStatusProducer;
    @Mock
    private TaskRepository taskRepository;
    private TaskMapper taskMapper;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskMapper = Mappers.getMapper(TaskMapper.class);
        taskService = new TaskService(kafkaTasksStatusProducer, taskRepository, taskMapper);
    }

    @Test
    @DisplayName("getTasks: получение всех задач")
    void getTasks() {
        Tasks mockTasks = this.creatTask();
        when(taskRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(mockTasks)));
        Page<TasksDto> tasksDtoPage = taskService.getTasks(PageRequest.of(0, 10));

        assertNotNull(tasksDtoPage);
        assertEquals(1, tasksDtoPage.getContent().size());
        assertEquals(mockTasks.getTitle(), tasksDtoPage.getContent().get(0).getTitle());
        verify(taskRepository, times(1)).findAll(any(PageRequest.class));

    }

    @Test
    @DisplayName("getTaskById: получение задачи по id")
    void getTaskById() {
        Tasks mockTasks = this.creatTask();
        when(taskRepository.findById(mockTasks.getTasksId())).thenReturn(Optional.of(mockTasks));
        TasksDto testTasksDto = taskService.getTaskById(mockTasks.getTasksId());

        assertNotNull(testTasksDto);
        assertEquals(mockTasks.getTasksId(), testTasksDto.getTasksId());
        assertEquals(mockTasks.getTitle(), testTasksDto.getTitle());
        assertEquals(mockTasks.getDescription(), testTasksDto.getDescription());
        assertEquals(mockTasks.getStatus(), testTasksDto.getStatus());
        assertEquals(mockTasks.getIsActive(), testTasksDto.getIsActive());
        assertNotNull(testTasksDto.getUserId());
        assertNotNull(testTasksDto.getStatus());
        assertNotNull(testTasksDto.getCreatedAt());
        assertNotNull(testTasksDto.getUpdatedAt());
        assertNotNull(testTasksDto.getIsActive());
        verify(taskRepository, times(1)).findById(any(UUID.class));

        assertThrows(EntityNotFoundException.class, () -> taskService.getTaskById(UUID.randomUUID()));
    }

    @Test
    @DisplayName("createTask: создание новой задачи")
    void createTask() {
        CreatTasksDto creatTasksDto = creatTaskDto();

        Tasks mockTasks = this.taskMapper.creatTasksFromDto(creatTasksDto);
        mockTasks.setTasksId(UUID.randomUUID());
        mockTasks.setCreatedAt(LocalDateTime.now());
        mockTasks.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.save(any(Tasks.class))).thenReturn(mockTasks);

        TasksDto testTasksDto = taskService.createTask(creatTasksDto);

        assertNotNull(testTasksDto);
        assertNotNull(testTasksDto.getTasksId());
        assertEquals(mockTasks.getTitle(), testTasksDto.getTitle());
        assertEquals(mockTasks.getDescription(), testTasksDto.getDescription());
        assertNotNull(testTasksDto.getUserId());
        assertNotNull(testTasksDto.getStatus());
        assertNotNull(testTasksDto.getCreatedAt());
        assertNotNull(testTasksDto.getUpdatedAt());
        assertNotNull(testTasksDto.getIsActive());

        verify(taskRepository, times(1)).save(any(Tasks.class));
    }

    @Test
    @DisplayName("updateNewStatusTask: обновление задачи по id - новый Status, отправка в Kafka ")
    void updateNewStatusTask() {
        UUID taskId = UUID.randomUUID();

        CreatTasksDto mockUpdateTasks = this.creatTaskDto();
        mockUpdateTasks.setTasksId(taskId);
        mockUpdateTasks.setStatus(Status.IN_PROGRESS);

        Tasks mockOldTasks = this.creatTask();
        mockOldTasks.setTasksId(taskId);
        mockOldTasks.setStatus(Status.PENDING);

        Tasks mockSaveTasks = this.creatTask();
        mockSaveTasks.setTasksId(taskId);
        mockSaveTasks.setStatus(Status.IN_PROGRESS);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockOldTasks));
        when(taskRepository.save(any(Tasks.class))).thenReturn(mockSaveTasks);
        doNothing().when(kafkaTasksStatusProducer).sendToUpdateStatus(any(TasksDto.class));

        assertNotEquals(mockUpdateTasks.getStatus(), mockOldTasks.getStatus());

        TasksDto testTasksDto = taskService.updateTask(taskId, mockUpdateTasks);

        assertNotNull(testTasksDto);
        assertEquals(mockUpdateTasks.getStatus(), testTasksDto.getStatus());
        assertEquals(mockUpdateTasks.getTasksId(), testTasksDto.getTasksId());
        assertNotNull(testTasksDto.getUserId());
        assertNotNull(testTasksDto.getStatus());
        assertNotNull(testTasksDto.getCreatedAt());
        assertNotNull(testTasksDto.getUpdatedAt());
        assertNotNull(testTasksDto.getIsActive());
        verify(kafkaTasksStatusProducer).sendToUpdateStatus(any(TasksDto.class));

    }

    @Test
    @DisplayName("updateTaskWithoutStatusUpdate: обновление задачи по id - Status не меняется, в Kafka не отправляем ")
    void updateTaskWithoutStatusUpdate() {
        UUID taskId = UUID.randomUUID();

        CreatTasksDto mockUpdateTasks = this.creatTaskDto();
        mockUpdateTasks.setTasksId(taskId);
        mockUpdateTasks.setStatus(Status.PENDING);

        Tasks mockOldTasks = this.creatTask();
        mockOldTasks.setTasksId(taskId);
        mockOldTasks.setStatus(Status.PENDING);

        Tasks mockSaveTasks = this.creatTask();
        mockSaveTasks.setTasksId(taskId);
        mockSaveTasks.setStatus(Status.PENDING);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockOldTasks));
        when(taskRepository.save(any(Tasks.class))).thenReturn(mockSaveTasks);
        doNothing().when(kafkaTasksStatusProducer).sendToUpdateStatus(any(TasksDto.class));

        assertEquals(mockUpdateTasks.getStatus(), mockOldTasks.getStatus());

        TasksDto testTasksDto = taskService.updateTask(taskId, mockUpdateTasks);

        assertNotNull(testTasksDto);
        assertEquals(mockUpdateTasks.getStatus(), testTasksDto.getStatus());
        assertEquals(mockUpdateTasks.getTasksId(), testTasksDto.getTasksId());
        assertNotNull(testTasksDto.getUserId());
        assertNotNull(testTasksDto.getStatus());
        assertNotNull(testTasksDto.getCreatedAt());
        assertNotNull(testTasksDto.getUpdatedAt());
        assertNotNull(testTasksDto.getIsActive());
        verify(kafkaTasksStatusProducer, never()).sendToUpdateStatus(any(TasksDto.class));
    }

    @Test
    @DisplayName("deleteTask: удаление задачи по id")
    void deleteTask() {
        Tasks mockTasks = this.creatTask();
        when(taskRepository.findById(mockTasks.getTasksId())).thenReturn(Optional.of(mockTasks));
        taskService.deleteTask(mockTasks.getTasksId());

        verify(taskRepository).findById(mockTasks.getTasksId());
        verify(taskRepository).delete(mockTasks);
        verify(taskRepository, times(1)).delete(any(Tasks.class));
    }

    @Test
    @DisplayName("deleteTaskEntityNotFoundException: удаление задачи - неверный id")
    void deleteTaskEntityNotFoundException() {
        UUID wrongId = UUID.randomUUID();
        when(taskRepository.findById(wrongId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.deleteTask(wrongId));

        verify(taskRepository).findById(wrongId);
        verify(taskRepository, never()).delete(any());
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