package org.example.configs;

import io.javalin.config.JavalinConfig;
import io.javalin.json.JavalinJackson;
import java.util.function.Consumer;

/**
 * Clase para la configuración centralizada de la aplicación Javalin.
 * Su propósito es mantener el archivo Main.java limpio y enfocado en el arranque.
 */
public class AppConfig {

    /**
     * Devuelve un objeto Consumer que aplica todas las configuraciones a la instancia de Javalin.
     * Un Consumer es una interfaz funcional que acepta un solo argumento y no devuelve resultado.
     * @return Un Consumer<JavalinConfig> para ser usado en Javalin.create().
     */
    public static Consumer<JavalinConfig> configure() {
        return config -> {
            // Habilita CORS (Cross-Origin Resource Sharing).
            config.http.enableCors(cors -> {
                cors.add(corsConfig -> {
                    // anyHost() permite peticiones desde cualquier origen. Ideal para desarrollo.
                    // En un entorno de producción, se debería restringir a dominios específicos.
                    // Ejemplo: corsConfig.allowHost("https://mi-tienda.com");
                    corsConfig.anyHost();
                });
            });

            // Establece Jackson como el motor para convertir objetos Java a JSON y viceversa.
            // Javalin lo usará automáticamente para manejar ctx.json() y ctx.bodyAsClass().
            config.jsonMapper(new JavalinJackson());
        };
    }
}