package com.example.task;

import com.example.dto.TaskDto;
import com.example.dto.NewTaskDto;
import com.example.dto.UpdateTaskDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static com.example.util.DateTimeConstant.FORMATTER_FOR_DATETIME;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class TaskDtoTest {

    @Autowired
    private JacksonTester<TaskDto> json;

    @Autowired
    private JacksonTester<NewTaskDto> jsonNew;

    @Autowired
    private JacksonTester<UpdateTaskDto> jsonUpdate;

    @Test
    @DisplayName("получена ДТО задачи, когда вызвана сериализация, " +
            "то получена сериализованная задача")
    void testTaskDto() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Title 1");
        taskDto.setDescription("Description 1");
        taskDto.setDueDate(LocalDateTime.now());
        taskDto.setCompleted(false);

        JsonContent<TaskDto> result = json.write(taskDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.title");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.dueDate");
        assertThat(result).hasJsonPath("$.completed");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(taskDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo(taskDto.getTitle());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(taskDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.dueDate").
                isEqualTo(taskDto.getDueDate().format(FORMATTER_FOR_DATETIME));
        assertThat(result).extractingJsonPathBooleanValue("$.completed")
                .isEqualTo(taskDto.getCompleted().booleanValue());
    }

    @Test
    @DisplayName("получена New ДТО задачи, когда вызвана сериализация, " +
            "то получена сериализованная задача")
    void testNewTaskDto() throws Exception {
        NewTaskDto taskDto = new NewTaskDto();
        taskDto.setTitle("Title 1");
        taskDto.setDescription("Description 1");
        taskDto.setDueDate(LocalDateTime.now());
        taskDto.setCompleted(false);

        JsonContent<NewTaskDto> result = jsonNew.write(taskDto);

        assertThat(result).hasJsonPath("$.title");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.dueDate");
        assertThat(result).hasJsonPath("$.completed");
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo(taskDto.getTitle());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(taskDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.dueDate").
                isEqualTo(taskDto.getDueDate().format(FORMATTER_FOR_DATETIME));
        assertThat(result).extractingJsonPathBooleanValue("$.completed")
                .isEqualTo(taskDto.getCompleted().booleanValue());
    }

    @Test
    @DisplayName("получена Update ДТО задачи, когда вызвана сериализация, " +
            "то получена сериализованная задача")
    void testUpdateTaskDto() throws Exception {
        UpdateTaskDto taskDto = new UpdateTaskDto();
        taskDto.setTitle("Title 1");
        taskDto.setDescription("Description 1");
        taskDto.setDueDate(LocalDateTime.now());
        taskDto.setCompleted(false);

        JsonContent<UpdateTaskDto> result = jsonUpdate.write(taskDto);

        assertThat(result).hasJsonPath("$.title");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.dueDate");
        assertThat(result).hasJsonPath("$.completed");
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo(taskDto.getTitle());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(taskDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.dueDate").
                isEqualTo(taskDto.getDueDate().format(FORMATTER_FOR_DATETIME));
        assertThat(result).extractingJsonPathBooleanValue("$.completed")
                .isEqualTo(taskDto.getCompleted().booleanValue());
    }

}