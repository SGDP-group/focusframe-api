package com.focusframe.focusframe_api.dto.subTasks;

import com.focusframe.focusframe_api.model.Subtask;

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
    public static SubTaskDto from (Subtask subTask) {
        return new SubTaskDto(
                subTask.getTask().getName(),
                subTask.getName(),
                subTask.getDescription(),
                subTask.getTaskOrder(),
                subTask.getStartTime(),
                subTask.getEndTime(),
                subTask.getDuration(),
                subTask.getEstimatedTime()
        );
    }}
