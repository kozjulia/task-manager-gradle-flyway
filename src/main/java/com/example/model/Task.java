package com.example.model;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "tasks")
@NoArgsConstructor
@Getter
@Setter
@ToString
@EntityListeners(TaskListener.class)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "SEQ_TASK", allocationSize = 10)
    @Column(name = "tasks_id")
    private Long id; // Идентификатор задачи

    @Column(name = "tasks_title", nullable = false)
    private String title; // Имя задачи

    @Column(name = "tasks_description")
    private String description; // Описание задачи

    @Column(name = "tasks_due_date", nullable = false)
    private LocalDateTime dueDate; // Срок выполнения задачи

    @Column(name = "tasks_completed")
    private Boolean completed; // Статус выполнения задачи

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title) && Objects.equals(description, task.description) && Objects.equals(dueDate, task.dueDate) && Objects.equals(completed, task.completed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, dueDate, completed);
    }

}