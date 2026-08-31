package com.clinic.clinic.service.impl;

import com.clinic.clinic.config.PasswordResetProperties;
import com.clinic.clinic.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final PasswordResetProperties properties;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            PasswordResetProperties properties) {

        this.mailSender = mailSender;
        this.properties = properties;
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink, String name) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(properties.getMailFrom());
            helper.setTo(to);
            helper.setSubject("Restablece tu contraseña");
            helper.setText(buildHtml(resetLink, name), true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send password reset email to address", e);
            throw new IllegalStateException("Unable to send password reset email", e);
        }
    }

    private String buildHtml(String resetLink, String name) {

        String greetingName = (name == null || name.isBlank()) ? "usuario" : escapeHtml(name);

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0;padding:0;background-color:#f4f6f8;font-family:Arial,Helvetica,sans-serif;color:#2d3748;">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f8;padding:32px 16px;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="width:100%%;max-width:600px;background-color:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 12px rgba(0,0,0,0.08);">
                          <tr>
                            <td style="background-color:#2563eb;padding:28px 40px;">
                              <h1 style="margin:0;color:#ffffff;font-size:22px;line-height:1.3;">Restablece tu contraseña</h1>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:32px 40px;">
                              <p style="margin:0 0 16px 0;font-size:15px;line-height:1.6;">Hola %s,</p>
                              <p style="margin:0 0 16px 0;font-size:15px;line-height:1.6;">
                                Recibimos una solicitud para restablecer la contraseña de tu cuenta.
                                Si realizaste esta solicitud, haz clic en el siguiente botón para elegir una nueva contraseña.
                              </p>
                              <table role="presentation" cellpadding="0" cellspacing="0" style="margin:24px 0;width:100%%;">
                                <tr>
                                  <td align="center">
                                    <a href="%s"
                                       style="display:inline-block;background-color:#2563eb;color:#ffffff;text-decoration:none;font-size:15px;font-weight:bold;padding:14px 32px;border-radius:8px;">
                                      Restablecer contraseña
                                    </a>
                                  </td>
                                </tr>
                              </table>
                              <p style="margin:0 0 16px 0;font-size:13px;color:#64748b;line-height:1.6;">
                                Este enlace es válido por un tiempo limitado. Si expira, deberás solicitar un nuevo restablecimiento.
                              </p>
                              <p style="margin:0 0 8px 0;font-size:13px;color:#64748b;line-height:1.6;">
                                Si no solicitaste este cambio, ignora este correo. Tu contraseña no cambiará a menos que completes el proceso.
                              </p>
                              <p style="margin:0;font-size:12px;color:#94a3b8;line-height:1.5;">
                                Por seguridad, no compartas este enlace con nadie.
                              </p>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:20px 40px;background-color:#f8fafc;border-top:1px solid #e2e8f0;">
                              <p style="margin:0;font-size:12px;color:#94a3b8;line-height:1.5;">
                                Si tienes problemas con el botón, copia y pega este enlace en tu navegador:<br>
                                <span style="word-break:break-all;">%s</span>
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(greetingName, resetLink, escapeHtml(resetLink));
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
