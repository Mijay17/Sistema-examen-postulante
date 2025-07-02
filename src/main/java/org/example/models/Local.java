package org.example.models;

import java.util.Objects;

public class Local {
    private Integer id;
    private String nombre;

    // Constructores
    public Local() {}

    public Local(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Local(String nombre) {
        this.nombre = nombre;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // toString
    @Override
    public String toString() {
        return String.format("Local{id=%d, nombre='%s'}", id, nombre);
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Local local = (Local) o;
        return Objects.equals(id, local.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
