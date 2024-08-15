package com.cmc.suppin.event.crawl.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CrawlResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrawlResultDTO {
        private String crawlTime;
        private int totalCommentCount;
    }
}
