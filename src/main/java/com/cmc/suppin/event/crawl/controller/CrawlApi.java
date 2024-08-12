package com.cmc.suppin.event.crawl.controller;

import com.cmc.suppin.event.crawl.controller.dto.CrawlResponseDTO;
import com.cmc.suppin.event.crawl.service.CrawlService;
import com.cmc.suppin.global.response.ApiResponse;
import com.cmc.suppin.global.response.ResponseCode;
import com.cmc.suppin.global.security.reslover.Account;
import com.cmc.suppin.global.security.reslover.CurrentAccount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Crawling", description = "Crawling 관련 API")
@RequestMapping("/api/v1/event")
public class CrawlApi {

    private final CrawlService crawlService;

    // 크롤링 URL 중복 검증
    @GetMapping("/comments/checkUrl")
    @Operation(summary = "크롤링 중복 검증 API",
            description = "주어진 URL과 eventId로 중복된 댓글 수집 이력이 있는지 확인합니다.<br><br>" +
                    "Request<br>" +
                    "- url: 중복 검증할 URL<br><br>" +
                    "Response<br>" +
                    "- 요청된 URL과 중복된 댓글 수집 이력이 있을 경우 '검증 및 확인되었습니다.' 출력<br>" +
                    "- 요청된 URL과 중복된 댓글 수집 이력이 없을 경우 '수집 이력이 없습니다.' 출력")
    public ResponseEntity<ApiResponse<String>> checkExistingComments(@RequestParam("url") String url, @CurrentAccount Account account) {
        String message = crawlService.checkExistingComments(url, account.userId());
        if (message != null) {
            return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, message));
        }
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, "수집 이력이 없습니다."));
    }

    // 유튜브 댓글 크롤링(DB 저장)
    @PostMapping("/crawling/comments")
    @Operation(summary = "유튜브 댓글 크롤링 API",
            description = "주어진 URL의 유튜브 댓글을 크롤링하여 해당 댓글 데이터를 DB에 저장합니다.<br><br>" +
                    "Request: url: 크롤링할 URL, eventId: 댓글을 수집할 eventId, forceUpdate: 댓글을 강제로 업데이트할지 여부(Boolean), Authorization: JWT 토큰을 포함한 인증 헤더 <br><br>" +
                    "<forceUpdate 입력 값이 true일 때> <br> " +
                    "- 동일한 URL에 대한 댓글 크롤링 요청이지만 강제로 업데이트하겠다는 의미이기 때문에, 기존 댓글 데이터를 삭제하고 새로 등록합니다. <br><br>" +
                    "<forceUpdate 입력 값이 false일 때> <br> " +
                    "크롤링하려는 URL이 중복되지 않았을 때의 요청이기 때문에, 새로운 댓글을 크롤링합니다. <br>" +
                    "- DB에 기존 댓글이 존재하는 경우: 크롤링을 중지하고 예외를 던집니다. <br>" +
                    "- DB에 기존 댓글이 존재하지 않는 경우: 새로운 댓글을 크롤링하고 이를 DB에 저장합니다.")
    public ResponseEntity<ApiResponse<CrawlResponseDTO.CrawlResultDTO>> crawlYoutubeComments(@RequestParam("url") String url, @RequestParam("eventId") Long eventId, @RequestParam("forceUpdate") boolean forceUpdate, @CurrentAccount Account account) {
        CrawlResponseDTO.CrawlResultDTO crawlResultDTO = crawlService.crawlYoutubeComments(url, eventId, account.userId(), forceUpdate);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, crawlResultDTO));
    }

//    @GetMapping("/count")
//    @Operation(summary = "크롤링된 전체 댓글 수 조회 API", description = "주어진 이벤트 ID와 URL의 댓글 수를 조회합니다.<br><br>" +
//            "Request: eventId: 조회할 이벤트의 ID, url: 댓글을 조회할 URL, Authorization: JWT 토큰을 포함한 인증 헤더<br>" +
//            "Response: 댓글 수")
//    public ResponseEntity<ApiResponse<CommentResponseDTO.CommentCountsDTO>> getCommentsCount(
//            @RequestParam Long eventId,
//            @RequestParam String url,
//            @CurrentAccount Account account) {
//        int count = commentService.getCommentsCount(eventId, url, account.userId());
//        return ResponseEntity.ok(ApiResponse.of(CommentResponseDTO.CommentCountsDTO(count)));
//    }

    // TODO: 인스타그램 게시글 크롤링
}
