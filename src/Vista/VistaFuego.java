package Vista;

import Modelo.Fuego;
import Modelo.Config;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class VistaFuego extends JPanel {
    private Fuego fuego; // El modelo del fuego
    private BufferedImage imagenFuego; // Imagen donde se dibuja el fuego
    private BufferedImage imagenFondo; // Imagen de fondo (chimenea)

    // Donde se coloca el fuego en la pantalla
    private int posicionXFuego = 330;      // Posicion X del fuego
    private int posicionYFuego = 323;      // Posicion Y del fuego
    private int anchoFuego = 280;          // Ancho del fuego
    private int altoFuego = 150;           // Alto del fuego

    public VistaFuego(Fuego fuego) {
        this.fuego = fuego;
        // Tamaño de la ventana
        setPreferredSize(new Dimension(Config.ANCHO_VENTANA, Config.ALTO_VENTANA));
        setBackground(Color.BLACK);
        // Creo la imagen donde se dibujara el fuego
        imagenFuego = new BufferedImage(fuego.getAnchoFuego(), fuego.getAltoFuego(), BufferedImage.TYPE_INT_ARGB);
        cargarFondo(); // Cargo la imagen de fondo
    }

    // Intento cargar la imagen de la chimenea
    private void cargarFondo() {
        try {
            // Cargo la imagen desde la carpeta img
            imagenFondo = ImageIO.read(new File("C:\\trabajos clase\\Segundo año\\programacion\\java\\clase jumi\\Fuego\\src\\img\\Casa_imagen de fondo.jpg"));
            System.out.println("Imagen de fondo cargada bien");
        } catch (IOException e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
            System.err.println("Revisa que la imagen este en: src/img/Casa_imagen de fondo.jpg");
            imagenFondo = null; // Si no hay imagen, no pasa nada
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Pongo la mejor calidad para que se vea bien
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 1. DIBUJO EL FONDO (la chimenea)
        if (imagenFondo != null) {
            g2d.drawImage(imagenFondo, 0, 0, Config.ANCHO_VENTANA, Config.ALTO_VENTANA, null);
        } else {
            // Si no hay imagen, pongo un fondo marron
            g2d.setColor(new Color(40, 25, 15));
            g2d.fillRect(0, 0, Config.ANCHO_VENTANA, Config.ALTO_VENTANA);
        }

        // 2. DIBUJO EL FUEGO en su posicion
        actualizarFuego();
        g2d.drawImage(imagenFuego, posicionXFuego, posicionYFuego, anchoFuego, altoFuego, null);
    }

    // Actualizo la imagen del fuego con las temperaturas actuales
    private void actualizarFuego() {
        int[] temperaturas = fuego.getTemperaturaPixeles();
        int ancho = fuego.getAnchoFuego();
        int alto = fuego.getAltoFuego();

        // Recorro todos los pixeles del fuego
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int temperatura = temperaturas[y * ancho + x];
                Color color = convertirTemperaturaAColor(temperatura);
                imagenFuego.setRGB(x, y, color.getRGB());
            }
        }

        suavizarFuego(); // Aplico un efecto de blur para que sea mas real
    }

    // Convierto la temperatura en un color (de negro a amarillo brillante)
    private Color convertirTemperaturaAColor(int temperatura) {
        if (temperatura < 20) return new Color(0, 0, 0, 0); // Transparente si esta frio

        float progreso = (float) temperatura / 1023.0f; // De 0 a 1

        // Segun lo caliente que este, elijo un color
        if (progreso < 0.08f) {
            return mezclarColores(0.0f, 0.08f, progreso,
                    new Color(0, 0, 0, 0),           // Negro transparente
                    new Color(30, 0, 0, 80));        // Rojo oscuro
        } else if (progreso < 0.2f) {
            return mezclarColores(0.08f, 0.2f, progreso,
                    new Color(30, 0, 0, 80),         // Rojo oscuro
                    new Color(80, 5, 0, 140));       // Rojo medio
        } else if (progreso < 0.35f) {
            return mezclarColores(0.2f, 0.35f, progreso,
                    new Color(80, 5, 0, 140),        // Rojo medio
                    new Color(160, 25, 0, 190));     // Rojo anaranjado
        } else if (progreso < 0.55f) {
            return mezclarColores(0.35f, 0.55f, progreso,
                    new Color(160, 25, 0, 190),      // Rojo anaranjado
                    new Color(220, 60, 0, 220));     // Naranja
        } else if (progreso < 0.75f) {
            return mezclarColores(0.55f, 0.75f, progreso,
                    new Color(220, 60, 0, 220),      // Naranja
                    new Color(255, 120, 0, 240));    // Naranja claro
        } else if (progreso < 0.9f) {
            return mezclarColores(0.75f, 0.9f, progreso,
                    new Color(255, 120, 0, 240),     // Naranja claro
                    new Color(255, 180, 40, 250));   // Amarillo anaranjado
        } else {
            return mezclarColores(0.9f, 1.0f, progreso,
                    new Color(255, 180, 40, 250),    // Amarillo anaranjado
                    new Color(255, 220, 100, 255));  // Amarillo brillante
        }
    }

    // Mezclo dos colores segun el progreso
    private Color mezclarColores(float inicio, float fin, float progreso,
                                 Color colorInicio, Color colorFin) {
        float factor = (progreso - inicio) / (fin - inicio);
        factor = Math.max(0, Math.min(1, factor)); // Me aseguro de que este entre 0 y 1

        // Calculo los nuevos componentes del color
        int r = (int)(colorInicio.getRed() + (colorFin.getRed() - colorInicio.getRed()) * factor);
        int g = (int)(colorInicio.getGreen() + (colorFin.getGreen() - colorInicio.getGreen()) * factor);
        int b = (int)(colorInicio.getBlue() + (colorFin.getBlue() - colorInicio.getBlue()) * factor);
        int a = (int)(colorInicio.getAlpha() + (colorFin.getAlpha() - colorInicio.getAlpha()) * factor);

        return new Color(r, g, b, a);
    }

    // Aplico un efecto de blur para suavizar el fuego
    private void suavizarFuego() {
        BufferedImage imagenSuavizada = new BufferedImage(imagenFuego.getWidth(), imagenFuego.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Recorro todos los pixeles excepto los bordes
        for (int y = 1; y < imagenFuego.getHeight() - 1; y++) {
            for (int x = 1; x < imagenFuego.getWidth() - 1; x++) {
                int r = 0, g = 0, b = 0, a = 0;

                // Promedio de los 9 pixeles alrededor
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        Color pixel = new Color(imagenFuego.getRGB(x + dx, y + dy), true);
                        r += pixel.getRed();
                        g += pixel.getGreen();
                        b += pixel.getBlue();
                        a += pixel.getAlpha();
                    }
                }

                // Color promedio
                Color promedio = new Color(r/9, g/9, b/9, a/9);
                imagenSuavizada.setRGB(x, y, promedio.getRGB());
            }
        }

        // Copio la imagen suavizada a la original
        for (int y = 1; y < imagenFuego.getHeight() - 1; y++) {
            for (int x = 1; x < imagenFuego.getWidth() - 1; x++) {
                imagenFuego.setRGB(x, y, imagenSuavizada.getRGB(x, y));
            }
        }
    }

    // Para cambiar donde se dibuja el fuego
    public void cambiarPosicionFuego(int x, int y) {
        this.posicionXFuego = x;
        this.posicionYFuego = y;
        repaint(); // Vuelvo a dibujar
    }

    // Para cambiar el tamaño del fuego
    public void cambiarTamañoFuego(int ancho, int alto) {
        this.anchoFuego = ancho;
        this.altoFuego = alto;
        repaint(); // Vuelvo a dibujar
    }

    // Getters para saber donde esta el fuego
    public int getPosicionXFuego() { return posicionXFuego; }
    public int getPosicionYFuego() { return posicionYFuego; }
    public int getAnchoFuego() { return anchoFuego; }
    public int getAltoFuego() { return altoFuego; }
}