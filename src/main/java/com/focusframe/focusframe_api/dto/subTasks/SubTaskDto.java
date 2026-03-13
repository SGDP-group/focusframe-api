package com.focusframe.focusframe_api.dto.subTasks;

import java.time.LocalDateTime;

public record SubTaskDto(
        String mainTaskName,
        String name,
        String description,
        Integer taskOrder,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer duration,
        Integer estimatedTime


) {
    public SubTaskDto fromDto(SubTaskDto subTaskDto) {
        return new SubTaskDto(
                subTaskDto.mainTaskName,
                subTaskDto.name(),
                subTaskDto.description(),
                subTaskDto.taskOrder(),
                subTaskDto.startTime(),
                subTaskDto.endTime(),
                subTaskDto.duration(),
                subTaskDto.estimatedTime()
        );
    }}

