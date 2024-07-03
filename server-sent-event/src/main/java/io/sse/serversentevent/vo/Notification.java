package io.sse.serversentevent.vo;

import lombok.Builder;

@Builder
public class Notification {

    private String userId;
    private String sendData;
    private String content;

    @Builder
    public Notification(String userId, String sendData, String content) {
        this.userId = userId;
        this.sendData = sendData;
        this.content = content;
    }

}
