package app;

import javax.swing.SwingUtilities;
import vista.SistemaPadelGUI;

/**
 * Clase principal que contiene el punto de entrada (main method) de la aplicación.
 * Su única responsabilidad es iniciar la Interfaz Gráfica de Usuario (GUI)
 * de una manera segura para los hilos de Swing.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SistemaPadelGUI gui = new SistemaPadelGUI();
            gui.setVisible(true);
        });
    }
}