package com.example.swagger;

public class ResponseExample {

    public static final String TASK_ERROR_400_EXAMPLE = """
            {
               "status": "BAD_REQUEST",
               "reason": "Incorrectly made request.",
               "info": ": Ошибка! Имя задачи может содержать минимум 2, максимум 250 символов. : Ошибка! Имя задачи не может быть пустым. ",
               "message": "Field: name. Error: Ошибка! Имя задачи может содержать минимум 2, максимум 250 символов. Value:  Field: name. Error: Ошибка! Имя задачи не может быть пустым. Value:  ",
               "timestamp": "2024-04-10 19:07:59"}""";

    public static final String TASK_ERROR_404_EXAMPLE = """
            {
               "status": "NOT_FOUND",
               "reason": "The required object was not found.",
               "info": "Не найдено.",
               "message": "Задача с id = 20 не найдена.",
               "timestamp": "2024-04-10 19:09:40"}""";

}