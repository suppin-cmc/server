package com.cmc.suppin.fcm.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * FCM에 실제 전송될 데이터의 DTO
 */
@Getter
@Builder
public class FcmMessageDTO {
    private boolean validateOnly;
    private FcmMessageDTO.Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private FcmMessageDTO.Notification notification;
        private String token;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }
}
