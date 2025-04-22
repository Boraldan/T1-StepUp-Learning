package ru.boraldan.aop.taskaop.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.boraldan.aop.taskaop.domen.Status;
import ru.boraldan.aop.taskaop.domen.Tasks;
import ru.boraldan.aop.taskaop.domen.dto.CreatTasksDto;
import ru.boraldan.aop.taskaop.domen.dto.TasksDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {

    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskMapper = Mappers.getMapper(TaskMapper.class);
    }

    @Test
    @DisplayName("toDtoFromTasks: маппинг из Tasks в TasksDto")
    void testToDtoFromTasks() {
        Tasks task = creatTask(1);

        TasksDto dto = taskMapper.toDtoFromTasks(task);

        assertNotNull(task);
        assertNotNull(dto);
        assertEquals(task.getTitle(), dto.getTitle());
        assertEquals(task.getDescription(), dto.getDescription());
        assertEquals(task.getStatus(), dto.getStatus());
        assertEquals(task.getUserId(), dto.getUserId());
        assertEquals(task.getCreatedAt(), dto.getCreatedAt());
        assertEquals(task.getUpdatedAt(), dto.getUpdatedAt());
        assertEquals(dto.getIsActive(), task.getIsActive());
    }

    @Test
    @DisplayName("creatTasksFromDto: маппинг из CreatTasksDto в Tasks")
    void testCreateTasksFromDto() {
        CreatTasksDto dto = creatTaskDto(1);

        Tasks task = taskMapper.creatTasksFromDto(dto);

        assertNotNull(dto);
        assertNotNull(task);
        assertEquals(dto.getTitle(), task.getTitle());
        assertEquals(dto.getDescription(), task.getDescription());
        assertEquals(task.getUserId(), dto.getUserId());
        assertEquals(dto.getStatus(), task.getStatus());
        assertEquals(dto.getIsActive(), task.getIsActive());
        assertNull(task.getTasksId());
    }

    @Test
    @DisplayName("updateTasksFromDto: обновление Tasks из CreatTasksDto")
    void testUpdateTasksFromDto() {
        UUID taskId = UUID.randomUUID();
        Tasks task = creatTask(1);
        task.setTasksId(taskId);
        task.setStatus(Status.PENDING);

        CreatTasksDto dto = creatTaskDto(1);
        dto.setTasksId(null);
        dto.setStatus(Status.IN_PROGRESS);

        Tasks updated = taskMapper.updateTasksFromDto(dto, task);

        assertEquals(dto.getTitle(), updated.getTitle());
        assertEquals(dto.getDescription(), updated.getDescription());
        assertEquals(dto.getStatus(), updated.getStatus());
        assertEquals(task.getTasksId(), updated.getTasksId());
        assertNotNull(updated.getTasksId());
    }

    @Test
    @DisplayName("toTasksDtoList - список Tasks в список TasksDto")
    void testToTasksDtoList() {
        Tasks task = creatTask(1);
        Tasks task2 = creatTask(2);

        List<TasksDto> result = taskMapper.toTasksDtoList(List.of(task, task2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(task.getTasksId(), result.get(0).getTasksId());
        assertEquals(task.getTitle(), result.get(0).getTitle());
        assertEquals(task.getDescription(), result.get(0).getDescription());
        assertEquals(task.getStatus(), result.get(0).getStatus());
        assertEquals(task.getUserId(), result.get(0).getUserId());
        assertEquals(task.getIsActive(), result.get(0).getIsActive());
        assertNotEquals(task2.getTasksId(), result.get(0).getTasksId());
        assertNotEquals(task2.getTitle(), result.get(0).getTitle());
        assertEquals(task2.getTasksId(), result.get(1).getTasksId());
        assertEquals(task2.getTitle(), result.get(1).getTitle());
        assertEquals(task2.getDescription(), result.get(1).getDescription());
        assertEquals(task2.getStatus(), result.get(1).getStatus());
        assertEquals(task2.getUserId(), result.get(1).getUserId());
        assertEquals(task2.getIsActive(), result.get(1).getIsActive());
    }

    @Test
    @DisplayName("toTasksDtoPage - Page<Tasks> в Page<TasksDto>")
    void testToTasksDtoPage() {
        Tasks task = creatTask(1);
        Tasks task2 = creatTask(2);

        Page<Tasks> tasksPage = new PageImpl<>(List.of(task, task2), PageRequest.of(0, 10), 1);
        Page<TasksDto> dtoPage = taskMapper.toTasksDtoPage(tasksPage);

        assertNotNull(dtoPage);
        assertEquals(2, dtoPage.getTotalElements());
        assertEquals(task.getTasksId(), dtoPage.getContent().get(0).getTasksId());
        assertEquals(task.getTitle(), dtoPage.getContent().get(0).getTitle());
        assertEquals(task.getDescription(), dtoPage.getContent().get(0).getDescription());
        assertEquals(task.getStatus(), dtoPage.getContent().get(0).getStatus());
        assertEquals(task.getUserId(), dtoPage.getContent().get(0).getUserId());
        assertEquals(task.getIsActive(), dtoPage.getContent().get(0).getIsActive());
        assertNotEquals(task2.getTasksId(), dtoPage.getContent().get(0).getTasksId());
        assertNotEquals(task2.getTitle(), dtoPage.getContent().get(0).getTitle());
        assertEquals(task2.getTasksId(), dtoPage.getContent().get(1).getTasksId());
        assertEquals(task2.getTitle(), dtoPage.getContent().get(1).getTitle());
        assertEquals(task2.getDescription(), dtoPage.getContent().get(1).getDescription());
        assertEquals(task2.getStatus(), dtoPage.getContent().get(1).getStatus());
        assertEquals(task2.getUserId(), dtoPage.getContent().get(1).getUserId());
        assertEquals(task2.getIsActive(), dtoPage.getContent().get(1).getIsActive());
    }

    private Tasks creatTask(int count) {
        return TaskBuilder.create()
                .setTaskId(UUID.randomUUID())
                .setTitle("MockTask %s".formatted(count))
                .setDescription("Описание задания %s".formatted(count))
                .setUserId(UUID.randomUUID())
                .setStatus(Status.PENDING)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setIsActive(true)
                .build();
    }

    private CreatTasksDto creatTaskDto(int count) {
        CreatTasksDto creatTasksDto = new CreatTasksDto();
        creatTasksDto.setTasksId(UUID.randomUUID());
        creatTasksDto.setTitle("MockTaskDto %s".formatted(count));
        creatTasksDto.setDescription("MockTaskDto %s".formatted(count));
        creatTasksDto.setUserId(UUID.randomUUID());
        creatTasksDto.setStatus(Status.PENDING);
        creatTasksDto.setIsActive(true);
        return creatTasksDto;
    }

}