package com.adoptame.controllers;

import com.adoptame.models.Solicitud;
import com.adoptame.repositories.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudController {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @PostMapping
    public ResponseEntity<Solicitud> crearSolicitud(@RequestBody Solicitud solicitud) {
        Solicitud nuevaSolicitud = solicitudRepository.save(solicitud);
        return ResponseEntity.ok(nuevaSolicitud);
    }

    @GetMapping
    public ResponseEntity<List<Solicitud>> obtenerSolicitudes() {
        return ResponseEntity.ok(solicitudRepository.findAll());
    }
}