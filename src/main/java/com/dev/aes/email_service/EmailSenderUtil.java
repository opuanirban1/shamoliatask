package com.dev.aes.email_service;

import com.dev.aes.exception.OcrDmsException;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailSenderUtil {
    private final AesMailSender aesMailSender;
    private final FreeMarkerConfigurer config;

    public EmailSenderUtil(AesMailSender aesMailSender, FreeMarkerConfigurer config) {
        this.aesMailSender = aesMailSender;
        this.config = config;
    }
    public void sendUserRegistrationMail(String email){
        Map<String, Object> model = new HashMap<>();
        model.put("email", email);
        try{
            String messageBody = FreeMarkerTemplateUtils.processTemplateIntoString(
                    config.getConfiguration().getTemplate("user_registration.ftl"), model);
            String subject = "User Registration Confirmation";
            aesMailSender.sendMail(messageBody, email, subject);
        }catch (Exception e){
            throw new OcrDmsException("Error occurred while sending the confirmation mail to created user");
        }
    }
    public void passwordChangeConfirmationEmail(String email){
        Map<String, Object> model = new HashMap<>();
        model.put("email", email);
        try {
            String messageBody = FreeMarkerTemplateUtils.processTemplateIntoString(
                    config.getConfiguration().getTemplate("password_change_confirmation.ftl"), model);
            String subject = "Password Change confirmation";
            aesMailSender.sendMail(messageBody, email, subject);
        } catch (MessagingException | TemplateException | IOException ie) {
            throw new OcrDmsException(ie.getMessage());
        }
    }
    public void sendPasswordVerificationEmail(String url, String email){
        Map<String, Object> model = new HashMap<>();
        model.put("url", url);
        model.put("email", email);
        try {
            String subject = "Password change verification Link";
            String messageBody = FreeMarkerTemplateUtils.processTemplateIntoString(
                    config.getConfiguration().getTemplate("password_verification_mail.ftl"), model);
            aesMailSender.sendMail(messageBody, email, subject);
        } catch (MessagingException | TemplateException | IOException ie) {
            throw new OcrDmsException(ie.getMessage());
        }
    }
}
