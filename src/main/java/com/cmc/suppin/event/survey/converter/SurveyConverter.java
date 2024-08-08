package com.cmc.suppin.event.survey.converter;

import com.cmc.suppin.event.events.domain.Event;
import com.cmc.suppin.event.survey.controller.dto.SurveyRequestDTO;
import com.cmc.suppin.event.survey.controller.dto.SurveyResponseDTO;
import com.cmc.suppin.event.survey.domain.*;

import java.util.List;
import java.util.stream.Collectors;

public class SurveyConverter {

    public static Survey toSurveyEntity(Event event, String uuid) {
        return Survey.builder()
                .event(event)
                .uuid(uuid)
                .build();
    }

    public static Question toQuestionEntity(SurveyRequestDTO.SurveyCreateDTO.QuestionDTO questionDTO, Survey survey) {
        return Question.builder()
                .survey(survey)
                .questionType(questionDTO.getQuestionType())
                .questionText(questionDTO.getQuestionText())
                .build();
    }

    public static List<QuestionOption> toQuestionOptionEntities(List<String> options, Question question) {
        return options.stream()
                .map(option -> QuestionOption.builder()
                        .optionText(option)
                        .question(question)
                        .build())
                .collect(Collectors.toList());
    }

    public static List<PersonalInfoCollectOption> toPersonalInfoCollectOptionEntities(List<String> personalInfoOptions, Survey survey) {
        return personalInfoOptions.stream()
                .map(option -> PersonalInfoCollectOption.builder()
                        .optionName(option)
                        .survey(survey)
                        .build())
                .collect(Collectors.toList());
    }

    public static SurveyResponseDTO.SurveyResultDTO toSurveyResultDTO(Survey survey, Event event) {
        List<SurveyResponseDTO.SurveyResultDTO.PersonalInfoOptionDTO> personalInfoOptions = survey.getPersonalInfoList().stream()
                .map(option -> SurveyResponseDTO.SurveyResultDTO.PersonalInfoOptionDTO.builder()
                        .optionName(option.getOptionName())
                        .build())
                .collect(Collectors.toList());

        List<SurveyResponseDTO.SurveyResultDTO.QuestionDTO> questions = survey.getQuestionList().stream()
                .map(question -> SurveyResponseDTO.SurveyResultDTO.QuestionDTO.builder()
                        .questionType(question.getQuestionType())
                        .questionText(question.getQuestionText())
                        .options(question.getQuestionOptionList().stream()
                                .map(QuestionOption::getOptionText)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return SurveyResponseDTO.SurveyResultDTO.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .eventDescription(event.getDescription())
                .startDate(event.getStartDate().toString())
                .endDate(event.getEndDate().toString())
                .announcementDate(event.getAnnouncementDate().toString())
                .personalInfoOptions(personalInfoOptions)
                .questions(questions)
                .build();
    }

    public static AnonymousParticipant toAnonymousParticipant(SurveyRequestDTO.SurveyAnswerDTO.ParticipantDTO dto, Survey survey) {
        return AnonymousParticipant.builder()
                .survey(survey)
                .name(dto.getName())
                .address(dto.getAddress())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .isAgreed(dto.getIsAgreed())
                .build();
    }

    public static Answer toAnswer(SurveyRequestDTO.SurveyAnswerDTO.AnswerDTO dto, Question question, AnonymousParticipant participant) {
        return Answer.builder()
                .question(question)
                .anonymousParticipant(participant)
                .answerText(dto.getAnswerText() != null ? dto.getAnswerText() : "")
                .build();
    }

    public static AnswerOption toAnswerOption(SurveyRequestDTO.SurveyAnswerDTO.AnswerDTO.AnswerOptionDTO dto, Answer answer, QuestionOption questionOption) {
        return AnswerOption.builder()
                .answer(answer)
                .questionOption(questionOption)
                .build();
    }
}
