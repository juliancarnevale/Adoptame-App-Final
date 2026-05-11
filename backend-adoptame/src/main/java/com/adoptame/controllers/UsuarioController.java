package com.adoptame.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.adoptame.repositories.UsuarioRepository;
import com.adoptame.entities.Usuario;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository repository;

    public UsuarioController(UsuarioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return repository.findAll();
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {

        Optional<Usuario> usuarioExistente = repository.findByEmail(usuario.getEmail());
        
        if (usuarioExistente.isPresent()) {

            return ResponseEntity.status(409).body("El correo electrónico ya está registrado.");
        }
        
        try {

            Usuario nuevoUsuario = repository.save(usuario);
            return ResponseEntity.status(201).body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");
        
        Optional<Usuario> userOpt = repository.findByEmail(email);
        
        if (userOpt.isPresent() && userOpt.get().getPasswordHash().equals(password)) {
            return ResponseEntity.ok(userOpt.get()); 
        }
        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarPerfil(@PathVariable Long id, @RequestBody Usuario datosActualizados) {
        return repository.findById(id).map(user -> {
            user.setNombre(datosActualizados.getNombre());
            user.setEmail(datosActualizados.getEmail());
            user.setTelefono(datosActualizados.getTelefono());
            if (datosActualizados.getFotoPerfil() != null) {
                user.setFotoPerfil(datosActualizados.getFotoPerfil());
            }
            return ResponseEntity.ok(repository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }
}