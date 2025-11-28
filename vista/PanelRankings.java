package vista;

import controlador.GestorSistema;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.Equipo;
import modelo.Jugador;
import util.Estilo;
import util.Ranking;

public class PanelRankings extends JPanel {
    
    private GestorSistema gestor;
    private JTable tablaJugadores;
    private JTable tablaEquipos;
    private DefaultTableModel modeloJugadores;
    private DefaultTableModel modeloEquipos;

    public PanelRankings(GestorSistema gestor, CardLayout cardLayout, JPanel mainPanel) {
        this.gestor = gestor;
        
        Estilo.decorarPanel(this);
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título del Panel
        this.add(Estilo.crearTitulo("Rankings por Ratio de Victorias"), BorderLayout.NORTH);

        // CONTENEDOR DE RANKINGS 
        JPanel rankingsPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 1 fila, 2 columnas, espacio horizontal
        Estilo.decorarPanel(rankingsPanel);

        // TABLA DE JUGADORES-
        String[] colJugadores = {"#", "Jugador", "G/P", "Ratio %"};
        modeloJugadores = new DefaultTableModel(colJugadores, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaJugadores = new JTable(modeloJugadores);
        Estilo.decorarTabla(tablaJugadores);
        
        // Ajuste de ancho de columnas para que el # sea pequeño
        tablaJugadores.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaJugadores.getColumnModel().getColumn(1).setPreferredWidth(150);

        JScrollPane scrollJugadores = new JScrollPane(tablaJugadores);
        scrollJugadores.getViewport().setBackground(Estilo.BLANCO);
        scrollJugadores.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL), 
            "Ranking de Jugadores", 
            javax.swing.border.TitledBorder.LEFT, 
            javax.swing.border.TitledBorder.TOP, 
            Estilo.FUENTE_SUBTITULO, 
            Estilo.COLOR_PRINCIPAL
        ));
        rankingsPanel.add(scrollJugadores);

        // TABLA DE EQUIPOS
        String[] colEquipos = {"#", "Equipo", "G/P", "Ratio %"};
        modeloEquipos = new DefaultTableModel(colEquipos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaEquipos = new JTable(modeloEquipos);
        Estilo.decorarTabla(tablaEquipos);
        
        tablaEquipos.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaEquipos.getColumnModel().getColumn(1).setPreferredWidth(150);
        
        JScrollPane scrollEquipos = new JScrollPane(tablaEquipos);
        scrollEquipos.getViewport().setBackground(Estilo.BLANCO);
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
        
        // BOTÓN VOLVER
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
        //Actualizar Tabla Jugadores
        modeloJugadores.setRowCount(0);
        List<Jugador> jugadores = gestor.getJugadoresRegistrados();
        Ranking.ordenarJugadoresPorRatio(jugadores);
        
        int i = 1;
        for (Jugador j : jugadores) {
            int total = j.getPartidosGanados() + j.getPartidosPerdidos();
            double ratio = total == 0 ? 0 : (double)j.getPartidosGanados() / total * 100;
            
            Object[] fila = {
                i++,
                j.getNombre() + " " + j.getApellido(),
                j.getPartidosGanados() + "/" + j.getPartidosPerdidos(),
                String.format("%.1f%%", ratio)
            };
            modeloJugadores.addRow(fila);
        }
        
        // Actualizar Tabla Equipos
        modeloEquipos.setRowCount(0);
        List<Equipo> equipos = gestor.getEquiposRegistrados();
        Ranking.ordenarEquiposPorRatio(equipos);
        
        i = 1;
        for (Equipo e : equipos) {
            int total = e.getPartidosGanados() + e.getPartidosPerdidos();
            double ratio = total == 0 ? 0 : (double)e.getPartidosGanados() / total * 100;
            
            Object[] fila = {
                i++,
                e.getNombre(),
                e.getPartidosGanados() + "/" + e.getPartidosPerdidos(),
                String.format("%.1f%%", ratio)
            };
            modeloEquipos.addRow(fila);
        }
    }
}