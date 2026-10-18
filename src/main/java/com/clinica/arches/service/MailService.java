package com.clinica.arches.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final ObjectProvider<JavaMailSender> sender;
    private final String remitente;
    private final boolean mostrarEnlaceEnLog;

    public MailService(ObjectProvider<JavaMailSender> sender,
                       @Value("${app.mail.from:no-reply@arches.local}") String remitente,
                       @Value("${app.mail.log-links:false}") boolean mostrarEnlaceEnLog) {
        this.sender = sender;
        this.remitente = remitente;
        this.mostrarEnlaceEnLog = mostrarEnlaceEnLog;
    }

    public void enviarRecuperacion(String correo, String nombre, String enlace) {
        JavaMailSender s = sender.getIfAvailable();
        if (s == null) {
            log.warn("SMTP no configurado (spring.mail.host): no se envió el correo de recuperación a {}", correo);
            if (mostrarEnlaceEnLog) {
                log.warn("[solo desarrollo] enlace de recuperación: {}", enlace);
            }
            return;
        }
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom(remitente);
        m.setTo(correo);
        m.setSubject("ARCHES · Recuperación de contraseña");
        m.setText("Hola " + nombre + ",\n\n"
                + "Recibimos una solicitud para restablecer su contraseña. Use este enlace (vence en 30 minutos "
                + "y solo funciona una vez):\n\n" + enlace + "\n\n"
                + "Si usted no lo solicitó, ignore este mensaje.");
        s.send(m);
    }
}
