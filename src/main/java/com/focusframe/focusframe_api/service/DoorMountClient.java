package com.focusframe.focusframe_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DoorMountClient {

    private static final Logger log = LoggerFactory.getLogger(DoorMountClient.class);

    private final SseService sseService;

    public DoorMountClient(SseService sseService) {
        this.sseService = sseService;
    }

    public void sendColorSignal(String signalType, int red, int green, int blue) {
        log.info("Broadcasting DoorMount signal '{}' — RGB({}, {}, {})", signalType, red, green, blue);
        sseService.broadcast("color", Map.of(
                "type", signalType,
                "red", red,
                "green", green,
                "blue", blue
        ));
    }
}
