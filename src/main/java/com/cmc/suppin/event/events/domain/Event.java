package com.cmc.suppin.event.events.domain;

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
}
