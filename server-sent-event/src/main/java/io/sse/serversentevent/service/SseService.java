package io.sse.serversentevent.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void addEmitter(String userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
    }

    @Scheduled(fixedRate = 1000)
    public void sendEvents() {
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(1);
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(userId);
            }
        });
    }
}
