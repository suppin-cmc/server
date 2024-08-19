package com.cmc.suppin.fcm.service;

import com.cmc.suppin.event.events.domain.Event;
import com.cmc.suppin.fcm.domain.DeviceToken;
import com.cmc.suppin.fcm.domain.respository.DeviceTokenRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final DeviceTokenRepository deviceTokenRepository;

    public void sendEventEndNotification(Event event) {
        // 이벤트 종료 알림 전송 로직 구현
        String message = "[ " + event.getTitle() + " ] 의 응답을 확인하시고 당첨자를 선정해 주세요.";
        sendPushNotification(event, message);
    }

    public void sendAnnouncementNotification(Event event) {
        // 당첨자 발표일 알림 전송 로직 구현
        String message = "[ " + event.getTitle() + " ] 의 당첨자 발표일입니다.";
        sendPushNotification(event, message);
    }

    public void sendPushNotification(Event event, String message) {
        List<DeviceToken> deviceTokens = deviceTokenRepository.findAllByMember(event.getMember());

        List<String> tokenStrings = deviceTokens.stream()
                .map(DeviceToken::getDeviceToken)
                .collect(Collectors.toList());

        if (!tokenStrings.isEmpty()) {
            Notification notification = Notification.builder()
                    .setTitle(event.getTitle())
                    .setBody(message)
                    .build();

            MulticastMessage notificationMessage = MulticastMessage.builder()
                    .setNotification(notification)
                    .putData("message", message)
                    .addAllTokens(tokenStrings)
                    .build();

            try {
                BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(notificationMessage);
                System.out.println("Successfully sent message: " + response.getSuccessCount() + " messages sent successfully");
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
                System.err.println("Error sending message: " + e.getMessage());
            }
        }
    }

    private List<String> getDeviceTokensForEvent(Event event) {
        // 이벤트에 참여한 사용자들의 디바이스 토큰을 가져오는 로직
        return new ArrayList<>(); // 예시로 빈 리스트 반환
    }
}


