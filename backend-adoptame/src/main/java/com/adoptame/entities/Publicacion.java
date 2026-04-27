package com.adoptame.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posteos")
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Muchos perros pueden ser publicados por un mismo usuario
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToOne // Una publicación suele ser para un perro específico
    @JoinColumn(name = "perro_id", nullable = false)
    private Perro perro;

    private String titulo;
    
    @Column(length = 1000)
    private String detallesAdicionales;

    private LocalDateTime fechaPublicacion;

    @PrePersist
    protected void onCreate() {
        fechaPublicacion = LocalDateTime.now();
    }

    public Publicacion() {}

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Perro getPerro() { return perro; }
    public void setPerro(Perro perro) { this.perro = perro; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDetallesAdicionales() { return detallesAdicionales; }
    public void setDetallesAdicionales(String detallesAdicionales) { this.detallesAdicionales = detallesAdicionales; }
    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
}