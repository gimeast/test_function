package io.sse.serversentevent.vo;

import lombok.Builder;

@Builder
public class Notification {

    private String receiver;
    private String sendData;
    private String content;

    @Builder
    public Notification(String receiver, String sendData, String content) {
        this.receiver = receiver;
        this.sendData = sendData;
        this.content = content;
    }

}
