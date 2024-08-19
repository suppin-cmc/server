package com.cmc.suppin.fcm.controller;

import com.cmc.suppin.fcm.controller.dto.DeviceTokenRequestDTO;
import com.cmc.suppin.fcm.controller.dto.FcmSendDTO;
import com.cmc.suppin.fcm.service.FcmService;
import com.cmc.suppin.global.response.ApiResponse;
import com.cmc.suppin.global.security.reslover.Account;
import com.cmc.suppin.global.security.reslover.CurrentAccount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Tag(name = "FCM", description = "FCM 푸시알림 관련 API")
@RequestMapping("/api/v1/fcm")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Object>> pushMessage(@RequestBody @Validated FcmSendDTO fcmSendDto) throws IOException {
        log.debug("[+] 푸시 메시지를 전송합니다.");
        int result = fcmService.sendMessageTo(fcmSendDto);

        return ResponseEntity.ok(ApiResponse.of(result));
    }

    @PostMapping("/register")
    @Operation(summary = "FCM 디바이스 토큰 등록 API", description = "앱을 시작할 때, 디바이스 토큰을 저장합니다.<br><br> DeviceType : ANDROID, IOS, OTHER")
    public ResponseEntity<Void> registerDeviceToken(@RequestBody @Validated DeviceTokenRequestDTO request, @CurrentAccount Account account) {
        fcmService.registerDeviceToken(account.userId(), request.getToken(), request.getDeviceType());
        return ResponseEntity.ok().build();
    }
}

