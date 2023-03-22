package com.cosmos.email.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class EmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSender.class);

    @Autowired
    private JavaMailSender javaMailSender;

    public EmailStatus sendPlainText(String to, String subject, String text, String from, InternetAddress addr[]) {
        return sendMime(to, subject, text, false, from, addr);
    }

    public EmailStatus sendHtml(String to, String subject, String htmlBody, String from, InternetAddress addr[]) {
        return sendMime(to, subject, htmlBody, true, from, addr);
    }

    private EmailStatus sendMime(String to, String subject, String text, Boolean isHtml, String from, InternetAddress addr[]) {
        try {
            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setTo(to);
            helper.setSubject(subject);

            if (addr != null)
                helper.setBcc(addr);

            helper.setFrom(new InternetAddress("recommendertest83@gmail.com", from));
            helper.setText(text, isHtml);

            javaMailSender.send(mail);
            LOGGER.info("Send email '{}' to: {}", subject, to);

            return new EmailStatus(to, subject, text).success();
        } catch (Exception e) {
            LOGGER.error(String.format("Problem with sending email to: {}, error message: {}", to, e.getMessage()));
            return new EmailStatus(to, subject, text).error(e.getMessage());
        }
    }
}