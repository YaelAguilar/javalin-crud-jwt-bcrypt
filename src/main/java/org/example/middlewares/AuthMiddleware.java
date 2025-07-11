package org.example.middlewares;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.http.Handler;
import org.example.models.Role;
import org.example.utils.JwtUtil;

public class AuthMiddleware {

    /**
     * Middleware que requiere autenticación.
     * Verifica la presencia y validez de un token JWT en el header 'Authorization'.
     * Si es válido, extrae el ID y el rol del usuario y los añade al contexto de la petición.
     * @return un Handler de Javalin.
     */
    public static Handler requireAuth() {
        return ctx -> {
            String authHeader = ctx.header("Authorization");
            if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
                throw new AuthException("Token no proporcionado o con formato incorrecto.");
            }

            String token = authHeader.substring(7);

            try {
                DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
                
                int userId = Integer.parseInt(decodedJWT.getSubject());
                Role userRole = Role.valueOf(decodedJWT.getClaim("role").asString());
                
                ctx.attribute("userId", userId);
                ctx.attribute("userRole", userRole);
                
            } catch (JWTVerificationException e) {
                throw new AuthException("Token inválido o expirado: " + e.getMessage());
            } catch (NumberFormatException | NullPointerException e) {
                throw new AuthException("El token contiene datos de usuario corruptos.");
            }
        };
    }

    /**
     * Middleware que requiere que el usuario autenticado tenga el rol de ADMIN.
     * DEBE ejecutarse DESPUÉS de requireAuth().
     * @return un Handler de Javalin.
     */
    public static Handler requireAdmin() {
        return ctx -> {
            Role userRole = ctx.attribute("userRole");
            if (userRole == null || userRole != Role.ADMIN) {
                throw new AdminAccessOnlyException("Se requiere rol de Administrador para esta acción.");
            }
        };
    }
    
    /**
     * Excepción personalizada para errores de autenticación (401 Unauthorized).
     */
    public static class AuthException extends RuntimeException {
        public AuthException(String message) {
            super(message);
        }
    }

    /**
     * Excepción personalizada para errores de autorización/permisos (403 Forbidden).
     */
    public static class AdminAccessOnlyException extends RuntimeException {
        public AdminAccessOnlyException(String message) {
            super(message);
        }
    }
}