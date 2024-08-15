package com.cmc.suppin.event.survey.domain.repository;

import com.cmc.suppin.event.survey.domain.AnonymousParticipant;
import com.cmc.suppin.event.survey.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnonymousParticipantRepository extends JpaRepository<AnonymousParticipant, Long> {
    boolean existsByPhoneNumberAndSurveyId(String phoneNumber, Long surveyId);

    Optional<AnonymousParticipant> findByIdAndSurveyIdAndIsWinnerTrue(Long id, Long surveyId);

    List<AnonymousParticipant> findBySurveyIdAndIsWinnerTrue(Long surveyId);

    List<AnonymousParticipant> findBySurveyAndIsWinnerTrue(Survey survey);
}
