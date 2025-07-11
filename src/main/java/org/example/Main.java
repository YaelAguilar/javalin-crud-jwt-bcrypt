package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.example.configs.AppConfig;
import org.example.configs.DbConfig;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        DbConfig.init();

        ObjectMapper jacksonMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(jacksonMapper));
            
            // 1. Configuración de CORS
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost(); // Permite cualquier host
                    // it.allowCredentials = true; // Si necesitarás cookies/sesiones
                    // it.exposeHeader("Authorization"); // Si necesitarás exponer headers personalizados
                });
            });

            if (AppConfig.isDevelopment()) {
                config.plugins.enableDevLogging();
            }
        });

        // Endpoint de prueba
        app.get("/", ctx -> ctx.json(Map.of(
            "status", "Ok",
            "message", "¡El servidor del e-commerce está funcionando!",
            "environment", AppConfig.getEnvironment()
        )));

        // Hook de apagado
        setupShutdownHook(app);

        // Iniciar servidor
        app.start(AppConfig.getServerHost(), AppConfig.getServerPort());
        
        System.out.println("Servidor iniciado en http://" + AppConfig.getServerHost() + ":" + AppConfig.getServerPort());
        System.out.println("Entorno actual: " + AppConfig.getEnvironment());
    }

    private static void setupShutdownHook(Javalin app) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cerrando la aplicación...");
            DbConfig.close();
            app.stop();
            System.out.println("Aplicación cerrada de forma segura.");
        }));
    }
}