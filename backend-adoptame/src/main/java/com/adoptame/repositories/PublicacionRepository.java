package com.adoptame.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adoptame.entities.Publicacion;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
}