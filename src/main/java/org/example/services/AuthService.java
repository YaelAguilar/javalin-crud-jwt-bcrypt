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

    // Inyectamos el DAO a través del constructor.
    public AuthService(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Lógica para registrar un nuevo usuario (cliente).
     * @param registerDTO Datos del formulario de registro.
     * @return El DTO del usuario recién creado.
     */
    public UserDTO register(RegisterDTO registerDTO) {
        // 1. Validación de datos de entrada
        if (registerDTO.name() == null || registerDTO.name().isBlank() ||
            registerDTO.email() == null || registerDTO.email().isBlank() ||
            registerDTO.password() == null || registerDTO.password().isBlank()) {
            throw new IllegalArgumentException("Nombre, email y contraseña son obligatorios.");
        }

        // 2. Verificar si el email ya está en uso
        if (userDAO.findByEmail(registerDTO.email()).isPresent()) {
            throw new IllegalArgumentException("El email '" + registerDTO.email() + "' ya está registrado.");
        }

        // 3. Hashear la contraseña
        String hashedPassword = BCrypt.withDefaults().hashToString(12, registerDTO.password().toCharArray());

        // 4. Crear la entidad User para guardarla en la BD
        User newUser = new User();
        newUser.setName(registerDTO.name());
        newUser.setEmail(registerDTO.email());
        newUser.setPassword(hashedPassword);
        newUser.setRole(Role.CUSTOMER); // Por defecto, los usuarios registrados son clientes

        // 5. Guardar el usuario usando el DAO
        User savedUser = userDAO.save(newUser);
        
        // 6. Convertir la entidad guardada a un DTO para la respuesta (sin la contraseña)
        return new UserDTO(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getRole(),
            savedUser.getCreatedAt()
        );
    }
    
    /**
     * Lógica para el inicio de sesión de un usuario.
     * @param loginDTO Credenciales de inicio de sesión.
     * @return Un Optional que contiene el User si las credenciales son válidas.
     */
    public Optional<User> login(LoginDTO loginDTO) {
        // 1. Validación
        if (loginDTO.email() == null || loginDTO.password() == null) {
            return Optional.empty();
        }

        // 2. Buscar usuario por email
        Optional<User> userOptional = userDAO.findByEmail(loginDTO.email());
        if (userOptional.isEmpty()) {
            return Optional.empty(); // Email no encontrado
        }

        User user = userOptional.get();

        // 3. Verificar la contraseña hasheada
        BCrypt.Result result = BCrypt.verifyer().verify(loginDTO.password().toCharArray(), user.getPassword());
        
        if (result.verified) {
            return Optional.of(user); // Contraseña correcta
        }
        
        return Optional.empty(); // Contraseña incorrecta
    }
}