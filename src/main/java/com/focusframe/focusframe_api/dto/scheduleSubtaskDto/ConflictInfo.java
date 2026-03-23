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
public class ConflictInfo {
    
    private String type;  // "SUBTASK" or "TASK"
    
    private Integer id;  // subtask ID or task ID
    
    private Integer taskId;  // Parent task ID (for context)
    
    private String taskName;  // Parent task name (for context)
    
    private String subtaskDescription;  // Clear description of what conflicts
    
    private LocalDateTime endTime;
    
    private String resolution;  // How it was resolved
}
