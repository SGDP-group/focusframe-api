package com.focusframe.focusframe_api.controller;

import com.focusframe.focusframe_api.dto.subTasksDto.SubTaskDto;
import com.focusframe.focusframe_api.model.Subtask;
import com.focusframe.focusframe_api.service.SubtaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/subtasks")
@CrossOrigin(origins = "*")
public class SubtaskController {
    
    @Autowired
    private SubtaskService subtaskService;

    
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "taskName", "taskId", "name", "description", "taskOrder", "startTime", "endTime", "duration", "estimatedTime"
    );
    
    private static final Set<String> ALLOWED_DIRECTIONS = Set.of("asc", "desc");
    
    @GetMapping
    public ResponseEntity<List<Subtask>> getAllSubtasks() {
        return ResponseEntity.ok(subtaskService.getAllSubtasks());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Subtask> getSubtaskById(@PathVariable Integer id) {
        return subtaskService.getSubtaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<SubTaskDto>> getSubtasksByTaskId(@PathVariable Integer taskId) {
        return ResponseEntity.ok(subtaskService.getSubtasksByTaskId(taskId));
    }
    
    @GetMapping("/status/{statusId}")
    public ResponseEntity<List<Subtask>> getSubtasksByStatusId(@PathVariable Integer statusId) {
        return ResponseEntity.ok(subtaskService.getSubtasksByStatusId(statusId));
    }
    
    @GetMapping("/completed/{completed}")
    public ResponseEntity<List<Subtask>> getSubtasksByCompleted(@PathVariable Boolean completed) {
        return ResponseEntity.ok(subtaskService.getSubtasksByCompleted(completed));
    }
    
    @GetMapping("/task/{taskId}/completed/{completed}")
    public ResponseEntity<List<Subtask>> getSubtasksByTaskIdAndCompleted(
            @PathVariable Integer taskId, 
            @PathVariable Boolean completed) {
        return ResponseEntity.ok(subtaskService.getSubtasksByTaskIdAndCompleted(taskId, completed));
    }

    @GetMapping("/due-today")
    public ResponseEntity<List<SubTaskDto>> getDueTodayUpcomingOrOngoingSubtasks(
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            return ResponseEntity.badRequest().build();
        }

        if (!ALLOWED_DIRECTIONS.contains(direction.toLowerCase())) {
            return ResponseEntity.badRequest().build();
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return ResponseEntity.ok(subtaskService.getDueTodayUpcomingOrOngoing(sort));
    }
//
//
//    @GetMapping("/today")
//    public ResponseEntity<List<SubTaskDto>> getTodaySubtasks(
//            @RequestParam Integer MainTaskId,
//            @RequestParam(defaultValue = "startTime") String sortBy,
//            @RequestParam(defaultValue = "asc") String direction) {
//
//
//        Task taskId  = taskService.getTaskById(MainTaskId);
//
//        // Validate sortBy
//        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        // Validate direction
//        if (!ALLOWED_DIRECTIONS.contains(direction.toLowerCase())) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        // Define the start and end of the current day
//        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
//        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
//
//        // Handle Sorting
//        Sort sort = direction.equalsIgnoreCase("desc")
//                ? Sort.by(sortBy).descending()
//                : Sort.by(sortBy).ascending();
//
//        List<SubTaskDto> subtasks = subtaskService.getTodaySubTasksWhichNotCompletedByTaskId(taskId,startOfDay, endOfDay, sort);
//
//        return ResponseEntity.ok(subtasks);
//    }







    @PostMapping
    public ResponseEntity<Subtask> createSubtask(@RequestBody Subtask subtask) {
        try {
            Subtask createdSubtask = subtaskService.createSubtask(subtask);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSubtask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Subtask> updateSubtask(@PathVariable Integer id, @RequestBody Subtask subtask) {
        try {
            Subtask updatedSubtask = subtaskService.updateSubtask(id, subtask);
            return ResponseEntity.ok(updatedSubtask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<Subtask> partialUpdateSubtask(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        try {
            Subtask updatedSubtask = subtaskService.partialUpdateSubtask(id, updates);
            return ResponseEntity.ok(updatedSubtask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubtask(@PathVariable Integer id) {
        try {
            subtaskService.deleteSubtask(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
