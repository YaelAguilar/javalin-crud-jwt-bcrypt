package org.example.daos;

import org.example.models.User;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad User.
 */
public interface IUserDAO {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * @param email El email del usuario a buscar.
     * @return un Optional que contiene el User si se encuentra, o un Optional vacío si no.
     */
    Optional<User> findByEmail(String email);

    /**
     * Guarda un nuevo usuario en la base de datos.
     * Asigna el ID generado por la base de datos al objeto User.
     * @param user El objeto User a guardar, con todos los campos excepto el id.
     * @return El objeto User guardado, ahora con su id asignado.
     */
    User save(User user);

    /**
     * Busca un usuario por su ID único.
     * @param id El ID del usuario a buscar.
     * @return un Optional que contiene el User si se encuentra, o un Optional vacío si no.
     */
    Optional<User> findById(int id);
}