package com.stayFinder.proyectoFinal.services.emailService.implementations;

import com.stayFinder.proyectoFinal.services.emailService.interfaces.EmailServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailServiceInterface {

    private final JavaMailSender mailSender;

    @Override
    @org.springframework.scheduling.annotation.Async
    public void sendReservationConfirmation(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);

            System.out.println("📧 Email de confirmación enviado a: " + toEmail);
        } catch (Exception e) {
            System.out.println("⚠️ Simulación de envío de email (confirmación): " + e.getMessage());
        }
    }

    @Override
    @org.springframework.scheduling.annotation.Async
    public void sendReservationCancellation(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);

            System.out.println("📧 Email de cancelación enviado a: " + toEmail);
        } catch (Exception e) {
            System.out.println("⚠️ Simulación de envío de email (cancelación): " + e.getMessage());
        }
    }

    @Override
    @org.springframework.scheduling.annotation.Async
    public void sendHostApplicationDecision(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);

            System.out.println("📧 Email de decisión de anfitrión enviado a: " + toEmail);
        } catch (Exception e) {
            System.out.println("⚠️ Simulación de envío de email (decisión anfitrión): " + e.getMessage());
        }
    }
}
