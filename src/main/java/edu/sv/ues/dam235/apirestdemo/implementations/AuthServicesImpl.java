package edu.sv.ues.dam235.apirestdemo.implementations;

import edu.sv.ues.dam235.apirestdemo.configs.CustomerDetailServices;
import edu.sv.ues.dam235.apirestdemo.dtos.TokenDTO;
import edu.sv.ues.dam235.apirestdemo.dtos.UserRegisterDTO;
import edu.sv.ues.dam235.apirestdemo.entities.User;
import edu.sv.ues.dam235.apirestdemo.repositories.UserRepository;
import edu.sv.ues.dam235.apirestdemo.services.AuthServices;
import edu.sv.ues.dam235.apirestdemo.utilities.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServicesImpl implements AuthServices {

    private final AuthenticationManager authenticationManager;
    private final CustomerDetailServices customerDetailServices;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServicesImpl(AuthenticationManager authenticationManager,
                            CustomerDetailServices customerDetailServices,
                            JwtUtil jwtUtil,
                            UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.customerDetailServices = customerDetailServices;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TokenDTO login(String user, String pass) {
        TokenDTO token = new TokenDTO();
        try {
            // 1. Autenticar con Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user, pass)
            );

            // 2. Cargar los detalles del usuario
            UserDetails usuarioDetail = customerDetailServices.loadUserByUsername(user);

            // 3. Verificar que el usuario esté activo
            if (customerDetailServices.getUserDetail().getActive()) {
                // 4. Generar el token JWT
                token = jwtUtil.generateToken(user, usuarioDetail);
                return token;
            } else {
                token.setMsj("Usuario inactivo");
                return null;
            }
        } catch (BadCredentialsException bad) {
            log.error("Credenciales incorrectas: {}", bad.getMessage());
            token.setMsj("Credenciales incorrectas!");
            return null;
        } catch (Exception e) {
            log.error("Error inesperado en login: {}", e.getMessage());
            token.setMsj("Error inesperado");
            return null;
        }
    }

    @Override
    public String logout(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String email = jwtUtil.extractUsername(token);
            return "Sesión cerrada correctamente para: " + email;
        } catch (Exception e) {
            return "Token inválido o expirado";
        }
    }

    @Override
    public UserRegisterDTO register(UserRegisterDTO dto) {
        // 1. Verificar si el email ya existe
        User existing = userRepository.findByEmail(dto.getEmail());
        if (existing != null) {
            return null;
        }

        // 2. Crear la nueva entidad
        User newUser = new User();
        newUser.setName(dto.getName());
        newUser.setLastName(dto.getLastName());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setActive(true);

        // 3. Guardar en la BD
        userRepository.save(newUser);

        return dto;
    }
}
