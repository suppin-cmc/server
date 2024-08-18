package com.cmc.suppin.event.survey.controller.dto;

import com.cmc.suppin.global.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class SurveyRequestDTO {

    // 설문 생성 요청 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SurveyCreateDTO {
        @NotNull
        private Long eventId;
        private String consentFormHtml; // 개인정보 수집 동의서 HTML 필드
        private List<PersonalInfoOptionDTO> personalInfoOptionList;
        private List<QuestionDTO> questionList;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class QuestionDTO {
            @NotBlank(message = "질문 유형: SUBJECTIVE(주관식), SINGLE_CHOICE(객관식(단일 선택)), MULTIPLE_CHOICE(객관식(복수 선택))")
            private QuestionType questionType;
            @NotBlank(message = "질문 내용을 입력해주세요")
            private String questionText;
            private List<String> options; // 객관식 질문일 경우 선택지 리스트
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class PersonalInfoOptionDTO {
            @NotBlank(message = "개인정보 수집 항목을 입력해주세요")
            private String optionName;
        }
    }

    // 설문 답변 요청 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SurveyAnswerDTO {
        @NotNull
        private Long surveyId;
        @Valid
        private ParticipantDTO participant;
        @Valid
        private List<AnswerDTO> answers;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ParticipantDTO {
            private String name;
            private String fullAddress;
            private String extraAddress;
            private String email;
            private String phoneNumber;
            private String instagramId;
            @NotNull
            private Boolean isAgreed;
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class AnswerDTO {
            @NotNull
            private Long questionId;
            private String answerText;
            private List<AnswerOptionDTO> answerOptions;

            @Getter
            @NoArgsConstructor
            @AllArgsConstructor
            @Builder
            public static class AnswerOptionDTO {
                @NotNull
                private Long questionOptionId;
            }
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RandomSelectionRequestDTO {
        @NotNull
        private Long surveyId;
        @NotNull
        private Long questionId;
        @NotNull
        private Integer winnerCount;
        @NotNull
        private String startDate;
        @NotNull
        private String endDate;
        @NotNull
        private Integer minLength;
        @NotNull
        private List<String> keywords;
    }
}
