package com.example.model;

import com.example.mapper.TaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

@Slf4j
public class TaskListener {

    private final TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);

    @PostLoad
    private void afterLoad(Task task) {
        log.info("Получена задача, id = {}: {}.", task.getId(), taskMapper.toTaskDto(task));
    }

    @PostPersist
    private void afterPersist(Task task) {
        log.info("Добавлена новая задача: {}.", taskMapper.toTaskDto(task));
    }

    @PostUpdate
    private void afterUpdate(Task task) {
        log.info("Обновлена задача с id = {}: {}.", task.getId(), taskMapper.toTaskDto(task));
    }

    @PostRemove
    private void afterRemove(Task task) {
        log.info("Удалена задача с id = {}.", task.getId());
    }

}