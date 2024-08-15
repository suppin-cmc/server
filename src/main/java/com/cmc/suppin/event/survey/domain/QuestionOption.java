package com.cmc.suppin.event.survey.domain;

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
public class QuestionOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_option_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(columnDefinition = "VARCHAR(50)", nullable = false)
    private String optionText;

    @OneToMany(mappedBy = "questionOption")
    @Builder.Default
    private List<AnswerOption> answerOptionList = new ArrayList<>();

}

