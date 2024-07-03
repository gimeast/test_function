package io.sse.serversentevent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final SseService sseService;

    /**
     *
     * @param userId 받는 사람
     * @param sendData 받는 사람에게 전달할 데이터
     * @return 저장한 전자결재의 IDX를 반환한다.
     */
    public Long createApproval(String userId, String requestData) {

        //TODO: 전자결재 저장하는 로직
        Long approvalIdx = null;
        //...
        approvalIdx = new Random().nextLong(100); //저장된 전자결재의 IDX

        sseService.send(userId, requestData, "전자결재가 도착하였습니다.");

        return approvalIdx;
    }
}
