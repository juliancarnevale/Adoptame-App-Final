package com.adoptame.models;

public class Usuario {
    private Long id;
    private String nombre;
    private String email;
    private String tipo; // particular o asociacion

    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
}