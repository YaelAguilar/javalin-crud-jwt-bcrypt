package org.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.configs.AppConfig;
import org.example.models.User;
import java.util.Date;

public class JwtUtil {

    // Usamos el algoritmo HMAC256 con la clave secreta del .env
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(AppConfig.getJwtSecretKey());
    private static final String ISSUER = "TuEcommerceAPI";

    /**
     * Genera un token JWT para un usuario.
     * @param user El usuario para quien se genera el token.
     * @return El token JWT como un String.
     */
    public static String generateToken(User user) {
        try {
            Date now = new Date();
            Date expirationDate = new Date(now.getTime() + AppConfig.getJwtExpiration());

            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(String.valueOf(user.getId())) // Guardamos el ID del usuario
                    .withClaim("email", user.getEmail())      // Guardamos el email
                    .withClaim("role", user.getRole().name()) // Guardamos el rol
                    .withIssuedAt(now)
                    .withExpiresAt(expirationDate)
                    .sign(ALGORITHM);
        } catch (JWTCreationException exception){
            // En un caso real, loggearíamos este error.
            throw new RuntimeException("Error al generar el token JWT.", exception);
        }
    }

    /**
     * Valida un token y devuelve su contenido decodificado.
     * @param token El token a validar.
     * @return Un objeto DecodedJWT si el token es válido.
     * @throws JWTVerificationException si el token es inválido o ha expirado.
     */
    public static DecodedJWT verifyToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(ALGORITHM)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }
}