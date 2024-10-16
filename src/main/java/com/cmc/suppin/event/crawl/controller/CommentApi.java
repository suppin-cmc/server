package com.cmc.suppin.event.crawl.controller;

import com.cmc.suppin.event.crawl.controller.dto.CommentRequestDTO;
import com.cmc.suppin.event.crawl.controller.dto.CommentResponseDTO;
import com.cmc.suppin.event.crawl.service.CommentService;
import com.cmc.suppin.global.response.ApiResponse;
import com.cmc.suppin.global.response.ResponseCode;
import com.cmc.suppin.global.security.reslover.Account;
import com.cmc.suppin.global.security.reslover.CurrentAccount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam("eventId") Long eventId,
            @RequestParam("url") String url,
            @Parameter(description = "조회할 페이지 번호 (1부터 시작)")
            @RequestParam("page") int page,
            @Parameter(description = "한 페이지당 댓글 수")
            @RequestParam("size") int size,
            @CurrentAccount Account account) {
        CommentResponseDTO.CrawledCommentListDTO comments = commentService.getComments(eventId, url, page, size, account.userId());
        return ResponseEntity.ok(ApiResponse.of(comments));
    }

    @PostMapping("/draft-winners")
    @Operation(summary = "당첨자 랜덤 추첨 결과 조회 API(댓글 이벤트)", description = "주어진 조건에 따라 이벤트의 당첨자를 추첨합니다.")
    public ResponseEntity<ApiResponse<CommentResponseDTO.WinnerResponseDTO>> drawWinners(
            @RequestBody @Valid CommentRequestDTO.WinnerRequestDTO request,
            @CurrentAccount Account account) {
        CommentResponseDTO.WinnerResponseDTO winners = commentService.drawWinners(request, account.userId());
        return ResponseEntity.ok(ApiResponse.of(winners));
    }

    @GetMapping("/winners/keywordFiltering")
    @Operation(summary = "키워드별 당첨자 조회 API", description = "주어진 키워드에 따라 1차 랜덤 추첨된 당첨자 중에서 키워드가 포함된 당첨자들을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CommentResponseDTO.CommentDetailDTO>>> getWinnersByKeyword(
            @RequestParam("eventId") Long eventId,
            @RequestParam("keyword") String keyword,
            @CurrentAccount Account account) {
        List<CommentResponseDTO.CommentDetailDTO> filteredWinners = commentService.getCommentsByKeyword(eventId, keyword, account.userId());
        return ResponseEntity.ok(ApiResponse.of(filteredWinners));
    }

    @DeleteMapping("/")
    @Operation(summary = "댓글 이벤트 당첨자 리스트 삭제 API(당첨자 재추첨 시, 기존 당첨자 리스트를 삭제한 후 진행해야 합니다.", description = "모든 당첨자들의 isWinner 값을 false로 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteWinners(@RequestParam("eventId") Long eventId, @CurrentAccount Account account) {
        commentService.deleteWinners(eventId);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS));
    }
}
