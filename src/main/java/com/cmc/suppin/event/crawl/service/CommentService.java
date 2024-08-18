package com.cmc.suppin.event.crawl.service;

import com.cmc.suppin.event.crawl.controller.dto.CommentRequestDTO;
import com.cmc.suppin.event.crawl.controller.dto.CommentResponseDTO;
import com.cmc.suppin.event.crawl.converter.CommentConverter;
import com.cmc.suppin.event.crawl.domain.Comment;
import com.cmc.suppin.event.crawl.domain.repository.CommentRepository;
import com.cmc.suppin.event.events.domain.Event;
import com.cmc.suppin.event.events.domain.repository.EventRepository;
import com.cmc.suppin.global.enums.UserStatus;
import com.cmc.suppin.member.domain.Member;
import com.cmc.suppin.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    // 크롤링된 댓글 조회
    public CommentResponseDTO.CrawledCommentListDTO getComments(Long eventId, String url, int page, int size, String userId) {
        Member member = memberRepository.findByUserIdAndStatusNot(userId, UserStatus.DELETED)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Event event = eventRepository.findByIdAndMemberId(eventId, member.getId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("commentDate").descending());
        Page<Comment> comments = commentRepository.findByEventIdAndUrlAndCommentDateBefore(eventId, url, event.getEndDate(), pageable);

        int totalComments = commentRepository.countByEventIdAndUrl(eventId, url);

        String crawlTime = comments.isEmpty() ? "" : comments.getContent().get(0).getCrawlTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        return CommentConverter.toCommentListDTO(comments.getContent(), crawlTime, totalComments);
    }

    // 당첨자 조건별 랜덤 추첨(댓글 이벤트)
    @Transactional
    public CommentResponseDTO.WinnerResponseDTO drawWinners(CommentRequestDTO.WinnerRequestDTO request, String userId) {
        Member member = memberRepository.findByUserIdAndStatusNot(userId, UserStatus.DELETED)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Event event = eventRepository.findByIdAndMemberId(request.getEventId(), member.getId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // 당첨자 선별 조건 Event 엔티티에 저장
        event.setSelectionCriteria(request);
        eventRepository.save(event);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        // 날짜 필터링을 위한 조건 설정
        if (request.getStartDate() != null && !request.getStartDate().isEmpty()) {
            startDateTime = LocalDateTime.parse(request.getStartDate(), dateTimeFormatter);
        }
        if (request.getEndDate() != null && !request.getEndDate().isEmpty()) {
            endDateTime = LocalDateTime.parse(request.getEndDate(), dateTimeFormatter);
        }

        List<Comment> comments;

        // 날짜 조건이 있을 경우에만 필터링, 없으면 전체 댓글을 조회
        if (startDateTime != null && endDateTime != null) {
            comments = commentRepository.findByEventIdAndCommentDateBetween(event.getId(), startDateTime, endDateTime);
        } else {
            comments = commentRepository.findByEventId(event.getId());
        }

        // 키워드 필터링(OR 로직) 및 minLength 필터링 추가
        List<Comment> filteredComments = comments.stream()
                .filter(comment -> request.getKeywords().isEmpty() || request.getKeywords().stream().anyMatch(keyword -> comment.getCommentText().contains(keyword)))
                .filter(comment -> comment.getCommentText().length() >= request.getMinLength())
                .collect(Collectors.toList());

        // 랜덤 추첨
        Collections.shuffle(filteredComments);
        List<Comment> winners = filteredComments.stream().limit(request.getWinnerCount()).collect(Collectors.toList());

        // 당첨된 댓글의 isWinner 값을 true로 업데이트
        winners.forEach(winner -> {
            winner.setIsWinner(true);
            commentRepository.save(winner);  // 업데이트된 Comment 엔티티를 저장
        });

        return CommentConverter.toWinnerResponseDTO(winners, request);
    }


    // 키워드별 댓글 조회
    public List<CommentResponseDTO.CommentDetailDTO> getCommentsByKeyword(Long eventId, String keyword, String userId) {
        Member member = memberRepository.findByUserIdAndStatusNot(userId, UserStatus.DELETED)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Event event = eventRepository.findByIdAndMemberId(eventId, member.getId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        List<Comment> comments = commentRepository.findByEventIdAndCommentTextContaining(eventId, keyword);

        return comments.stream()
                .map(CommentConverter::toCommentDetailDTO)
                .collect(Collectors.toList());
    }

    public List<CommentResponseDTO.CommentDetailDTO> filterWinnersByKeyword(List<CommentResponseDTO.CommentDetailDTO> winners, String keyword) {
        return winners.stream()
                .filter(winner -> winner.getCommentText().contains(keyword))
                .collect(Collectors.toList());
    }

    public CommentResponseDTO.CommentEventWinnersWithCriteria getCommentEventWinnersWithCriteria(Long eventId, String userId) {
        Member member = memberRepository.findByUserIdAndStatusNot(userId, UserStatus.DELETED)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Event event = eventRepository.findByIdAndMemberId(eventId, member.getId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found or does not belong to the user"));

        List<Comment> winners = commentRepository.findByEventIdAndIsWinnerTrue(eventId);

        List<CommentResponseDTO.CommentEventWinners> winnerList = winners.stream()
                .map(CommentConverter::toCommentEventWinners)
                .collect(Collectors.toList());

        return CommentResponseDTO.CommentEventWinnersWithCriteria.builder()
                .winners(winnerList)
                .winnerCount(event.getWinnerCount())
                .minLength(event.getMinLength())
                .startDate(event.getSelectionStartDate() != null ? event.getSelectionStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null)
                .endDate(event.getSelectionEndDate() != null ? event.getSelectionEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null)
                .keywords(event.getKeywords())
                .build();
    }


    public void deleteWinners(Long eventId) {
        List<Comment> comments = commentRepository.findByEventIdAndIsWinnerTrue(eventId);

        for (Comment comment : comments) {
            comment.setIsWinner(false);
            commentRepository.save(comment);
        }
    }

}
