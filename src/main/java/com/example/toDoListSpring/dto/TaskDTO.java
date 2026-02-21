package com.example.toDoListSpring.dto;

import com.example.toDoListSpring.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;
    private TaskStatus status;
}
