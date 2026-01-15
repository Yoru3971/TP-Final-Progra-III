package com.viandasApp.api.ServiceGenerales;

import com.viandasApp.api.Pedido.model.DetallePedido;
import com.viandasApp.api.Pedido.model.EstadoPedido;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    // VALIDACION EMAIL CUENTA
    @Async
    public void sendValidacionCuenta(String to, String nombre, String link) {
        String emailContent = buildValidacionCuentaEmail(nombre, link);
        sendEmail(to, "Confirma tu cuenta - MiViandita", emailContent);
    }

    @Async
    public void sendRecoveryEmail(String to, String nombre, String token) {
        String link = "http://localhost:4200/change-password?token=" + token;
        String contenido = buildRecoveryEmail(nombre, link);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(contenido, true);
            helper.setTo(to);
            helper.setSubject("Restablecer Contraseña - MiViandita");
            helper.setFrom(emailFrom);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("Error enviando email de recuperación: " + e.getMessage());
        }
    }

    // CONFIRMACION POR EMAIL AL CLIENTE DE RECLAMO ENVIADO
    @Async
    public void sendReclamoConfirmacion(String to, String nombre, String ticketCode) {
        String contenido = buildReclamoUserEmail(nombre, ticketCode);
        sendEmail(to, "Reclamo Recibido - Ticket: " + ticketCode, contenido);
    }

    // NOTIFICACION POR MAIL AL ADMIN DE UN NUEVO RECLAMO
    @Async
    public void sendReclamoNotificacionAdmin(String ticketCode, String categoria, String descripcion, String usuarioEmail) {
        String contenido = buildReclamoAdminEmail(ticketCode, categoria, descripcion, usuarioEmail);
        sendEmail(emailFrom, "NUEVO RECLAMO - " + categoria + " [" + ticketCode + "]", contenido);
    }

    // AVISO AL CLIENTE QUE SU RECLAMO TUVO CAMBIO DE ESTADO
    @Async
    public void sendCambioEstadoReclamo(String to, String nombre, String ticketCode, String nuevoEstado, String respuestaAdmin) {
        String contenido = buildCambioEstadoEmail(nombre, ticketCode, nuevoEstado, respuestaAdmin);
        sendEmail(to, "Actualización de reclamo - " + ticketCode, contenido);
    }

    @Async
    public void sendPedidoConfirmacionCliente(String to, String nombreCliente, Long pedidoId, String emprendimientoName, Double total, List<DetallePedido> items) {
        String contenido = buildPedidoClienteEmail(nombreCliente, pedidoId, emprendimientoName, total, items);
        sendEmail(to, "Tu Pedido fue recibido #" + pedidoId, contenido);
    }

    @Async
    public void sendPedidoNuevoDueno(String to, String nombreDueno, Long pedidoId, String nombreCliente, Double total, List<DetallePedido> items) {
        String contenido = buildPedidoDuenoEmail(nombreDueno, pedidoId, nombreCliente, total, items);
        sendEmail(to, "Nuevo pedido recibido #" + pedidoId, contenido);
    }

    @Async
    public void sendPedidoEstadoUpdate(String to, String nombreCliente, Long pedidoId, String emprendimientoName, EstadoPedido nuevoEstado, List<DetallePedido> items) {
        String asunto = "Actualización sobre tu pedido #" + pedidoId;
        String contenido = buildPedidoEstadoEmail(nombreCliente, pedidoId, emprendimientoName, nuevoEstado, items);
        sendEmail(to, asunto, contenido);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(content, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(emailFrom);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println("Error enviando email a " + to + ": " + e.getMessage());
        }
    }


    // -------------------  HTML BUILDERS (contenido del mail)  -------------------

    // Validación de cuenta
    private String buildValidacionCuentaEmail(String nombre, String link) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', sans-serif; background-color: #fff7ed; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 12px; padding: 40px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); border-top: 6px solid #eb8334; border-bottom: 6px solid #eb8334; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .header h1 { color: #eb8334; font-size: 26px; margin: 0; font-weight: bold; }
                    .content { color: #555; line-height: 1.6; font-size: 16px; text-align: center; }
                    .btn { display: inline-block; background-color: #eb8334; color: #ffffff !important; padding: 12px 30px; text-decoration: none; border-radius: 50px; font-weight: bold; font-size: 16px; margin: 25px 0; box-shadow: 0 4px 6px rgba(235, 131, 52, 0.3); }
                    .btn:hover { background-color: #d35400; color: #ffffff !important; }
                    .footer { text-align: center; font-size: 12px; color: #999; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                    .footer img { height: 25px; vertical-align: middle; }
                    .footer span { vertical-align: middle; margin-left: 8px; font-weight: 500; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>¡Bienvenido a MiViandita!</h1>
                    </div>
                    <div class="content">
                        <p>¡Hola <strong>%s</strong>, gracias por registrarte!</p>
                        <p>Estás a un solo paso de empezar a disfrutar de las mejores viandas.</p>
                        <p>Por favor, valida tu correo electrónico haciendo click en el siguiente botón:</p>
                        
                        <a href="%s" class="btn" style="color: #ffffff !important;">Activar mi cuenta</a>
                        
                        <p style="font-size: 13px; color: #777; margin-top: 20px;">
                           Si no creaste esta cuenta, puedes ignorar este mensaje.<br>
                           El enlace expirará en 15 minutos.
                        </p>
                    </div>
                    <div class="footer">
                        <img src="https://res.cloudinary.com/dsgqbotzi/image/upload/v1767926581/default_vianda_rb2ila.png" alt="Logo">
                        <span>MiViandita</span>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombre, link);
    }

    // Pedidos
    private String buildViandasListHtml(List<DetallePedido> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table style='width: 100%; border-collapse: collapse; margin-top: 15px;'>");
        sb.append("<tr style='background-color: #fff7ed; color: #e68033;'><th style='padding: 8px; text-align: left;'>Vianda</th><th style='padding: 8px; text-align: center;'>Cant.</th><th style='padding: 8px; text-align: right;'>Subtotal</th></tr>");

        for (DetallePedido item : items) {
            sb.append("<tr>");
            sb.append("<td style='padding: 8px; border-bottom: 1px solid #eee;'>").append(item.getVianda().getNombreVianda()).append("</td>");
            sb.append("<td style='padding: 8px; border-bottom: 1px solid #eee; text-align: center;'>").append(item.getCantidad()).append("</td>");
            sb.append("<td style='padding: 8px; border-bottom: 1px solid #eee; text-align: right;'>$").append(item.getSubtotal()).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private String buildPedidoClienteEmail(String nombre, Long id, String emprendimiento, Double total, List<DetallePedido> items) {
        String itemsHtml = buildViandasListHtml(items);
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', sans-serif; background-color: #fff7ed; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 12px; padding: 40px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); border-top: 6px solid #eb8334; border-bottom: 6px solid #eb8334; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .header h1 { color: #eb8334; font-size: 26px; margin: 0; font-weight: bold; }
                    .info { color: #555; line-height: 1.6; font-size: 16px; margin-bottom: 25px; text-align: center; }
                    .status-line { 
                        text-align: center; 
                        margin: 25px 0; 
                        font-size: 18px; 
                        padding-bottom: 15px;
                        border-bottom: 1px dashed #ddd;
                    }
                    .id-badge { background-color: #fff7ed; color: #e68033; padding: 5px 12px; border-radius: 15px; font-weight: bold; font-size: 16px; border: 1px solid #ffdcb0; }
                    .separator { color: #ccc; margin: 0 10px; }
                    .status-text { font-weight: bold; color: #f5b041; }
                    .total { text-align: right; font-size: 20px; font-weight: bold; color: #eb8334; margin-top: 20px; border-top: 1px solid #eee; padding-top: 10px; }
                    .footer { text-align: center; font-size: 12px; color: #999; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                    .footer img { height: 25px; vertical-align: middle; }
                    .footer span { vertical-align: middle; margin-left: 8px; font-weight: 500; }
                    .center-text { text-align: center; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Pedido Recibido</h1>
                    </div>
                    
                    <p class="info">Hola <strong>%s</strong>, tu pedido a <strong>%s</strong> se ha registrado correctamente y está pendiente de aprobación.</p>
                    
                    <div class="status-line">
                        <span class="id-badge">Pedido #%d</span>
                        <span class="separator">|</span>
                        <span class="status-text">PENDIENTE</span>
                    </div>
                    
                    <h3 style="color: #555; margin-top: 0;">Detalle del pedido:</h3>
                    %s
                    
                    <div class="total">Total: $%s</div>
                    
                    <p class="info center-text" style="margin-top: 30px; font-size: 14px; color: #777;">Te notificaremos cuando el emprendimiento acepte tu pedido.</p>
                    
                    <div class="footer">
                        <img src="https://res.cloudinary.com/dsgqbotzi/image/upload/v1767926581/default_vianda_rb2ila.png" alt="Logo">
                        <span>MiViandita</span>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombre, emprendimiento, id, itemsHtml, total);
    }

    private String buildPedidoDuenoEmail(String nombreDueno, Long id, String cliente, Double total, List<DetallePedido> items) {
        String itemsHtml = buildViandasListHtml(items);
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', sans-serif; background-color: #f8f9fa; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 12px; padding: 40px; border-top: 6px solid #eb8334; border-bottom: 6px solid #eb8334; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .title { color: #eb8334; font-size: 26px; font-weight: bold; margin: 0; }
                    .content { color: #555; line-height: 1.6; font-size: 16px; margin-bottom: 25px; text-align: center; }
                    .status-line {
                        text-align: center;
                        margin: 25px 0; 
                        font-size: 18px; 
                        padding-bottom: 15px;
                        border-bottom: 1px dashed #ddd;
                    }
                    .id-badge { background-color: #fff7ed; color: #e68033; padding: 5px 12px; border-radius: 15px; font-weight: bold; font-size: 16px; border: 1px solid #ffdcb0; }
                    .separator { color: #ccc; margin: 0 10px; }
                    .status-text { font-weight: bold; color: #f5b041; }
                    .total { font-size: 20px; font-weight: bold; color: #eb8334; text-align: right; margin-top: 20px; border-top: 1px solid #eee; padding-top: 10px; }
                    .footer { text-align: center; font-size: 12px; color: #999; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                    .footer img { height: 25px; vertical-align: middle; }
                    .footer span { vertical-align: middle; margin-left: 8px; font-weight: 500; }
                    .center-text { text-align: center; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="title">Nuevo Pedido</div>
                    </div>
                    
                    <p class="content">Hola <strong>%s</strong>, <strong>%s</strong> te ha realizado un pedido y espera tu respuesta.</p>
                    
                    <div class="status-line">
                        <span class="id-badge">Pedido #%d</span>
                        <span class="separator">|</span>
                        <span class="status-text">PENDIENTE</span>
                    </div>
                    
                    <h3 style="color: #555; margin-top: 0;">Detalle:</h3>
                    %s
                    
                    <div class="total">Total: $%s</div>
                    
                    <p class="content center-text" style="margin-top: 30px;">Ingresa a MiViandita para aceptar o rechazar el pedido.</p>
                    
                    <div class="footer">
                        <img src="https://res.cloudinary.com/dsgqbotzi/image/upload/v1767926581/default_vianda_rb2ila.png" alt="Logo">
                        <span>MiViandita</span>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombreDueno, cliente, id, itemsHtml, total);
    }

    private String buildPedidoEstadoEmail(String nombre, Long id, String emprendimiento, EstadoPedido estado, List<DetallePedido> items) {

        String colorEstado = estado == EstadoPedido.ACEPTADO ? "#27ae60" : "#c0392b";
        String textoEstado = estado == EstadoPedido.ACEPTADO ? "ACEPTADO" : "RECHAZADO";

        String mensajeExtra = estado == EstadoPedido.ACEPTADO
                ? "Tu pedido fue aceptado, el emprendimiento se contactará contigo para coordinar la entrega."
                : "Lo sentimos, el emprendimiento no puede tomar tu pedido en este momento.";

        String itemsHtml = buildViandasListHtml(items);
        double totalCalculado = items.stream().mapToDouble(DetallePedido::getSubtotal).sum();

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 30px auto; background: #ffffff; border-radius: 12px; padding: 40px; border-top: 6px solid #eb8334; border-bottom: 6px solid #eb8334; box-shadow: 0 4px 15px rgba(0,0,0,0.05); }
                    .header { text-align: center; margin-bottom: 25px; }
                    .header h1 { margin: 0; color: #eb8334; font-size: 26px; font-weight: bold; }
                    .body { color: #555; font-size: 16px; line-height: 1.6; }
                    .status-line { text-align: center; margin: 25px 0; font-size: 18px; padding-bottom: 15px; border-bottom: 1px dashed #ddd; }
                    .id-badge { background-color: #fff7ed; color: #e68033; padding: 5px 12px; border-radius: 15px; font-weight: bold; font-size: 16px; border: 1px solid #ffdcb0; }
                    .separator { color: #ccc; margin: 0 10px; }
                    .status-text { font-weight: bold; }
                    .message { margin-top: 30px; margin-bottom: 10px; text-align: center; color: #666; font-size: 16px; }
                    .total { text-align: right; font-size: 20px; font-weight: bold; color: #eb8334; margin-top: 15px; border-top: 1px solid #eee; padding-top: 10px; }
                    .footer { text-align: center; font-size: 12px; color: #999; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                    .footer img { height: 25px; vertical-align: middle; }
                    .footer span { vertical-align: middle; margin-left: 8px; font-weight: 500; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Actualización de Pedido</h1>
                    </div>
                    <div class="body">
                        <p style="text-align: center;">Hola <strong>%s</strong>, hay novedades sobre tu pedido a <strong>%s</strong>.</p>
                        
                        <div class="status-line">
                            <span class="id-badge">Pedido #%d</span>
                            <span class="separator">|</span>
                            <span class="status-text" style="color: %s;">%s</span>
                        </div>
                        
                        <h4 style="color: #555; margin-bottom: 5px; margin-top: 0;">Resumen del pedido:</h4>
                        %s
                        
                        <div class="total">Total: $%s</div>
                        
                        <p class="message">%s</p>
                    </div>
                    
                    <div class="footer">
                        <img src="https://res.cloudinary.com/dsgqbotzi/image/upload/v1767926581/default_vianda_rb2ila.png" alt="Logo">
                        <span>MiViandita</span>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombre, emprendimiento, id, colorEstado, textoEstado, itemsHtml, totalCalculado, mensajeExtra);
    }

    // Reclamos
    private String buildReclamoUserEmail(String nombre, String ticket) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #fff7ed; }
                    .container { max-width: 600px; margin: 0 auto; padding: 40px 20px; }
                    .card { background-color: #ffffff; border-radius: 12px; padding: 40px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); border-top: 6px solid #eb8334; border-bottom: 6px solid #eb8334; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .title { color: #eb8334; font-size: 26px; font-weight: bold; margin: 0; }
                    .text { color: #555555; font-size: 16px; line-height: 1.6; }
                    .ticket-box { background-color: #fff7ed; border: 1px dashed #fe9039; border-radius: 8px; padding: 15px; text-align: center; margin: 25px 0; }
                    .ticket-code { color: #e68033; font-size: 28px; font-weight: bold; letter-spacing: 2px; }
                    .ticket-label { color: #e07b3d; font-size: 12px; text-transform: uppercase; letter-spacing: 1px; font-weight: bold; }
                    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #999999; border-top: 1px solid #eee; padding-top: 20px; }
                    .footer img { height: 25px; vertical-align: middle; }
                    .footer span { vertical-align: middle; margin-left: 8px; font-weight: 500; }
                    .center-text { text-align: center; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="card">
                        <div class="header">
                            <h1 class="title">Reporte Recibido</h1>
                        </div>
                        
                        <p class="text center-text">Hola, hemos recibido tu reporte correctamente. Nuestro equipo de soporte ya tiene constancia del problema y lo revisará a la brevedad.</p>
                        
                        <div class="ticket-box">
                            <div class="ticket-label">Tu código de seguimiento</div>
                            <div class="ticket-code">%s</div>
                        </div>
                        
                        <p class="text center-text" style="margin-top: 30px;">Si necesitamos más información, te contactaremos a este mismo correo.</p>
                        
                        <div class="footer">
                            <img src="https://res.cloudinary.com/dsgqbotzi/image/upload/v1767926581/default_vianda_rb2ila.png" alt="Logo">
                            <span>MiViandita</span>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(ticket);
    }

    private String buildReclamoAdminEmail(String ticket, String cat, String desc, String userEmail) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; }
                    .container { max-width: 600px; margin: 0 auto; padding: 40px 20px; }
                    .card { background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 5px rgba(0,0,0,0.1); padding: 40px; border-top: 6px solid #eb8334; border-bottom: 6px solid #eb8334; }
                    .header { text-align: center; margin-bottom: 20px; }
                    .header h2 { color: #eb8334; margin: 0; font-size: 26px; font-weight: bold; }
                    .content { padding: 10px; }
                    .row { margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 15px; }
                    .label { color: #999; font-size: 12px; text-transform: uppercase; font-weight: bold; display: block; margin-bottom: 5px; }
                    .value { color: #333; font-size: 16px; font-weight: 500; }
                    .status-line {
                        text-align: center;
                        margin: 25px 0;
                        font-size: 18px;
                        padding-bottom: 15px;
                        border-bottom: 1px dashed #ddd;
                    }
                    .id-badge, .cat-badge {
                        background-color: #fff7ed;
                        color: #e68033;
                        padding: 5px 12px;
                        border-radius: 15px;
                        font-weight: bold;
                        font-size: 16px;
                        border: 1px solid #ffdcb0;
                    }
                    .separator { color: #ccc; margin: 0 10px; }
                    .description-box { background-color: #fcfcfc; border-left: 4px solid #fe9039; padding: 15px; margin-top: 10px; color: #555; font-style: italic; line-height: 1.5; }
                    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #aaa; border-top: 1px solid #eee; padding-top: 20px; }
                    .footer img { height: 25px; vertical-align: middle; }
                    .footer span { vertical-align: middle; margin-left: 8px; font-weight: 500; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="card">
                        <div class="header">
                            <h2>Nuevo Reclamo</h2>
                        </div>
                        <div class="content">
                            
                            <div class="status-line">
                                <span class="id-badge">%s</span>
                                <span class="separator">|</span>
                                <span class="cat-badge">%s</span>
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
                        
                        <div class="footer">
                            <img src="https://res.cloudinary.com/dsgqbotzi/image/upload/v1767926581/default_vianda_rb2ila.png" alt="Logo">
                            <span>MiViandita - Admin</span>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(ticket, cat, userEmail, userEmail, desc);
    }

    private String buildCambioEstadoEmail(String nombre, String ticket, String estado, String respuesta) {
        // Si no hay respuesta del admin, ponemos un texto genérico
        String adminResponseHtml = (respuesta != null && !respuesta.isBlank())
                ? "<div style='background-color: #fff7ed; border-left: 4px solid #eb8334; padding: 15px; margin: 20px 0; color: #555;'>" + respuesta + "</div>"
                : "<p>Tu reclamo ha sido revisado y su estado ha cambiado.</p>";

        String colorBadge = estado.equals("RESUELTO") ? "#27ae60" : (estado.equals("RECHAZADO") ? "#c0392b" : "#f39c12");

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 12px; padding: 40px; border-top: 6px solid #eb8334; border-bottom: 6px solid #eb8334; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; margin-bottom: 30px; }
                    .header h1 { color: #eb8334; font-size: 26px; margin: 0; font-weight: bold; }
                    .content { padding: 10px; color: #555; font-size: 16px; line-height: 1.6; }
                    .status-line { 
                        text-align: center; 
                        margin: 25px 0; 
                        font-size: 18px; 
                        padding-bottom: 15px;
                        border-bottom: 1px dashed #ddd;
                    }
                    .id-badge { background-color: #fff7ed; color: #e68033; padding: 5px 12px; border-radius: 15px; font-weight: bold; font-size: 16px; border: 1px solid #ffdcb0; }
                    .separator { color: #ccc; margin: 0 10px; }
                    .status-text { font-weight: bold; }
                    .footer { text-align: center; font-size: 12px; color: #999; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
                    .footer img { height: 25px; vertical-align: middle; }
                    .footer span { vertical-align: middle; margin-left: 8px; font-weight: 500; }
                    .center-text { text-align: center; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Actualización de Ticket</h1>
                    </div>
                    <div class="content">
                        <p class="center-text">Hola <strong>%s</strong>, hay novedades sobre tu reclamo.</p>
                        
                        <div class="status-line">
                            <span class="id-badge">%s</span>
                            <span class="separator">|</span>
                            <span class="status-text" style="color: %s;">%s</span>
                        </div>
                        
                        <h3>Respuesta del soporte:</h3>
                        %s
                        
                        <p class="center-text" style="font-size: 13px; color: #777; margin-top: 30px;">
                           Si consideras que esto no resuelve tu problema, por favor responde a este correo o inicia un nuevo contacto.
                        </p>
                    </div>
                    <div class="footer">
                        <img src="https://res.cloudinary.com/dsgqbotzi/image/upload/v1767926581/default_vianda_rb2ila.png" alt="Logo">
                        <span>MiViandita</span>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombre, ticket, colorBadge, estado, adminResponseHtml);
    }

    // HTML PARA OLVIDE CONTRASEÑA
    private String buildRecoveryEmail(String nombre, String link) {
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
                    .btn-container { text-align: center; margin: 30px 0; }
                    .btn { background-color: #eb8334; color: #ffffff !important; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block; }
                    .btn:hover { background-color: #d6762b; }
                    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #999999; }
                    .warning { font-size: 13px; color: #777; margin-top: 20px; border-top: 1px solid #eee; padding-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="card">
                        <div class="header">
                            <h1 class="title">Recuperación de Contraseña</h1>
                        </div>
                        
                        <p class="text">Hola <strong>%s</strong>,</p>
                        <p class="text">Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en MiViandita.</p>
                        
                        <div class="btn-container">
                            <a href="%s" class="btn">Cambiar mi contraseña</a>
                        </div>
                        
                        <p class="text">Este enlace expirará en 15 minutos por tu seguridad.</p>
                        
                        <div class="warning">
                            Si tú no solicitaste este cambio, puedes ignorar este correo tranquilamente. Tu contraseña seguirá siendo la misma.
                        </div>
                    </div>
                    
                    <div class="footer">
                        &copy; MiViandita - Tus viandas favoritas
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombre, link);
    }
}
