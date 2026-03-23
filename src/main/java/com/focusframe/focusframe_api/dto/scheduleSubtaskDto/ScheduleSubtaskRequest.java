package com.focusframe.focusframe_api.dto.scheduleSubtaskDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSubtaskRequest {
    
    @NotNull(message = "User ID is required")
    private Integer userId;
    
    @NotEmpty(message = "At least one subtask is required")
    @Valid
    private List<ScheduleSubtaskRequestItem> subtasks;
}
