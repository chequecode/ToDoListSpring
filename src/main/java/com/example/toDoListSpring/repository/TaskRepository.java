package com.example.toDoListSpring.repository;

import com.example.toDoListSpring.entity.Task;
import com.example.toDoListSpring.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByStatus(Pageable pageable, @Param("status") TaskStatus status);
    Boolean existsByName(String name);
}
