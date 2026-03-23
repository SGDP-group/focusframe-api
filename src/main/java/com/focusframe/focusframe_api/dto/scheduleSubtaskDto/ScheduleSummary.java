package com.focusframe.focusframe_api.dto.scheduleSubtaskDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSummary {
    
    private Integer totalRequested;
    
    private Integer totalScheduled;
    
    private Integer conflictsDetected;
    
    private Integer resolvedConflicts;
    
    private Integer failedToResolve;
}
