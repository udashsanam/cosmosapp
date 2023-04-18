package com.cosmos.email.helper;

import com.cosmos.email.model.EmailSentStatus;
import com.cosmos.email.model.EmailSentStatusRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EmailHtmlSender {

    @Autowired
    private EmailSentStatusRepo emailSentStatusRepo;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailHtmlSender.class);

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Async
    public void sendPasswordResetCode(String email, String code) {
        LOGGER.info("Sending Email.....");

        Context context = new Context();
        context.setVariable("code", code);

        sendEmail(email, "email/forget-password", "Cosmos Astrology - Change Password", context);

    }

    @Async
    public void sendVerifyEmail(String email, String code, String password, String name) {
        LOGGER.info("Sending Email.....");
        String url = "http://www.system.cosmosastrology.com/#/api/verify-email?token=" + code + "&email=" + email;


        Context context = new Context();
        context.setVariable("verifyEmailUrl", url);
        context.setVariable("password", password);
        context.setVariable("name", name);

        sendEmail(email, "email/verify-email","Cosmos Astrology - Verify Email", context);
    }

    private void sendEmail(String email, String template, String subject, Context context) {
        String body = templateEngine.process(template, context);
        EmailStatus emailStatus = null;

        emailStatus = emailSender.sendHtml(email, subject, body, "noreply.cosmosastrology@gmail.com", null);


        if (emailStatus.isError()) {
            LOGGER.info("Cannot Send Email...");
            LOGGER.error(emailStatus.getErrorMessage());
            emailSentStatusRepo.save(new EmailSentStatus(email, false, emailStatus.getErrorMessage()));
        } else {
            emailSentStatusRepo.save(new EmailSentStatus(email, true, null));
            LOGGER.info("Email Sent to " + email);
        }
    }


}