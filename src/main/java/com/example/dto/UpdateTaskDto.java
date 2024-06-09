package com.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import static com.example.util.DateTimeConstant.PATTERN_FOR_DATETIME;

@Data
@Schema(description = "Обновленная задача")
public class UpdateTaskDto {

    @Positive(message = "Ошибка! Идентификатор задачи должен быть положительным.")
    @Schema(description = "Идентификатор", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Size(min = 2, max = 250, message =
            "Ошибка! Имя задачи может содержать минимум 2, максимум 250 символов.")
    @Schema(description = "Имя задачи", example = "Сходить в кино")
    private String title;

    @Size(max = 1000, message =
            "Ошибка! Описание задачи может содержать максимум 1000 символов.")
    @Schema(description = "Описание задачи", example = "Сходить в кино")
    private String description;

    @Schema(description = "Срок выполнения задачи", example = "2024-06-15 19:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_DATETIME)
    private LocalDateTime dueDate;

    @Schema(description = "Задача выполнена", example = "true")
    private Boolean completed;

}