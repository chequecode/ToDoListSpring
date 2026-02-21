package com.example.toDoListSpring.mapper;

import com.example.toDoListSpring.dto.TaskUpdateDTO;
import com.example.toDoListSpring.entity.Task;
import com.example.toDoListSpring.dto.TaskDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", defaultValue = "TODO")
    Task toEntity(TaskDTO inDTO);

    TaskDTO toDTO(Task task);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateFromUpdateDTO(TaskUpdateDTO dto, @MappingTarget Task entity);

    List<TaskDTO> toDtoList(List<Task> tasks);
}
