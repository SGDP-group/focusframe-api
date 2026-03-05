package com.focusframe.focusframe_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subtask_statuses")
public class SubtaskStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String name;
    
    public SubtaskStatus() {
    }
    
    public SubtaskStatus(String name) {
        this.name = name;
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
}
