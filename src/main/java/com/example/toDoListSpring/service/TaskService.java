package com.example.toDoListSpring.service;

import com.example.toDoListSpring.dto.TaskDTO;
import com.example.toDoListSpring.dto.TaskUpdateDTO;
import com.example.toDoListSpring.entity.Task;
import com.example.toDoListSpring.entity.TaskStatus;
import com.example.toDoListSpring.exception.AlreadyExistsException;
import lombok.RequiredArgsConstructor;
import com.example.toDoListSpring.mapper.TaskMapper;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.toDoListSpring.repository.TaskRepository;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public Page<TaskDTO> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable).map(taskMapper::toDTO);
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("не найдена задача c id: " + id));
        return taskMapper.toDTO(task);
    }

    public Page<TaskDTO> getTasksByStatus(Pageable pageable, TaskStatus status) {
        return taskRepository.findByStatus(pageable, status).map(taskMapper::toDTO);
    }

    public TaskDTO createTask(TaskDTO inDTO) {
        if (taskRepository.existsByName(inDTO.getName())) {
            throw new AlreadyExistsException("this task already exists");
        }
        Task task = taskRepository.save(taskMapper.toEntity(inDTO));
        return taskMapper.toDTO(task);
    }

    public void deleteTaskById(Long id) {
        Task task = taskRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("не найдена задача с id: " + id));
        taskRepository.deleteById(id);
    }

    public TaskDTO updateTask(Long id, TaskUpdateDTO dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("не найдена задача с id: " + id));

        if (taskRepository.existsByName(dto.getName())) {
            throw new AlreadyExistsException("задача с подобным названием уже существует: " + dto.getName());
        }

        taskMapper.updateFromUpdateDTO(dto, task);
        Task saved = taskRepository.save(task);
        return taskMapper.toDTO(saved);
    }
}
