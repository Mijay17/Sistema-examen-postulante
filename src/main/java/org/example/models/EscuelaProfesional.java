package org.example.models;

import java.util.Objects;

public class EscuelaProfesional {
    private Integer id;
    private String nombre;
    private Integer idLocal;

    // Relación con Local (opcional para facilitar consultas)
    private Local local;

    // Constructores
    public EscuelaProfesional() {}

    public EscuelaProfesional(Integer id, String nombre, Integer idLocal) {
        this.id = id;
        this.nombre = nombre;
        this.idLocal = idLocal;
    }

    public EscuelaProfesional(String nombre, Integer idLocal) {
        this.nombre = nombre;
        this.idLocal = idLocal;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getIdLocal() { return idLocal; }
    public void setIdLocal(Integer idLocal) { this.idLocal = idLocal; }

    public Local getLocal() { return local; }
    public void setLocal(Local local) { this.local = local; }

    // toString
    @Override
    public String toString() {
        return String.format("EscuelaProfesional{id=%d, nombre='%s', idLocal=%d}",
                id, nombre, idLocal);
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EscuelaProfesional that = (EscuelaProfesional) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}