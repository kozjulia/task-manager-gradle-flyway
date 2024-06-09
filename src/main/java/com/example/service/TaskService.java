package com.example.service;

import com.example.dto.TaskDto;
import com.example.dto.NewTaskDto;
import com.example.dto.UpdateTaskDto;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;

public interface TaskService {

    TaskDto getTaskById(Long taskId);

    Page<TaskDto> getAllTasks(Boolean completed, LocalDateTime dateStart, LocalDateTime dateEnd, Integer from, Integer size);

    TaskDto saveTask(NewTaskDto newTaskDto);

    TaskDto updateTask(Long taskId, UpdateTaskDto updateTaskDto);

    void deleteTaskById(Long taskId);

}