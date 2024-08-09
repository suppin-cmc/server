package com.cmc.suppin.event.events.controller;

import com.cmc.suppin.event.events.controller.dto.EventRequestDTO;
import com.cmc.suppin.event.events.controller.dto.EventResponseDTO;
import com.cmc.suppin.event.events.converter.EventConverter;
import com.cmc.suppin.event.events.domain.Event;
import com.cmc.suppin.event.events.service.EventService;
import com.cmc.suppin.global.response.ApiResponse;
import com.cmc.suppin.global.response.ResponseCode;
import com.cmc.suppin.global.security.reslover.Account;
import com.cmc.suppin.global.security.reslover.CurrentAccount;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Event", description = "Event 관련 API")
@RequestMapping("/api/v1/events")
public class EventApi {

    private final EventService eventService;

    @GetMapping("/all")
    @Operation(summary = "전체 이벤트 조회 API", description = "사용자의 모든 이벤트와 설문 및 댓글 수를 조회합니다.")
    public ResponseEntity<ApiResponse<List<EventResponseDTO.EventInfoDTO>>> getAllEventsWithCounts(@CurrentAccount Account account) {
        List<EventResponseDTO.EventInfoDTO> events = eventService.getAllEvents(account.userId());
        return ResponseEntity.ok(ApiResponse.of(events));
    }

    @PostMapping("/new/comment/crawling")
    @Operation(summary = "댓글 이벤트 생성 API", description = "댓글 이벤트를 생성합니다. 자세한 요청 및 응답 형식은 노션 API 문서를 참고하시면 됩니다.")
    public ResponseEntity<ApiResponse<EventResponseDTO.EventInfoDTO>> createCommentEvent(@RequestBody @Valid EventRequestDTO.CommentEventCreateDTO request, @CurrentAccount Account account) {
        Event event = eventService.createCommentEvent(request, account.userId());
        EventResponseDTO.EventInfoDTO response = EventConverter.toEventInfoDTO(event);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PostMapping("/new/survey")
    @Operation(summary = "설문 이벤트 생성 API", description = "설문 이벤트를 생성합니다. 자세한 요청 및 응답 형식은 노션 API 문서를 참고하시면 됩니다.")
    public ResponseEntity<ApiResponse<EventResponseDTO.EventInfoDTO>> createSurveyEvent(@RequestBody @Valid EventRequestDTO.SurveyEventCreateDTO request, @CurrentAccount Account account) {
        Event event = eventService.createSurveyEvent(request, account.userId());
        EventResponseDTO.EventInfoDTO response = EventConverter.toEventInfoDTO(event);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @PutMapping("/{eventId}/update")
    @Operation(summary = "이벤트 수정 API", description = "PathVariable: eventId, Request : title, description, url, startDate, endDate, announcementDate")
    public ResponseEntity<ApiResponse<Void>> updateEvent(@PathVariable Long eventId, @RequestBody @Valid EventRequestDTO.EventUpdateDTO request, @CurrentAccount Account account) {
        eventService.updateEvent(eventId, request, account.userId());
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS));
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "이벤트 삭제 API", description = "PathVariable: eventId, JWT 토큰만 주시면 됩니다.")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long eventId, @CurrentAccount Account account) {
        eventService.deleteEvent(eventId, account.userId());
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS));
    }
}
