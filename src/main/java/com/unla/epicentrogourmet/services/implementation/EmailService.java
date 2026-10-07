package com.unla.epicentrogourmet.services.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.unla.epicentrogourmet.services.IEmailService;

@Service("emailService")
public class EmailService implements IEmailService {

	private static final Logger log = LoggerFactory.getLogger(EmailService.class);

	private final JavaMailSender mailSender;
	private final String remitente;

	public EmailService(JavaMailSender mailSender, @Value("${app.mail.from}") String remitente) {
		this.mailSender = mailSender;
		this.remitente = remitente;
	}

	@Override
	public boolean enviarCredencialesEmpleado(String destinatario, String nombre, String username, String password) {
		SimpleMailMessage mensaje = new SimpleMailMessage();
		mensaje.setFrom(remitente);
		mensaje.setTo(destinatario);
		mensaje.setSubject("Epicentro Gourmet - Tus credenciales de acceso");
		mensaje.setText("Hola " + nombre + ",\n\n"
				+ "Fuiste dado de alta como personal en Epicentro Gourmet. Para ingresar a la aplicación usá:\n\n"
				+ "  Usuario: " + username + "\n"
				+ "  Contraseña: " + password + "\n\n"
				+ "Epicentro Gourmet");
		try {
			mailSender.send(mensaje);
			return true;
		} catch (Exception e) {
			log.warn("No se pudo enviar el mail de credenciales a {}: {}", destinatario, e.getMessage());
			return false;
		}
	}
}
