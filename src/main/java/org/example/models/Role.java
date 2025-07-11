package org.example.models;

import io.javalin.security.RouteRole;

public enum Role implements RouteRole {
    ADMIN,      // Para administradores con todos los permisos
    CUSTOMER    // Para clientes/compradores
}