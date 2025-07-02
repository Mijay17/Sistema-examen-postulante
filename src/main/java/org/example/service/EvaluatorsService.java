package org.example.service;

import org.example.models.Respuestas;
import org.example.models.ResultadoExamen;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class EvaluatorsService {

    public static ResultadoExamen calcularResultado(Respuestas respuestas, String clave) {
        if (respuestas == null || clave == null || clave.length() != 100) {
            throw new IllegalArgumentException("Respuestas o clave inválidas.");
        }

        int correctas = respuestas.contarRespuestasCorrectas(clave);
        int incorrectas = respuestas.contarRespuestasIncorrectas(clave);
        int nulas = respuestas.contarRespuestasNulas();

        double puntaje = (correctas * 20.0) - (incorrectas * 1.125);

        // Redondear a 2 decimales
        BigDecimal bd = new BigDecimal(puntaje).setScale(2, RoundingMode.HALF_UP);
        puntaje = bd.doubleValue();

        ResultadoExamen resultado = new ResultadoExamen(respuestas.getCodigoPostulante());
        resultado.setPuntaje(puntaje);
        resultado.setRespuestasCorrectas(correctas);
        resultado.setRespuestasIncorrectas(incorrectas);
        resultado.setRespuestasNulas(nulas);
        resultado.setFechaEvaluacion(LocalDateTime.now());

        // Observación simple
        if (puntaje == 0.0) {
            resultado.setObservacion(ResultadoExamen.ObservacionEnum.NO_SE_PRESENTO);
        } else {
            resultado.setObservacion(ResultadoExamen.ObservacionEnum.ALCANZANTE);
        }

        // Mérito se puede calcular luego por ordenamiento
        resultado.setMerito("");

        return resultado;
    }

    // MÉTODO AGREGADO para compatibilidad con HelloController
    public static float calcularPuntaje(String respuestasPostulante, String clave) {
        if (respuestasPostulante == null || clave == null || clave.length() != 100) {
            throw new IllegalArgumentException("Respuestas o clave inválidas.");
        }

        // Crear objeto Respuestas temporal para usar los métodos existentes
        Respuestas respuestas = new Respuestas("TEMP", respuestasPostulante);

        int correctas = respuestas.contarRespuestasCorrectas(clave);
        int incorrectas = respuestas.contarRespuestasIncorrectas(clave);

        double puntaje = (correctas * 20.0) - (incorrectas * 1.125);

        // Redondear a 2 decimales
        BigDecimal bd = new BigDecimal(puntaje).setScale(2, RoundingMode.HALF_UP);

        return bd.floatValue();
    }
}