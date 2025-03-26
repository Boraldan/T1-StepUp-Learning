package ru.boraldan.aop.taskaop.tool;

import org.mapstruct.*;
import ru.boraldan.aop.taskaop.domen.Task;
import ru.boraldan.aop.taskaop.domen.dto.TaskDto;


@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "taskId", ignore = true)
    Task toTaskFromDto(TaskDto dto);

    @Mapping(target = "taskId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Task updateTaskFromDto(TaskDto dto, @MappingTarget Task entity);
}
