package org.example.models;

import java.util.Objects;

public class AnioProceso {
    private Integer id;
    private Integer year;
    private String proceso;

    // Constructores
    public AnioProceso() {}

    public AnioProceso(Integer id, Integer year, String proceso) {
        this.id = id;
        this.year = year;
        this.proceso = proceso;
    }

    public AnioProceso(Integer year, String proceso) {
        this.year = year;
        this.proceso = proceso;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getProceso() { return proceso; }
    public void setProceso(String proceso) { this.proceso = proceso; }

    // toString
    @Override
    public String toString() {
        return String.format("AnioProceso{id=%d, year=%d, proceso='%s'}", id, year, proceso);
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnioProceso that = (AnioProceso) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
