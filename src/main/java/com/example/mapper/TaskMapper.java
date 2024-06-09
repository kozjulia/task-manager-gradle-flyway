package com.example.mapper;

import com.example.dto.TaskDto;
import com.example.dto.NewTaskDto;
import com.example.dto.UpdateTaskDto;
import com.example.model.Task;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDto toTaskDto(Task task);

    @Mapping(target = "id", ignore = true)
    Task toTaskFromNewTaskDto(NewTaskDto newTaskDto);

    @Mapping(target = "id", source = "task.id")
    @Mapping(target = "title", source = "updateTaskDto.title")
    @Mapping(target = "description", source = "updateTaskDto.description")
    @Mapping(target = "dueDate", source = "updateTaskDto.dueDate")
    @Mapping(target = "completed", source = "updateTaskDto.completed")
    Task toTaskFromUpdateTaskDto(Task task, UpdateTaskDto updateTaskDto);

    List<TaskDto> convertTaskListToTaskDtoList(List<Task> list);

}