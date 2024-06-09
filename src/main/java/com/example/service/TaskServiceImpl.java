package com.example.service;

import com.example.dto.TaskDto;
import com.example.dto.NewTaskDto;
import com.example.dto.UpdateTaskDto;
import com.example.exception.NotFoundException;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import com.example.repository.TaskRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {


    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Transactional(readOnly = true)
    @Override
    public TaskDto getTaskById(Long taskId) {
        return taskMapper.toTaskDto(returnTask(taskId));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TaskDto> getAllTasks(
            Boolean completed, LocalDateTime dateStart, LocalDateTime dateEnd, Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));
        if (dateStart == null) dateStart = LocalDateTime.now();
        if (dateEnd == null) dateEnd = LocalDateTime.now().plusYears(100);

        Page<Task> tasksPage = taskRepository.findAllByParams(completed, dateStart, dateEnd, page);
        return tasksPage.map(taskMapper::toTaskDto);
    }

    @Override
    public TaskDto saveTask(NewTaskDto newTaskDto) {
        Task task = taskRepository.save(taskMapper.toTaskFromNewTaskDto(newTaskDto));
        return taskMapper.toTaskDto(task);
    }

    @Override
    public TaskDto updateTask(Long taskId, UpdateTaskDto updateTaskDto) {
        Task task = returnTask(taskId);
        task = taskMapper.toTaskFromUpdateTaskDto(task, updateTaskDto);
        return taskMapper.toTaskDto(task);
    }

    @Override
    public void deleteTaskById(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    private Task returnTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Задача с id = " + taskId + " не найдена."));
    }

}