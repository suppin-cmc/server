package com.cmc.suppin.event.survey.controller.dto;

import com.cmc.suppin.global.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class SurveyResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SurveyCreateResponse {
        private Long surveyId;
        private String uuid;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SurveyViewDTO {
        private Long eventId;
        private String eventTitle;
        private String eventDescription;
        private String startDate;
        private String endDate;
        private String announcementDate;
        private String consentFormHtml;
        private List<PersonalInfoOptionDTO> personalInfoOptions;
        private Long surveyId;
        private List<QuestionDTO> questions;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class QuestionDTO {
            private Long questionId;
            private QuestionType questionType;
            private String questionText;
            private List<OptionDTO> options;

            @Getter
            @NoArgsConstructor
            @AllArgsConstructor
            @Builder
            public static class OptionDTO {
                private Long questionOptionId;
                private String optionText;
            }
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class PersonalInfoOptionDTO {
            private String option;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SurveyAnswerResultDTO {
        private Long questionId;
        private String questionText;
        private int totalPages;
        private long totalElements;
        private List<AnswerDTO> answers;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class AnswerDTO {
            private String participantName;
            private String answerText;
            private List<String> selectedOptions;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RandomSelectionResponseDTO {
        private SelectionCriteriaDTO selectionCriteria;
        private List<WinnerDTO> winners;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class WinnerDTO {
            private Long participantId;
            private String participantName;
            private String answerText;
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class SelectionCriteriaDTO {
            private Integer winnerCount;
            private LocalDateTime startDate;
            private LocalDateTime endDate;
            private Integer minLength;
            private List<String> keywords;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WinnerDetailDTO {
        private String name;
        private String phoneNumber;
        private String fullAddress;
        private String extraAddress;
        private String email;
        private String instagramId;
        private List<AnswerDetailDTO> answers;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class AnswerDetailDTO {
            private String questionText;
            private String answerText;
            private List<String> selectedOptions; // 객관식 질문의 경우 선택된 옵션 리스트
        }
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SurveyEventWinners {
        private String name;
        private List<WinnerDetailDTO.AnswerDetailDTO> answers;
    }

    // 당첨자 선별 조건도 포함하여 반환하기 위한 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SurveyEventWinnersResponse {
        private SelectionCriteriaDTO selectionCriteria;
        private List<SurveyEventWinners> winners;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class SelectionCriteriaDTO {
            private Integer winnerCount;
            private LocalDateTime selectionStartDate;
            private LocalDateTime selectionEndDate;
            private Integer minLength;
            private List<String> keywords;
        }
    }
}
