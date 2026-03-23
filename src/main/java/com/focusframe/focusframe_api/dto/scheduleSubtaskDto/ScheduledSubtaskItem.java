package com.focusframe.focusframe_api.dto.scheduleSubtaskDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledSubtaskItem {
    
    private String description;
    
    private Integer estimatedTime;
    
    private LocalDateTime preferredStartTime;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Boolean conflictDetected;
    
    private ConflictInfo conflictWith;
}
