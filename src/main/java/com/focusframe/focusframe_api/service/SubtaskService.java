package com.focusframe.focusframe_api.service;

import com.focusframe.focusframe_api.dto.subTasksDto.SubTaskDto;
import com.focusframe.focusframe_api.model.Subtask;
import com.focusframe.focusframe_api.model.SubtaskStatus;
import com.focusframe.focusframe_api.repository.SubtaskRepository;
import com.focusframe.focusframe_api.repository.TaskRepository;
import com.focusframe.focusframe_api.repository.SubtaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubtaskService {
    
    @Autowired
    private SubtaskRepository subtaskRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private SubtaskStatusRepository subtaskStatusRepository;
    
    public List<Subtask> getAllSubtasks() {
        return subtaskRepository.findAll();
    }
    
    public Optional<Subtask> getSubtaskById(Integer id) {
        return subtaskRepository.findById(id);
    }
    
    public List<SubTaskDto> getSubtasksByTaskId(Integer taskId) {
        return subtaskRepository.findByTask_IdOrderByTaskOrderAsc(taskId);
    }
    
    public List<Subtask> getSubtasksByStatusId(Integer statusId) {
        return subtaskRepository.findByStatus_Id(statusId);
    }
    
    public List<Subtask> getSubtasksByCompleted(Boolean completed) {
        return subtaskRepository.findByCompleted(completed);
    }

//    public List<SubTaskDto> getTodaySubTasksWhichNotCompleted(LocalDateTime startOfDay, LocalDateTime endOfDay, Sort sort) {
//        return subtaskRepository.findByStartTimeBetweenAndCompletedFalse(startOfDay, endOfDay, sort)
//                .stream()
//                .map(SubTaskDto::from)
//                .collect(Collectors.toList());
//    }
//
//    public List<SubTaskDto> getTodaySubTasksWhichNotCompletedByTaskId(Integer taskId, LocalDateTime startOfDay, LocalDateTime endOfDay, Sort sort) {
//        return subtaskRepository.findByTask_IdAndStartTimeBetweenAndCompletedFalse(taskId, startOfDay, endOfDay, sort)
//                .stream()
//                .map(SubTaskDto::from)
//                .collect(Collectors.toList());
//    }

    public List<Subtask> getSubtasksByTaskIdAndCompleted(Integer taskId, Boolean completed) {
        return subtaskRepository.findByTask_IdAndCompleted(taskId, completed);
    }

    
    public Subtask createSubtask(Subtask subtask) {
        if (subtask.getTask() == null || subtask.getTask().getId() == null) {
            throw new IllegalArgumentException("Task is required and must have a valid ID");
        }
        
        subtask.setTask(taskRepository.findById(subtask.getTask().getId())
            .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + subtask.getTask().getId())));

        if (subtask.getStatus() == null || subtask.getStatus().getId() == null) {
            SubtaskStatus defaultStatus = subtaskStatusRepository.findById(1)
                .orElseThrow(() -> new IllegalArgumentException("Default status not found"));
            subtask.setStatus(defaultStatus);
        } else {
            subtask.setStatus(subtaskStatusRepository.findById(subtask.getStatus().getId())
                .orElseThrow(() -> new IllegalArgumentException("Status not found with id: " + subtask.getStatus().getId())));
        }
        
        return subtaskRepository.save(subtask);
    }
    
    public Subtask updateSubtask(Integer id, Subtask subtaskDetails) {
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subtask not found with id: " + id));
        
        subtask.setName(subtaskDetails.getName());
        subtask.setDescription(subtaskDetails.getDescription());
        if (subtaskDetails.getTask() != null && subtaskDetails.getTask().getId() != null) {
            subtask.setTask(taskRepository.findById(subtaskDetails.getTask().getId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + subtaskDetails.getTask().getId())));
        }
        subtask.setTaskOrder(subtaskDetails.getTaskOrder());
        subtask.setStartTime(subtaskDetails.getStartTime());
        subtask.setEndTime(subtaskDetails.getEndTime());
        subtask.setDuration(subtaskDetails.getDuration());
        subtask.setEstimatedTime(subtaskDetails.getEstimatedTime());
        subtask.setCompleted(subtaskDetails.getCompleted());
        subtask.setProductive(subtaskDetails.getProductive());
        subtask.setIsTracked(subtaskDetails.getIsTracked());
        subtask.setStatus(subtaskDetails.getStatus());
        subtask.setIsAiBreakdown(subtaskDetails.getIsAiBreakdown());
        if (subtaskDetails.getStatus() != null && subtaskDetails.getStatus().getId() != null) {
            subtask.setStatus(subtaskStatusRepository.findById(subtaskDetails.getStatus().getId())
                .orElseThrow(() -> new IllegalArgumentException("SubtaskStatus not found with id: " + subtaskDetails.getStatus().getId())));
        } else if (subtaskDetails.getStatus() != null && subtaskDetails.getStatus().getId() == null) {
            throw new IllegalArgumentException("Status ID is required if status is provided");
        }
        
        return subtaskRepository.save(subtask);
    }
    
    public Subtask partialUpdateSubtask(Integer id, Map<String, Object> updates) {
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subtask not found with id: " + id));
        
        if (updates.containsKey("name")) {
            subtask.setName((String) updates.get("name"));
        }
        if (updates.containsKey("description")) {
            subtask.setDescription((String) updates.get("description"));
        }
        if (updates.containsKey("taskId")) {
            Integer taskId = (Integer) updates.get("taskId");
            if (taskId != null) {
                subtask.setTask(taskRepository.findById(taskId)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId)));
            }
        }
        if (updates.containsKey("taskOrder")) {
            subtask.setTaskOrder((Integer) updates.get("taskOrder"));
        }
        if (updates.containsKey("startTime")) {
            // Assuming startTime is sent as LocalDateTime or string, but for simplicity, cast
            subtask.setStartTime((LocalDateTime) updates.get("startTime"));
        }
        if (updates.containsKey("duration")) {
            subtask.setDuration((Integer) updates.get("duration"));
        }
        if (updates.containsKey("estimatedTime")) {
            subtask.setEstimatedTime((Integer) updates.get("estimatedTime"));
        }
        if (updates.containsKey("completed")) {
            subtask.setCompleted((Boolean) updates.get("completed"));
        }
        if (updates.containsKey("productive")) {
            subtask.setProductive((Integer) updates.get("productive"));
        }
        if (updates.containsKey("isTracked")) {
            subtask.setIsTracked((Boolean) updates.get("isTracked"));
        }
        if (updates.containsKey("isAiBreakdown")) {
            subtask.setIsAiBreakdown((Boolean) updates.get("isAiBreakdown"));
        }
        if (updates.containsKey("statusId")) {
            Integer statusId = (Integer) updates.get("statusId");
            if (statusId != null) {
                subtask.setStatus(subtaskStatusRepository.findById(statusId)
                    .orElseThrow(() -> new IllegalArgumentException("SubtaskStatus not found with id: " + statusId)));
            }
        }
        
        return subtaskRepository.save(subtask);
    }
    
    public void deleteSubtask(Integer id) {
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subtask not found with id: " + id));
        subtaskRepository.delete(subtask);
    }
}
