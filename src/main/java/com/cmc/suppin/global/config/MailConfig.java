package com.cmc.suppin.global.config;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class MailConfig {
    private final JavaMailSender javaMailSender;

    public boolean sendMail(String toEmail, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress("suppindev@gmail.com", "Suppin", "UTF-8"));
            helper.setTo(toEmail);
            helper.setSubject("Suppin 인증번호");

            // Format the current date and time
            String formattedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm"));

            // Use StringBuilder to construct the HTML email body
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<!DOCTYPE html>")
                    .append("<html lang=\"en\">")
                    .append("<head>")
                    .append("<meta charset=\"UTF-8\">")
                    .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                    .append("<title>Suppin Email Verification</title>")
                    .append("</head>")
                    .append("<body style=\"font-family: Arial, sans-serif;\">")
                    .append("<div style=\"padding: 20px; border: 1px solid #eaeaea; max-width: 600px; margin: 0 auto;\">")
                    .append("<div style=\"padding: 20px; border-bottom: 1px solid #eaeaea; text-align: center;\">")
                    .append("<img src=\"cid:suppinLogo\" alt=\"Suppin Logo\" style=\"width: 100%; height: 120px; object-fit: cover;\">")
                    .append("<h2 style=\"color: #333;\"><span style=\"color: #1a73e8;\">[Suppin]</span> 인증번호를 안내해 드립니다.</h2>")
                    .append("</div>")
                    .append("<div style=\"padding: 20px;\">")
                    .append("<p style=\"color: #000000;\">안녕하세요, Suppin을 이용해주셔서 감사합니다 :)</p>")
                    .append("<p style=\"color: #000000;\">Suppin 회원가입을 위해 인증번호 안내 드립니다. 아래 인증번호를 5분 이내로 입력하여 이메일 인증을 완료해 주세요.</p>")
                    .append("<div style=\"font-size: 24px; font-weight: bold; margin: 20px 0; color: #1a73e8; text-align: center;\">") // 인증번호 색상
                    .append(code)
                    .append("</div>")
                    .append("<table style=\"width: 100%; border-collapse: collapse;\">")
                    .append("<tbody>")
                    .append("<tr><td style=\"padding: 10px; border: 1px solid #eaeaea;\">인증 번호</td><td style=\"padding: 10px; border: 1px solid #eaeaea;\">")
                    .append(code)
                    .append("</td></tr>")
                    .append("<tr><td style=\"padding: 10px; border: 1px solid #eaeaea;\">요청 일시</td><td style=\"padding: 10px; border: 1px solid #eaeaea;\">")
                    .append(formattedDateTime)
                    .append("</td></tr>")
                    .append("</tbody>")
                    .append("</table>")
                    .append("</div>")
                    .append("<div style=\"padding: 20px; border-top: 1px solid #eaeaea; text-align: center; color: #999;\">")
                    .append("<p>감사합니다.</p>")
                    .append("<p style=\"font-size: 12px;\">※ 본 메일은 Suppin 서비스 이용에 관한 안내 메일입니다.</p>")
                    .append("</div>")
                    .append("</div>")
                    .append("</body>")
                    .append("</html>");

            helper.setText(emailBody.toString(), true);

            // Add inline image
            ClassPathResource logoImage = new ClassPathResource("static/images/suppin-logo2.png");
            helper.addInline("suppinLogo", logoImage);

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}




