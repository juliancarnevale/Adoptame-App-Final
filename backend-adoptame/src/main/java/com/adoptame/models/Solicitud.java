package com.adoptame.models;

import jakarta.persistence.*;

@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long perroId;
    private String nombreAdoptante;
    private String telefono;
    private String correo;
    private String direccion;
    
    @Column(columnDefinition = "TEXT")
    private String experiencia;
    
    @Column(columnDefinition = "TEXT")
    private String motivo;
    
    private String estado;

    public Solicitud() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPerroId() { return perroId; }
    public void setPerroId(Long perroId) { this.perroId = perroId; }

    public String getNombreAdoptante() { return nombreAdoptante; }
    public void setNombreAdoptante(String nombreAdoptante) { this.nombreAdoptante = nombreAdoptante; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getExperiencia() { return experiencia; }
    public void setExperiencia(String experiencia) { this.experiencia = experiencia; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}