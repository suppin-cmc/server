package com.cmc.suppin.fcm.service;

import com.cmc.suppin.fcm.controller.dto.FcmMessageDTO;
import com.cmc.suppin.fcm.controller.dto.FcmSendDTO;
import com.cmc.suppin.fcm.domain.DeviceToken;
import com.cmc.suppin.fcm.domain.DeviceType;
import com.cmc.suppin.fcm.domain.respository.DeviceTokenRepository;
import com.cmc.suppin.global.enums.UserStatus;
import com.cmc.suppin.member.domain.Member;
import com.cmc.suppin.member.domain.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final MemberRepository memberRepository;

    /**
     * 푸시 메시지 처리를 수행하는 비즈니스 로직
     *
     * @param fcmSendDTO 모바일에서 전달받은 Object
     * @return 성공(1), 실패(0)
     */
    public int sendMessageTo(FcmSendDTO fcmSendDTO) throws IOException {

        try {
            String message = makeMessage(fcmSendDTO);
            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + getAccessToken());

            HttpEntity<String> entity = new HttpEntity<>(message, headers);

            String API_URL = "https://fcm.googleapis.com/v1/projects/suppin-a5657/messages:send";
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return 1;
            } else {
                log.error("FCM 메시지 전송 실패: {}", response.getStatusCode());
                return 0;
            }
        } catch (Exception e) {
            log.error("FCM 메시지 전송 중 예외 발생", e);
            return 0;
        }
    }

    /**
     * Firebase Admin SDK의 비공개 키를 참조하여 Bearer 토큰을 발급 받습니다.
     *
     * @return Bearer token, String
     */
    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/suppin-a5657-firebase-adminsdk.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("<https://www.googleapis.com/auth/cloud-platform>"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    /**
     * FCM 전송 정보를 기반으로 메시지를 구성합니다. (Object -> String)
     *
     * @param fcmSendDTO, 모바일에서 전달받은 Object
     * @return String
     */
    private String makeMessage(FcmSendDTO fcmSendDTO) throws JsonProcessingException {

        ObjectMapper om = new ObjectMapper();
        FcmMessageDTO fcmMessageDto = FcmMessageDTO.builder()
                .message(FcmMessageDTO.Message.builder()
                        .token(fcmSendDTO.getToken())
                        .notification(FcmMessageDTO.Notification.builder()
                                .title(fcmSendDTO.getTitle())
                                .body(fcmSendDTO.getBody())
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();

        return om.writeValueAsString(fcmMessageDto);
    }

    /**
     * 클라이언트로부터 Device Token을 수신하여 저장합니다.
     */
    @Transactional
    public void registerDeviceToken(String userId, String token, DeviceType deviceType) {
        Member member = memberRepository.findByUserIdAndStatusNot(userId, UserStatus.DELETED)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        deviceTokenRepository.findByDeviceToken(token)
                .orElseGet(() -> {
                    DeviceToken deviceToken = DeviceToken.builder()
                            .member(member)
                            .deviceToken(token)
                            .deviceType(deviceType)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return deviceTokenRepository.save(deviceToken);
                });
    }
}
