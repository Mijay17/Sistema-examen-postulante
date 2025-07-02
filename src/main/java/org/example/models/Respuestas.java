package org.example.models;

import java.util.Objects;

public class Respuestas {
    private Integer id;
    private String codigoPostulante;
    private String respuesta; // String de 100 caracteres con respuestas

    // Para facilitar el procesamiento
    private Postulante postulante;

    // Constructores
    public Respuestas() {}

    public Respuestas(Integer id, String codigoPostulante, String respuesta) {
        this.id = id;
        this.codigoPostulante = codigoPostulante;
        this.respuesta = respuesta;
    }

    public Respuestas(String codigoPostulante, String respuesta) {
        this.codigoPostulante = codigoPostulante;
        this.respuesta = respuesta;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigoPostulante() { return codigoPostulante; }
    public void setCodigoPostulante(String codigoPostulante) { this.codigoPostulante = codigoPostulante; }

    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }

    public Postulante getPostulante() { return postulante; }
    public void setPostulante(Postulante postulante) { this.postulante = postulante; }

    // Métodos de utilidad para procesamiento paralelo
    public char[] getRespuestasArray() {
        return respuesta != null ? respuesta.toCharArray() : new char[0];
    }

    public boolean isRespuestaValida() {
        if (respuesta == null || respuesta.length() != 100) {
            return false;
        }
        return respuesta.matches("^[ABCD*]+$");
    }

    public int contarRespuestasCorrectas(String gabarito) {
        if (!isRespuestaValida() || gabarito == null || gabarito.length() != 100) {
            return 0;
        }

        int correctas = 0;
        for (int i = 0; i < 100; i++) {
            if (respuesta.charAt(i) == gabarito.charAt(i)) {
                correctas++;
            }
        }
        return correctas;
    }

    public int contarRespuestasIncorrectas(String gabarito) {
        if (!isRespuestaValida() || gabarito == null || gabarito.length() != 100) {
            return 0;
        }

        int incorrectas = 0;
        for (int i = 0; i < 100; i++) {
            char miRespuesta = respuesta.charAt(i);
            if (miRespuesta != '*' && miRespuesta != gabarito.charAt(i)) {
                incorrectas++;
            }
        }
        return incorrectas;
    }

    public int contarRespuestasNulas() {
        if (!isRespuestaValida()) {
            return 0;
        }

        int nulas = 0;
        for (char c : respuesta.toCharArray()) {
            if (c == '*') {
                nulas++;
            }
        }
        return nulas;
    }

    // toString
    @Override
    public String toString() {
        return String.format("Respuestas{id=%d, codigoPostulante='%s', respuesta='%s'}",
                id, codigoPostulante,
                respuesta != null ? respuesta.substring(0, Math.min(20, respuesta.length())) + "..." : "null");
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Respuestas that = (Respuestas) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}