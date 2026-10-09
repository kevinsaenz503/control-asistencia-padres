package com.saenz.login.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Asistencia {

    private Long idAsistencia;
    private Long idPadre;
    private Long idReunion;
    private String nombrePadre;
    private String dui;
    private String estado;
    private LocalTime horaLlegada;
    private String tipoReunion;
    private LocalDate fechaReunion;
    private String firmaDigital;

    public Asistencia() {}

    public Long getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(Long idAsistencia) { this.idAsistencia = idAsistencia; }
    public Long getIdPadre() { return idPadre; }
    public void setIdPadre(Long idPadre) { this.idPadre = idPadre; }
    public Long getIdReunion() { return idReunion; }
    public void setIdReunion(Long idReunion) { this.idReunion = idReunion; }
    public String getNombrePadre() { return nombrePadre; }
    public void setNombrePadre(String nombrePadre) { this.nombrePadre = nombrePadre; }
    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalTime getHoraLlegada() { return horaLlegada; }
    public void setHoraLlegada(LocalTime horaLlegada) { this.horaLlegada = horaLlegada; }
    public String getTipoReunion() { return tipoReunion; }
    public void setTipoReunion(String tipoReunion) { this.tipoReunion = tipoReunion; }
    public LocalDate getFechaReunion() { return fechaReunion; }
    public void setFechaReunion(LocalDate fechaReunion) { this.fechaReunion = fechaReunion; }
    public String getFirmaDigital() { return firmaDigital; }
    public void setFirmaDigital(String firmaDigital) { this.firmaDigital = firmaDigital; }

    public Object get() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
