package com.example.task;

import com.example.controller.TaskController;
import com.example.dto.TaskDto;
import com.example.dto.NewTaskDto;
import com.example.dto.UpdateTaskDto;
import com.example.exception.ApiError;
import com.example.exception.NotFoundException;
import com.example.exception.NotSaveException;
import com.example.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TaskService taskService;

    private final TaskDto taskDto = new TaskDto();
    private final TaskDto taskDto2 = new TaskDto();

    @BeforeEach
    public void addTasks() {
        taskDto.setId(1L);
        taskDto.setTitle("Title 1");
        taskDto.setDescription("Description 1");
        taskDto.setDueDate(LocalDateTime.now());
        taskDto.setCompleted(false);

        taskDto2.setId(2L);
        taskDto.setTitle("Title 2");
        taskDto.setDescription("Description 2");
        taskDto.setDueDate(LocalDateTime.now().plusDays(5L));
        taskDto.setCompleted(false);
    }

    @SneakyThrows
    @Test
    @DisplayName("получена задача по ид, когда задача найдена, " +
            "то ответ статус ок, и она возвращается")
    void getTaskById_whenTaskFound_thenReturnedTask() {
        Long taskId = 0L;
        when(taskService.getTaskById(anyLong())).thenReturn(taskDto);

        String result = mockMvc.perform(get("/tasks/{taskId}", taskId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(taskDto), equalTo(result));
        verify(taskService, times(1)).getTaskById(taskId);
    }

    @SneakyThrows
    @Test
    @DisplayName("получена задача по ид, когда задача не найдена, " +
            "то ответ статус не найден")
    void getTaskById_whenTaskNotFound_thenReturnedNotFound() {
        Long taskId = 0L;
        NotFoundException exception = new NotFoundException("Задача с идентификатором 0 не найдена.");
        when(taskService.getTaskById(anyLong())).thenThrow(exception);

        String result = mockMvc.perform(get("/tasks/{taskId}", taskId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ApiError apiError = objectMapper.readValue(result, ApiError.class);

        assertThat("Задача с идентификатором 0 не найдена.", equalTo(apiError.getMessage()));
        verify(taskService, times(1)).getTaskById(anyLong());
    }

    @SneakyThrows
    @Test
    @DisplayName("получены все задачи, когда вызваны, то ответ статус ок и список задач")
    void getAllTasks_whenInvoked_thenResponseStatusOkWithTasksCollectionInBody() {
        List<TaskDto> tasks = List.of(taskDto, taskDto2);
        Page<TaskDto> tasksPage = new PageImpl<>(tasks, PageRequest.of(0, 10), 2);
        when(taskService.getAllTasks(any(), any(), any(), anyInt(), anyInt())).thenReturn(tasksPage);

        String result = mockMvc.perform(get("/tasks")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(tasksPage), equalTo(result));
        verify(taskService, times(1)).getAllTasks(any(), any(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    @DisplayName("сохранена задача, когда задача валидна, " +
            "то ответ статус ок, и она сохраняется")
    void saveTask_whenTaskValid_thenSavedTask() {
        when(taskService.saveTask(any(NewTaskDto.class))).thenReturn(taskDto);

        String result = mockMvc.perform(post("/tasks")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(taskDto), equalTo(result));
        verify(taskService, times(1)).saveTask(any(NewTaskDto.class));
    }

    @SneakyThrows
    @Test
    @DisplayName("сохранена задача, когда задача валидна, " +
            "то ответ статус конфликт, и она не сохраняется")
    void saveTask_whenTaskNotSaves_thenReturnedConflict() {
        NotSaveException exception = new NotSaveException("Задача не была создана.");
        when(taskService.saveTask(any(NewTaskDto.class))).thenThrow(exception);

        String result = mockMvc.perform(post("/tasks")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ApiError apiError = objectMapper.readValue(result, ApiError.class);

        assertThat("Задача не была создана.", equalTo(apiError.getMessage()));
        verify(taskService, times(1)).saveTask(any(NewTaskDto.class));
    }

    @SneakyThrows
    @Test
    @DisplayName("сохранена задача, когда задача не валидна, " +
            "то ответ статус бед реквест, и она не сохраняется")
    void saveTask_whenTaskNotValid_thenReturnedError() {
        NewTaskDto newTaskDto = new NewTaskDto();
        newTaskDto.setTitle("1");
        newTaskDto.setDueDate(LocalDateTime.now().plusDays(5));
        newTaskDto.setCompleted(true);

        String result = mockMvc.perform(post("/tasks")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTaskDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ApiError apiError = objectMapper.readValue(result, ApiError.class);

        assertThat("Field: title. Error: Ошибка! Имя задачи может содержать минимум 2, " +
                "максимум 250 символов. Value: 1 ", equalTo(apiError.getMessage()));
        verify(taskService, never()).saveTask(any(NewTaskDto.class));
    }

    @SneakyThrows
    @Test
    @DisplayName("обновлена задача, когда задача валидна, " +
            "то ответ статус ок, и она обновляется")
    void updateTask_whenTaskValid_thenUpdatedTask() {
        Long taskId = 0L;
        when(taskService.updateTask(anyLong(), any(UpdateTaskDto.class))).thenReturn(taskDto2);

        String result = mockMvc.perform(put("/tasks/{id}", taskId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(taskDto2), equalTo(result));
        verify(taskService, times(1)).updateTask(anyLong(), any(UpdateTaskDto.class));
    }

    @SneakyThrows
    @Test
    @DisplayName("обновлена задача, когда задача не найдена, " +
            "то ответ статус конфликт, и она не обновляется")
    void updateTask_whenTaskNotFound_thenReturnedConflict() {
        Long taskId = 0L;
        NotFoundException exception = new NotFoundException("Задача не была обновлена.");
        when(taskService.updateTask(anyLong(), any(UpdateTaskDto.class))).thenThrow(exception);

        String result = mockMvc.perform(put("/tasks/{id}", taskId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto2)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ApiError apiError = objectMapper.readValue(result, ApiError.class);

        assertThat("Задача не была обновлена.", equalTo(apiError.getMessage()));
        verify(taskService, times(1)).updateTask(anyLong(), any(UpdateTaskDto.class));
    }

    @SneakyThrows
    @Test
    @DisplayName("удалена задача, когда вызвано, то ответ статус нет контента")
    void deleteTaskById_whenInvoked_thenDeletedTask() {
        Long taskId = 0L;

        String result = mockMvc.perform(delete("/tasks/{taskId}", taskId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(result, blankString());
        verify(taskService, times(1)).deleteTaskById(taskId);
    }

}