package com.example.firebasegrupo5;

public class Personas {

    private String id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String fechanac;
    private String foto;

    // Constructor vacío requerido para Firebase
    public Personas() {
    }

    public Personas(String id, String nombres, String apellidos, String correo, String fechanac, String foto) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.fechanac = fechanac;
        this.foto = foto;
    }
    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFechanac() {
        return fechanac;
    }

    public void setFechanac(String fechanac) {
        this.fechanac = fechanac;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
