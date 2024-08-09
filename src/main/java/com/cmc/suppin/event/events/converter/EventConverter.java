package com.cmc.suppin.event.events.converter;

import com.cmc.suppin.event.events.controller.dto.EventRequestDTO;
import com.cmc.suppin.event.events.controller.dto.EventResponseDTO;
import com.cmc.suppin.event.events.domain.Event;
import com.cmc.suppin.global.enums.EventType;
import com.cmc.suppin.member.domain.Member;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class EventConverter {

    public static Event toCommentEventEntity(EventRequestDTO.CommentEventCreateDTO request, Member member) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return Event.builder()
                .type(EventType.COMMENT)
                .title(request.getTitle())
                .description(request.getDescription())
                .url(request.getUrl())
                .startDate(LocalDate.parse(request.getStartDate(), formatter).atStartOfDay())
                .endDate(LocalDate.parse(request.getEndDate(), formatter).atStartOfDay())
                .announcementDate(LocalDate.parse(request.getAnnouncementDate(), formatter).atStartOfDay())
                .member(member)
                .build();
    }

    public static Event toSurveyEventEntity(EventRequestDTO.SurveyEventCreateDTO request, Member member) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return Event.builder()
                .type(EventType.SURVEY)
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(LocalDate.parse(request.getStartDate(), formatter).atStartOfDay())
                .endDate(LocalDate.parse(request.getEndDate(), formatter).atStartOfDay())
                .announcementDate(LocalDate.parse(request.getAnnouncementDate(), formatter).atStartOfDay())
                .member(member)
                .build();
    }

    public static EventResponseDTO.CommentEventDetailDTO toEventDetailDTO(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return EventResponseDTO.CommentEventDetailDTO.builder()
                .type(event.getType())
                .title(event.getTitle())
                .url(event.getUrl())
                .startDate(event.getStartDate().format(formatter))
                .endDate(event.getEndDate().format(formatter))
                .announcementDate(event.getAnnouncementDate().format(formatter))
                .build();
    }

    public static EventResponseDTO.EventInfoDTO toEventInfoDTO(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Optional<String> url = Optional.empty();
        if (event.getType() == EventType.COMMENT) {
            url = Optional.ofNullable(event.getUrl());
        }

        // 설문 응답 수 합산
        int surveyAnswerCount = event.getSurveyList().stream()
                .mapToInt(survey -> survey.getAnonymousParticipantList().size())
                .sum();

        return EventResponseDTO.EventInfoDTO.builder()
                .eventId(event.getId())
                .type(event.getType())
                .title(event.getTitle())
                .url(url)
                .startDate(event.getStartDate().format(formatter))
                .endDate(event.getEndDate().format(formatter))
                .announcementDate(event.getAnnouncementDate().format(formatter))
                .surveyCount(surveyAnswerCount)
                .commentCount(event.getCommentList().size())
                .status(event.getStatus())
                .build();
    }

    public static Event toUpdatedEventEntity(EventRequestDTO.EventUpdateDTO request, Member member) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Event.EventBuilder eventBuilder = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(LocalDate.parse(request.getStartDate(), formatter).atStartOfDay())
                .endDate(LocalDate.parse(request.getEndDate(), formatter).atStartOfDay())
                .announcementDate(LocalDate.parse(request.getAnnouncementDate(), formatter).atStartOfDay())
                .member(member);

        // Only set URL if the event type is COMMENT
        if (request.getType() == EventType.COMMENT) {
            eventBuilder.url(request.getUrl());
        }

        return eventBuilder.build();
    }


}
