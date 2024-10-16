package com.cmc.suppin.event.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledTasks {

    private final EventService eventService;

    @Scheduled(cron = "0 * * * * *") // 매분마다 실행
    public void updateEventStatuses() {
        eventService.updateEventStatus();
    }
}
