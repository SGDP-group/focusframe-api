package com.focusframe.focusframe_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DoorMountClient {

    private static final Logger log = LoggerFactory.getLogger(DoorMountClient.class);

    private final SseService sseService;

    public DoorMountClient(SseService sseService) {
        this.sseService = sseService;
    }

    public void sendColorSignal(String signalType, int red, int green, int blue) {
        sendColorSignal(signalType, red, green, blue, null, null, null);
    }

    public void sendColorSignal(String signalType,
                                int red,
                                int green,
                                int blue,
                                Integer userId,
                                String sessionId,
                                Long version) {
        log.info("Broadcasting DoorMount signal '{}' — RGB({}, {}, {})", signalType, red, green, blue);

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", signalType);
        payload.put("red", red);
        payload.put("green", green);
        payload.put("blue", blue);

        if (userId != null) {
            payload.put("userId", userId);
        }
        if (sessionId != null && !sessionId.isBlank()) {
            payload.put("sessionId", sessionId);
        }
        if (version != null) {
            payload.put("version", version);
        }

        sseService.broadcast("color", payload);
    }
}
