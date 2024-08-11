package com.cmc.suppin.fcm.controller.dto;

import lombok.*;

/**
 * 모바일에서 전달받은 객체를 FCM으로 전송하기 위한 DTO
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmSendDTO {
    private String token;

    private String title;

    private String body;

    @Builder(toBuilder = true)
    public FcmSendDTO(String token, String title, String body) {
        this.token = token;
        this.title = title;
        this.body = body;
    }
}
