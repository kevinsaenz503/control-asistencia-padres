package com.saenz.login.model;

import java.time.LocalDate;

public class Reunion {

    private Long idReunion;
    private LocalDate fecha;
    private String tipoReunion;

    public Reunion() {
    }

    public Reunion(Long idReunion,
                   LocalDate fecha,
                   String tipoReunion) {

        this.idReunion = idReunion;
        this.fecha = fecha;
        this.tipoReunion = tipoReunion;
    }

    public Long getIdReunion() {
        return idReunion;
    }

    public void setIdReunion(Long idReunion) {
        this.idReunion = idReunion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipoReunion() {
        return tipoReunion;
    }

    public void setTipoReunion(String tipoReunion) {
        this.tipoReunion = tipoReunion;
    }
}