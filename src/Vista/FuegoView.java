package Vista;

import Modelo.FuegoModelo;
import Modelo.Config;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class FuegoView extends JPanel {
    private FuegoModelo model;
    private BufferedImage fireImage;

    public FuegoView(FuegoModelo model) {
        this.model = model;
        setPreferredSize(new Dimension(Config.ANCHO_VENTANA, Config.ALTO_VENTANA));
        setBackground(new Color(10, 10, 20)); // Fondo azul oscuro para mejor contraste
        fireImage = new BufferedImage(model.getFireWidth(), model.getFireHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // MÁXIMA CALIDAD GRÁFICA PARA EVITAR PIXELADO
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        actualizarImagenFuego();

        // Dibujar con escalado de alta calidad
        g2d.drawImage(fireImage, 0, 0, Config.ANCHO_VENTANA, Config.ALTO_VENTANA, null);
    }

    private void actualizarImagenFuego() {
        int[] temperatura = model.getPixelTemperature();
        int width = model.getFireWidth();
        int height = model.getFireHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int temp = temperatura[y * width + x];
                Color color = temperaturaAColorSuave(temp);
                fireImage.setRGB(x, y, color.getRGB());
            }
        }

        // Aplicar blur suave para eliminar pixelado
        aplicarBlurSuave();
    }

    private Color temperaturaAColorSuave(int temperatura) {
        if (temperatura < 20) return new Color(0, 0, 0, 0);

        // INTERPOLACIÓN CONTINUA - Sin escalones bruscos
        float progreso = (float) temperatura / 1023.0f;

        if (progreso < 0.08f) {
            return interpolarColor(0.0f, 0.08f, progreso,
                    new Color(0, 0, 0, 0),
                    new Color(30, 0, 0, 80));
        } else if (progreso < 0.2f) {
            return interpolarColor(0.08f, 0.2f, progreso,
                    new Color(30, 0, 0, 80),
                    new Color(80, 5, 0, 140));
        } else if (progreso < 0.35f) {
            return interpolarColor(0.2f, 0.35f, progreso,
                    new Color(80, 5, 0, 140),
                    new Color(160, 25, 0, 190));
        } else if (progreso < 0.55f) {
            return interpolarColor(0.35f, 0.55f, progreso,
                    new Color(160, 25, 0, 190),
                    new Color(220, 60, 0, 220));
        } else if (progreso < 0.75f) {
            return interpolarColor(0.55f, 0.75f, progreso,
                    new Color(220, 60, 0, 220),
                    new Color(255, 120, 0, 240));
        } else if (progreso < 0.9f) {
            return interpolarColor(0.75f, 0.9f, progreso,
                    new Color(255, 120, 0, 240),
                    new Color(255, 180, 40, 250));
        } else {
            return interpolarColor(0.9f, 1.0f, progreso,
                    new Color(255, 180, 40, 250),
                    new Color(255, 220, 100, 255));
        }
    }

    private Color interpolarColor(float inicio, float fin, float progreso,
                                  Color colorInicio, Color colorFin) {
        float factor = (progreso - inicio) / (fin - inicio);
        factor = Math.max(0, Math.min(1, factor));

        int r = (int)(colorInicio.getRed() + (colorFin.getRed() - colorInicio.getRed()) * factor);
        int g = (int)(colorInicio.getGreen() + (colorFin.getGreen() - colorInicio.getGreen()) * factor);
        int b = (int)(colorInicio.getBlue() + (colorFin.getBlue() - colorInicio.getBlue()) * factor);
        int a = (int)(colorInicio.getAlpha() + (colorFin.getAlpha() - colorInicio.getAlpha()) * factor);

        return new Color(r, g, b, a);
    }

    private void aplicarBlurSuave() {
        // Blur 3x3 simple para suavizar bordes pixelados
        BufferedImage blurred = new BufferedImage(fireImage.getWidth(), fireImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 1; y < fireImage.getHeight() - 1; y++) {
            for (int x = 1; x < fireImage.getWidth() - 1; x++) {
                int r = 0, g = 0, b = 0, a = 0;

                // Promediar con vecinos 3x3
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        Color pixel = new Color(fireImage.getRGB(x + dx, y + dy), true);
                        r += pixel.getRed();
                        g += pixel.getGreen();
                        b += pixel.getBlue();
                        a += pixel.getAlpha();
                    }
                }

                Color promedio = new Color(r/9, g/9, b/9, a/9);
                blurred.setRGB(x, y, promedio.getRGB());
            }
        }

        // Copiar resultado de vuelta
        for (int y = 1; y < fireImage.getHeight() - 1; y++) {
            for (int x = 1; x < fireImage.getWidth() - 1; x++) {
                fireImage.setRGB(x, y, blurred.getRGB(x, y));
            }
        }
    }
}