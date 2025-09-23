package com.itau.thecatapi.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendBreedInfoEmail(String toEmail, String subject, String breedInfo, List<String> imageUrls) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);

            String htmlContent = buildEmailContent(breedInfo, imageUrls);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            logger.info("Email enviado com sucesso para: {}", toEmail);

        } catch (Exception e) {
            logger.error("Erro ao enviar email para {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Falha no envio do email", e);
        }
    }

    private String buildEmailContent(String breedInfo, List<String> imageUrls) {
        StringBuilder html = new StringBuilder();
        html.append("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: #333;">Informações sobre Raças de Gatos</h2>
                <div style="background-color: #f5f5f5; padding: 15px; border-radius: 5px;">
            """);
        html.append(breedInfo.replace("\n", "<br>"));
        html.append("</div>");

        if (imageUrls != null && !imageUrls.isEmpty()) {
            html.append("""
                <h3 style="color: #666;">📸 Imagens:</h3>
                <div style="display: flex; flex-wrap: wrap;">
                """);
            for (String url : imageUrls) {
                html.append(String.format("""
                    <div style="margin: 10px;">
                        <img src="%s" width="200" style="border-radius: 5px; border: 1px solid #ddd;"/>
                    </div>
                    """, url));
            }
            html.append("</div>");
        }

        html.append("""
            <br>
            <p style="color: #999; font-size: 12px;">
                Este email foi gerado automaticamente pelo Cat API Service.
            </p>
            </body>
            </html>
            """);

        return html.toString();
    }
}