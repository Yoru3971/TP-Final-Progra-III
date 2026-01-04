package com.viandasApp.api.ServiceGenerales;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Async
    public void sendValidacionCuenta(String to, String emailContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setText(emailContent, true);
            helper.setTo(to);
            helper.setSubject("Confirma tu cuenta - ViandasApp");

            helper.setFrom(emailFrom);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("Error al enviar el correo", e);
        }
    }

    @Async
    public void sendReclamoConfirmacion(String to, String nombre, String ticketCode) {
        String contenido = buildReclamoUserEmail(nombre, ticketCode);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(contenido, true);
            helper.setTo(to);
            helper.setSubject("Reclamo Recibido - Ticket: " + ticketCode);
            helper.setFrom(emailFrom);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("Error enviando confirmación de reclamo: " + e.getMessage());
        }
    }

    @Async
    public void sendReclamoNotificacionAdmin(String ticketCode, String categoria, String descripcion, String usuarioEmail) {
        String contenido = buildReclamoAdminEmail(ticketCode, categoria, descripcion, usuarioEmail);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(contenido, true);
            //lo enviamos al mail de la app, se podria cambiar a uno especializado para reclamos o de admins
            helper.setTo(emailFrom);
            helper.setSubject("NUEVO RECLAMO - " + categoria + " [" + ticketCode + "]");
            helper.setFrom(emailFrom);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("Error enviando notificación al admin: " + e.getMessage());
        }
    }

    // HTML para el USUARIO
    private String buildReclamoUserEmail(String nombre, String ticket) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #fff7ed; }
                    .container { max-width: 600px; margin: 0 auto; padding: 40px 20px; }
                    .card { background-color: #ffffff; border-radius: 12px; padding: 40px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); border-top: 6px solid #eb8334; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .title { color: #eb8334; font-size: 24px; font-weight: bold; margin: 0; }
                    .text { color: #555555; font-size: 16px; line-height: 1.6; }
                    .ticket-box { background-color: #fff7ed; border: 1px dashed #fe9039; border-radius: 8px; padding: 15px; text-align: center; margin: 25px 0; }
                    .ticket-code { color: #e68033; font-size: 28px; font-weight: bold; letter-spacing: 2px; }
                    .ticket-label { color: #e07b3d; font-size: 12px; text-transform: uppercase; letter-spacing: 1px; font-weight: bold; }
                    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #999999; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="card">
                        <div class="header">
                            <h1 class="title">Reporte Recibido</h1>
                        </div>
                        
                        <p class="text">Hola,</p>
                        <p class="text">Hemos recibido tu reporte correctamente. Nuestro equipo de soporte ya tiene constancia del problema y lo revisará a la brevedad.</p>
                        
                        <div class="ticket-box">
                            <div class="ticket-label">Tu código de seguimiento</div>
                            <div class="ticket-code">%s</div>
                        </div>
                        
                        <p class="text">Si necesitamos más información, te contactaremos a este mismo correo.</p>
                        <p class="text" style="margin-top: 30px;">Atte.<br><strong style="color: #eb8334;">Equipo MiViandita</strong></p>
                    </div>
                    
                    <div class="footer">
                        &copy; 2025 MiViandita - Tus viandas favoritas
                    </div>
                </div>
            </body>
            </html>
            """.formatted(ticket);
    }

    // HTML para el ADMIN
    private String buildReclamoAdminEmail(String ticket, String cat, String desc, String userEmail) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; }
                    .container { max-width: 600px; margin: 0 auto; padding: 40px 20px; }
                    .card { background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
                    .header { background-color: #eb8334; padding: 20px; text-align: center; }
                    .header h2 { color: #ffffff; margin: 0; font-size: 20px; text-transform: uppercase; letter-spacing: 1px; }
                    .content { padding: 30px; }
                    .row { margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 15px; }
                    .label { color: #999; font-size: 12px; text-transform: uppercase; font-weight: bold; display: block; margin-bottom: 5px; }
                    .value { color: #333; font-size: 16px; font-weight: 500; }
                    .badge { background-color: #fff7ed; color: #e68033; padding: 4px 10px; border-radius: 4px; border: 1px solid #fe9039; font-weight: bold; font-size: 14px; display: inline-block;}
                    .description-box { background-color: #fcfcfc; border-left: 4px solid #fe9039; padding: 15px; margin-top: 10px; color: #555; font-style: italic; line-height: 1.5; }
                    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #aaa; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="card">
                        <div class="header">
                            <h2>Nuevo Reclamo</h2>
                        </div>
                        <div class="content">
                            <div class="row">
                                <span class="label">Ticket ID</span>
                                <span class="value" style="font-family: monospace; font-size: 18px;">%s</span>
                            </div>
                            
                            <div class="row">
                                <span class="label">Categoría</span>
                                <span class="badge">%s</span>
                            </div>

                            <div class="row">
                                <span class="label">Usuario Reportante</span>
                                <a href="mailto:%s" style="color: #eb8334; text-decoration: none; font-weight: bold;">%s</a>
                            </div>

                            <div style="margin-top: 25px;">
                                <span class="label">Detalle del problema</span>
                                <div class="description-box">
                                    "%s"
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="footer">
                        Sistema de Reportes - MiViandita
                    </div>
                </div>
            </body>
            </html>
            """.formatted(ticket, cat, userEmail, userEmail, desc);
    }
}
