import Modelo.Fuego;
import Vista.FuegoView;
import Controller.FuegoController;

import javax.swing.*;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Fuego model = new Fuego();
        FuegoView view = new FuegoView(model);
        FuegoController controller = new FuegoController(model, view);

        JFrame frame = new JFrame("🔥 FUEGO CON LLAMAS SUAVES");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.BLACK);
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        controller.iniciar();
    }
}