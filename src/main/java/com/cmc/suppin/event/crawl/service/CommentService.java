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

        String crawlTime = comments.isEmpty() ? "" : comments.getContent().get(0).getCrawlTime().format(DateTimeFormatter.ofPattern("yyyy. MM. dd HH:mm"));

        return CommentConverter.toCommentListDTO(comments.getContent(), crawlTime, totalComments);
    }

    // 당첨자 조건별 랜덤 추첨(댓글 이벤트)
    public CommentResponseDTO.WinnerResponseDTO drawWinners(CommentRequestDTO.WinnerRequestDTO request, String userId) {
        Member member = memberRepository.findByUserIdAndStatusNot(userId, UserStatus.DELETED)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Event event = eventRepository.findByIdAndMemberId(request.getEventId(), member.getId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy. MM. dd HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(request.getStartDate(), dateTimeFormatter);
        LocalDateTime endDateTime = LocalDateTime.parse(request.getEndDate(), dateTimeFormatter);

        List<Comment> comments = commentRepository.findByEventIdAndCommentDateBetween(event.getId(), startDateTime, endDateTime);

        // 키워드 필터링(OR 로직) 및 minLength 필터링 추가
        List<Comment> filteredComments = comments.stream()
                .filter(comment -> request.getKeywords().stream().anyMatch(keyword -> comment.getCommentText().contains(keyword)))
                .filter(comment -> comment.getCommentText().length() >= request.getMinLength())
                .collect(Collectors.toList());

        // 랜덤 추첨
        Collections.shuffle(filteredComments);
        List<Comment> winners = filteredComments.stream().limit(request.getWinnerCount()).collect(Collectors.toList());

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

}
