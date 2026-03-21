package com.focusframe.focusframe_api.controller;
import com.focusframe.focusframe_api.model.SubtaskStatus;
import com.focusframe.focusframe_api.service.SubtaskStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/subtask-statuses")
@CrossOrigin(origins = "*")
public class SubtaskStatusController {
    @Autowired
    private SubtaskStatusService subtaskStatusService;

    @GetMapping
    public ResponseEntity<List<SubtaskStatus>> getAllStatuses() {
        return ResponseEntity.ok(subtaskStatusService.getAllStatuses());
    }
    @GetMapping("/{id}")
    public ResponseEntity<SubtaskStatus> getStatusById(@PathVariable Integer id) {
        return subtaskStatusService.getStatusById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<SubtaskStatus> getStatusByName(@PathVariable String name) {
        return subtaskStatusService.getStatusByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<SubtaskStatus> createStatus(@RequestBody SubtaskStatus status) {
        try {
            SubtaskStatus createdStatus = subtaskStatusService.createStatus(status);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<SubtaskStatus> updateStatus(
            @PathVariable Integer id,
            @RequestBody SubtaskStatus statusDetails) {
        try {
            SubtaskStatus updatedStatus = subtaskStatusService.updateStatus(id, statusDetails);
            return ResponseEntity.ok(updatedStatus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Integer id) {
        try {
            subtaskStatusService.deleteStatus(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/exists/{name}")
    public ResponseEntity<Boolean> statusExists(@PathVariable String name) {
        return ResponseEntity.ok(subtaskStatusService.statusExists(name));
    }
}
