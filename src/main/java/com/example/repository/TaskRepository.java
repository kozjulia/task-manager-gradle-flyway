package com.example.repository;

import com.example.model.Task;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
            select t from Task t 
            where ((:completed is null or t.completed = :completed) 
            and (t.dueDate BETWEEN :dateStart and :dateEnd)) 
            """)
    Page<Task> findAllByParams(
            @Param("completed") Boolean completed,
            @Param("dateStart") LocalDateTime dateStart,
            @Param("dateEnd") LocalDateTime dateEnd,
            Pageable page);

}