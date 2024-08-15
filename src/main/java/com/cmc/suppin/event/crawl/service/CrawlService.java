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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            LocalDateTime firstCommentDate = existingComments.get(0).getCreatedAt();
            return "동일한 URL의 댓글을 " + firstCommentDate.toLocalDate() + " 일자에 수집한 이력이 있습니다.";
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
        } else {
            // 기존 댓글이 존재하는 경우: 크롤링을 중지하고 예외를 던집니다.
            // 기존 댓글이 존재하지 않는 경우: 새로운 댓글을 크롤링하고 이를 DB에 저장합니다.

            List<Comment> existingComments = commentRepository.findByUrlAndEventId(url, eventId);
            if (!existingComments.isEmpty()) {
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

        WebDriver driver = new ChromeDriver(options);
        driver.get(url);

        Set<String> uniqueComments = new HashSet<>();

        try {
            Thread.sleep(5000); // 초기 로딩 대기

            long endTime = System.currentTimeMillis() + 600000; // 스크롤 시간을 10분으로 설정 (600,000ms)
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

            int previousCommentCount = 0;
            int currentCommentCount;

            while (System.currentTimeMillis() < endTime) {
                jsExecutor.executeScript("window.scrollTo(0, document.documentElement.scrollHeight);");

                Thread.sleep(1000); // 1초 대기

                String pageSource = driver.getPageSource();
                Document doc = Jsoup.parse(pageSource);
                Elements comments = doc.select("ytd-comment-thread-renderer");

                currentCommentCount = comments.size();

                LocalDateTime crawlTime = LocalDateTime.now();

                for (Element commentElement : comments) {
                    String author = commentElement.select("#author-text span").text();
                    String text = commentElement.select("#content yt-attributed-string#content-text").text();
                    String time = commentElement.select("#header-author #published-time-text").text().replace("(수정됨)", "");

                    if (!uniqueComments.contains(text)) {
                        uniqueComments.add(text);

                        // 엔티티 저장
                        LocalDateTime actualCommentDate = DateConverter.convertRelativeTime(time);
                        Comment comment = CommentConverter.toCommentEntity(author, text, actualCommentDate, url, event);
                        comment.setCrawlTime(crawlTime);
                        commentRepository.save(comment);
                    }
                }

                // 더 이상 새로운 댓글이 없을 때, 크롤링 종료
                if (currentCommentCount == previousCommentCount) {
                    break; // 새로운 댓글이 로드되지 않으면 루프를 종료합니다.
                }
                previousCommentCount = currentCommentCount;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return CommentConverter.toCrawlResultDTO(LocalDateTime.now(), uniqueComments.size());
    }
}

