package com.adoptame.models;

public class Publicacion {
    private Long id;
    private Long usuarioId;
    private Long perroId;
    private String titulo;
    private String detallesAdicionales;
    private String fechaPublicacion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getPerroId() { return perroId; }
    public void setPerroId(Long perroId) { this.perroId = perroId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDetallesAdicionales() { return detallesAdicionales; }
    public void setDetallesAdicionales(String detallesAdicionales) { this.detallesAdicionales = detallesAdicionales; }
    public String getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(String fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
}