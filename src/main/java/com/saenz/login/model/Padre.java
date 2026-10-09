package com.saenz.login.model;

public class Padre {

    private Long idPadre;
    private String nombreCompleto;
    private String dui;
    private String telefono;
    private String nombreEstudiante;
    private String gradoSeccion;

    public Padre() {
    }

    public Padre(Long idPadre,
                 String nombreCompleto,
                 String dui,
                 String telefono,
                 String nombreEstudiante,
                 String gradoSeccion) {

        this.idPadre = idPadre;
        this.nombreCompleto = nombreCompleto;
        this.dui = dui;
        this.telefono = telefono;
        this.nombreEstudiante = nombreEstudiante;
        this.gradoSeccion = gradoSeccion;
    }

    public Long getIdPadre() {
        return idPadre;
    }

    public void setIdPadre(Long idPadre) {
        this.idPadre = idPadre;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public String getGradoSeccion() {
        return gradoSeccion;
    }

    public void setGradoSeccion(String gradoSeccion) {
        this.gradoSeccion = gradoSeccion;
    }
}