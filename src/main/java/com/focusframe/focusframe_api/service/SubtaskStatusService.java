package com.focusframe.focusframe_api.service;

import com.focusframe.focusframe_api.model.SubtaskStatus;
import com.focusframe.focusframe_api.repository.SubtaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubtaskStatusService {
    
    @Autowired
    private SubtaskStatusRepository subtaskStatusRepository;
    
    /**
     * Get all subtask statuses
     */
    public List<SubtaskStatus> getAllStatuses() {
        return subtaskStatusRepository.findAll();
    }
    
    /**
     * Get a subtask status by ID
     */
    public Optional<SubtaskStatus> getStatusById(Integer id) {
        return subtaskStatusRepository.findById(id);
    }
    
    /**
     * Get a subtask status by name
     */
    public Optional<SubtaskStatus> getStatusByName(String name) {
        return subtaskStatusRepository.findByName(name);
    }
    
    /**
     * Create a new subtask status
     */
    public SubtaskStatus createStatus(SubtaskStatus status) {
        if (status.getName() == null || status.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Status name cannot be empty");
        }
        
        if (subtaskStatusRepository.existsByName(status.getName())) {
            throw new IllegalArgumentException("Status with name '" + status.getName() + "' already exists");
        }
        
        return subtaskStatusRepository.save(status);
    }
    
    /**
     * Update a subtask status
     */
    public SubtaskStatus updateStatus(Integer id, SubtaskStatus statusDetails) {
        SubtaskStatus status = subtaskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Status not found with id: " + id));
        
        if (statusDetails.getName() != null && !statusDetails.getName().isEmpty()) {
            // Check if new name already exists (and is not the same as current)
            if (!status.getName().equals(statusDetails.getName()) && 
                subtaskStatusRepository.existsByName(statusDetails.getName())) {
                throw new IllegalArgumentException("Status with name '" + statusDetails.getName() + "' already exists");
            }
            status.setName(statusDetails.getName());
        }
        
        return subtaskStatusRepository.save(status);
    }
    
    /**
     * Delete a subtask status
     */
    public void deleteStatus(Integer id) {
        SubtaskStatus status = subtaskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Status not found with id: " + id));
        subtaskStatusRepository.delete(status);
    }
    
    /**
     * Check if a status exists by name
     */
    public boolean statusExists(String name) {
        return subtaskStatusRepository.existsByName(name);
    }
}

