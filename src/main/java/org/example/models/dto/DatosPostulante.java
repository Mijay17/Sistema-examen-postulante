package org.example.models.dto;

public class DatosPostulante {
    private String codigo;
    private String nombre;
    private int idEscuela;
    private String respuestas;
    private String nombreEscuela; // Opcional para mostrar

    // Constructores
    public DatosPostulante() {}

    public DatosPostulante(String codigo, String nombre, int idEscuela, String respuestas) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.idEscuela = idEscuela;
        this.respuestas = respuestas;
    }

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getIdEscuela() { return idEscuela; }
    public void setIdEscuela(int idEscuela) { this.idEscuela = idEscuela; }

    public String getRespuestas() { return respuestas; }
    public void setRespuestas(String respuestas) { this.respuestas = respuestas; }

    public String getNombreEscuela() { return nombreEscuela; }
    public void setNombreEscuela(String nombreEscuela) { this.nombreEscuela = nombreEscuela; }

    @Override
    public String toString() {
        return String.format("DatosPostulante{codigo='%s', nombre='%s', escuela=%d}",
                codigo, nombre, idEscuela);
    }
}