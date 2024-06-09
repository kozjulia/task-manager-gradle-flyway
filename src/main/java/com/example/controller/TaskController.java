package com.example.controller;

import com.example.dto.TaskDto;
import com.example.dto.TaskPage;
import com.example.dto.NewTaskDto;
import com.example.dto.UpdateTaskDto;
import com.example.exception.ApiError;
import com.example.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import static com.example.swagger.ResponseExample.TASK_ERROR_400_EXAMPLE;
import static com.example.swagger.ResponseExample.TASK_ERROR_404_EXAMPLE;
import static com.example.util.DateTimeConstant.PATTERN_FOR_DATETIME;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Задачи", description = "Взаимодействие с задачами")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить задачи",
            description = "Позволяет возвратить информацию о задаче по id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDto.class))}),
            @ApiResponse(responseCode = "404", description = "The required object was not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = {@ExampleObject(value = TASK_ERROR_404_EXAMPLE)})})})
    /**
     * Получить информацию о задаче по её id.
     */
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable @Parameter(description = "Идентификатор задачи", required = true) Long id) {
        TaskDto taskDto = taskService.getTaskById(id);
        return ResponseEntity.ok().body(taskDto);
    }

    @GetMapping
    @Operation(
            summary = "Получить все задачи",
            description = "Позволяет возвратить информацию о всех задачах")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskPage.class))})})
    /**
     * Получить список всех задач.
     */
    public ResponseEntity<Page<TaskDto>> getAllTasks(
            @RequestParam(name = "Статус выполнения задачи", required = false)
            @Parameter(description = "Статус выполнения задачи") Boolean completed,
            @PastOrPresent @RequestParam(required = false) @Parameter(description = "дата с")
            @DateTimeFormat(pattern = PATTERN_FOR_DATETIME) LocalDateTime dateStart,
            @RequestParam(required = false) @Parameter(description = "дата до")
            @DateTimeFormat(pattern = PATTERN_FOR_DATETIME) LocalDateTime dateEnd,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
            @Parameter(description = "номер страницы") Integer from,
            @Positive @Max(500) @RequestParam(name = "size", defaultValue = "10")
            @Parameter(description = "размер страницы") Integer size
            ) {
        Page<TaskDto> taskDtos = taskService.getAllTasks(completed, dateStart, dateEnd, from, size);
        log.info("Получен список задач с completed = {}, dateStart = {}, dateEnd = {} from = {}, size = {}, " +
                        "количество = {}.", completed, dateStart, dateEnd, from, size, taskDtos.stream().count());
        return ResponseEntity.ok().body(taskDtos);
    }

    @PostMapping
    @Validated
    @Operation(
            summary = "Добавить задачу",
            description = "Позволяет добавить новую задачу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDto.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrectly made request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = {@ExampleObject(value = TASK_ERROR_400_EXAMPLE)})})})
    /**
     * Создать новую задачу.
     */
    public ResponseEntity<TaskDto> saveTask(@Valid @RequestBody NewTaskDto newTaskDto) {
        TaskDto taskDto = taskService.saveTask(newTaskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDto);
    }

    @PutMapping("/{id}")
    @Validated
    @Operation(
            summary = "Редактировать задачу",
            description = "Позволяет редактировать данные задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDto.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrectly made request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = {@ExampleObject(value = TASK_ERROR_400_EXAMPLE)})}),
            @ApiResponse(responseCode = "404", description = "The required object was not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = {@ExampleObject(value = TASK_ERROR_404_EXAMPLE)})})})
    /**
     * Обновить информацию о задаче.
     */
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable @Parameter(description = "Идентификатор задачи", required = true) Long id,
            @Valid @RequestBody UpdateTaskDto updateTaskDto) {
        TaskDto taskDto = taskService.updateTask(id, updateTaskDto);
        return ResponseEntity.ok(taskDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить задачу",
            description = "Позволяет удалить задачу по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content")})
    /**
     * Удалить задачу.
     */
    public void deleteTaskById(
            @PathVariable @Parameter(description = "Идентификатор задачи", required = true) Long id) {
        taskService.deleteTaskById(id);
    }

}