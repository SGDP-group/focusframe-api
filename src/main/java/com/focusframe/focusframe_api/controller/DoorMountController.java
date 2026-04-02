package com.focusframe.focusframe_api.controller;

import com.focusframe.focusframe_api.service.DoorMountLedStateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/doormount")
@CrossOrigin(origins = "*")
public class DoorMountController {

    private final DoorMountLedStateService doorMountLedStateService;

    public DoorMountController(DoorMountLedStateService doorMountLedStateService) {
        this.doorMountLedStateService = doorMountLedStateService;
    }

    @GetMapping("/led-state")
    public ResponseEntity<Map<String, Object>> getLedState(@RequestParam Integer userId) {
        try {
            return ResponseEntity.ok(doorMountLedStateService.getLedStateByUserId(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
