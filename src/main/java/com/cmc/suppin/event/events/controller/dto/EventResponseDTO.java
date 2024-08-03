package com.cmc.suppin.event.events.controller.dto;

import com.cmc.suppin.global.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EventResponseDTO {

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
