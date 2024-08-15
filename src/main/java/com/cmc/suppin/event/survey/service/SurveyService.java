package com.cmc.suppin.event.survey.service;

import com.cmc.suppin.event.events.domain.Event;
import com.cmc.suppin.event.events.domain.repository.EventRepository;
import com.cmc.suppin.event.survey.controller.dto.SurveyRequestDTO;
import com.cmc.suppin.event.survey.controller.dto.SurveyResponseDTO;
import com.cmc.suppin.event.survey.converter.SurveyConverter;
import com.cmc.suppin.event.survey.domain.*;
import com.cmc.suppin.event.survey.domain.repository.*;
import com.cmc.suppin.event.survey.exception.SurveyErrorCode;
import com.cmc.suppin.event.survey.exception.SurveyException;
import com.cmc.suppin.global.enums.UserStatus;
import com.cmc.suppin.member.domain.Member;
import com.cmc.suppin.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SurveyService {

    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final PersonalInfoCollectOptionRepository personalInfoCollectOptionRepository;
    private final EventRepository eventRepository;
    private final AnonymousParticipantRepository anonymousParticipantRepository;
    private final AnswerRepository answerRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final AnswerCustomRepository answerCustomRepository;

    @Transactional
    public SurveyResponseDTO.SurveyCreateResponse createSurvey(SurveyRequestDTO.SurveyCreateDTO request, String userId) {
        // 사용자 식별
        Member member = memberRepository.findByUserIdAndStatusNot(userId, UserStatus.DELETED)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 이벤트 식별
        Event event = eventRepository.findByIdAndMemberId(request.getEventId(), member.getId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found or does not belong to the user"));

        // Survey 엔티티 생성 및 저장
        String uuid = UUID.randomUUID().toString();
        Survey survey = SurveyConverter.toSurveyEntity(event, uuid, request.getConsentFormHtml());
        surveyRepository.save(survey);

        // 각 개인정보 항목 처리 및 저장
        if (request.getPersonalInfoOptionList() != null && !request.getPersonalInfoOptionList().isEmpty()) {
            List<PersonalInfoCollectOption> personalInfoOptions = SurveyConverter.toPersonalInfoCollectOptionEntities(
                    request.getPersonalInfoOptionList().stream().map(SurveyRequestDTO.SurveyCreateDTO.PersonalInfoOptionDTO::getOptionName).collect(Collectors.toList()), survey);
            personalInfoCollectOptionRepository.saveAll(personalInfoOptions);
        }

        // 각 질문 처리 및 저장
        for (SurveyRequestDTO.SurveyCreateDTO.QuestionDTO questionDTO : request.getQuestionList()) {
            Question question = SurveyConverter.toQuestionEntity(questionDTO, survey);
            questionRepository.save(question);

            // 객관식 복수 선택 질문인 경우 처리 및 저장
            if (questionDTO.getOptions() != null && !questionDTO.getOptions().isEmpty()) {
                List<QuestionOption> options = SurveyConverter.toQuestionOptionEntities(questionDTO.getOptions(), question);
                questionOptionRepository.saveAll(options);
            }
        }

        return SurveyResponseDTO.SurveyCreateResponse.builder()
                .surveyId(survey.getId())
                .uuid(survey.getUuid())
                .build();
    }

    // 생성된 설문지 조회
    @Transactional(readOnly = true)
    public SurveyResponseDTO.SurveyResultDTO getSurveyBySurveyId(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));

        Event event = survey.getEvent();
        return SurveyConverter.toSurveyResultDTO(survey, event);
    }

    @Transactional(readOnly = true)
    public SurveyResponseDTO.SurveyResultDTO getSurveyByUuid(String uuid) {
        Survey survey = surveyRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found for UUID: " + uuid));

        Event event = survey.getEvent();
        return SurveyConverter.toSurveyResultDTO(survey, event);
    }

    // 설문 응답 저장
    @Transactional
    public void saveSurveyAnswers(SurveyRequestDTO.SurveyAnswerDTO request) {
        Survey survey = surveyRepository.findById(request.getSurveyId())
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));

        // 중복 핸드폰 번호 체크
        String phoneNumber = request.getParticipant().getPhoneNumber();
        boolean exists = anonymousParticipantRepository.existsByPhoneNumberAndSurveyId(phoneNumber, request.getSurveyId());
        if (exists) {
            throw new SurveyException(SurveyErrorCode.DUPLICATE_PHONENUMBER);
        }

        AnonymousParticipant participant = SurveyConverter.toAnonymousParticipant(request.getParticipant(), survey);
        anonymousParticipantRepository.save(participant);

        for (SurveyRequestDTO.SurveyAnswerDTO.AnswerDTO answerDTO : request.getAnswers()) {
            Question question = questionRepository.findById(answerDTO.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("Question not found"));

            Answer answer = SurveyConverter.toAnswer(answerDTO, question, participant);
            answerRepository.save(answer);

            if (answerDTO.getAnswerOptions() != null) {
                for (SurveyRequestDTO.SurveyAnswerDTO.AnswerDTO.AnswerOptionDTO optionDTO : answerDTO.getAnswerOptions()) {
                    QuestionOption questionOption = questionOptionRepository.findById(optionDTO.getQuestionOptionId())
                            .orElseThrow(() -> new IllegalArgumentException("QuestionOption not found"));

                    AnswerOption answerOption = SurveyConverter.toAnswerOption(optionDTO, answer, questionOption);
                    answerOptionRepository.save(answerOption);
                }
            }
        }
    }

    // 질문별 설문 응답 결과 조회
    @Transactional(readOnly = true)
    public SurveyResponseDTO.SurveyAnswerResultDTO getSurveyAnswers(Long surveyId, Long questionId, int page, int size, String userId) {
        // 사용자 식별
        Member member = memberRepository.findByUserIdAndStatusNot(userId, UserStatus.DELETED)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Question question = questionRepository.findByIdAndSurveyId(questionId, surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found for the given survey"));

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Answer> answersPage = answerRepository.findByQuestionId(questionId, pageable);

        return SurveyConverter.toSurveyAnswerResultDTO(question, answersPage);
    }

    // 랜덤 추첨 당첨자 선정
    @Transactional
    public SurveyResponseDTO.RandomSelectionResponseDTO selectRandomWinners(SurveyRequestDTO.RandomSelectionRequestDTO request, String userId) {
        // 사용자 식별
        Member member = memberRepository.findByUserIdAndStatusNot(userId, UserStatus.DELETED)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 설문 및 질문 식별
        Survey survey = surveyRepository.findById(request.getSurveyId())
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));

        Question question = questionRepository.findByIdAndSurveyId(request.getQuestionId(), request.getSurveyId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found for the given survey"));

        // 키워드를 OR 조건으로 연결
        List<String> keywordPatterns = request.getKeywords().stream()
                .map(keyword -> "%" + keyword.toLowerCase() + "%")
                .collect(Collectors.toList());

        // 조건에 맞는 주관식 답변 조회
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd HH:mm");

        List<Answer> eligibleAnswers = answerCustomRepository.findEligibleAnswers(
                request.getQuestionId(), LocalDateTime.parse(request.getStartDate(), formatter), LocalDateTime.parse(request.getEndDate(), formatter),
                request.getMinLength(), request.getKeywords());

        // 랜덤 추첨
        Collections.shuffle(eligibleAnswers);
        List<Answer> selectedWinners = eligibleAnswers.stream()
                .limit(request.getWinnerCount())
                .collect(Collectors.toList());

        // 당첨자 업데이트(isWinner -> True 설정) 및 WinnerDTO 생성
        List<SurveyResponseDTO.RandomSelectionResponseDTO.WinnerDTO> winners = selectedWinners.stream()
                .map(answer -> {
                    AnonymousParticipant participant = answer.getAnonymousParticipant();
                    participant.setIsWinner(true); // isWinner 값을 True로 설정
                    anonymousParticipantRepository.save(participant); // 저장

                    return SurveyConverter.toWinnerDTO(participant, answer.getAnswerText());
                })
                .collect(Collectors.toList());

        // 응답시, 조건도 함께 포함해주기 위한 조건 객체 생성
        SurveyResponseDTO.RandomSelectionResponseDTO.SelectionCriteriaDTO criteria = SurveyConverter.toSelectionCriteriaDTO(request);

        // 응답 객체 생성
        return SurveyConverter.toRandomSelectionResponseDTO(winners, criteria);
    }

    @Transactional(readOnly = true)
    public SurveyResponseDTO.WinnerDetailDTO getWinnerDetails(Long surveyId, Long participantId) {
        AnonymousParticipant participant = anonymousParticipantRepository.findByIdAndSurveyIdAndIsWinnerTrue(participantId, surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Winner not found for the given survey"));

        // 모든 답변을 조회하여 응답 DTO로 변환
        List<SurveyResponseDTO.WinnerDetailDTO.AnswerDetailDTO> answers = participant.getAnswerList().stream()
                .map(answer -> SurveyResponseDTO.WinnerDetailDTO.AnswerDetailDTO.builder()
                        .questionText(answer.getQuestion().getQuestionText())
                        .answerText(answer.getAnswerText())
                        .selectedOptions(answer.getAnswerOptionList().stream()
                                .map(answerOption -> answerOption.getQuestionOption().getOptionText())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return SurveyConverter.toWinnerDetailDTO(participant, answers);
    }

    public void deleteWinners(Long surveyId) {
        List<AnonymousParticipant> participants = anonymousParticipantRepository.findBySurveyIdAndIsWinnerTrue(surveyId);

        for (AnonymousParticipant participant : participants) {
            participant.setIsWinner(false);
            anonymousParticipantRepository.save(participant);
        }
    }

    public List<SurveyResponseDTO.SurveyEventWinners> getSurveyEventWinners(Long surveyId, String userId) {
        Member member = memberRepository.findByUserIdAndStatusNot(userId, UserStatus.DELETED)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));

        List<AnonymousParticipant> winners = anonymousParticipantRepository.findBySurveyAndIsWinnerTrue(survey);

        return winners.stream()
                .map(SurveyConverter::toSurveyEventWinners)
                .collect(Collectors.toList());
    }

}
