package com.adoptame.models;

public class Perro {
    private Long id; 
    private String nombre;
    private String raza;
    private int edad;
    private String tamanio;
    private String sexo;
    private String estado;
    private String descripcion;
    private String imagenPath;
    private String imagenExtra1;
    private String imagenExtra2;
    private String imagenExtra3;

    public Perro() {}

   
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getTamanio() { return tamanio; }
    public void setTamanio(String tamanio) { this.tamanio = tamanio; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getImagenPath() { return imagenPath; }
    public void setImagenPath(String imagenPath) { this.imagenPath = imagenPath; }
    public String getImagenExtra1() { return imagenExtra1; }
    public void setImagenExtra1(String imagenExtra1) { this.imagenExtra1 = imagenExtra1; }
    public String getImagenExtra2() { return imagenExtra2; }
    public void setImagenExtra2(String imagenExtra2) { this.imagenExtra2 = imagenExtra2; }
    public String getImagenExtra3() { return imagenExtra3; }
    public void setImagenExtra3(String imagenExtra3) { this.imagenExtra3 = imagenExtra3; }
}