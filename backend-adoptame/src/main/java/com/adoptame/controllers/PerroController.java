package com.adoptame.controllers;

import org.springframework.web.bind.annotation.*;
import com.adoptame.repositories.PerroRepository;
import com.adoptame.entities.Perro;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/perros")
@CrossOrigin(origins = "*")
public class PerroController {

    private final PerroRepository repository;

    public PerroController(PerroRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Perro> listarPerros(
            @RequestParam(required = false) String raza,
            @RequestParam(required = false) String tamanio,
            @RequestParam(required = false) String sexo) {
        return repository.buscarCombinado(raza, tamanio, sexo);
    }

    @GetMapping("/{id}")
    public Optional<Perro> obtenerPorId(@PathVariable Long id) {
        return repository.findById(id);
    }

    @PostMapping
    public Perro guardar(@RequestBody Perro perro) {
        return repository.save(perro);
    }

    @PutMapping("/{id}")
    public Perro actualizar(@PathVariable Long id, @RequestBody Perro perroActualizado) {
        return repository.findById(id)
            .map(perro -> {
                perro.setNombre(perroActualizado.getNombre());
                perro.setRaza(perroActualizado.getRaza());
                perro.setEdad(perroActualizado.getEdad());
                perro.setSexo(perroActualizado.getSexo());
                perro.setTamanio(perroActualizado.getTamanio());
                perro.setDescripcion(perroActualizado.getDescripcion());
                perro.setEstado(perroActualizado.getEstado());
                // Actualizar también las rutas de imágenes
                perro.setImagenPath(perroActualizado.getImagenPath());
                perro.setImagenExtra1(perroActualizado.getImagenExtra1());
                perro.setImagenExtra2(perroActualizado.getImagenExtra2());
                perro.setImagenExtra3(perroActualizado.getImagenExtra3());
                return repository.save(perro);
            })
            .orElseGet(() -> {
                perroActualizado.setId(id);
                return repository.save(perroActualizado);
            });
    }

    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        repository.deleteById(id);
    }
}