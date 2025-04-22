package ru.boraldan.aop.taskaop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.boraldan.aop.taskaop.domen.Status;
import ru.boraldan.aop.taskaop.domen.Tasks;
import ru.boraldan.aop.taskaop.domen.dto.CreatTasksDto;
import ru.boraldan.aop.taskaop.repository.TaskRepository;
import ru.boraldan.aop.taskaop.container.TaskTestContainers;
import ru.boraldan.aop.taskaop.tool.TaskBuilder;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class TaskControllerTest extends TaskTestContainers {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void getTasks() throws Exception {
        for (int i = 1; i <= 15; i++) {
            Tasks task = TaskBuilder.create()
                    .setTitle("Задача " + i)
                    .setDescription("Описание " + i)
                    .setUserId(UUID.randomUUID())
                    .setStatus(Status.PENDING)
                    .setIsActive(true)
                    .build();
            taskRepository.save(task);
        }

        mockMvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void createTask() throws Exception {
        CreatTasksDto task = new CreatTasksDto();
        task.setTitle("Тестовая задача");
        task.setDescription("Описание");
        task.setUserId(UUID.randomUUID());
        task.setStatus(Status.PENDING);
        task.setIsActive(true);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.userId").value(task.getUserId().toString()))
                .andExpect(jsonPath("$.status").value(task.getStatus().toString()))
                .andExpect(jsonPath("$.isActive").value(task.getIsActive()));
    }

    @Test
    void getTaskById() throws Exception {
        Tasks task = TaskBuilder.create()
                .setTitle("Найти по id")
                .setDescription("desc")
                .setUserId(UUID.randomUUID())
                .setStatus(Status.PENDING)
                .setIsActive(true)
                .build();

        task = taskRepository.save(task);

        mockMvc.perform(get("/tasks/" + task.getTasksId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.userId").value(task.getUserId().toString()))
                .andExpect(jsonPath("$.status").value(task.getStatus().toString()))
                .andExpect(jsonPath("$.isActive").value(task.getIsActive()));

        mockMvc.perform(get("/tasks/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask() throws Exception {
        Tasks task = TaskBuilder.create()
                .setTitle("Старое название")
                .setDescription("старенькое")
                .setUserId(UUID.randomUUID())
                .setStatus(Status.PENDING)
                .setIsActive(true)
                .build();
        task = taskRepository.save(task);

        CreatTasksDto updateDto = new CreatTasksDto();
        updateDto.setTitle("Новое название");
        updateDto.setDescription("новенькое");
        updateDto.setUserId(task.getUserId());
        updateDto.setStatus(Status.COMPLETED);
        updateDto.setIsActive(true);

        mockMvc.perform(put("/tasks/" + task.getTasksId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateDto.getTitle()))
                .andExpect(jsonPath("$.status").value(updateDto.getStatus().toString()));

        mockMvc.perform(put("/tasks/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask() throws Exception {
        Tasks task = TaskBuilder.create()
                .setTitle("Удалить задание")
                .setDescription("удаление")
                .setUserId(UUID.randomUUID())
                .setStatus(Status.PENDING)
                .setIsActive(true)
                .build();
        task = taskRepository.save(task);

        mockMvc.perform(delete("/tasks/" + task.getTasksId()))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(taskRepository.findById(task.getTasksId()).isPresent());

        mockMvc.perform(delete("/tasks/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());


    }
}