package com.cmc.suppin.event.survey.converter;

import com.cmc.suppin.event.events.domain.Event;
import com.cmc.suppin.event.survey.controller.dto.SurveyRequestDTO;
import com.cmc.suppin.event.survey.controller.dto.SurveyResponseDTO;
import com.cmc.suppin.event.survey.domain.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SurveyConverter {

    public static Survey toSurveyEntity(Event event, String uuid, String consentFormHtml) {
        return Survey.builder()
                .event(event)
                .uuid(uuid)
                .consentFormHtml(consentFormHtml)
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

    public static SurveyResponseDTO.SurveyViewDTO toSurveyViewResultDTO(Survey survey, Event event) {
        List<SurveyResponseDTO.SurveyViewDTO.PersonalInfoOptionDTO> personalInfoOptions = survey.getPersonalInfoList().stream()
                .map(option -> SurveyResponseDTO.SurveyViewDTO.PersonalInfoOptionDTO.builder()
                        .option(option.getOptionName())
                        .build())
                .collect(Collectors.toList());

        List<SurveyResponseDTO.SurveyViewDTO.QuestionDTO> questions = survey.getQuestionList().stream()
                .map(question -> SurveyResponseDTO.SurveyViewDTO.QuestionDTO.builder()
                        .questionId(question.getId())
                        .questionType(question.getQuestionType())
                        .questionText(question.getQuestionText())
                        .options(question.getQuestionOptionList().stream()
                                .map(option -> SurveyResponseDTO.SurveyViewDTO.QuestionDTO.OptionDTO.builder()
                                        .questionOptionId(option.getId())
                                        .optionText(option.getOptionText())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return SurveyResponseDTO.SurveyViewDTO.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .eventDescription(event.getDescription())
                .startDate(event.getStartDate().toString())
                .endDate(event.getEndDate().toString())
                .announcementDate(event.getAnnouncementDate().toString())
                .consentFormHtml(survey.getConsentFormHtml())
                .personalInfoOptions(personalInfoOptions)
                .surveyId(survey.getId())
                .questions(questions)
                .build();
    }


    public static SurveyResponseDTO.SurveyAnswerResultDTO toSurveyAnswerResultDTO(Question question, Page<Answer> answersPage) {
        List<SurveyResponseDTO.SurveyAnswerResultDTO.AnswerDTO> answers = answersPage.stream()
                .map(answer -> SurveyResponseDTO.SurveyAnswerResultDTO.AnswerDTO.builder()
                        .participantName(answer.getAnonymousParticipant().getName())
                        .answerText(answer.getAnswerText())
                        .selectedOptions(answer.getAnswerOptionList().stream()
                                .map(answerOption -> answerOption.getQuestionOption().getOptionText())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return SurveyResponseDTO.SurveyAnswerResultDTO.builder()
                .questionId(question.getId())
                .questionText(question.getQuestionText())
                .answers(answers)
                .totalPages(answersPage.getTotalPages())
                .totalElements(answersPage.getTotalElements())
                .build();
    }

    public static AnonymousParticipant toAnonymousParticipant(SurveyRequestDTO.SurveyAnswerDTO.ParticipantDTO dto, Survey survey) {
        return AnonymousParticipant.builder()
                .survey(survey)
                .name(dto.getName())
                .fullAddress(dto.getFullAddress())
                .extraAddress(dto.getExtraAddress())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .isAgreed(dto.getIsAgreed())
                .build();
    }

    public static Answer toAnswer(SurveyRequestDTO.SurveyAnswerDTO.AnswerDTO dto, Question question, AnonymousParticipant participant) {
        return Answer.builder()
                .question(question)
                .anonymousParticipant(participant)
                .answerText(dto.getAnswerText())
                .build();
    }

    public static AnswerOption toAnswerOption(SurveyRequestDTO.SurveyAnswerDTO.AnswerDTO.AnswerOptionDTO dto, Answer answer, QuestionOption questionOption) {
        return AnswerOption.builder()
                .answer(answer)
                .questionOption(questionOption)
                .build();
    }

    public static SurveyResponseDTO.RandomSelectionResponseDTO.SelectionCriteriaDTO toSelectionCriteriaDTO(SurveyRequestDTO.RandomSelectionRequestDTO request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd HH:mm");

        return SurveyResponseDTO.RandomSelectionResponseDTO.SelectionCriteriaDTO.builder()
                .winnerCount(request.getWinnerCount())
                .startDate(LocalDateTime.parse(request.getStartDate(), formatter))
                .endDate(LocalDateTime.parse(request.getEndDate(), formatter))
                .minLength(request.getMinLength())
                .keywords(request.getKeywords())
                .build();
    }

    public static SurveyResponseDTO.RandomSelectionResponseDTO.WinnerDTO toWinnerDTO(AnonymousParticipant participant, String answerText) {
        return SurveyResponseDTO.RandomSelectionResponseDTO.WinnerDTO.builder()
                .participantId(participant.getId())
                .participantName(participant.getName())
                .answerText(answerText)
                .build();
    }

    public static SurveyResponseDTO.RandomSelectionResponseDTO toRandomSelectionResponseDTO(List<SurveyResponseDTO.RandomSelectionResponseDTO.WinnerDTO> winners, SurveyResponseDTO.RandomSelectionResponseDTO.SelectionCriteriaDTO criteria) {
        return SurveyResponseDTO.RandomSelectionResponseDTO.builder()
                .winners(winners)
                .selectionCriteria(criteria)
                .build();
    }

    public static SurveyResponseDTO.WinnerDetailDTO toWinnerDetailDTO(AnonymousParticipant participant, List<SurveyResponseDTO.WinnerDetailDTO.AnswerDetailDTO> answers) {
        return SurveyResponseDTO.WinnerDetailDTO.builder()
                .name(participant.getName())
                .phoneNumber(participant.getPhoneNumber())
                .fullAddress(participant.getFullAddress())
                .extraAddress(participant.getExtraAddress())
                .email(participant.getEmail())
                .instagramId(participant.getInstagramId())
                .answers(answers)
                .build();
    }

    public static SurveyResponseDTO.SurveyEventWinners toSurveyEventWinners(AnonymousParticipant participant) {
        return SurveyResponseDTO.SurveyEventWinners.builder()
                .name(participant.getName())
                .answers(participant.getAnswerList().stream()
                        .map(answer -> SurveyResponseDTO.WinnerDetailDTO.AnswerDetailDTO.builder()
                                .questionText(answer.getQuestion().getQuestionText())
                                .answerText(answer.getAnswerText())
                                .selectedOptions(answer.getAnswerOptionList().stream()
                                        .map(answerOption -> answerOption.getQuestionOption().getOptionText())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

}
