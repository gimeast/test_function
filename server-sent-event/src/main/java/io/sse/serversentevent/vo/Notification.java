package io.sse.serversentevent.vo;

import lombok.Builder;

@Builder
public class Notification {

    private String userId;
    private String data;

    @Builder
    public Notification(String userId, String data) {
        this.userId = userId;
        this.data = data;
    }

}
