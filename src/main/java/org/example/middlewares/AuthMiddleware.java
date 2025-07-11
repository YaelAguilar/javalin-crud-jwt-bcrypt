package org.example.middlewares;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.http.Handler;
import org.example.models.Role;
import org.example.utils.JwtUtil;
import org.jetbrains.annotations.NotNull;

public class AuthMiddleware {

    /**
     * Middleware para requerir autenticación.
     * Verifica el token y añade los datos del usuario al contexto si es válido.
     */
    public static void requireAuthentication(@NotNull Handler handler) throws Exception {
    }

    public static Handler requireAuth() {
        return ctx -> {
            // 1. Obtener el header 'Authorization'
            String authHeader = ctx.header("Authorization");
            if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
                // Lanzamos una excepción que será capturada por nuestro manejador de errores.
                throw new AuthException("Token no proporcionado o con formato incorrecto.");
            }

            // 2. Extraer el token
            String token = authHeader.substring(7);

            try {
                // 3. Verificar el token usando nuestro JwtUtil
                DecodedJWT decodedJWT = JwtUtil.verifyToken(token);

                // 4. Extraer datos y añadirlos al contexto para uso posterior
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
    
    public static class AuthException extends RuntimeException {
        public AuthException(String message) {
            super(message);
        }
    }
}