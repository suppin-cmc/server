package com.cmc.suppin.event.crawl.service;

import com.cmc.suppin.event.crawl.controller.dto.CrawlResponseDTO;
import com.cmc.suppin.event.crawl.converter.CommentConverter;
import com.cmc.suppin.event.crawl.converter.DateConverter;
import com.cmc.suppin.event.crawl.domain.Comment;
import com.cmc.suppin.event.crawl.domain.repository.CommentRepository;
import com.cmc.suppin.event.crawl.exception.CrawlErrorCode;
import com.cmc.suppin.event.crawl.exception.CrawlException;
import com.cmc.suppin.event.events.domain.Event;
import com.cmc.suppin.event.events.domain.repository.EventRepository;
import com.cmc.suppin.global.enums.UserStatus;
import com.cmc.suppin.member.domain.Member;
import com.cmc.suppin.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CrawlService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    public String checkExistingComments(String url, String userId) {
        Member member = memberRepository.findByUserIdAndStatusNot(userId, UserStatus.DELETED)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        List<Comment> existingComments = commentRepository.findByUrl(url);
        if (!existingComments.isEmpty()) {
            LocalDateTime firstCommentDate = existingComments.get(0).getCrawlTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return "동일한 URL의 댓글을 " + firstCommentDate.format(formatter) + " 일자에 수집한 이력이 있습니다.";
        }

        return null;
    }

    public CrawlResponseDTO.CrawlResultDTO crawlYoutubeComments(String url, Long eventId, String userId, boolean forceUpdate) {
        Member member = memberRepository.findByUserIdAndStatusNot(userId, UserStatus.DELETED)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        Event event = eventRepository.findByIdAndMemberId(eventId, member.getId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (forceUpdate) {
            // 기존 댓글 삭제
            commentRepository.deleteByUrlAndEventId(url, eventId);

            // 삭제 후, 확인을 위한 로그 출력 또는 추가 검증
            if (commentRepository.existsByUrlAndEventId(url, eventId)) {
                throw new RuntimeException("기존 댓글 삭제에 실패했습니다.");
            }
        } else {
            // 기존 댓글이 존재하는 경우: 크롤링을 중지하고 예외를 던집니다.
            if (commentRepository.existsByUrlAndEventId(url, eventId)) {
                throw new CrawlException(CrawlErrorCode.DUPLICATE_URL);
            }
        }

        // 크롤링 코드 실행
        String chromeDriverPath = System.getenv("CHROME_DRIVER_PATH");
        if (chromeDriverPath != null && !chromeDriverPath.isEmpty()) {
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        } else {
            throw new RuntimeException("CHROME_DRIVER_PATH 환경 변수가 설정되지 않았습니다.");
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--blink-settings=imagesEnabled=false"); // 이미지 로딩 비활성화
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL); // 페이지 로드 전략 설정

        WebDriver driver = new ChromeDriver(options);
        driver.get(url);

        Set<String> uniqueComments = new HashSet<>();

        try {
            Thread.sleep(5000); // 초기 로딩 대기

            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

            // Queue와 관련된 로직
            Queue<Long> heightQueue = new LinkedList<>();
            int maxQueueSize = 50;
            int enqueueCount = 0;
            int retryCount = 0;

            while (true) {
                jsExecutor.executeScript("window.scrollTo(0, document.documentElement.scrollHeight);");
                Thread.sleep(100); // 0.1초 대기

                long newPageHeight = (long) jsExecutor.executeScript("return document.documentElement.scrollHeight");

                if (enqueueCount > maxQueueSize) {
                    break;
                }

                if (heightQueue.isEmpty()) {
                    heightQueue.offer(newPageHeight);
                    enqueueCount++;
                } else {
                    if (heightQueue.peek().equals(newPageHeight)) {
                        heightQueue.offer(newPageHeight);
                        enqueueCount++;
                    } else {
                        heightQueue.clear();
                        heightQueue.offer(newPageHeight);
                        enqueueCount = 1;
                    }
                }

                // 댓글 파싱
                String pageSource = driver.getPageSource();
                Document doc = Jsoup.parse(pageSource);
                Elements comments = doc.select("ytd-comment-thread-renderer");

                for (Element commentElement : comments) {
                    String author = commentElement.select("#author-text span").text();
                    String text = commentElement.select("#content yt-attributed-string#content-text").text();
                    String time = commentElement.select("#header-author #published-time-text").text().replace("(수정됨)", "");

                    if (!uniqueComments.contains(text)) {
                        uniqueComments.add(text);

                        // 엔티티 저장
                        LocalDateTime actualCommentDate = DateConverter.convertRelativeTime(time);
                        Comment comment = CommentConverter.toCommentEntity(author, text, actualCommentDate, url, event);
                        comment.setCrawlTime(LocalDateTime.now());
                        commentRepository.save(comment);
                    }
                }

                // 크롤링 중간 중복 확인 및 종료 조건
                if (comments.size() == uniqueComments.size()) {
                    if (retryCount >= 3) {
                        break; // 3번 이상 시도해도 새로운 댓글이 로드되지 않으면 종료합니다.
                    }
                    retryCount++;
                } else {
                    retryCount = 0;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // InterruptedException을 받을 때 인터럽트 상태를 복구
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return CommentConverter.toCrawlResultDTO(LocalDateTime.now(), uniqueComments.size());
    }


}

