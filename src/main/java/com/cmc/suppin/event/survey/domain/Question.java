package com.cmc.suppin.event.survey.domain;

import com.cmc.suppin.global.enums.QuestionType;
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
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @OneToMany(mappedBy = "question")
    @Builder.Default
    private List<QuestionOption> questionOptionList = new ArrayList<>();

    @OneToMany(mappedBy = "question")
    @Builder.Default
    private List<Answer> answerList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String questionText;

}
