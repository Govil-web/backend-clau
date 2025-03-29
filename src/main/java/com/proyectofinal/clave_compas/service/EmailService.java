package com.proyectofinal.clave_compas.service;

import com.proyectofinal.clave_compas.dto.UserDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendRegistrationConfirmationEmail(UserDTO user) {
        try {
            log.info("Preparando correo de confirmación para: {}", user.email());

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(user.email());
            helper.setSubject("Bienvenido a Clave&Compás - Confirmación de Registro");
            
            Context context = new Context();
            context.setVariable("name", user.firstName() + " " + user.lastName());
            context.setVariable("email", user.email());
            context.setVariable("loginUrl", frontendUrl + "/login");
            
            log.info("Procesando plantilla para: {}", user.email());

            String htmlContent = templateEngine.process("registration-confirmation", context);
            if (htmlContent == null || htmlContent.isEmpty()) {
                log.error("La plantilla procesada está vacía para: {}", user.email());
                // Fallback to a simple text message if template processing fails
                helper.setText("Bienvenido a Clave&Compás, " + user.firstName() + "! Tu cuenta ha sido creada exitosamente.", false);
            } else {
                helper.setText(htmlContent, true);
            }
            log.info("Enviando correo a: {}", user.email());
            emailSender.send(message);
            log.info("Correo enviado exitosamente a: {}", user.email());
        } catch (MessagingException e) {
            log.error("Error al enviar correo a {}: {}", user.email(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado al enviar correo a {}: {}", user.email(), e.getMessage(), e);
        }
    }
    
    public void resendRegistrationConfirmationEmail(UserDTO user) {
        log.info("Reenviando correo de confirmación a: {}", user.email());
        sendRegistrationConfirmationEmail(user);
    }
}