package io.sse.serversentevent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final SseService sseService;

    /**
     *
     * @param receiver 받는 사람
     * @param sendData 받는 사람에게 전달할 데이터
     * @return 저장한 전자결재의 IDX를 반환한다.
     */
    public String createApproval(String receiver, String sendData) {

        //TODO: 전자결재 저장하는 로직
        String approvalIdx = null;
        //...
        approvalIdx = "100100"; //저장된 전자결재의 IDX

        sseService.send(receiver, sendData, "전자결재가 도착하였습니다.");

        return approvalIdx;
    }
}
