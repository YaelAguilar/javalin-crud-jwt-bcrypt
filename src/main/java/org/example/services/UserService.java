package org.example.services;

import org.example.daos.IUserDAO;
import org.example.models.User;
import org.example.models.dtos.user.UserDTO;

import java.util.NoSuchElementException;

public class UserService {
    private final IUserDAO userDAO;

    public UserService(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Busca un usuario por su ID y lo convierte en un DTO.
     * @param id El ID del usuario a buscar.
     * @return El UserDTO correspondiente.
     * @throws NoSuchElementException si no se encuentra el usuario.
     */
    public UserDTO findUserById(int id) {
        // Buscamos el usuario y lo mapeamos a un DTO.
        // Si no se encuentra, orElseThrow lanza una excepción.
        return userDAO.findById(id)
                .map(this::mapUserToUserDTO)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Mapea una entidad User a un UserDTO.
     * @param user La entidad User a convertir.
     * @return El UserDTO resultante.
     */
    private UserDTO mapUserToUserDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt()
        );
    }
}