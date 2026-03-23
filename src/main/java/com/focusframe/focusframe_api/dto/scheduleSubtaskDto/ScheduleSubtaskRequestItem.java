package com.focusframe.focusframe_api.dto.scheduleSubtaskDto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSubtaskRequestItem {
    
    @NotBlank(message = "Subtask description is required")
    private String description;
    
    @NotNull(message = "Estimated time is required")
    @Min(value = 1, message = "Estimated time must be greater than 0 minutes")
    @Max(value = 480, message = "Estimated time cannot exceed 8 hours (480 minutes)")
    private Integer estimatedTime;
    
    @NotBlank(message = "Preferred date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Preferred date must be in format YYYY-MM-DD")
    private String preferredDate;
    
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$|^$", message = "Preferred start time must be in format HH:mm or null")
    private String preferredStartTime;
}
