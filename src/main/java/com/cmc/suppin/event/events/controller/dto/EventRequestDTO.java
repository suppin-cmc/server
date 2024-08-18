package com.cmc.suppin.event.events.controller.dto;

import com.cmc.suppin.global.enums.EventType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EventRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentEventCreateDTO {
        @NotNull
        private EventType type;

        @NotEmpty
        private String title;

        private String description;

        @NotEmpty
        private String url;

        @NotEmpty
        private String startDate;
        @NotEmpty
        private String endDate;
        @NotEmpty
        private String announcementDate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SurveyEventCreateDTO {
        @NotNull
        private EventType type;

        @NotEmpty
        private String title;

        @NotEmpty
        private String description;

        @NotEmpty
        private String startDate;
        @NotEmpty
        private String endDate;
        @NotEmpty
        private String announcementDate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EventUpdateDTO {
        @NotNull
        private EventType type;
        private String title;
        private String description;
        private String url;

        @NotEmpty
        private String startDate;
        @NotEmpty
        private String endDate;
        @NotEmpty
        private String announcementDate;
    }
}
