package org.example.routes;

import io.javalin.apibuilder.EndpointGroup;
import java.util.Map;
import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * Agrupa todas las rutas de la aplicación.
 * Implementa EndpointGroup para ser registrada directamente en Javalin.
 */
public class MainRoutes implements EndpointGroup {

    @Override
    public void addEndpoints() {
        // Ruta GET para la raíz del servidor ("/").
        // Esta ruta es útil como "health check" para verificar que la API está viva y respondiendo.
        get("/", ctx -> {
            // Devuelve una respuesta JSON.
            ctx.json(Map.of("status", "ok", "message", "API de e-commerce funcionando!"));
            ctx.status(200); // Establece el código de estado HTTP a 200 OK.
        });
    }
}