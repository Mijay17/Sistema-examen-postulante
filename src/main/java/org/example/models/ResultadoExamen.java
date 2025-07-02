package org.example.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class ResultadoExamen {
    private Integer id;
    private String codigoPostulante;
    private Double puntaje;
    private String merito;
    private ObservacionEnum observacion;
    private Integer respuestasCorrectas;
    private Integer respuestasIncorrectas;
    private Integer respuestasNulas;
    private LocalDateTime fechaEvaluacion;

    // Para facilitar consultas
    private Postulante postulante;

    // Enum para observaciones
    public enum ObservacionEnum {
        ALCANZANTE("ALCANZANTE"),
        NO_SE_PRESENTO("NO SE PRESENTO");

        private final String valor;

        ObservacionEnum(String valor) {
            this.valor = valor;
        }

        public String getValor() {
            return valor;
        }

        public static ObservacionEnum fromString(String valor) {
            for (ObservacionEnum obs : ObservacionEnum.values()) {
                if (obs.valor.equals(valor)) {
                    return obs;
                }
            }
            return null;
        }
    }

    // Constructores
    public ResultadoExamen() {
        this.puntaje = 0.0;
        this.respuestasCorrectas = 0;
        this.respuestasIncorrectas = 0;
        this.respuestasNulas = 0;
        this.fechaEvaluacion = LocalDateTime.now();
    }

    public ResultadoExamen(String codigoPostulante) {
        this();
        this.codigoPostulante = codigoPostulante;
    }

    public ResultadoExamen(String codigoPostulante, Double puntaje, String merito, ObservacionEnum observacion) {
        this(codigoPostulante);
        this.puntaje = puntaje;
        this.merito = merito;
        this.observacion = observacion;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigoPostulante() { return codigoPostulante; }
    public void setCodigoPostulante(String codigoPostulante) { this.codigoPostulante = codigoPostulante; }

    public Double getPuntaje() { return puntaje; }
    public void setPuntaje(Double puntaje) { this.puntaje = puntaje; }

    public String getMerito() { return merito; }
    public void setMerito(String merito) { this.merito = merito; }

    public ObservacionEnum getObservacion() { return observacion; }
    public void setObservacion(ObservacionEnum observacion) { this.observacion = observacion; }

    public Integer getRespuestasCorrectas() { return respuestasCorrectas; }
    public void setRespuestasCorrectas(Integer respuestasCorrectas) { this.respuestasCorrectas = respuestasCorrectas; }

    public Integer getRespuestasIncorrectas() { return respuestasIncorrectas; }
    public void setRespuestasIncorrectas(Integer respuestasIncorrectas) { this.respuestasIncorrectas = respuestasIncorrectas; }

    public Integer getRespuestasNulas() { return respuestasNulas; }
    public void setRespuestasNulas(Integer respuestasNulas) { this.respuestasNulas = respuestasNulas; }

    public LocalDateTime getFechaEvaluacion() { return fechaEvaluacion; }
    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) { this.fechaEvaluacion = fechaEvaluacion; }

    public Postulante getPostulante() { return postulante; }
    public void setPostulante(Postulante postulante) { this.postulante = postulante; }

    // Métodos de utilidad
    public String getNombrePostulante() {
        return postulante != null ? postulante.getNombre() : "";
    }

    public String getNombreEscuela() {
        return postulante != null && postulante.getEscuelaProfesional() != null ?
                postulante.getEscuelaProfesional().getNombre() : "";
    }

    public void calcularPuntaje() {
        if (respuestasCorrectas != null && respuestasIncorrectas != null) {
            // Fórmula: +2 por correcta, -0.5 por incorrecta
            this.puntaje = (respuestasCorrectas * 2.0) - (respuestasIncorrectas * 0.5);
            // Puntaje mínimo es 0
            if (this.puntaje < 0) {
                this.puntaje = 0.0;
            }
        }
    }

    public boolean esAprobado() {
        return puntaje != null && puntaje >= 11.0;
    }

    // toString
    @Override
    public String toString() {
        return String.format("ResultadoExamen{id=%d, codigo='%s', puntaje=%.3f, merito='%s', observacion=%s}",
                id, codigoPostulante, puntaje, merito, observacion);
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultadoExamen that = (ResultadoExamen) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
