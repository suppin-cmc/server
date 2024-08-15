package com.cmc.suppin.event.survey.domain.repository;

import com.cmc.suppin.event.survey.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Optional<Survey> findByUuid(String uuid);
}
