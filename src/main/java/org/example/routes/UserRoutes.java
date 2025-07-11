package org.example.routes;

import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import org.example.controllers.UserController;
import org.example.middlewares.AuthMiddleware;

public class UserRoutes {
    private final UserController userController;

    public UserRoutes(UserController userController) {
        this.userController = userController;
    }

    public void register(Javalin app) {
        app.routes(() -> {
            ApiBuilder.path("/api/users", () -> {
                // Aplicamos el middleware a todas las rutas dentro de este grupo.
                // Cualquier petición a /api/users/* deberá estar autenticada.
                ApiBuilder.before(AuthMiddleware.requireAuth());

                // GET /api/users/profile
                ApiBuilder.get("/profile", userController::getProfile);

            });
        });
    }
}