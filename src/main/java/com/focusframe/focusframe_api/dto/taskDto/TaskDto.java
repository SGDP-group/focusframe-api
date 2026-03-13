package com.focusframe.focusframe_api.dto.taskDto;

import com.focusframe.focusframe_api.dto.subTasksDto.SubTaskDto;
import com.focusframe.focusframe_api.model.Task;

import java.util.List;

public record TaskDto(
        Integer id,
        String name

) {
    public TaskDto from (Task task) {
        return new TaskDto(
                task.getId(),
                task.getName());

    }
}
