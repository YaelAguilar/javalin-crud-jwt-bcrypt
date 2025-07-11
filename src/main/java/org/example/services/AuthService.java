package org.example.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.daos.IUserDAO;
import org.example.models.Role;
import org.example.models.User;
import org.example.models.dtos.auth.LoginDTO;
import org.example.models.dtos.auth.RegisterDTO;
import org.example.models.dtos.user.UserDTO;

import java.util.Optional;

public class AuthService {
    
    private final IUserDAO userDAO;

    public AuthService(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserDTO register(RegisterDTO registerDTO) {
        System.out.println("\n--- AuthService.register() Debug ---");
        System.out.println("Intentando registro para email: " + registerDTO.email());
        // 1. Validación de datos de entrada
        if (registerDTO.name() == null || registerDTO.name().isBlank() ||
            registerDTO.email() == null || registerDTO.email().isBlank() ||
            registerDTO.password() == null || registerDTO.password().isBlank()) {
            System.out.println("Resultado: Nombre, email o contraseña vacíos/nulos.");
            throw new IllegalArgumentException("Nombre, email y contraseña son obligatorios.");
        }

        // 2. Verificar si el email ya está en uso
        if (userDAO.findByEmail(registerDTO.email()).isPresent()) {
            System.out.println("Resultado: El email ya está registrado.");
            throw new IllegalArgumentException("El email '" + registerDTO.email() + "' ya está registrado.");
        }

        // 3. Hashear la contraseña
        String hashedPassword = BCrypt.withDefaults().hashToString(12, registerDTO.password().toCharArray());
        System.out.println("Hash generado para el registro: " + hashedPassword); // <-- Log útil aquí

        // 4. Crear la entidad User para guardarla en la BD
        User newUser = new User();
        newUser.setName(registerDTO.name());
        newUser.setEmail(registerDTO.email());
        newUser.setPassword(hashedPassword);
        newUser.setRole(Role.CUSTOMER); // Por defecto, los usuarios registrados son clientes

        // 5. Guardar el usuario usando el DAO
        User savedUser = userDAO.save(newUser);
        
        // 6. Convertir la entidad guardada a un DTO para la respuesta (sin la contraseña)
        System.out.println("Resultado: Usuario registrado con éxito. ID: " + savedUser.getId());
        return new UserDTO(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getRole(),
            savedUser.getCreatedAt()
        );
    }
    
    public Optional<User> login(LoginDTO loginDTO) {
        System.out.println("\n--- AuthService.login() Debug ---");
        System.out.println("Intentando login para email: " + loginDTO.email());
        System.out.println("Contraseña recibida (sin hashear): " + loginDTO.password());

        if (loginDTO.email() == null || loginDTO.email().isBlank() ||
            loginDTO.password() == null || loginDTO.password().isBlank()) {
            System.out.println("Resultado: Email o contraseña vacíos/nulos.");
            return Optional.empty(); // Credenciales inválidas
        }

        Optional<User> userOptional = userDAO.findByEmail(loginDTO.email());
        if (userOptional.isEmpty()) {
            System.out.println("Resultado: Usuario NO encontrado para email: " + loginDTO.email());
            return Optional.empty(); // Usuario no encontrado
        }

        User user = userOptional.get();
        System.out.println("Usuario encontrado en BD: " + user.getEmail() + ", Rol: " + user.getRole());
        System.out.println("Contraseña hasheada de la BD: " + user.getPassword());

        BCrypt.Result result = BCrypt.verifyer().verify(loginDTO.password().toCharArray(), user.getPassword());
        
        if (result.verified) {
            System.out.println("Resultado: Contraseña verificada con éxito. Login OK.");
            return Optional.of(user);
        } else {
            System.out.println("Resultado: Verificación de contraseña FALLIDA.");
            return Optional.empty(); // Contraseña incorrecta
        }
    }
}