package com.cmc.suppin.event.survey.domain;

import com.cmc.suppin.event.events.domain.Event;
import com.cmc.suppin.global.domain.BaseDateTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Survey extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PersonalInfoCollectOption> personalInfoList = new ArrayList<>();

    @OneToMany(mappedBy = "survey")
    @Builder.Default
    private List<Question> questionList = new ArrayList<>();

    @OneToMany(mappedBy = "survey")
    @Builder.Default
    private List<AnonymousParticipant> anonymousParticipantList = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(nullable = false, updatable = false, unique = true)
    private String uuid;

    @Column(columnDefinition = "TEXT")
    private String consentFormHtml;
}

