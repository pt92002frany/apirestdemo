package edu.sv.ues.dam235.apirestdemo.controllers;

import edu.sv.ues.dam235.apirestdemo.dtos.LoginDTO;
import edu.sv.ues.dam235.apirestdemo.dtos.TokenDTO;
import edu.sv.ues.dam235.apirestdemo.dtos.UserRegisterDTO;
import edu.sv.ues.dam235.apirestdemo.services.AuthServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthServices authServices;

    public AuthController(AuthServices authServices) {
        this.authServices = authServices;
    }

    /**
     * Endpoint de login.
     * POST /auth/login
     * Body: { "user": "email", "pass": "password" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            TokenDTO token = authServices.login(loginDTO.getUser(), loginDTO.getPass());

            if (token == null) {
                Map<String, String> error = new HashMap<>();
                error.put("msj", "Credenciales incorrectas o usuario inactivo");
                return ResponseEntity.status(401).body(error);
            }

            return ResponseEntity.ok(token);

        } catch (Exception e) {
            log.error("Error en login: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Endpoint de logout.
     * POST /auth/logout
     * Header: Authorization: Bearer <token>
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String token) {
        String msj = authServices.logout(token);
        Map<String, String> response = new HashMap<>();
        response.put("msj", msj);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de registro de nuevos usuarios.
     * POST /auth/register
     * Body: { "name": "...", "lastName": "...", "email": "...", "password": "..." }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO dto) {
        UserRegisterDTO result = authServices.register(dto);

        if (result == null) {
            Map<String, String> error = new HashMap<>();
            error.put("msj", "El correo ya está registrado");
            return ResponseEntity.status(409).body(error);
        }

        Map<String, String> response = new HashMap<>();
        response.put("msj", "Usuario registrado exitosamente");
        response.put("email", result.getEmail());
        return ResponseEntity.status(201).body(response);
    }
}