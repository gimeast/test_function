package io.sse.serversentevent.controller;

import io.sse.serversentevent.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    /**
     * 클라이언트와 sse 연결을 맺는다.
     * @param userId 클라이언트 아이디
     * @param lastEventId 미수신 알림을 보내기 위한 파라미터
     * @return
     */
    @GetMapping(value = "/subscribe/{userId}", produces = "text/event-stream")
    public ResponseEntity<SseEmitter> subscribe(
            @PathVariable String userId,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
//        return sseService.subscribe(userId, lastEventId);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "event-stream", StandardCharsets.UTF_8))
                .body(sseService.subscribe(userId, lastEventId));
    }

}