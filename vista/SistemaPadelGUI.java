package vista;

import controlador.GestorSistema;
import java.awt.*;
import javax.swing.*;
import persistencia.IPersistencia;
import persistencia.PersistenciaSerializable;

public class SistemaPadelGUI extends JFrame {

    private GestorSistema gestor;
    private IPersistencia persistencia;

    private PanelMenuPrincipal panelMenu;
    private PanelGestionJugadores panelJugadores;
    private PanelGestionEquipos panelEquipos;
    private PanelGestionSedes panelSedes;
    private PanelGestionCompeticiones panelCompeticiones;
    private PanelGestionArbitros panelArbitros; 
    private PanelRankings panelRankings;
    private PanelHistorial panelHistorial;
    private PanelDisponibilidadCanchas panelDisponibilidad; 

    public SistemaPadelGUI() {
        gestor = new GestorSistema();
        persistencia = new PersistenciaSerializable(); 
        
        gestor.setPersistencia(persistencia);
        
        // Cargar datos al inicio (si el archivo existe)
        persistencia.cargarDatos(gestor);
        
        // El sistema iniciará vacío si la carga falla.

        setTitle("Sistema de Gestión de Torneos para Pádel");
        setSize(1000, 800); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // INSTANCIACIÓN DE LOS PANELES
        panelJugadores = new PanelGestionJugadores(gestor, cardLayout, mainPanel);
        panelEquipos = new PanelGestionEquipos(gestor, cardLayout, mainPanel);
        panelSedes = new PanelGestionSedes(gestor, cardLayout, mainPanel);
        panelCompeticiones = new PanelGestionCompeticiones(gestor, cardLayout, mainPanel);
        panelArbitros = new PanelGestionArbitros(gestor, cardLayout, mainPanel);
        panelRankings = new PanelRankings(gestor, cardLayout, mainPanel);
        panelHistorial = new PanelHistorial(gestor, cardLayout, mainPanel);
        panelDisponibilidad = new PanelDisponibilidadCanchas(gestor, cardLayout, mainPanel); 

        panelMenu = new PanelMenuPrincipal(cardLayout, mainPanel,
            panelJugadores::actualizar,
            panelEquipos::actualizar,
            panelSedes::actualizar,
            panelCompeticiones::actualizar,
            panelArbitros::actualizar, 
            panelRankings::actualizar,
            panelHistorial::actualizar,
            panelDisponibilidad::actualizar
        );

        //  AÑADIR PANELES AL CARDLAYOUT
        mainPanel.add(panelMenu, "MENU");
        mainPanel.add(panelJugadores, "JUGADORES");
        mainPanel.add(panelEquipos, "EQUIPOS");
        mainPanel.add(panelSedes, "SEDES");
        mainPanel.add(panelCompeticiones, "TORNEOS_PARTIDOS");
        mainPanel.add(panelArbitros, "ARBITROS");
        mainPanel.add(panelRankings, "RANKINGS");
        mainPanel.add(panelHistorial, "HISTORIAL");
        mainPanel.add(panelDisponibilidad, "DISPONIBILIDAD"); 

        add(mainPanel);
        setVisible(true);
    }
    
}