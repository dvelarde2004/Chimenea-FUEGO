package Controller;

import Modelo.Fuego;
import Vista.VistaFuego;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorFuego {
    private Fuego fuego; // El modelo del fuego
    private VistaFuego vista; // Lo que se ve en pantalla
    private Timer temporizador; // Para que se actualice solo

    // Constructor normal, le paso el fuego y la vista
    public ControladorFuego(Fuego fuego, VistaFuego vista) {
        this.fuego = fuego;
        this.vista = vista;
        configurarTemporizador(); // Prepara el temporizador
    }

    // Configuro el temporizador para que se actualice solo
    private void configurarTemporizador() {
        temporizador = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fuego.propagarFuego(); // El fuego se extiende
                vista.repaint(); // Vuelvo a dibujar la pantalla
            }
        });
    }

    // Empieza la propagacion del fuego
    public void empezar() {
        temporizador.start();
    }

    // Para el fuego
    public void parar() {
        temporizador.stop();
    }
}