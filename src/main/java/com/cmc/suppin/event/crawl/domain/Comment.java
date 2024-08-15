package com.cmc.suppin.event.crawl.domain;

import com.cmc.suppin.event.events.domain.Event;
import com.cmc.suppin.global.domain.BaseDateTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;


@Entity
@Getter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String url;

    @Column(nullable = false)
    private String author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String commentText;

    @Column(nullable = false)
    private LocalDateTime commentDate;

    @Column(nullable = false)
    private LocalDateTime crawlTime;

    public void setCrawlTime(LocalDateTime crawlTime) {
        this.crawlTime = crawlTime;
    }

}
