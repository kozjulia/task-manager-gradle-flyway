package com.example.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import static com.example.util.DateTimeConstant.PATTERN_FOR_DATETIME;

@Getter
@Setter
@ToString
@Schema(description = "Ошибка")
public class ApiError {

    @Schema(description = "Код статуса HTTP-ответа")
    private HttpStatus status;

    @Schema(description = "Общее описание причины ошибки")
    private String reason;

    @Schema(description = "Сообщение об ошибке заказчику")
    private String info;

    @Schema(description = "Техническое сообщение об ошибке")
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_DATETIME)
    @Schema(description = "Дата и время когда произошла ошибка")
    private LocalDateTime timestamp;

    @Schema(description = "Список стектрейсов или описания ошибок")
    private List<String> errors;

}