package com.focusframe.focusframe_api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subtasks")
public class Subtask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonBackReference
    private Task task;
    
    @Column(name = "task_order", nullable = false)
    @Builder.Default
    private Integer taskOrder = 1;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private Integer duration;
    
    @Column(name = "estimated_time")
    private Integer estimatedTime;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completed = false;

    @Column
    private Integer productive;

    @Column(name = "is_tracked", nullable = false)
    @Builder.Default
    private Boolean isTracked = true;

    @Column(name = "is_ai_breakdown", nullable = false)
    @Builder.Default
    private Boolean isAiBreakdown = false;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private SubtaskStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


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
        if (status == null) {
            status = SubtaskStatus.builder().id(1).build();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
   
}
