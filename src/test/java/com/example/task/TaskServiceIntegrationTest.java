package com.example.task;

import com.example.dto.TaskDto;
import com.example.dto.NewTaskDto;
import com.example.dto.UpdateTaskDto;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import com.example.service.TaskService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class TaskServiceIntegrationTest {

    private final EntityManager em;
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @Test
    @DisplayName("получена задача по ид, когда задача найдена, тогда она возвращается")
    void getTaskById_whenTaskFound_thenReturnedTask() {
        NewTaskDto taskDto = makeTaskDto("task1");
        Task task = taskMapper.toTaskFromNewTaskDto(taskDto);
        em.persist(task);
        Long taskId = task.getId();
        taskDto = makeTaskDto("task2");
        em.persist(taskMapper.toTaskFromNewTaskDto(taskDto));

        TaskDto resultTask = taskService.getTaskById(taskId);

        TypedQuery<Task> query = em.createQuery("Select c from Task c where c.id = :id", Task.class);
        TaskDto expectedTask = taskMapper.toTaskDto(query
                .setParameter("id", taskId)
                .getSingleResult());

        assertThat(resultTask, equalTo(expectedTask));
    }

    @Test
    @DisplayName("получены все задачи, когда вызваны, то получен непустой список")
    void getAllTasks_whenInvoked_thenReturnedTasksPage() {
        NewTaskDto taskDto = makeTaskDto("task1");
        em.persist(taskMapper.toTaskFromNewTaskDto(taskDto));
        taskDto = makeTaskDto("task2");
        em.persist(taskMapper.toTaskFromNewTaskDto(taskDto));
        taskDto = makeTaskDto("task3");
        em.persist(taskMapper.toTaskFromNewTaskDto(taskDto));

        Page<TaskDto> tasks = taskService.getAllTasks(false, null, null, 0, 10);

        TypedQuery<Task> query1 = em.createQuery("Select c from Task c where c.title = :title", Task.class);
        TaskDto task1 = taskMapper.toTaskDto(query1
                .setParameter("title", "task1")
                .getSingleResult());

        TypedQuery<Task> query2 = em.createQuery("Select c from Task c where c.title = :title", Task.class);
        TaskDto task2 = taskMapper.toTaskDto(query2
                .setParameter("title", "task2")
                .getSingleResult());

        TypedQuery<Task> query3 = em.createQuery("Select c from Task c where c.title = :title", Task.class);
        TaskDto task3 = taskMapper.toTaskDto(query3
                .setParameter("title", "task3")
                .getSingleResult());

        assertThat(tasks, hasItem(task1));
        assertThat(tasks, hasItem(task2));
        assertThat(tasks, hasItem(task3));
        assertThat(tasks, allOf(hasItem(task1), hasItem(task2), hasItem(task3)));
    }

    @Test
    @DisplayName("сохранена задача, когда задача валидна, тогда она сохраняется")
    void saveTask_whenTaskValid_thenSavedTask() {
        NewTaskDto taskDto = makeTaskDto("task1");
        taskService.saveTask(taskDto);

        TypedQuery<Task> query = em.createQuery("Select c from Task c where c.title = :title", Task.class);
        Task task = query
                .setParameter("title", taskDto.getTitle())
                .getSingleResult();

        assertThat(task.getId(), notNullValue());
        assertThat(task.getTitle(), equalTo(taskDto.getTitle()));
    }

    @Test
    @DisplayName("обновлена задача, когда задача валидна, тогда она обновляется")
    void updateTask_whenTaskFound_thenUpdatedTask() {
        NewTaskDto taskDto = makeTaskDto("task1");
        Task task = taskMapper.toTaskFromNewTaskDto(taskDto);
        em.persist(task);
        Long taskId = task.getId();

        UpdateTaskDto updateTaskDto = new UpdateTaskDto();
        updateTaskDto.setTitle("task2");
        taskService.updateTask(taskId, updateTaskDto);

        TypedQuery<Task> query = em.createQuery("Select c from Task c where c.title = :title", Task.class);
        Task expectedTask = query
                .setParameter("title", taskDto.getTitle())
                .getSingleResult();

        assertThat(task, equalTo(expectedTask));
    }

    @Test
    @DisplayName("удалена задача, когда вызвано, тогда она удаляется")
    void deleteTaskById_whenInvoked_thenDeletedTask() {
        NewTaskDto taskDto = makeTaskDto("task1");
        Task task = taskMapper.toTaskFromNewTaskDto(taskDto);
        em.persist(task);
        Long taskId = task.getId();

        taskService.deleteTaskById(taskId);

        TypedQuery<Task> query = em.createQuery("Select c from Task c where c.id = :id", Task.class);
        Task expectedTask = query
                .setParameter("id", taskId)
                .getResultList()
                .stream().findFirst().orElse(null);

        assertThat(expectedTask, nullValue());
    }

    private NewTaskDto makeTaskDto(String title) {
        NewTaskDto taskDto = new NewTaskDto();
        taskDto.setTitle(title);
        taskDto.setDueDate(LocalDateTime.now().plusDays(7));

        return taskDto;
    }

}