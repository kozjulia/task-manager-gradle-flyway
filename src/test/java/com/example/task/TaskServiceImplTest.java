package com.example.task;

import com.example.dto.TaskDto;
import com.example.dto.NewTaskDto;
import com.example.dto.UpdateTaskDto;
import com.example.exception.NotFoundException;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import com.example.repository.TaskRepository;
import com.example.service.TaskServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskMapper taskMapper;

    private final TaskMapper taskMapperBean = Mappers.getMapper(TaskMapper.class);

    @Test
    @DisplayName("получена задача по ид, когда задача найдена, тогда она возвращается")
    void getTaskById_whenTaskFound_thenReturnedTask() {
        Long taskId = 0L;
        Task expectedTask = new Task();
        expectedTask.setId(taskId);
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskMapperBean.toTaskDto(expectedTask));
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(expectedTask));

        TaskDto actualTask = taskService.getTaskById(taskId);

        assertThat(taskMapper.toTaskDto(expectedTask), equalTo(actualTask));
        InOrder inOrder = inOrder(taskRepository, taskMapper);
        inOrder.verify(taskRepository, times(1)).findById(anyLong());
        inOrder.verify(taskMapper, times(2)).toTaskDto(any(Task.class));
    }

    @Test
    @DisplayName("получена задача по ид, когда задача не найдена, " +
            "тогда выбрасывается исключение")
    void getTaskById_whenTaskNotFound_thenExceptionThrown() {
        Long taskId = 0L;
        when(taskRepository.findById(anyLong())).
                thenThrow(new NotFoundException("Задача с идентификатором 0 не найдена."));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> taskService.getTaskById(taskId));

        assertThat("Задача с идентификатором 0 не найдена.", equalTo(exception.getMessage()));
        verify(taskRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("получены все задачи, когда вызваны, то получен непустой список")
    void getAllTasks_whenInvoked_thenReturnedTasksCollectionInPage() {
        Task task = new Task();
        List<Task> expectedTasks = List.of(task, task);
        Page<Task> expectedTasksPage = new PageImpl<>(
                expectedTasks, PageRequest.of(0, 1), 2);
        when(taskRepository.findAllByParams(anyBoolean(), any(), any(), any(Pageable.class))).
                thenReturn(expectedTasksPage);
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskMapperBean.toTaskDto(task));

        Page<TaskDto> actualTasks = taskService.getAllTasks(true, null, null, 0, 10);

        assertThat(expectedTasksPage.map(taskMapperBean::toTaskDto), equalTo(actualTasks));
        InOrder inOrder = inOrder(taskRepository, taskMapper);
        inOrder.verify(taskRepository, times(1)).
                findAllByParams(anyBoolean(), any(), any(), any(Pageable.class));
        inOrder.verify(taskMapper, atLeast(1)).toTaskDto(any(Task.class));
    }

    @Test
    @DisplayName("сохранена задача, когда задача валидна, тогда она сохраняется")
    void saveTask_whenTaskValid_thenSavedTask() {
        NewTaskDto taskToSave = new NewTaskDto();
        taskToSave.setTitle("1");
        Task task = taskMapperBean.toTaskFromNewTaskDto(taskToSave);
        when(taskMapper.toTaskFromNewTaskDto(any(NewTaskDto.class))).
                thenReturn(taskMapperBean.toTaskFromNewTaskDto(taskToSave));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskMapperBean.toTaskDto(task));

        TaskDto actualTask = taskService.saveTask(taskToSave);

        assertThat(taskToSave.getTitle(), equalTo(actualTask.getTitle()));
        InOrder inOrder = inOrder(taskMapper, taskRepository, taskMapper);
        inOrder.verify(taskMapper, times(1)).
                toTaskFromNewTaskDto(any(NewTaskDto.class));
        inOrder.verify(taskRepository, times(1)).save(any(Task.class));
        inOrder.verify(taskMapper, times(1)).toTaskDto(any(Task.class));
    }

    @Test
    @DisplayName("обновлена задача, когда задача валидна, тогда она обновляется")
    void updateTask_whenTaskFound_thenUpdatedTask() {
        Long taskId = 0L;
        Task oldTask = new Task();
        oldTask.setTitle("1");
        oldTask.setDescription("1");
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(oldTask));

        UpdateTaskDto updateTask = new UpdateTaskDto();
        updateTask.setTitle("2");
        Task newTask = taskMapperBean.toTaskFromUpdateTaskDto(oldTask, updateTask);
        when(taskMapper.toTaskFromUpdateTaskDto(any(Task.class), any(UpdateTaskDto.class))).
                thenReturn(newTask);
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskMapperBean.toTaskDto(newTask));

        TaskDto actualTask = taskService.updateTask(taskId, updateTask);

        assertThat(newTask.getTitle(), equalTo(actualTask.getTitle()));
        assertThat(newTask.getDescription(), equalTo(actualTask.getDescription()));
        InOrder inOrder = inOrder(taskRepository, taskMapper, taskMapper);
        inOrder.verify(taskRepository, times(1)).findById(anyLong());
        inOrder.verify(taskMapper, times(1)).
                toTaskFromUpdateTaskDto(any(Task.class), any(UpdateTaskDto.class));
        inOrder.verify(taskMapper, times(1)).toTaskDto(any(Task.class));
    }

    @Test
    @DisplayName("обновлена задача, когда задача не найдена, тогда выбрасывается исключение")
    void updateTask_whenTaskNotFound_thenExceptionThrown() {
        Long taskId = 0L;
        UpdateTaskDto taskDto = new UpdateTaskDto();
        when(taskRepository.findById(anyLong())).
                thenThrow(new NotFoundException("Задача с идентификатором 0 не найдена."));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> taskService.updateTask(taskId, taskDto));

        assertThat("Задача с идентификатором 0 не найдена.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(taskRepository, taskMapper);
        inOrder.verify(taskRepository, times(1)).findById(anyLong());
        inOrder.verify(taskMapper, never()).toTaskDto(any(Task.class));
    }

    @Test
    @DisplayName("удалена задача, когда вызвано, тогда она удаляется")
    void deleteTaskById_whenInvoked_thenDeletedTask() {
        Long taskId = 0L;

        taskService.deleteTaskById(taskId);

        verify(taskRepository, times(1)).deleteById(taskId);
    }

}