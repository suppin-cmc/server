package com.cmc.suppin.event.crawl.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class CommentResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrawledCommentListDTO {
        private int totalCommentCount;
        private int participantCount;
        private String crawlTime;
        private List<CommentDetailDTO> comments;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentDetailDTO {
        private String author;
        private String commentText;
        private String commentDate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WinnerResponseDTO {
        private int winnerCount;
        private int minLength;
        private String startDate;
        private String endDate;
        private List<CommentDetailDTO> winners;
    }
}
