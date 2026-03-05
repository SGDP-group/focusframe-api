package com.focusframe.focusframe_api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "subtasks")
public class Subtask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "task_id", nullable = false)
    private Integer taskId;
    
    @Column(name = "task_order", nullable = false)
    private Integer taskOrder = 1;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "duration")
    private Integer duration;
    
    @Column(name = "estimated_time")
    private Integer estimatedTime;
    
    @Column(nullable = false)
    private Boolean completed = false;
    
    @Column
    private Integer productive;
    
    @Column(name = "is_tracked", nullable = false)
    private Boolean isTracked = true;
    
    @Column(name = "is_ai_breakdown", nullable = false)
    private Boolean isAiBreakdown = false;
    
    @Column(name = "status_id", nullable = false)
    private Integer statusId = 1;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public Subtask() {
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (taskOrder == null) {
            taskOrder = 1;
        }
        if (completed == null) {
            completed = false;
        }
        if (isTracked == null) {
            isTracked = true;
        }
        if (isAiBreakdown == null) {
            isAiBreakdown = false;
        }
        if (statusId == null) {
            statusId = 1;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
    
    public Integer getTaskOrder() {
        return taskOrder;
    }
    
    public void setTaskOrder(Integer taskOrder) {
        this.taskOrder = taskOrder;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public Integer getEstimatedTime() {
        return estimatedTime;
    }
    
    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
    
    public Boolean getCompleted() {
        return completed;
    }
    
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
    
    public Integer getProductive() {
        return productive;
    }
    
    public void setProductive(Integer productive) {
        if (productive != null && (productive < 0 || productive > 10)) {
            throw new IllegalArgumentException("Productive must be between 0 and 10");
        }
        this.productive = productive;
    }
    
    public Boolean getIsTracked() {
        return isTracked;
    }
    
    public void setIsTracked(Boolean isTracked) {
        this.isTracked = isTracked;
    }
    
    public Boolean getIsAiBreakdown() {
        return isAiBreakdown;
    }
    
    public void setIsAiBreakdown(Boolean isAiBreakdown) {
        this.isAiBreakdown = isAiBreakdown;
    }
    
    public Integer getStatusId() {
        return statusId;
    }
    
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
