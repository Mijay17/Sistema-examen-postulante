package org.example.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EvaluatorsService {

    public static float calcularPuntaje(String respuestas, String clave) {
        if (respuestas == null || clave == null || respuestas.length() != clave.length()) {
            throw new IllegalArgumentException("Las cadenas de respuestas y clave deben tener la misma longitud y no ser nulas.");
        }

        float puntaje = 0;

        for (int i = 0; i < clave.length(); i++) {
            char r = respuestas.charAt(i);
            char c = clave.charAt(i);

            if (r == c) {
                puntaje += 20;
            } else if (r != '*' && r != c) {
                puntaje -= 1.275;
            }
        }

        BigDecimal bd = new BigDecimal(Float.toString(puntaje));
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.floatValue();
    }
}