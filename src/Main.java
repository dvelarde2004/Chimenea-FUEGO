import Modelo.Fuego;
import Vista.VistaFuego;
import Controller.ControladorFuego;

import javax.swing.*;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {
        // Pongo el estilo de la ventana como el del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Creo el fuego, la vista y el controlador
        Fuego fuego = new Fuego();
        VistaFuego vista = new VistaFuego(fuego);
        ControladorFuego controlador = new ControladorFuego(fuego, vista);

        // Creo la ventana principal
        JFrame ventana = new JFrame("🔥 SIMULADOR DE FUEGO REALISTA");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Para cerrar con la X
        ventana.setBackground(Color.BLACK); // Fondo negro por si acaso
        ventana.add(vista); // Añado la vista del fuego
        ventana.pack(); // Ajusto el tamaño automaticamente
        ventana.setLocationRelativeTo(null); // Centrada en la pantalla
        ventana.setVisible(true); // Hago visible la ventana

        // Empiezo la simulacion del fuego
        controlador.empezar();
    }
}