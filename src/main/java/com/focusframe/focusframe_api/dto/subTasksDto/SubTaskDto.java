package com.focusframe.focusframe_api.dto.subTasksDto;

import com.focusframe.focusframe_api.model.Subtask;

import java.time.LocalDateTime;

public record SubTaskDto(
        Integer id,        // Add this field
        String taskName,
        Integer taskId,
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
                subTask.getId(),    // Map the ID here
                subTask.getTask().getName(),
                subTask.getTask().getId(),
                subTask.getName(),
                subTask.getDescription(),
                subTask.getTaskOrder(),
                subTask.getStartTime(),
                subTask.getEndTime(),
                subTask.getDuration(),
                subTask.getEstimatedTime()
        );
    }}
