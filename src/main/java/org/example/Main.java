package org.example;

import io.javalin.Javalin;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        
        Javalin app = Javalin.create();

        app.get("/", ctx -> {
            ctx.json(Map.of("status", "ok", "message", "Servidor mínimo funcionando!"));
        });

        app.start(7070);
        
        System.out.println("Servidor mínimo iniciado en http://localhost:7070");
        System.out.println("Si esto funciona, el entorno es correcto.");
    }
}