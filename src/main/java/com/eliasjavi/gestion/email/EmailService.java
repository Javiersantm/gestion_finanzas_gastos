package com.eliasjavi.gestion.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            // Enviamos el correo
            mailSender.send(message);
            System.out.println("📧 Email enviado correctamente a: " + to);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar email a " + to + ": " + e.getMessage());
            // No lanzamos excepción para no romper la transacción del gasto/bizum si falla el email
        }
    }
}