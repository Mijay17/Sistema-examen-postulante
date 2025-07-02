package org.example.models.dto;
import java.util.List;
import java.util.List;
import java.util.Objects;

public class ResultadoEvaluacionDTO {
    private List<org.example.models.ResultadoExamen> resultados;
    private long tiempoEjecucionMs;
    private int totalProcesados;
    private int totalAprobados;
    private int numeroHilos;
    private double promedioGeneral;

    // Constructores
    public ResultadoEvaluacionDTO() {}

    public ResultadoEvaluacionDTO(List<org.example.models.ResultadoExamen> resultados) {
        this.resultados = resultados;
        this.totalProcesados = resultados != null ? resultados.size() : 0;
        calcularEstadisticas();
    }

    // Getters y Setters
    public List<org.example.models.ResultadoExamen> getResultados() { return resultados; }
    public void setResultados(List<org.example.models.ResultadoExamen> resultados) {
        this.resultados = resultados;
        this.totalProcesados = resultados != null ? resultados.size() : 0;
        calcularEstadisticas();
    }

    public long getTiempoEjecucionMs() { return tiempoEjecucionMs; }
    public void setTiempoEjecucionMs(long tiempoEjecucionMs) { this.tiempoEjecucionMs = tiempoEjecucionMs; }

    public int getTotalProcesados() { return totalProcesados; }
    public void setTotalProcesados(int totalProcesados) { this.totalProcesados = totalProcesados; }

    public int getTotalAprobados() { return totalAprobados; }
    public void setTotalAprobados(int totalAprobados) { this.totalAprobados = totalAprobados; }

    public int getNumeroHilos() { return numeroHilos; }
    public void setNumeroHilos(int numeroHilos) { this.numeroHilos = numeroHilos; }

    public double getPromedioGeneral() { return promedioGeneral; }
    public void setPromedioGeneral(double promedioGeneral) { this.promedioGeneral = promedioGeneral; }

    // Métodos de utilidad
    private void calcularEstadisticas() {
        if (resultados != null && !resultados.isEmpty()) {
            totalAprobados = (int) resultados.stream()
                    .filter(org.example.models.ResultadoExamen::esAprobado)
                    .count();

            promedioGeneral = resultados.stream()
                    .mapToDouble(org.example.models.ResultadoExamen::getPuntaje)
                    .average()
                    .orElse(0.0);
        }
    }

    public double getPorcentajeAprobacion() {
        return totalProcesados > 0 ? (totalAprobados * 100.0) / totalProcesados : 0.0;
    }

    public double getThroughput() {
        return tiempoEjecucionMs > 0 ? (totalProcesados * 1000.0) / tiempoEjecucionMs : 0.0;
    }

    @Override
    public String toString() {
        return String.format("ResultadoEvaluacionDTO{procesados=%d, aprobados=%d, tiempo=%dms, hilos=%d, promedio=%.2f}",
                totalProcesados, totalAprobados, tiempoEjecucionMs, numeroHilos, promedioGeneral);
    }
}