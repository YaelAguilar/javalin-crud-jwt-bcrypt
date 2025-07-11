package org.example.routes;

import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import org.example.controllers.AuthController;

public class AuthRoutes {
    
    private final AuthController authController;

    public AuthRoutes(AuthController authController) {
        this.authController = authController;
    }

    public void register(Javalin app) {
        app.routes(() -> {
            // Agrupamos todas las rutas de autenticación bajo /api/auth
            ApiBuilder.path("/api/auth", () -> {
                ApiBuilder.post("/register", authController::register);
                ApiBuilder.post("/login", authController::login);
            });
        });
    }
}