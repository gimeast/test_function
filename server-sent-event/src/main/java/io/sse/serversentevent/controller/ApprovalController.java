package io.sse.serversentevent.controller;

import io.sse.serversentevent.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    /**
     * 전자결재 상신
     * @param receiver
     * @param sendData
     * @return
     */
    @PostMapping(value = "/approval")
    public ResponseEntity<Void> create(String receiver, String sendData) {
        String approvalIdx = approvalService.createApproval(receiver, sendData);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/approval/" + approvalIdx)
                .build();
    }

}
