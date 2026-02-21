package com.example.toDoListSpring.service;

import com.example.toDoListSpring.dto.TaskDTO;
import com.example.toDoListSpring.dto.TaskUpdateDTO;
import com.example.toDoListSpring.entity.Task;
import com.example.toDoListSpring.entity.TaskStatus;
import com.example.toDoListSpring.exception.AlreadyExistsException;
import com.example.toDoListSpring.exception.ResourceNotFoundException;
import com.example.toDoListSpring.mapper.TaskMapper;
import com.example.toDoListSpring.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TaskServiceTests {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDTO taskDTO;
    private TaskUpdateDTO taskUpdateDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        task = new Task();
        task.setId(1L);
        task.setName("test_task1");
        task.setDescription("123");
        task.setDeadline(LocalDate.parse("2000-10-10"));
        task.setStatus(TaskStatus.TODO);

        taskDTO = TaskDTO.builder()
                .id(1L)
                .name("test_task1")
                .description("123")
                .deadline(LocalDate.parse("2000-10-10"))
                .status(TaskStatus.TODO)
                .build();

        taskUpdateDTO = TaskUpdateDTO.builder()
                .name("updated_test_task1")
                .description("12345")
                .deadline(LocalDate.parse("2000-11-11"))
                .status(TaskStatus.IN_PROGRESS)
                .build();
    }

    @Test
    void getAllTasks_shouldReturnPageOfTasks() {
        //given
        Page<Task> taskPage = new PageImpl<>(List.of(task));
        when(taskRepository.findAll(pageable)).thenReturn(taskPage);
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        //when
        Page<TaskDTO> result = taskService.getAllTasks(pageable);


        //then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("test_task1");
        verify(taskRepository).findAll(pageable);
        verify(taskMapper).toDTO(task);
    }

    @Test
    void getTaskById_shouldReturnTask_whenExists() {
        //given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        //when
        TaskDTO result = taskService.getTaskById(1L);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(taskRepository).findById(1L);
    }

    @Test
    void getTaskById_shouldThrowNotFound_whenNotExists() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("не найдена задача c id: 999");
    }

    @Test
    void createTask_shouldCreateTask_whenNameNotExists() {
        //given
        when(taskRepository.existsByName("test_task1")).thenReturn(false);
        when(taskMapper.toEntity(taskDTO)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        //when
        TaskDTO result = taskService.createTask(taskDTO);

        //then
        assertThat(result.getName()).isEqualTo("test_task1");
        verify(taskRepository).existsByName("test_task1");
        verify(taskRepository).save(task);
    }

    @Test
    void createTask_shouldThrowAlreadyExists_whenNameAlreadyTaken() {
        when(taskRepository.existsByName("test_task1")).thenReturn(true);

        assertThatThrownBy(() -> taskService.createTask(taskDTO))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("эта задача уже существует: test_task1");
    }

    @Test
    void updateTask_shouldUpdateTask_whenValidData() {
        //given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.existsByName("updated_test_task1")).thenReturn(false);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        //when
        TaskDTO result = taskService.updateTask(1L, taskUpdateDTO);

        //then
        assertThat(result).isNotNull();
        verify(taskMapper).updateFromUpdateDTO(taskUpdateDTO, task);
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_shouldThrowAlreadyExists_whenNewNameIsTaken() {
        //given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.existsByName("updated_test_task1")).thenReturn(true);

        //then
        assertThatThrownBy(() -> taskService.updateTask(1L, taskUpdateDTO))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("задача с подобным названием уже существует: updated_test_task1");
    }

    @Test
    void deleteTaskById_shouldDelete_whenExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTaskById(1L);

        verify(taskRepository).findById(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTaskById_shouldThrowNotFound_whenNotExists() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTaskById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("не найдена задача с id: 999");
    }

    @Test
    void getTasksByStatus_shouldReturnFilteredPage() {
        Page<Task> page = new PageImpl<>(List.of(task));
        when(taskRepository.findByStatus(pageable, TaskStatus.TODO)).thenReturn(page);
        when(taskMapper.toDTO(task)).thenReturn(taskDTO);

        Page<TaskDTO> result = taskService.getTasksByStatus(pageable, TaskStatus.TODO);

        assertThat(result.getContent()).hasSize(1);
        verify(taskRepository).findByStatus(pageable, TaskStatus.TODO);
    }
}
