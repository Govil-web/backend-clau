package com.proyectofinal.clave_compas.service;

import com.proyectofinal.clave_compas.dto.UserDTO;
import com.proyectofinal.clave_compas.bd.clavecompas.entities.ReservationEntity;
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
import java.time.temporal.ChronoUnit;
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

    public void sendReservationConfirmationEmail(UserDTO user, ReservationEntity reservation) {
        try {
            log.info("Preparing reservation confirmation email for: {}", user.email());

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(user.email());
            helper.setSubject("Confirmación de Reserva en Clave&Compás");
            
            // Calculate total days of reservation
            long daysOfReservation = ChronoUnit.DAYS.between(
                reservation.getStartDate(),
                reservation.getEndDate()
            ) + 1; // Including both start and end date

            // Product price - ensure it's not null
            double productPrice = reservation.getProduct().getPrice() != null ? 
                reservation.getProduct().getPrice().doubleValue() : 0.0;

            // Reservation quantity
            int reservationQuantity = reservation.getQuantity();

            // Calculate total price
            double totalPrice = productPrice * reservationQuantity * daysOfReservation;
            
            Context context = new Context();
            context.setVariable("name", user.firstName() + " " + user.lastName());
            context.setVariable("reservationId", reservation.getIdReservation());
            context.setVariable("productName", reservation.getProduct().getName());
            context.setVariable("productDescription", reservation.getProduct().getDescription());
            context.setVariable("quantity", reservationQuantity);
            context.setVariable("startDate", reservation.getStartDate());
            context.setVariable("endDate", reservation.getEndDate());
            context.setVariable("daysOfReservation", daysOfReservation);
            context.setVariable("pricePerDay", productPrice);
            context.setVariable("totalPrice", totalPrice);
            
            String htmlContent = templateEngine.process("reservation-confirmation", context);
            if (htmlContent == null || htmlContent.isEmpty()) {
                log.error("Processed template is empty for: {}", user.email());
                helper.setText(String.format(
                    "Hola %s,\n\nTu reserva #%d para %s ha sido confirmada.\n\n" +
                    "Producto: %s\n" +
                    "Cantidad: %d\n" +
                    "Fecha de inicio: %s\n" +
                    "Fecha de fin: %s\n" +
                    "Días de reserva: %d\n" +
                    "Precio por día: $%.2f\n" +
                    "Precio total: $%.2f\n\n" +
                    "Gracias por usar Clave&Compás!",
                    user.firstName(), reservation.getIdReservation(), 
                    reservation.getProduct().getName(),
                    reservation.getProduct().getName(),
                    reservation.getQuantity(),
                    reservation.getStartDate(), 
                    reservation.getEndDate(),
                    daysOfReservation,
                    reservation.getProduct().getPrice(),
                    totalPrice
                ), false);
            } else {
                helper.setText(htmlContent, true);
            }
            
            emailSender.send(message);
            log.info("Reservation confirmation email sent successfully to: {}", user.email());
        } catch (MessagingException e) {
            log.error("Error sending reservation email to {}: {}", user.email(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending reservation email to {}: {}", user.email(), e.getMessage(), e);
        }
    }
}