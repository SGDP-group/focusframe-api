package com.focusframe.focusframe_api.service;

import com.focusframe.focusframe_api.model.Subtask;
import com.focusframe.focusframe_api.repository.SubtaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubtaskService {
    
    @Autowired
    private SubtaskRepository subtaskRepository;
    
    public List<Subtask> getAllSubtasks() {
        return subtaskRepository.findAll();
    }
    
    public Optional<Subtask> getSubtaskById(Integer id) {
        return subtaskRepository.findById(id);
    }
    
    public List<Subtask> getSubtasksByTaskId(Integer taskId) {
        return subtaskRepository.findByTaskIdOrderByTaskOrderAsc(taskId);
    }
    
    public List<Subtask> getSubtasksByStatusId(Integer statusId) {
        return subtaskRepository.findByStatusId(statusId);
    }
    
    public List<Subtask> getSubtasksByCompleted(Boolean completed) {
        return subtaskRepository.findByCompleted(completed);
    }
    
    public List<Subtask> getSubtasksByTaskIdAndCompleted(Integer taskId, Boolean completed) {
        return subtaskRepository.findByTaskIdAndCompleted(taskId, completed);
    }
    
    public Subtask createSubtask(Subtask subtask) {
        return subtaskRepository.save(subtask);
    }
    
    public Subtask updateSubtask(Integer id, Subtask subtaskDetails) {
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subtask not found with id: " + id));
        
        subtask.setName(subtaskDetails.getName());
        subtask.setDescription(subtaskDetails.getDescription());
        subtask.setTaskId(subtaskDetails.getTaskId());
        subtask.setTaskOrder(subtaskDetails.getTaskOrder());
        subtask.setStartTime(subtaskDetails.getStartTime());
        subtask.setDuration(subtaskDetails.getDuration());
        subtask.setEstimatedTime(subtaskDetails.getEstimatedTime());
        subtask.setCompleted(subtaskDetails.getCompleted());
        subtask.setProductive(subtaskDetails.getProductive());
        subtask.setIsTracked(subtaskDetails.getIsTracked());
        subtask.setIsAiBreakdown(subtaskDetails.getIsAiBreakdown());
        subtask.setStatusId(subtaskDetails.getStatusId());
        
        return subtaskRepository.save(subtask);
    }
    
    public Subtask partialUpdateSubtask(Integer id, Subtask subtaskDetails) {
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subtask not found with id: " + id));
        
        if (subtaskDetails.getName() != null) {
            subtask.setName(subtaskDetails.getName());
        }
        if (subtaskDetails.getDescription() != null) {
            subtask.setDescription(subtaskDetails.getDescription());
        }
        if (subtaskDetails.getTaskId() != null) {
            subtask.setTaskId(subtaskDetails.getTaskId());
        }
        if (subtaskDetails.getTaskOrder() != null) {
            subtask.setTaskOrder(subtaskDetails.getTaskOrder());
        }
        if (subtaskDetails.getStartTime() != null) {
            subtask.setStartTime(subtaskDetails.getStartTime());
        }
        if (subtaskDetails.getDuration() != null) {
            subtask.setDuration(subtaskDetails.getDuration());
        }
        if (subtaskDetails.getEstimatedTime() != null) {
            subtask.setEstimatedTime(subtaskDetails.getEstimatedTime());
        }
        if (subtaskDetails.getCompleted() != null) {
            subtask.setCompleted(subtaskDetails.getCompleted());
        }
        if (subtaskDetails.getProductive() != null) {
            subtask.setProductive(subtaskDetails.getProductive());
        }
        if (subtaskDetails.getIsTracked() != null) {
            subtask.setIsTracked(subtaskDetails.getIsTracked());
        }
        if (subtaskDetails.getIsAiBreakdown() != null) {
            subtask.setIsAiBreakdown(subtaskDetails.getIsAiBreakdown());
        }
        if (subtaskDetails.getStatusId() != null) {
            subtask.setStatusId(subtaskDetails.getStatusId());
        }
        
        return subtaskRepository.save(subtask);
    }
    
    public void deleteSubtask(Integer id) {
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subtask not found with id: " + id));
        subtaskRepository.delete(subtask);
    }
}
