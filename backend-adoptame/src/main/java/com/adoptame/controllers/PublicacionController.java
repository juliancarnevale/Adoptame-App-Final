package com.adoptame.controllers;

import org.springframework.web.bind.annotation.*;
import com.adoptame.repositories.PublicacionRepository;
import com.adoptame.entities.Publicacion;
import java.util.List;

@RestController
@RequestMapping("/api/publicaciones")
@CrossOrigin(origins = "*")
public class PublicacionController {

    private final PublicacionRepository repository;

    public PublicacionController(PublicacionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Publicacion> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Publicacion crear(@RequestBody Publicacion publicacion) {
        return repository.save(publicacion);
    }
}