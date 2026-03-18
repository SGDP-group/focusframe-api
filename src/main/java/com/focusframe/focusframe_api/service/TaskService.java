package com.focusframe.focusframe_api.service;

import com.focusframe.focusframe_api.model.Task;
import com.focusframe.focusframe_api.repository.TaskRepository;
import com.focusframe.focusframe_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private  UserRepository  userRepository;

    public TaskService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public Optional<Task> getTaskById(Integer id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByUserId(Integer userId) {
        return taskRepository.findByUser_Id(userId);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }
    
    public Task updateTask(Integer id, String name, Integer userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        task.setName(name);
        task.setUser(userRepository.getReferenceById(userId));
        return taskRepository.save(task);
    }
    
    public void deleteTask(Integer id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    public List<Task> getTasksWithIncompleteSubtasksBeforeOrOnToday(Integer userId) {
        LocalDateTime today = LocalDate.now().atTime(LocalTime.MAX);
        return taskRepository.findTasksWithIncompleteSubtasksBeforeOrOn(userId, today);
    }
}
