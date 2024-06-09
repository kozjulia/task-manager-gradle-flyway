create table tasks
(
    tasks_id          BIGINT primary key not null,
    tasks_title       varchar(256)       not null,
    tasks_description varchar(1024),
    tasks_due_date    TIMESTAMP WITHOUT TIME ZONE not null,
    tasks_completed   boolean
);

create sequence SEQ_TASK
    start with 1 INCREMENT BY 10;