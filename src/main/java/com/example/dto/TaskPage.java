package com.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

@Schema(name = "Page<TaskDto>", description = "Page of TaskDto")
public abstract class TaskPage implements Page<TaskDto> {

}