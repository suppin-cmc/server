package com.cmc.suppin.fcm.service;

import com.cmc.suppin.event.events.domain.Event;
import com.cmc.suppin.event.events.domain.repository.EventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PushNotificationScheduler {

    private final EventRepository eventRepository;
    private final PushNotificationService pushNotificationService;

    public PushNotificationScheduler(EventRepository eventRepository, PushNotificationService pushNotificationService) {
        this.eventRepository = eventRepository;
        this.pushNotificationService = pushNotificationService;
    }

    @Scheduled(cron = "0 0 * * * *") // 매 정시에 실행
    public void checkEventEndDates() {
        List<Event> events = eventRepository.findAll(); // 모든 이벤트 조회

        LocalDateTime now = LocalDateTime.now();

        for (Event event : events) {
            if (event.getEndDate() != null && event.getEndDate().isBefore(now)) {
                // 이벤트 종료일이 지났을 때
                pushNotificationService.sendEventEndNotification(event);
            }

            if (event.getAnnouncementDate() != null && event.getAnnouncementDate().isBefore(now)) {
                // 당첨자 발표일이 지났을 때
                pushNotificationService.sendAnnouncementNotification(event);
            }
        }
    }
}


