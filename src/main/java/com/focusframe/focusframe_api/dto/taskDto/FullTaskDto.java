package com.focusframe.focusframe_api.dto.taskDto;

import com.focusframe.focusframe_api.dto.subTasksDto.SubTaskDto;
import com.focusframe.focusframe_api.model.Task;

import java.util.List;

public record FullTaskDto(
        Integer id,
        String name,
        List<SubTaskDto>subTasks

) {
    public FullTaskDto from (Task task) {
        return new FullTaskDto(
                task.getId(),
                task.getName(),
                task.getSubTasks().stream()
                        .map(SubTaskDto::from)
                        .toList()
        );

    }
}
