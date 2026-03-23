package com.focusframe.focusframe_api.dto.scheduleSubtaskDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSubtaskResponse {
    
    private Boolean success;
    
    private String message;
    
    private ScheduleSummary summary;  // ✓ High-level overview
    
    private List<ScheduledSubtaskItem> scheduledSubtasks;
    
    private List<ConflictSummary> conflictSummary;  // ✓ Detailed conflict info (replaces old conflicts array)
}
