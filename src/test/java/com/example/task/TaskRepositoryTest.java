package com.example.task;

import com.example.model.Task;
import com.example.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private final Task task1 = new Task();
    private final Task task2 = new Task();
    private final Task task3 = new Task();

    @BeforeEach
    public void addTasks() {
        task1.setTitle("Задача 1");
        task1.setDescription("Описание 1");
        task1.setDueDate(LocalDateTime.now().plusDays(1));
        task1.setCompleted(true);
        taskRepository.save(task1);

        task2.setTitle("Задача 2");
        task2.setDescription("Описание 2");
        task2.setDueDate(LocalDateTime.now().plusDays(2));
        task2.setCompleted(false);
        taskRepository.save(task2);

        task3.setTitle("Задача 3");
        task3.setDescription("Описание 3");
        task3.setDueDate(LocalDateTime.now().plusDays(3));
        task3.setCompleted(false);
        taskRepository.save(task3);
    }

    @AfterEach
    public void deleteItems() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("получен список задач по доступности, когда вызвано, то получен список")
    void findAllByParams_whenInvoked_thenReturnedTasksCollectionInPage() {
        Pageable page = PageRequest.of(0, 10);
        Page<Task> tasksPage = taskRepository.findAllByParams(
                false, LocalDateTime.now(), LocalDateTime.now().plusYears(100), page);
        List<Task> tasks = tasksPage.getContent();

        assertThat(2, equalTo(tasks.size()));
        assertThat(task2, equalTo(tasks.get(0)));
        assertThat(task3, equalTo(tasks.get(1)));
    }

}