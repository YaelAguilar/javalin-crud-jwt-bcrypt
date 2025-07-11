package org.example.controllers;

import io.javalin.http.Context;

import org.example.models.dtos.user.UserDTO;
import org.example.services.UserService;
import java.util.Map;
import java.util.NoSuchElementException;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Maneja la petición para obtener el perfil del usuario autenticado.
     */
    public void getProfile(Context ctx) {
        try {
            // Obtenemos el userId que el AuthMiddleware puso en el contexto.
            // Si el atributo no existe o no es un entero, saltará una excepción.
            int userId = ctx.attribute("userId");
            
            UserDTO userProfile = userService.findUserById(userId);
            ctx.status(200).json(Map.of("success", true, "data", userProfile));
            
        } catch (NoSuchElementException e) {
            // Esto podría pasar si el usuario fue borrado después de que el token fue emitido.
            ctx.status(404).json(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", "Error interno al obtener el perfil."));
        }
    }
}