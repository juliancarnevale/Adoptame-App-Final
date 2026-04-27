package com.adoptame.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adoptame.entities.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Esto nos servirá para el Login más adelante
    Optional<Usuario> findByEmail(String email);
}