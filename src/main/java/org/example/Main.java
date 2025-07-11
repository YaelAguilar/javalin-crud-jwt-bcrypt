package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.example.configs.AppConfig;
import org.example.configs.DbConfig;
import org.example.controllers.AuthController;
import org.example.controllers.UserController;
import org.example.daos.IUserDAO;
import org.example.daos.impl.UserDAO;
import org.example.middlewares.AuthMiddleware;
import org.example.routes.AuthRoutes;
import org.example.routes.UserRoutes;
import org.example.services.AuthService;
import org.example.services.UserService;

import java.util.Map;
import java.util.NoSuchElementException;

public class Main {
    public static void main(String[] args) {
        DbConfig.init();

        // --- Inyección de Dependencias Manual ---
        IUserDAO userDAO = new UserDAO();
        AuthService authService = new AuthService(userDAO);
        UserService userService = new UserService(userDAO); // <-- Instanciar UserService
        AuthController authController = new AuthController(authService);
        UserController userController = new UserController(userService); // <-- Instanciar UserController
        AuthRoutes authRoutes = new AuthRoutes(authController);
        UserRoutes userRoutes = new UserRoutes(userController); // <-- Instanciar UserRoutes

        ObjectMapper jacksonMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(jacksonMapper));
            config.plugins.enableCors(cors -> cors.add(it -> {
                it.anyHost();
                it.exposeHeader("Authorization"); // Exponer el header para que el cliente lo pueda leer
            }));
            if (AppConfig.isDevelopment()) {
                config.plugins.enableDevLogging();
            }
        });

        // --- Manejadores de Excepciones ---
        app.exception(AuthMiddleware.AuthException.class, (e, ctx) -> {
            ctx.status(401).json(Map.of("success", false, "message", e.getMessage()));
        });
        app.exception(NoSuchElementException.class, (e, ctx) -> {
            ctx.status(404).json(Map.of("success", false, "message", e.getMessage()));
        });

        // --- Registrar las rutas ---
        authRoutes.register(app);
        userRoutes.register(app);

        // Endpoint de prueba
        app.get("/", ctx -> ctx.json(Map.of(
            "status", "Ok",
            "message", "¡El servidor del e-commerce está funcionando!",
            "environment", AppConfig.getEnvironment()
        )));

        setupShutdownHook(app);
        app.start(AppConfig.getServerHost(), AppConfig.getServerPort());
        
        System.out.println("Servidor iniciado en http://" + AppConfig.getServerHost() + ":" + AppConfig.getServerPort());
    }

    private static void setupShutdownHook(Javalin app) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cerrando la aplicación...");
            DbConfig.close();
            app.stop();
        }));
    }
}