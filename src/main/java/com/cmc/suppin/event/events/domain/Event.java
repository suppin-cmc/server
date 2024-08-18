package com.cmc.suppin.event.events.domain;

import com.cmc.suppin.event.crawl.controller.dto.CommentRequestDTO;
import com.cmc.suppin.event.crawl.domain.Comment;
import com.cmc.suppin.event.survey.domain.Survey;
import com.cmc.suppin.global.domain.BaseDateTimeEntity;
import com.cmc.suppin.global.enums.EventStatus;
import com.cmc.suppin.global.enums.EventType;
import com.cmc.suppin.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Event extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "event")
    @Builder.Default
    private List<Survey> surveyList = new ArrayList<>();

    @OneToMany(mappedBy = "event")
    @Builder.Default
    private List<Comment> commentList = new ArrayList<>();

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column
    private LocalDateTime announcementDate;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    // 당첨자 선별 조건
    @Column
    private Integer winnerCount;

    @Column
    private LocalDateTime selectionStartDate;

    @Column
    private LocalDateTime selectionEndDate;

    @Column
    private Integer minLength;

    @ElementCollection
    @CollectionTable(name = "event_keywords", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    public void setMember(Member member) {
        this.member = member;
        member.getEventList().add(this);
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setWinnerCount(Integer winnerCount) {
        this.winnerCount = winnerCount;
    }

    public void setSelectionStartDate(LocalDateTime selectionStartDate) {
        this.selectionStartDate = selectionStartDate;
    }

    public void setSelectionEndDate(LocalDateTime selectionEndDate) {
        this.selectionEndDate = selectionEndDate;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setSelectionCriteria(CommentRequestDTO.WinnerRequestDTO request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        this.winnerCount = request.getWinnerCount();

        // startDate와 endDate가 빈 문자열("")이거나 null일 경우 null로 설정
        if (request.getStartDate() == null || request.getStartDate().isEmpty()) {
            this.selectionStartDate = null; // 필터링을 하지 않도록 null로 설정
        } else {
            this.selectionStartDate = LocalDateTime.parse(request.getStartDate(), formatter);
        }

        if (request.getEndDate() == null || request.getEndDate().isEmpty()) {
            this.selectionEndDate = null; // 필터링을 하지 않도록 null로 설정
        } else {
            this.selectionEndDate = LocalDateTime.parse(request.getEndDate(), formatter);
        }

        this.minLength = request.getMinLength();

        // keywords가 빈 문자열만 있을 경우 처리
        this.keywords = (request.getKeywords() == null || request.getKeywords().isEmpty() ||
                (request.getKeywords().size() == 1 && request.getKeywords().get(0).isEmpty())) ?
                new ArrayList<>() : request.getKeywords();
    }

}
