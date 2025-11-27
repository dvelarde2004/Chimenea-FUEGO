package Controller;

import Modelo.FuegoModelo;
import Vista.FuegoView;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FuegoController {
    private FuegoModelo model;
    private FuegoView view;
    private Timer timer;

    public FuegoController(FuegoModelo model, FuegoView view) {
        this.model = model;
        this.view = view;
        inicializarTimer();
    }

    private void inicializarTimer() {
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.propagarFuego();
                view.repaint();
            }
        });
    }

    public void iniciar() {
        timer.start();
    }

    public void detener() {
        timer.stop();
    }
}