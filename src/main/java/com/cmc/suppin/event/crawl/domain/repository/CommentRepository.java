package com.cmc.suppin.event.crawl.domain.repository;

import com.cmc.suppin.event.crawl.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventId(Long eventId);

    List<Comment> findByUrl(String url);

    void deleteByUrlAndEventId(String url, Long eventId);

    Page<Comment> findByEventIdAndUrlAndCommentDateBefore(Long eventId, String url, LocalDateTime endDate, Pageable pageable);

    List<Comment> findByUrlAndEventId(String url, Long eventId);

    int countByEventIdAndUrl(Long eventId, String url);

    List<Comment> findByEventIdAndCommentDateBetween(Long eventId, LocalDateTime start, LocalDateTime end);

    List<Comment> findByEventIdAndCommentTextContaining(Long eventId, String keyword);

    List<Comment> findByEventIdAndIsWinnerTrue(Long eventId);


    boolean existsByUrlAndEventId(String url, Long eventId);
}
