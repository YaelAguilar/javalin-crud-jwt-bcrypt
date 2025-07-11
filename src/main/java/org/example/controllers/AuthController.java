package org.example.controllers;

import io.javalin.http.Context;
import org.example.models.User;
import org.example.models.dtos.auth.LoginDTO;
import org.example.models.dtos.auth.RegisterDTO;
import org.example.models.dtos.user.UserDTO;
import org.example.services.AuthService;
import org.example.utils.JwtUtil;
import java.util.Map;
import java.util.Optional;

public class AuthController {
    
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Maneja la petición de registro de usuario.
     */
    public void register(Context ctx) {
        try {
            RegisterDTO registerDTO = ctx.bodyAsClass(RegisterDTO.class);
            UserDTO newUser = authService.register(registerDTO);
            ctx.status(201).json(Map.of("success", true, "data", newUser));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", "Error interno del servidor."));
        }
    }

    /**
     * Maneja la petición de inicio de sesión.
     */
    public void login(Context ctx) {
        try {
            LoginDTO loginDTO = ctx.bodyAsClass(LoginDTO.class);
            Optional<User> userOptional = authService.login(loginDTO);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String token = JwtUtil.generateToken(user);
                
                // Exponemos el token en el header y en el cuerpo para mayor flexibilidad
                ctx.header("Authorization", "Bearer " + token);
                ctx.status(200).json(Map.of(
                    "success", true,
                    "message", "Login exitoso.",
                    "token", token
                ));
            } else {
                ctx.status(401).json(Map.of("success", false, "message", "Credenciales inválidas."));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", "Error interno del servidor."));
        }
    }
}