package org.example.service;

import java.util.concurrent.ThreadLocalRandom;

public class GeneratorService {
    public static String generarRespuestasAleatorias() {
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < 100; i++) {
            resultado.append(generarRespuestaPonderada());
        }

        return resultado.toString();
    }

    private static char generarRespuestaPonderada() {
        int aciertos = ThreadLocalRandom.current().nextInt(0, 10);
        int rand = ThreadLocalRandom.current().nextInt(100); // [0, 99]
        if (aciertos < 9) {
            if (rand < 25) {
                return 'A';
            } else if (rand < 50) { // 15 + 25
                return 'B';
            } else if (rand < 75) { // 40 + 35
                return 'C';
            } else  { // 75 + 20
                return 'D';
            }
        }else{
            return '*';
        }

    }
}
