package com.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

import static com.example.util.DateTimeConstant.PATTERN_FOR_DATETIME;

@Data
@Schema(description = "Задача")
public class TaskDto {

    @Schema(description = "Идентификатор", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Имя задачи", example = "Сходить в кино")
    private String title;

    @Schema(description = "Описание задачи", example = "Летом начался новый фильм, надо сходить")
    private String description;

    @Schema(description = "Срок выполнения задачи", example = "2024-06-15 19:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_DATETIME)
    private LocalDateTime dueDate;

    @Schema(description = "Задача выполнена", example = "true")
    private Boolean completed;

}