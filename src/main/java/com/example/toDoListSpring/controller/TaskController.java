package com.example.toDoListSpring.controller;

import com.example.toDoListSpring.dto.TaskUpdateDTO;
import com.example.toDoListSpring.entity.TaskStatus;
import com.example.toDoListSpring.service.TaskService;
import com.example.toDoListSpring.dto.TaskDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/by-status/{status}")
    public List<TaskDTO> getTasksByStatus(@PathVariable("status") TaskStatus status) {
        return taskService.getTasksByStatus(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping
    private ResponseEntity<TaskDTO> createTask(@RequestBody @Valid TaskDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(dto));
    }

    @PutMapping("/{id}")
    private ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody @Valid TaskUpdateDTO dto) {
        return ResponseEntity.ok(taskService.updateTask(id, dto));
    }

}
