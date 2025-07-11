package org.example.configs;

import io.javalin.Javalin;
import org.example.middlewares.AuthMiddleware;
import java.util.Map;
import java.util.NoSuchElementException; // Para 404
import java.lang.IllegalArgumentException; // Para 400

public class ExceptionHandlerConfig {

    public static void register(Javalin app) {
        // Manejador para AuthException (401 Unauthorized)
        app.exception(AuthMiddleware.AuthException.class, (e, ctx) -> {
            ctx.status(401).json(Map.of("success", false, "message", e.getMessage()));
        });

        // Manejador para AdminAccessOnlyException (403 Forbidden)
        app.exception(AuthMiddleware.AdminAccessOnlyException.class, (e, ctx) -> {
            ctx.status(403).json(Map.of("success", false, "message", e.getMessage()));
        });

        // Manejador para NoSuchElementException (404 Not Found)
        app.exception(NoSuchElementException.class, (e, ctx) -> {
            ctx.status(404).json(Map.of("success", false, "message", e.getMessage()));
        });

        // Manejador para IllegalArgumentException (400 Bad Request)
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        });

        // Manejador general para cualquier otra Exception no capturada (500 Internal Server Error)
        app.exception(Exception.class, (e, ctx) -> {
            System.err.println("Error no controlado: " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace para depuración
            ctx.status(500).json(Map.of("success", false, "message", "Error interno del servidor. Contacte al administrador."));
        });
        
        // Manejador para errores 404 de rutas no encontradas (no se ejecuta si una ruta Javalin ya ha sido definida)
        app.error(404, ctx -> {
            if (ctx.result() == null) { // Solo si no se ha escrito ya una respuesta
                ctx.json(Map.of("success", false, "message", "Endpoint no encontrado: " + ctx.method() + " " + ctx.path()));
            }
        });
    }
}