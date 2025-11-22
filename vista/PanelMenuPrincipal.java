package vista;

import java.awt.*;
import javax.swing.*;
import util.Estilo;

public class PanelMenuPrincipal extends JPanel {

    public PanelMenuPrincipal(CardLayout cardLayout, JPanel mainPanel, Runnable... updateActions) {
        // Usamos Estilo.decorarPanel para aplicar el fondo
        Estilo.decorarPanel(this);
        
        this.setLayout(new GridLayout(10, 1, 15, 15)); // Aumentado de 9 a 10
        this.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // El título se crea y se decora con el método estático
        JLabel titulo = Estilo.crearTitulo("Sistema de Gestión de Pádel");
        this.add(titulo);

        // Botones de navegación
        this.add(crearBotonNavegacion("Gestionar Jugadores", "JUGADORES", cardLayout, mainPanel, updateActions[0]));
        this.add(crearBotonNavegacion("Gestionar Equipos", "EQUIPOS", cardLayout, mainPanel, updateActions[1]));
        this.add(crearBotonNavegacion("Gestionar Sedes y Canchas", "SEDES", cardLayout, mainPanel, updateActions[2]));
        this.add(crearBotonNavegacion("Gestionar Competiciones", "TORNEOS_PARTIDOS", cardLayout, mainPanel, updateActions[3]));
        
        // --- NUEVO BOTÓN ---
        this.add(crearBotonNavegacion("Gestionar Árbitros", "ARBITROS", cardLayout, mainPanel, updateActions[4]));
        
        this.add(crearBotonNavegacion("Ver Rankings", "RANKINGS", cardLayout, mainPanel, updateActions[5]));
        this.add(crearBotonNavegacion("Historial de Torneos", "HISTORIAL", cardLayout, mainPanel, updateActions[6]));
        this.add(crearBotonNavegacion("Consultar Disponibilidad de Canchas", "DISPONIBILIDAD", cardLayout, mainPanel, updateActions[7]));

        JButton btnSalir = new JButton("Salir");
        Estilo.decorarBoton(btnSalir); 
        btnSalir.setBackground(Estilo.COLOR_TEXTO); 
        btnSalir.addActionListener(e -> System.exit(0));
        this.add(btnSalir);
    }

    private JButton crearBotonNavegacion(String texto, String panelDestino, CardLayout cardLayout, JPanel mainPanel, Runnable accionPrevia) {
        JButton boton = new JButton(texto);
        
        Estilo.decorarBoton(boton); 
        
        boton.addActionListener(e -> {
            if (accionPrevia != null) {
                accionPrevia.run();
            }
            cardLayout.show(mainPanel, panelDestino);
        });
        return boton;
    }
}