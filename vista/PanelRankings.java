package vista;

import controlador.GestorSistema;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import modelo.Equipo;
import modelo.Jugador;
import util.Estilo;
import util.Ranking;

public class PanelRankings extends JPanel {
    private GestorSistema gestor;
    private JTextArea rankingJugadoresArea;
    private JTextArea rankingEquiposArea;

    public PanelRankings(GestorSistema gestor, CardLayout cardLayout, JPanel mainPanel) {
        this.gestor = gestor;
        
        // --- APLICACIÓN DE ESTILO AL PANEL ---
        Estilo.decorarPanel(this);
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título del Panel
        this.add(Estilo.crearTitulo("Rankings por Ratio de Victorias"), BorderLayout.NORTH);

        // --- CONTENEDOR DE RANKINGS (Dos columnas) ---
        JPanel rankingsPanel = new JPanel(new GridLayout(1, 2, 20, 20)); // Espacio horizontal de 20
        Estilo.decorarPanel(rankingsPanel);

        // --- RANKING DE JUGADORES ---
        rankingJugadoresArea = new JTextArea();
        rankingJugadoresArea.setEditable(false);
        rankingJugadoresArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        rankingJugadoresArea.setBackground(Estilo.BLANCO);
        rankingJugadoresArea.setForeground(Estilo.COLOR_TEXTO);
        
        JScrollPane scrollJugadores = new JScrollPane(rankingJugadoresArea);
        scrollJugadores.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL), 
            "Ranking de Jugadores", 
            javax.swing.border.TitledBorder.LEFT, 
            javax.swing.border.TitledBorder.TOP, 
            Estilo.FUENTE_SUBTITULO, 
            Estilo.COLOR_PRINCIPAL
        ));
        rankingsPanel.add(scrollJugadores);

        // --- RANKING DE EQUIPOS ---
        rankingEquiposArea = new JTextArea();
        rankingEquiposArea.setEditable(false);
        rankingEquiposArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        rankingEquiposArea.setBackground(Estilo.BLANCO);
        rankingEquiposArea.setForeground(Estilo.COLOR_TEXTO);
        
        JScrollPane scrollEquipos = new JScrollPane(rankingEquiposArea);
        scrollEquipos.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL), 
            "Ranking de Equipos", 
            javax.swing.border.TitledBorder.LEFT, 
            javax.swing.border.TitledBorder.TOP, 
            Estilo.FUENTE_SUBTITULO, 
            Estilo.COLOR_PRINCIPAL
        ));
        rankingsPanel.add(scrollEquipos);
        
        this.add(rankingsPanel, BorderLayout.CENTER);
        
        // --- BOTÓN VOLVER ---
        JPanel surPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Estilo.decorarPanel(surPanel);
        
        JButton btnVolver = new JButton("Volver al Menú Principal");
        Estilo.decorarBoton(btnVolver);
        btnVolver.setBackground(Estilo.COLOR_TEXTO);
        btnVolver.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        
        surPanel.add(btnVolver);
        this.add(surPanel, BorderLayout.SOUTH);
    }

    public void actualizar() {
        // --- Ranking Jugadores ---
        List<Jugador> jugadores = gestor.getJugadoresRegistrados();
        Ranking.ordenarJugadoresPorRatio(jugadores);
        StringBuilder sbJugadores = new StringBuilder();
        sbJugadores.append(String.format("%-4s %-25s %-10s %-7s\n", "#", "Jugador", "(G/P)", "Ratio %"));
        sbJugadores.append("----------------------------------------------------\n");
        int i = 1;
        for (Jugador j : jugadores) {
            int total = j.getPartidosGanados() + j.getPartidosPerdidos();
            double ratio = total == 0 ? 0 : (double)j.getPartidosGanados() / total * 100;
            sbJugadores.append(String.format("%-4d %-25.25s (%-2d/%-2d)  %.1f%%\n", i++, j.getNombre() + " " + j.getApellido(), j.getPartidosGanados(), j.getPartidosPerdidos(), ratio));
        }
        rankingJugadoresArea.setText(sbJugadores.toString());
        
        // --- Ranking Equipos ---
        List<Equipo> equipos = gestor.getEquiposRegistrados();
        Ranking.ordenarEquiposPorRatio(equipos);
        StringBuilder sbEquipos = new StringBuilder();
        sbEquipos.append(String.format("%-4s %-25s %-10s %-7s\n", "#", "Equipo", "(G/P)", "Ratio %"));
        sbEquipos.append("----------------------------------------------------\n");
        i = 1;
        for (Equipo e : equipos) {
            int total = e.getPartidosGanados() + e.getPartidosPerdidos();
            double ratio = total == 0 ? 0 : (double)e.getPartidosGanados() / total * 100;
            sbEquipos.append(String.format("%-4d %-25.25s (%-2d/%-2d)  %.1f%%\n", i++, e.getNombre(), e.getPartidosGanados(), e.getPartidosPerdidos(), ratio));
        }
        rankingEquiposArea.setText(sbEquipos.toString());
    }
}