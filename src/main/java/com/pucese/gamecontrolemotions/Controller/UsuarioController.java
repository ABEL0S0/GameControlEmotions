package com.pucese.gamecontrolemotions.Controller;

import com.pucese.gamecontrolemotions.Entity.Usuario;
import com.pucese.gamecontrolemotions.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/Usuarios")
    public ResponseEntity<?> addUsuario(@RequestBody Usuario usuario) {
        try {
            // Validar campos requeridos
            if (usuario.getNombreUsuario() == null || usuario.getApellido() == null ||
                    usuario.getCorreo() == null || usuario.getContrasena() == null) {
                return ResponseEntity
                        .badRequest()
                        .body("Todos los campos son requeridos");
            }

            // Verificar si el correo ya existe
            if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
                return ResponseEntity
                        .badRequest()
                        .body("El correo ya está registrado");
            }

            // Cifrar la contraseña
            String hashedPassword = passwordEncoder.encode(usuario.getContrasena());
            usuario.setContrasena(hashedPassword);

            Usuario savedUsuario = usuarioRepository.save(usuario);
            return ResponseEntity.ok(savedUsuario);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear usuario: " + e.getMessage());
        }
    }

    @GetMapping("/Usuarios/{id}")
    public ResponseEntity<?> getUserId(@PathVariable Integer id) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            if (usuario.isPresent()) {
                return ResponseEntity.ok(usuario.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener usuario: " + e.getMessage());
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String correo = credentials.get("correo");
            String contrasena = credentials.get("contrasena");

            if (correo == null || contrasena == null) {
                return ResponseEntity
                        .badRequest()
                        .body("Correo y contraseña son requeridos");
            }

            Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);

            if (usuario.isPresent() &&
                    passwordEncoder.matches(contrasena, usuario.get().getContrasena())) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Credenciales inválidas");
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en el login: " + e.getMessage());
        }
    }
}
