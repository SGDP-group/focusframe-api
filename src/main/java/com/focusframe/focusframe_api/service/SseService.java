package com.focusframe.focusframe_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    private static final Logger log = LoggerFactory.getLogger(SseService.class);

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter registerEmitter() {
        SseEmitter emitter = new SseEmitter(24 * 60 * 60 * 1000L); // 24-hour timeout

        emitters.add(emitter);
        log.info("SSE client connected — {} active subscriber(s)", emitters.size());

        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            log.info("SSE client disconnected (completion) — {} active subscriber(s)", emitters.size());
        });
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            log.info("SSE client disconnected (timeout) — {} active subscriber(s)", emitters.size());
        });
        emitter.onError(e -> {
            emitters.remove(emitter);
            log.info("SSE client disconnected (error) — {} active subscriber(s)", emitters.size());
        });

        return emitter;
    }

    public void broadcast(String eventType, Map<String, Object> data) {
        log.info("Broadcasting SSE event '{}' to {} subscriber(s): {}", eventType, emitters.size(), data);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventType)
                        .data(data));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);
                log.warn("Removed dead SSE emitter: {}", e.getMessage());
            }
        }
    }

    @Scheduled(fixedRate = 30000)
    public void sendHeartbeat() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().comment("heartbeat"));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }
    }
}
