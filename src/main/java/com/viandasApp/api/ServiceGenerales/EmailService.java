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

    @Async
    public void sendValidacionCuenta(String to, String emailContent) {
        sendEmail(to, "Confirma tu cuenta - MiViandita", emailContent);
    }

    @Async
    public void sendReclamoConfirmacion(String to, String nombre, String ticketCode) {
        String contenido = buildReclamoUserEmail(nombre, ticketCode);
        sendEmail(to, "Reclamo Recibido - Ticket: " + ticketCode, contenido);
    }

    @Async
    public void sendReclamoNotificacionAdmin(String ticketCode, String categoria, String descripcion, String usuarioEmail) {
        String contenido = buildReclamoAdminEmail(ticketCode, categoria, descripcion, usuarioEmail);
        sendEmail(emailFrom, "NUEVO RECLAMO - " + categoria + " [" + ticketCode + "]", contenido);
    }

    @Async
    public void sendCambioEstadoReclamo(String to, String nombre, String ticketCode, String nuevoEstado, String respuestaAdmin) {
        String contenido = buildCambioEstadoEmail(nombre, ticketCode, nuevoEstado, respuestaAdmin);
        sendEmail(to, "Actualización de tu Reclamo - " + ticketCode, contenido);
    }

    @Async
    public void sendPedidoConfirmacionCliente(String to, String nombreCliente, Long pedidoId, String emprendimientoName, Double total, List<DetallePedido> items) {
        String contenido = buildPedidoClienteEmail(nombreCliente, pedidoId, emprendimientoName, total, items);
        sendEmail(to, "Confirmación de Pedido #" + pedidoId, contenido);
    }

    @Async
    public void sendPedidoNuevoDueno(String to, String nombreDueno, Long pedidoId, String nombreCliente, Double total, List<DetallePedido> items) {
        String contenido = buildPedidoDuenoEmail(nombreDueno, pedidoId, nombreCliente, total, items);
        sendEmail(to, "Nuevo Pedido #" + pedidoId + " Recibido", contenido);
    }

    @Async
    public void sendPedidoEstadoUpdate(String to, String nombreCliente, Long pedidoId, String emprendimientoName, EstadoPedido nuevoEstado) {
        String contenido = buildPedidoEstadoEmail(nombreCliente, pedidoId, emprendimientoName, nuevoEstado);
        sendEmail(to, "Tu pedido #" + pedidoId + " ha sido " + nuevoEstado, contenido);
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

    // HTML BUILDERS PARA PEDIDOS Y RECLAMOS (contenido del mail)

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
                    .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 12px; padding: 30px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); border-top: 6px solid #eb8334; }
                    .header h1 { color: #eb8334; font-size: 24px; margin: 0 0 10px 0; }
                    .info { color: #555; line-height: 1.6; }
                    .total { text-align: right; font-size: 18px; font-weight: bold; color: #333; margin-top: 15px; }
                    .footer { text-align: center; font-size: 12px; color: #999; margin-top: 30px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>¡Pedido Confirmado!</h1>
                    </div>
                    <p class="info">Hola <strong>%s</strong>,</p>
                    <p class="info">Tu pedido <strong>#%d</strong> a <strong>%s</strong> se ha registrado correctamente y está pendiente de aprobación.</p>
                    
                    <h3>Detalle del pedido:</h3>
                    %s
                    
                    <div class="total">Total: $%s</div>
                    
                    <p class="info" style="margin-top: 20px;">Te notificaremos cuando el emprendimiento acepte tu pedido.</p>
                    
                    <div class="footer">Gracias por elegir MiViandita</div>
                </div>
            </body>
            </html>
            """.formatted(nombre, id, emprendimiento, itemsHtml, total);
    }

    private String buildPedidoDuenoEmail(String nombreDueno, Long id, String cliente, Double total, List<DetallePedido> items) {
        String itemsHtml = buildViandasListHtml(items);
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', sans-serif; background-color: #f8f9fa; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 12px; padding: 30px; border-left: 6px solid #eb8334; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
                    .title { color: #333; font-size: 22px; font-weight: bold; margin-bottom: 20px; }
                    .badge { background-color: #eb8334; color: white; padding: 5px 10px; border-radius: 4px; font-size: 14px; vertical-align: middle; }
                    .content { color: #555; line-height: 1.5; }
                    .btn { display: inline-block; background-color: #eb8334; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 20px; font-weight: bold; }
                    .total { font-size: 20px; font-weight: bold; color: #eb8334; text-align: right; margin-top: 10px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="title">Nueva Venta <span class="badge">#%d</span></div>
                    <p class="content">Hola <strong>%s</strong>,</p>
                    <p class="content"> <strong>%s</strong> ha realizado un nuevo pedido.</p>
                    
                    <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                    
                    %s
                    
                    <div class="total">Total: $%s</div>
                    
                    <p class="content">Ingresa a MiViandita para aceptar o rechazar el pedido.</p>
                    
                </div>
            </body>
            </html>
            """.formatted(id, nombreDueno, cliente, itemsHtml, total);
    }

    private String buildPedidoEstadoEmail(String nombre, Long id, String emprendimiento, EstadoPedido estado) {
        String color = estado == EstadoPedido.ACEPTADO ? "#27ae60" : "#c0392b";
        String mensajeExtra = estado == EstadoPedido.ACEPTADO
                ? "Tu pedido fue aceptado, el emprendimiento se contactará contigo para coordinar la entrega."
                : "Lo sentimos, el emprendimiento no puede tomar tu pedido en este momento.";

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 30px auto; background: #ffffff; border-radius: 8px; overflow: hidden; text-align: center; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
                    .header { background-color: %s; padding: 30px; color: white; }
                    .status-icon { font-size: 40px; margin-bottom: 10px; display: block; }
                    .body { padding: 40px; color: #555; }
                    .h2 { margin: 0; font-size: 24px; }
                    .order-ref { background: #f8f9fa; display: inline-block; padding: 10px 20px; border-radius: 50px; margin: 20px 0; font-weight: bold; color: #333; border: 1px solid #ddd; }
                    .footer { background-color: #f9f9f9; padding: 15px; font-size: 12px; color: #aaa; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="h2">Pedido %s</div>
                    </div>
                    <div class="body">
                        <p>Hola <strong>%s</strong>,</p>
                        <p>El estado de tu pedido a <strong>%s</strong> ha cambiado.</p>
                        
                        <div class="order-ref">Pedido #%d</div>
                        
                        <p style="font-size: 16px;">%s</p>
                    </div>
                    <div class="footer">MiViandita</div>
                </div>
            </body>
            </html>
            """.formatted(color, estado, nombre, emprendimiento, id, mensajeExtra);
    }

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
                    .container { max-width: 600px; margin: 20px auto; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background-color: #eb8334; padding: 20px; text-align: center; color: white; }
                    .content { padding: 30px; }
                    .status-badge { background-color: %s; color: white; padding: 5px 10px; border-radius: 4px; font-weight: bold; font-size: 14px; }
                    .footer { text-align: center; font-size: 12px; color: #aaa; padding: 20px; border-top: 1px solid #eee; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Actualización de Ticket</h1>
                    </div>
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        <p>Hay novedades sobre tu reclamo <strong>%s</strong>.</p>
                        
                        <p>Nuevo Estado: <span class="status-badge">%s</span></p>
                        
                        <h3>Respuesta del soporte:</h3>
                        %s
                        
                        <p style="font-size: 13px; color: #777; margin-top: 30px;">
                           Si consideras que esto no resuelve tu problema, por favor responde a este correo o inicia un nuevo contacto.
                        </p>
                    </div>
                    <div class="footer">Equipo MiViandita</div>
                </div>
            </body>
            </html>
            """.formatted(colorBadge, nombre, ticket, estado, adminResponseHtml);
    }
}
