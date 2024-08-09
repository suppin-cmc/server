package com.cmc.suppin.event.crawl.controller.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class CommentRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentListRequestDTO {
        private Long eventId;
        private String url;
        private int page;
        private int size;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WinnerRequestDTO {
        private Long eventId;
        private int winnerCount;
        private int minLength;

        @Pattern(regexp = "\\d{4}\\. \\d{2}\\. \\d{2} \\d{2}:\\d{2}", message = "날짜 형식은 yyyy. MM. dd HH:mm 이어야 합니다.")
        private String startDate;
        @Pattern(regexp = "\\d{4}\\. \\d{2}\\. \\d{2} \\d{2}:\\d{2}", message = "날짜 형식은 yyyy. MM. dd HH:mm 이어야 합니다.")
        private String endDate;

        private List<String> keywords;
    }
}
