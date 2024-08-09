package com.cmc.suppin.event.crawl.controller;

import com.cmc.suppin.event.crawl.controller.dto.CommentResponseDTO;
import com.cmc.suppin.event.crawl.service.CommentService;
import com.cmc.suppin.global.response.ApiResponse;
import com.cmc.suppin.global.security.reslover.Account;
import com.cmc.suppin.global.security.reslover.CurrentAccount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Event-Comments", description = "Crawling Comments 관련 API")
@RequestMapping("/api/v1/event/comments")
public class CommentApi {

    private final CommentService commentService;

    @GetMapping("/list")
    @Operation(summary = "크롤링된 전체 댓글 조회 API",
            description = "주어진 이벤트 ID와 URL의 댓글을 페이지네이션하여 이벤트의 endDate 전에 작성된 댓글들만 조회합니다. 자세한 요청 및 응답 형식은 노션 API 문서를 참고해주세요.")
    public ResponseEntity<ApiResponse<CommentResponseDTO.CrawledCommentListDTO>> getComments(
            @RequestParam Long eventId,
            @RequestParam String url,
            @Parameter(description = "조회할 페이지 번호 (1부터 시작)")
            @RequestParam int page,
            @Parameter(description = "한 페이지당 댓글 수")
            @RequestParam int size,
            @CurrentAccount Account account) {
        CommentResponseDTO.CrawledCommentListDTO comments = commentService.getComments(eventId, url, page, size, account.userId());
        return ResponseEntity.ok(ApiResponse.of(comments));
    }

    @GetMapping("/draft-winners")
    @Operation(summary = "조건별 당첨자 추첨 API(댓글 이벤트)", description = "주어진 조건에 따라 이벤트의 당첨자를 추첨합니다.")
    public ResponseEntity<ApiResponse<CommentResponseDTO.WinnerResponseDTO>> drawWinners(
            @RequestParam Long eventId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam int winnerCount,
            @RequestParam List<String> keywords,
            @CurrentAccount Account account) {
        CommentResponseDTO.WinnerResponseDTO winners = commentService.drawWinners(eventId, startDate, endDate, winnerCount, keywords, account.userId());
        return ResponseEntity.ok(ApiResponse.of(winners));
    }

    @GetMapping("/winners/keywordFiltering")
    @Operation(summary = "키워드별 당첨자 조회 API", description = "주어진 키워드에 따라 1차 랜덤 추첨된 당첨자 중에서 키워드가 포함된 당첨자들을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CommentResponseDTO.CommentDetailDTO>>> getWinnersByKeyword(
            @RequestParam Long eventId,
            @RequestParam String keyword,
            @CurrentAccount Account account) {
        List<CommentResponseDTO.CommentDetailDTO> filteredWinners = commentService.getCommentsByKeyword(eventId, keyword, account.userId());
        return ResponseEntity.ok(ApiResponse.of(filteredWinners));
    }
}
