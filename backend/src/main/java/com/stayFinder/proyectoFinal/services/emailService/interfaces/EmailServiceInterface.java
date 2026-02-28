package com.stayFinder.proyectoFinal.services.emailService.interfaces;

public interface EmailServiceInterface {
    void sendReservationConfirmation(String toEmail, String subject, String body);
    void sendReservationCancellation(String toEmail, String subject, String body);
}
