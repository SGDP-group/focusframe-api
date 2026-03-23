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
public class ConflictSummary {
    
    private Integer subtaskIndex;
    
    private String subtaskDescription;
    
    private LocalDateTime originalTime;
    
    private LocalDateTime rescheduledTime;
    
    private ConflictInfo conflictWith;
    
    private String timeShift;
}
