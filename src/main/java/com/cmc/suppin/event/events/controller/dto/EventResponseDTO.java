package com.cmc.suppin.event.events.controller.dto;

import com.cmc.suppin.global.enums.EventStatus;
import com.cmc.suppin.global.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

public class EventResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventInfoDTO {
        private Long eventId;
        private EventType type;
        private String title;
        private Optional<String> url;
        private String startDate;
        private String endDate;
        private String announcementDate;
        private Integer surveyCount;
        private Integer commentCount;
        private EventStatus status;
        private Long surveyId;
        private String uuid;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentEventDetailDTO {
        private EventType type;
        private String title;
        private String url;
        private String startDate;
        private String endDate;
        private String announcementDate;
    }
}
