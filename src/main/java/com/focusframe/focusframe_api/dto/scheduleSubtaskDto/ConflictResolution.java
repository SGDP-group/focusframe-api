package com.focusframe.focusframe_api.dto.scheduleSubtaskDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConflictResolution {
    
    private String aiSubtask;
    
    private String conflictingTask;
    
    private String resolution;
}
