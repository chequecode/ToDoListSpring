package com.example.toDoListSpring.repository;

import com.example.toDoListSpring.entity.Task;
import com.example.toDoListSpring.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(@Param("status") TaskStatus status);
    Boolean existsByName(String name);
}
