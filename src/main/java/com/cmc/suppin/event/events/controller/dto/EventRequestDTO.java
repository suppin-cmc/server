package com.cmc.suppin.event.events.controller.dto;

import com.cmc.suppin.global.enums.EventType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
        @Pattern(regexp = "\\d{4}\\. \\d{2}\\. \\d{2} \\d{2}:\\d{2}", message = "날짜 형식은 yyyy. MM. dd HH:mm 이어야 합니다.")
        private String startDate;
        @NotEmpty
        @Pattern(regexp = "\\d{4}\\. \\d{2}\\. \\d{2} \\d{2}:\\d{2}", message = "날짜 형식은 yyyy. MM. dd HH:mm 이어야 합니다.")
        private String endDate;
        @NotEmpty
        @Pattern(regexp = "\\d{4}\\. \\d{2}\\. \\d{2} \\d{2}:\\d{2}", message = "날짜 형식은 yyyy. MM. dd HH:mm 이어야 합니다.")
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
        @Pattern(regexp = "\\d{4}\\. \\d{2}\\. \\d{2} \\d{2}:\\d{2}", message = "날짜 형식은 yyyy. MM. dd HH:mm 이어야 합니다.")
        private String startDate;
        @NotEmpty
        @Pattern(regexp = "\\d{4}\\. \\d{2}\\. \\d{2} \\d{2}:\\d{2}", message = "날짜 형식은 yyyy. MM. dd HH:mm 이어야 합니다.")
        private String endDate;
        @NotEmpty
        @Pattern(regexp = "\\d{4}\\. \\d{2}\\. \\d{2} \\d{2}:\\d{2}", message = "날짜 형식은 yyyy. MM. dd HH:mm 이어야 합니다.")
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
        @Pattern(regexp = "\\d{4}\\. \\d{2}\\. \\d{2} \\d{2}:\\d{2}", message = "날짜 형식은 yyyy. MM. dd HH:mm 이어야 합니다.")
        private String startDate;
        @NotEmpty
        @Pattern(regexp = "\\d{4}\\. \\d{2}\\. \\d{2} \\d{2}:\\d{2}", message = "날짜 형식은 yyyy. MM. dd HH:mm 이어야 합니다.")
        private String endDate;
        @NotEmpty
        @Pattern(regexp = "\\d{4}\\. \\d{2}\\. \\d{2} \\d{2}:\\d{2}", message = "날짜 형식은 yyyy. MM. dd HH:mm 이어야 합니다.")
        private String announcementDate;
    }
}
