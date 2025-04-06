package ru.boraldan.aop.taskaop.tool;

import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.boraldan.aop.taskaop.domen.Tasks;
import ru.boraldan.aop.taskaop.domen.dto.CreatTasksDto;
import ru.boraldan.aop.taskaop.domen.dto.TasksDto;

import java.util.List;

// !!! ВАЖНО !!!  после внесения изменений в TaskMapper всегда надо запускать mvn clean
@Mapper(componentModel = "spring")
public interface TaskMapper {

    TasksDto toDtoFromTasks(Tasks tasks);

    @Mapping(target = "tasksId", ignore = true)
    Tasks creatTasksFromDto(CreatTasksDto dto);

    // Метод для работы с уже существующими объектами.
    @Mapping(target = "tasksId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Tasks updateTasksFromDto(CreatTasksDto dto, @MappingTarget Tasks entity);

    List<TasksDto> toTasksDtoList(List<Tasks> tasks);

    default Page<TasksDto> toTasksDtoPage(Page<Tasks> tasksPage) {
        List<TasksDto> tasksDtoList = this.toTasksDtoList(tasksPage.getContent());
        return new PageImpl<>(tasksDtoList, tasksPage.getPageable(), tasksPage.getTotalElements());
    }

}
