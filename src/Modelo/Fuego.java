package Modelo;

import java.util.Random;

public class Fuego {
    private int[] temperaturaPixeles; // Array con la temperatura de cada pixel
    private int anchoFuego;
    private int altoFuego;
    private Random aleatorio;

    public Fuego() {
        this.anchoFuego = Config.ANCHO_FUEGO;
        this.altoFuego = Config.ALTO_FUEGO;
        this.temperaturaPixeles = new int[anchoFuego * altoFuego];
        this.aleatorio = new Random();
        empezarFuego(); // Inicio el fuego
    }

    // Pongo el fuego inicial en la base
    private void empezarFuego() {
        // Primero pongo todo frio
        for (int i = 0; i < temperaturaPixeles.length; i++) {
            temperaturaPixeles[i] = Config.TEMPERATURA_MINIMA;
        }

        int filaBase = anchoFuego * (altoFuego - 1);

        // Base con variacion natural (no todo igual)
        for (int x = 0; x < anchoFuego; x++) {
            // Diferentes intensidades para que sea mas real
            double patronOndas = 0.6 + 0.4 * Math.sin(x * 0.08); // Entre 0.2 y 1.0
            double ruido = 0.7 + (aleatorio.nextDouble() * 0.6); // Entre 0.7 y 1.3
            double intensidad = patronOndas * ruido;

            temperaturaPixeles[filaBase + x] = (int)(700 + intensidad * 323);
        }

        // Añado puntos muy calientes aleatorios
        for (int i = 0; i < anchoFuego / 6; i++) {
            int x = aleatorio.nextInt(anchoFuego);
            temperaturaPixeles[filaBase + x] = 1023; // Puntos blancos muy calientes
        }
    }

    // Hago que el fuego se propague hacia arriba
    public void propagarFuego() {
        int inicioFila, inicioFilaAbajo, posicion, posicionAbajo;

        // Voy de abajo hacia arriba (empezando cerca de la base)
        for (int filaActual = altoFuego - 2; filaActual > 4; filaActual--) {
            inicioFila = anchoFuego * filaActual;
            inicioFilaAbajo = inicioFila + anchoFuego;

            for (int columnaActual = 2; columnaActual < anchoFuego - 2; columnaActual++) {
                posicion = inicioFila + columnaActual;
                posicionAbajo = inicioFilaAbajo + columnaActual;

                // Variacion aleatoria para que no sea todo uniforme
                double variacion = 0.8 + (aleatorio.nextDouble() * 0.4); // Entre 0.8 y 1.2

                // Calculo la nueva temperatura usando pixeles vecinos
                this.temperaturaPixeles[posicion] = (int) ((int) ((
                        temperaturaPixeles[posicion - 1] * 1.2D +
                                temperaturaPixeles[posicion] * 1.5D +
                                temperaturaPixeles[posicion + 1] * 1.2D +
                                temperaturaPixeles[posicionAbajo - 1] * 0.7D +
                                temperaturaPixeles[posicionAbajo] * 0.7D +
                                temperaturaPixeles[posicionAbajo + 1] * 0.7D
                ) / 5.98569 - 1.8D) * variacion);

                // Me aseguro de que no se pase de los limites
                if (this.temperaturaPixeles[posicion] < 0) {
                    temperaturaPixeles[posicion] = 0;
                } else if (temperaturaPixeles[posicion] > 1023) {
                    temperaturaPixeles[posicion] = 1023;
                }
            }
        }

        // Mantengo la base con variacion
        int filaBase = anchoFuego * (altoFuego - 1);
        for (int columnaActual = 0; columnaActual < anchoFuego; columnaActual++) {
            double variacion = 0.6 + 0.4 * Math.sin(columnaActual * 0.08 + aleatorio.nextDouble() * 0.5);
            int nuevoCalor = (int)(700 + variacion * 323);
            temperaturaPixeles[filaBase + columnaActual] = (int) (temperaturaPixeles[filaBase + columnaActual] * 0.4 + nuevoCalor * 0.6);
        }

        // Añado nuevos puntos calientes aleatorios
        if (aleatorio.nextDouble() < 0.1) { // 10% de probabilidad
            int x = aleatorio.nextInt(anchoFuego);
            temperaturaPixeles[filaBase + x] = 1023;
        }

        enfriarPixeles(); // Enfrío los pixeles
        aplicarMovimiento(); // Añado movimiento aleatorio
    }

    // Enfrío los pixeles calientes
    public void enfriarPixeles() {
        int[] temperaturaTemporal = temperaturaPixeles.clone();

        for (int i = anchoFuego; i < temperaturaPixeles.length - anchoFuego; i++) {
            if (temperaturaPixeles[i] > 300) {
                int enfriamiento = 8 + (temperaturaPixeles[i] / 100);
                temperaturaTemporal[i] = Math.max(0, temperaturaPixeles[i] - enfriamiento);
                enfriarVecinos(i, enfriamiento / 2, temperaturaTemporal);
            }
        }
        temperaturaPixeles = temperaturaTemporal;
    }

    // Enfrío los pixeles vecinos tambien
    private void enfriarVecinos(int posicion, int enfriamiento, int[] tempTemp) {
        int[] direcciones = {-anchoFuego, anchoFuego, -1, 1}; // Arriba, abajo, izquierda, derecha

        for (int dir : direcciones) {
            int vecino = posicion + dir;
            if (vecino >= 0 && vecino < tempTemp.length) {
                tempTemp[vecino] = Math.max(0, tempTemp[vecino] - enfriamiento);
            }
        }
    }

    // Añado movimiento aleatorio al fuego
    public void aplicarMovimiento() {
        for (int i = anchoFuego; i < temperaturaPixeles.length - anchoFuego; i++) {
            if (temperaturaPixeles[i] > 100 && aleatorio.nextDouble() < 0.3) {
                int cambio = aleatorio.nextInt(7) - 3; // Entre -3 y +3
                temperaturaPixeles[i] = Math.max(0, Math.min(1023, temperaturaPixeles[i] + cambio));
            }
        }
    }

    // Getters para acceder a los datos
    public int[] getTemperaturaPixeles() {
        return temperaturaPixeles;
    }

    public int getAnchoFuego() { return anchoFuego; }
    public int getAltoFuego() { return altoFuego; }
}