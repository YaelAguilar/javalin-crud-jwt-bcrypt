package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.example.configs.AppConfig;
import org.example.configs.DbConfig;
import org.example.controllers.AuthController;
import org.example.daos.IUserDAO;
import org.example.daos.impl.UserDAO;
import org.example.routes.AuthRoutes;
import org.example.services.AuthService;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        DbConfig.init();

        // --- Inyección de Dependencias Manual ---
        // 1. Capa de Datos
        IUserDAO userDAO = new UserDAO();
        // 2. Capa de Servicios
        AuthService authService = new AuthService(userDAO);
        // 3. Capa de Controladores
        AuthController authController = new AuthController(authService);
        // 4. Capa de Rutas
        AuthRoutes authRoutes = new AuthRoutes(authController);

        ObjectMapper jacksonMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(jacksonMapper));
            config.plugins.enableCors(cors -> cors.add(it -> it.anyHost()));
            if (AppConfig.isDevelopment()) {
                config.plugins.enableDevLogging();
            }
        });

        // --- Registrar las rutas en la aplicación ---
        authRoutes.register(app);
        
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