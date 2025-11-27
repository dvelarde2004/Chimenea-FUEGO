package Modelo;

import java.util.Random;

public class Fuego {
    private int[] pixelTemperature;
    private int fireWidth;
    private int fireHeight;
    private Random random;

    public Fuego() {
        this.fireWidth = Config.FIRE_WIDTH;
        this.fireHeight = Config.FIRE_HEIGHT;
        this.pixelTemperature = new int[fireWidth * fireHeight];
        this.random = new Random();
        inicializarFuegoNatural();
    }

    private void inicializarFuegoNatural() {
        for (int i = 0; i < pixelTemperature.length; i++) {
            pixelTemperature[i] = Config.TEMP_MINIMA;
        }

        int baseRow = fireWidth * (fireHeight - 1);

        // BASE CON VARIACIÓN NATURAL - NO UNIFORME
        for (int x = 0; x < fireWidth; x++) {
            // Diferentes intensidades en la base
            double patronOndulado = 0.6 + 0.4 * Math.sin(x * 0.08); // 0.2 a 1.0
            double ruidoAleatorio = 0.7 + (random.nextDouble() * 0.6); // 0.7 a 1.3
            double intensidad = patronOndulado * ruidoAleatorio;

            pixelTemperature[baseRow + x] = (int)(700 + intensidad * 323);
        }

        // AÑADIR "FOCOS" DE LLAMAS ALEATORIOS
        for (int i = 0; i < fireWidth / 6; i++) {
            int x = random.nextInt(fireWidth);
            pixelTemperature[baseRow + x] = 1023; // Puntos muy calientes
        }
    }

    public void propagarFuego() {
        int iniRow, iniBelowRow, pos, posBelow;

        for (int actualRow = fireHeight - 2; actualRow > 4; actualRow--) {
            iniRow = fireWidth * actualRow;
            iniBelowRow = iniRow + fireWidth;

            for (int actualCol = 2; actualCol < fireWidth - 2; actualCol++) {
                pos = iniRow + actualCol;
                posBelow = iniBelowRow + actualCol;

                // VARIACIÓN ALEATORIA PARA ROMPER UNIFORMIDAD
                double variacionLlama = 0.8 + (random.nextDouble() * 0.4); // 0.8 a 1.2

                this.pixelTemperature[pos] = (int) ((int) ((
                                        pixelTemperature[pos - 1] * 1.2D +
                                                pixelTemperature[pos] * 1.5D +
                                                pixelTemperature[pos + 1] * 1.2D +
                                                pixelTemperature[posBelow - 1] * 0.7D +
                                                pixelTemperature[posBelow] * 0.7D +
                                                pixelTemperature[posBelow + 1] * 0.7D
                                ) / 5.98569 - 1.8D) * variacionLlama); // ← APLICAR VARIACIÓN

                if (this.pixelTemperature[pos] < 0) {
                    pixelTemperature[pos] = 0;
                } else if (pixelTemperature[pos] > 1023) {
                    pixelTemperature[pos] = 1023;
                }
            }
        }

        // MANTENER BASE CON VARIACIÓN (no uniforme)
        int baseRow = fireWidth * (fireHeight - 1);
        for (int actualCol = 0; actualCol < fireWidth; actualCol++) {
            double variacion = 0.6 + 0.4 * Math.sin(actualCol * 0.08 + random.nextDouble() * 0.5);
            int nuevoCalor = (int)(700 + variacion * 323);
            pixelTemperature[baseRow + actualCol] = (int) (pixelTemperature[baseRow + actualCol] * 0.4 + nuevoCalor * 0.6);
        }

        // AÑADIR NUEVOS FOCOS ALEATORIOS
        if (random.nextDouble() < 0.1) { // 10% de probabilidad por frame
            int x = random.nextInt(fireWidth);
            pixelTemperature[baseRow + x] = 1023;
        }

        aplicarEnfriamientoNatural();
        aplicarTurbulencia(); // ← NUEVO: variación adicional
    }

    public void aplicarTurbulencia() {
        // PEQUEÑAS VARIACIONES ALEATORIAS PARA ROMPER PATRONES
        for (int i = fireWidth; i < pixelTemperature.length - fireWidth; i++) {
            if (pixelTemperature[i] > 100 && random.nextDouble() < 0.3) {
                int variacion = random.nextInt(7) - 3; // -3 a +3
                pixelTemperature[i] = Math.max(0, Math.min(1023, pixelTemperature[i] + variacion));
            }
        }
    }

    public void aplicarEnfriamientoNatural() {
        for (int i = 0; i < pixelTemperature.length; i++) {
            if (pixelTemperature[i] > 0) {
                double probabilidad = 0.4 + (0.4 * (1.0 - pixelTemperature[i] / 1023.0));

                if (random.nextDouble() < probabilidad) {
                    int enfriamiento = 1 + random.nextInt(8);
                    pixelTemperature[i] = Math.max(0, pixelTemperature[i] - enfriamiento);
                }
            }
        }
    }

    public int[] getPixelTemperature() {
        return pixelTemperature;
    }

    public int getFireWidth() { return fireWidth; }
    public int getFireHeight() { return fireHeight; }
}