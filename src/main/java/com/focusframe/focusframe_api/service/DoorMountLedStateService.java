package com.focusframe.focusframe_api.service;

import com.focusframe.focusframe_api.model.DoorMountLedState;
import com.focusframe.focusframe_api.model.User;
import com.focusframe.focusframe_api.repository.DoorMountLedStateRepository;
import com.focusframe.focusframe_api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class DoorMountLedStateService {

    private static final Logger log = LoggerFactory.getLogger(DoorMountLedStateService.class);

    private final DoorMountLedStateRepository doorMountLedStateRepository;
    private final UserRepository userRepository;
    private final DoorMountClient doorMountClient;

    public DoorMountLedStateService(DoorMountLedStateRepository doorMountLedStateRepository,
                                    UserRepository userRepository,
                                    DoorMountClient doorMountClient) {
        this.doorMountLedStateRepository = doorMountLedStateRepository;
        this.userRepository = userRepository;
        this.doorMountClient = doorMountClient;
    }

    public Map<String, Object> getLedStateByUserId(Integer userId) {
        DoorMountLedState state = getOrCreateState(userId);
        return toResponse(userId, state);
    }

    public void handleStatusTransition(Integer userId,
                                       Integer previousStatusId,
                                       Integer currentStatusId,
                                       String sessionId,
                                       Integer subtaskId,
                                       String subtaskName) {
        DoorMountLedState state = getOrCreateState(userId);
        boolean dirty = false;

        String normalizedSessionId = normalizeSessionId(sessionId);
        if (normalizedSessionId != null && !normalizedSessionId.equals(state.getSessionId())) {
            log.info("DoorMount session changed for user {}: '{}' -> '{}'", userId, state.getSessionId(), normalizedSessionId);
            state.setSessionId(normalizedSessionId);
            state.setGreenLocked(false);
            dirty = true;
        }

        if (!Objects.equals(previousStatusId, currentStatusId)) {
            state.setLastStatusId(currentStatusId);
            dirty = true;

            if (Integer.valueOf(2).equals(previousStatusId) && Integer.valueOf(3).equals(currentStatusId)) {
                state.setGreenLocked(true);
                sendAndPersistSignal(state, userId, "completed", 0, 255, 0);
                log.info("Subtask {} ('{}') moved 2->3; sent green signal", subtaskId, subtaskName);
                return;
            }

            if (Integer.valueOf(1).equals(previousStatusId) && Integer.valueOf(2).equals(currentStatusId)) {
                if (Boolean.TRUE.equals(state.getGreenLocked())) {
                    sendAndPersistSignal(state, userId, "resume", 0, 255, 0);
                    log.info("Subtask {} ('{}') moved 1->2 in same session after completion; kept green", subtaskId, subtaskName);
                } else {
                    sendAndPersistSignal(state, userId, "working", 255, 0, 0);
                    log.info("Subtask {} ('{}') moved 1->2; sent red signal", subtaskId, subtaskName);
                }
                return;
            }
        }

        if (dirty) {
            doorMountLedStateRepository.save(state);
        }
    }

    private DoorMountLedState getOrCreateState(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid userId for DoorMount state");
        }

        return doorMountLedStateRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
                    DoorMountLedState newState = DoorMountLedState.builder()
                            .user(user)
                            .greenLocked(false)
                            .signalType("idle")
                            .red(0)
                            .green(0)
                            .blue(0)
                            .version(0L)
                            .lastStatusId(null)
                            .sessionId(null)
                            .build();
                    return doorMountLedStateRepository.save(newState);
                });
    }

    private void sendAndPersistSignal(DoorMountLedState state,
                                      Integer userId,
                                      String signalType,
                                      int red,
                                      int green,
                                      int blue) {
        state.setSignalType(signalType);
        state.setRed(red);
        state.setGreen(green);
        state.setBlue(blue);
        long nextVersion = state.getVersion() == null ? 1L : state.getVersion() + 1L;
        state.setVersion(nextVersion);

        DoorMountLedState saved = doorMountLedStateRepository.save(state);
        doorMountClient.sendColorSignal(signalType, red, green, blue, userId, saved.getSessionId(), saved.getVersion());
    }

    private Map<String, Object> toResponse(Integer userId, DoorMountLedState state) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", userId);
        response.put("sessionId", state.getSessionId());
        response.put("greenLocked", state.getGreenLocked());
        response.put("lastStatusId", state.getLastStatusId());
        response.put("signalType", state.getSignalType());
        response.put("red", state.getRed());
        response.put("green", state.getGreen());
        response.put("blue", state.getBlue());
        response.put("version", state.getVersion());

        LocalDateTime updatedAt = state.getUpdatedAt();
        response.put("updatedAt", updatedAt == null ? null : updatedAt.toString());
        return response;
    }

    private String normalizeSessionId(String rawSessionId) {
        if (rawSessionId == null) {
            return null;
        }
        String normalized = rawSessionId.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
