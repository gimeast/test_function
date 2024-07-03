package io.sse.serversentevent.controller;

import io.sse.serversentevent.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    /**
     * sse 통신 테스트를 위한 api
     * @param userId
     * @param requestData
     * @return ResponseEntity
     */
    @PostMapping(value = "/approval")
    public ResponseEntity<Map<String, Object>> create(String userId, String requestData) {
        Map<String, Object> body = new HashMap<>();
        Long approvalIdx = approvalService.createApproval(userId, requestData);
        body.put("approvalIdx", approvalIdx);

        return new ResponseEntity<>(body, HttpStatus.OK);
    }

}
