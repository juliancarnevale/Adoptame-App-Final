package com.adoptame.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.adoptame.entities.Perro;
import java.util.List;

public interface PerroRepository extends JpaRepository<Perro, Long> {

    @Query("SELECT p FROM Perro p WHERE " +
           "(:raza IS NULL OR p.raza = :raza) AND " +
           "(:tamanio IS NULL OR p.tamanio = :tamanio) AND " +
           "(:sexo IS NULL OR p.sexo = :sexo)")
    List<Perro> buscarCombinado(
            @Param("raza") String raza, 
            @Param("tamanio") String tamanio, 
            @Param("sexo") String sexo);
}