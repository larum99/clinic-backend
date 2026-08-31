# Flujo Forgot Password / Reset Password

Implementación segura y profesional de recuperación de contraseña.

## Flujo funcional

1. El usuario envía su correo en `POST /api/auth/forgot-password`.
2. El backend genera un token aleatorio criptográficamente seguro (32 bytes, hex) y guarda solo su **hash SHA-256**.
3. El backend arma el enlace `FRONTEND_URL/reset-password?token=TOKEN` y lo envía por correo.
4. El usuario hace clic y el frontend llama a `POST /api/auth/reset-password` con el token y la nueva contraseña.
5. El backend valida que el token exista, no haya expirado y no esté usado; luego guarda la nueva contraseña con **BCrypt** e invalida el token.

## Seguridad

- El token **nunca se devuelve** en la respuesta de `forgot-password`.
- El token no se registra en logs.
- Solo se almacena el hash SHA-256 del token en base de datos.
- Expiración configurable (por defecto 30 min) y de **un solo uso**.
- Los tokens activos previos del usuario se invalidan al generar una nueva solicitud.
- La respuesta de `forgot-password` es genérica y **no revela si el correo existe** (evita enumeración de usuarios).
- La nueva contraseña se guarda con BCrypt (se reutiliza `PasswordEncoder`).

## Configuración (variables de entorno)

| Variable | Descripción | Dev (MailDev) |
|----------|-------------|---------------|
| `MAIL_HOST` | Host SMTP | `localhost` |
| `MAIL_PORT` | Puerto SMTP | `1025` |
| `MAIL_USERNAME` | Usuario SMTP (vacío en MailDev) | *(vacío)* |
| `MAIL_PASSWORD` | Password SMTP (vacío en MailDev) | *(vacío)* |
| `MAIL_SMTP_AUTH` | Activar autenticación | `false` |
| `MAIL_SMTP_STARTTLS_ENABLE` | Activar STARTTLS | `false` |
| `MAIL_FROM` | Remitente | `no-reply@clinic.local` |
| `PASSWORD_RESET_EXPIRATION_MINUTES` | Minutos de expiración | `30` |
| `FRONTEND_URL` | URL del frontend | `http://localhost:5173` |

## Desarrollo local con MailDev (recomendado)

MailDev captura los correos localmente sin enviarlos a Internet.

1. Levanta MailDev:
   ```bash
   docker compose -f docker-compose.mail.yml up
   ```
   - SMTP: `localhost:1025`
   - Interfaz web: http://localhost:1080

2. Config como dev (es el default de `application.yaml`):
   ```env
   MAIL_HOST=localhost
   MAIL_PORT=1025
   MAIL_SMTP_AUTH=false
   MAIL_SMTP_STARTTLS_ENABLE=false
   MAIL_FROM=no-reply@clinic.local
   ```

3. Ejecuta `forgot-password`, abre http://localhost:1080 y verás el correo con el enlace de reseteo.

## Prueba de integración con un SMTP real

1. Configura tus credenciales SMTP como variables de entorno (nunca hardcodear):
   ```env
   MAIL_HOST=smtp.tuproveedor.com
   MAIL_PORT=587
   MAIL_USERNAME=tu_usuario
   MAIL_PASSWORD=tu_password
   MAIL_SMTP_AUTH=true
   MAIL_SMTP_STARTTLS_ENABLE=true
   MAIL_FROM=tu_correo@dominio.com
   ```
2. Verás el correo en tu bandeja real.
3. Para cambiar entre MailDev y SMTP real solo se modifican estas variables; la lógica no cambia.

## Probar el flujo completo

1. Regístrate o usa un usuario existente.
2. `forgot-password` (obtén el token desde MailDev/correo).
3. `reset-password` con el token.
4. Inicia sesión con la nueva contraseña.

## Requests / Responses

### POST /api/auth/forgot-password

```json
{ "email": "usuario@email.com" }
```

Respuesta genérica (siempre la misma, exista o no el correo):

```json
{
  "message": "Si existe una cuenta asociada a este correo, recibirás instrucciones para restablecer tu contraseña."
}
```

### POST /api/auth/reset-password

```json
{
  "token": "TOKEN",
  "newPassword": "NuevaPassword123!"
}
```

Respuesta:

```json
{
  "message": "Password has been reset successfully."
}
```

Errores (token inválido/expirado/usado):

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Business Error",
  "message": "Invalid or expired password reset token.",
  "path": "/api/auth/reset-password"
}
```
