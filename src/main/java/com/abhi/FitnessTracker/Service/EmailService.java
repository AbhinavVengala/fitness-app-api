package com.abhi.FitnessTracker.Service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendPasswordResetEmail(String to, String token) {
        try {
             if (mailSender != null) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                
                helper.setFrom(senderEmail);
                helper.setTo(to);
                helper.setSubject("Reset Your Password - Fitness Tracker");
                
                String resetLink = "http://localhost:5173/reset-password?token=" + token;
                
                String htmlContent = String.format(
                    "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "  <div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>" +
                    "    <h2 style='color: #4f46e5;'>Fitness Tracker</h2>" +
                    "    <p>Hello,</p>" +
                    "    <p>You requested a password reset. Click the button below to reset your password:</p>" +
                    "    <div style='text-align: center; margin: 30px 0;'>" +
                    "      <a href='%s' style='background-color: #4f46e5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; font-weight: bold;'>Reset Password</a>" +
                    "    </div>" +
                    "    <p>Or copy this link: <br><a href='%s'>%s</a></p>" +
                    "    <p>This link will expire in 15 minutes.</p>" +
                    "    <p>If you didn't ask for this, you can ignore this email.</p>" +
                    "  </div>" +
                    "</body>" +
                    "</html>",
                    resetLink, resetLink, resetLink
                );
                
                helper.setText(htmlContent, true);
                mailSender.send(message);
                logger.info("Password reset email sent to: {}", to);
             } else {
                 logger.warn("JavaMailSender not configured.");
             }
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", to, e);
            throw new RuntimeException("Failed to send email");
        }
    }
}
