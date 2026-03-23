package com.focusframe.focusframe_api.controller;

import com.focusframe.focusframe_api.dto.scheduleSubtaskDto.ScheduleSubtaskRequest;
import com.focusframe.focusframe_api.dto.scheduleSubtaskDto.ScheduleSubtaskResponse;
import com.focusframe.focusframe_api.dto.subTasksDto.SubTaskDto;
import com.focusframe.focusframe_api.model.Subtask;
import com.focusframe.focusframe_api.model.Task;
import com.focusframe.focusframe_api.service.SubtaskSchedulingService;
import com.focusframe.focusframe_api.service.SubtaskService;
import com.focusframe.focusframe_api.service.TaskService;
import com.focusframe.focusframe_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/subtasks")
@CrossOrigin(origins = "*")
public class SubtaskController {

    private static final DateTimeFormatter DEVICE_DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DEVICE_TIME_FMT = DateTimeFormatter.ofPattern("HHmmss");
    
    @Autowired
    private SubtaskService subtaskService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;
    @Autowired
    private SubtaskSchedulingService subtaskSchedulingService;


    public SubtaskController(UserService userService, TaskService taskService, SubtaskService subtaskService) {
        this.userService = userService;
        this.taskService = taskService;
        this.subtaskService = subtaskService;
    }

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
            @RequestParam Integer userId,
            @RequestParam(required = false) String deviceDate,
            @RequestParam(required = false) String deviceTime,
            @RequestParam(required = false) Long deviceEpoch,
            @RequestParam(required = false) Integer tzOffsetMinutes) {
        LocalDateTime deviceNow = resolveDeviceNow(deviceDate, deviceTime, deviceEpoch, tzOffsetMinutes);
        return ResponseEntity.ok(subtaskService.getDueTodayUpcomingOrOngoing(userId, deviceNow));
    }

    private LocalDateTime resolveDeviceNow(String deviceDate,
                                           String deviceTime,
                                           Long deviceEpoch,
                                           Integer tzOffsetMinutes) {
        if (deviceDate != null && deviceTime != null) {
            try {
                LocalDate date = LocalDate.parse(deviceDate, DEVICE_DATE_FMT);
                LocalTime time = LocalTime.parse(deviceTime, DEVICE_TIME_FMT);
                return LocalDateTime.of(date, time);
            } catch (DateTimeParseException ignored) {
                /* Fall through to epoch fallback. */
            }
        }

        if (deviceEpoch != null && tzOffsetMinutes != null) {
            ZoneOffset offset = ZoneOffset.ofTotalSeconds(tzOffsetMinutes * 60);
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(deviceEpoch), offset);
        }

        return LocalDateTime.now();
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
    public ResponseEntity<?> updateSubtask(@PathVariable Integer id, @RequestBody Subtask subtask) {
        try {
            return ResponseEntity.ok(subtaskService.updateSubtask(id, subtask));
        } catch (Exception e) {
            System.out.println("UPDATE ERROR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateSubtask(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        try {
            Subtask updatedSubtask = subtaskService.partialUpdateSubtask(id, updates);
            return ResponseEntity.ok(updatedSubtask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            System.out.println("PATCH ERROR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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

    @PostMapping("/schedule-subtasks")
    public ResponseEntity<?> scheduleSubtasks(@Valid @RequestBody ScheduleSubtaskRequest request) {
        try {
            ScheduleSubtaskResponse response = subtaskSchedulingService.scheduleSubtasks(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "Failed to schedule subtasks: " + e.getMessage()
                ));
        }
    }
}
