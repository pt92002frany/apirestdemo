package edu.sv.ues.dam235.apirestdemo.services;

import edu.sv.ues.dam235.apirestdemo.dtos.TokenDTO;
import edu.sv.ues.dam235.apirestdemo.dtos.UserRegisterDTO;

public interface AuthServices {

    /**
     * Autentica un usuario y genera un token JWT si las credenciales son correctas.
     * @param user correo electrónico del usuario
     * @param pass contraseña del usuario
     * @return TokenDTO con el token generado, o null si las credenciales son incorrectas
     */
    TokenDTO login(String user, String pass);

    /**
     * Cierra la sesión del usuario. Como JWT es stateless, este método
     * valida el token y devuelve un mensaje de confirmación.
     * @param token JWT del usuario autenticado
     * @return mensaje de confirmación
     */
    String logout(String token);

    /**
     * Registra un nuevo usuario en el sistema.
     * @param userRegisterDTO datos del nuevo usuario
     * @return UserRegisterDTO si el registro fue exitoso, o null si el email ya existe
     */
    UserRegisterDTO register(UserRegisterDTO userRegisterDTO);
}