package com.cmc.suppin.event.survey.domain;

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
public class Answer extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "anonymous_participant_id")
    private AnonymousParticipant anonymousParticipant;

    @OneToMany(mappedBy = "answer")
    @Builder.Default
    private List<AnswerOption> answerOptionList = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String answerText;

}

