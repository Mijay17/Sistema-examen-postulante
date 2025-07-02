package org.example.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Postulante {
    private String codigoPostulante;
    private String nombre;
    private Integer idEscuela;
    private Integer idProceso;
    private LocalDateTime fechaRegistro;

    // Relaciones para facilitar consultas
    private EscuelaProfesional escuelaProfesional;
    private AnioProceso anioProceso;

    // Constructores
    public Postulante() {}

    public Postulante(String codigoPostulante, String nombre, Integer idEscuela, Integer idProceso) {
        this.codigoPostulante = codigoPostulante;
        this.nombre = nombre;
        this.idEscuela = idEscuela;
        this.idProceso = idProceso;
        this.fechaRegistro = LocalDateTime.now();
    }

    // Getters y Setters
    public String getCodigoPostulante() { return codigoPostulante; }
    public void setCodigoPostulante(String codigoPostulante) { this.codigoPostulante = codigoPostulante; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getIdEscuela() { return idEscuela; }
    public void setIdEscuela(Integer idEscuela) { this.idEscuela = idEscuela; }

    public Integer getIdProceso() { return idProceso; }
    public void setIdProceso(Integer idProceso) { this.idProceso = idProceso; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public EscuelaProfesional getEscuelaProfesional() { return escuelaProfesional; }
    public void setEscuelaProfesional(EscuelaProfesional escuelaProfesional) { this.escuelaProfesional = escuelaProfesional; }

    public AnioProceso getAnioProceso() { return anioProceso; }
    public void setAnioProceso(AnioProceso anioProceso) { this.anioProceso = anioProceso; }

    // Métodos de utilidad
    public String getNombreEscuela() {
        return escuelaProfesional != null ? escuelaProfesional.getNombre() : "";
    }

    public String getProcesoDescripcion() {
        return anioProceso != null ? anioProceso.getProceso() : "";
    }

    // toString
    @Override
    public String toString() {
        return String.format("Postulante{codigo='%s', nombre='%s', idEscuela=%d, idProceso=%d}",
                codigoPostulante, nombre, idEscuela, idProceso);
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Postulante that = (Postulante) o;
        return Objects.equals(codigoPostulante, that.codigoPostulante);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoPostulante);
    }
}