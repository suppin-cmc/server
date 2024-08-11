package com.cmc.suppin.fcm.controller;

import com.cmc.suppin.fcm.controller.dto.FcmSendDTO;
import com.cmc.suppin.fcm.service.FcmService;
import com.cmc.suppin.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@Tag(name = "FCM", description = "FCM 푸시알림 관련 API")
@RequestMapping("/api/v1/fcm")
public class FcmController {

    private final FcmService fcmService;

    public FcmController(FcmService fcmService) {
        this.fcmService = fcmService;
    }

    // 모바일로부터 사용자 FCM 토큰, 메시지 제목, 내용을 받아서 서비스를 처리
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Object>> pushMessage(@RequestBody @Validated FcmSendDTO fcmSendDto) throws IOException {
        log.debug("[+] 푸시 메시지를 전송합니다. ");
        int result = fcmService.sendMessageTo(fcmSendDto);

        return ResponseEntity.ok(ApiResponse.of(result));
    }
}
