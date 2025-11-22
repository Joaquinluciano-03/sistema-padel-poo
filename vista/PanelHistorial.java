package vista;

import controlador.GestorSistema;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import modelo.Partido;
import modelo.Torneo;
import util.Estilo;

public class PanelHistorial extends JPanel {
    private GestorSistema gestor;
    private JComboBox<String> torneoSelector;
    private JPanel detallesPanel;

    // Etiquetas de resumen
    private JLabel lblEstadoValor, lblMaxEquiposValor, lblGanadorValor;
    
    // Áreas de listas
    private JTextArea equiposTextArea, partidosPendientesArea, partidosFinalizadosArea;

    public PanelHistorial(GestorSistema gestor, CardLayout cardLayout, JPanel mainPanel) {
        this.gestor = gestor;
        
        // --- APLICACIÓN DE ESTILO AL PANEL PRINCIPAL ---
        Estilo.decorarPanel(this);
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        this.add(Estilo.crearTitulo("Historial y Seguimiento de Torneos"), BorderLayout.NORTH);

        // --- PANEL SUPERIOR DE SELECCIÓN ---
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        Estilo.decorarPanel(selectorPanel);
        
        torneoSelector = new JComboBox<>();
        torneoSelector.setFont(Estilo.FUENTE_NORMAL);
        torneoSelector.setPreferredSize(new Dimension(300, 30));
        torneoSelector.addActionListener(e -> mostrarDetallesTorneo());
        
        selectorPanel.add(new JLabel("Seleccionar Torneo:"));
        selectorPanel.add(torneoSelector);
        
        this.add(selectorPanel, BorderLayout.NORTH);

        // --- PANEL DE DETALLES (CENTRO) ---
        detallesPanel = new JPanel();
        detallesPanel.setLayout(new BorderLayout(15, 15));
        Estilo.decorarPanel(detallesPanel);
        this.add(new JScrollPane(detallesPanel), BorderLayout.CENTER); // Contenedor scrollable para detalles

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
    
    // --- LÓGICA DE ACTUALIZACIÓN ---

    public void actualizar() {
        // Guarda la selección actual para reestablecerla
        String selected = (String) torneoSelector.getSelectedItem();
        
        torneoSelector.removeAllItems();
        gestor.getTodosLosTorneos().forEach(t -> torneoSelector.addItem(t.getNombre()));
        
        // Si el combo estaba vacío, o el torneo sigue existiendo, lo selecciona.
        if (torneoSelector.getItemCount() > 0) {
            torneoSelector.setSelectedItem(selected);
            if (torneoSelector.getSelectedItem() == null) {
                 torneoSelector.setSelectedIndex(0);
            }
            mostrarDetallesTorneo();
        } else {
            detallesPanel.removeAll();
            detallesPanel.add(new JLabel("No hay torneos registrados para mostrar detalles."), BorderLayout.NORTH);
            detallesPanel.revalidate();
            detallesPanel.repaint();
        }
    }
    
    private void mostrarDetallesTorneo() {
        String nombreTorneo = (String) torneoSelector.getSelectedItem();
        if (nombreTorneo == null) return;
        
        Torneo t = gestor.buscarTorneoPorNombre(nombreTorneo);
        if (t == null) return;

        detallesPanel.removeAll();
        detallesPanel.setLayout(new BorderLayout(15, 15));
        
        // --- 1. PANEL DE RESUMEN (NORTE) ---
        JPanel resumenPanel = crearPanelResumen(t);
        detallesPanel.add(resumenPanel, BorderLayout.NORTH);

        // --- 2. PANEL DE LISTAS (CENTRO) ---
        JPanel listasPanel = crearPanelListas(t);
        detallesPanel.add(listasPanel, BorderLayout.CENTER);
        
        detallesPanel.revalidate();
        detallesPanel.repaint();
    }
    
    // --- MÉTODOS DE CONSTRUCCIÓN DE SUBPANELES ---
    
    private JPanel crearPanelResumen(Torneo t) {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 5));
        Estilo.decorarPanel(panel);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL), "Resumen", TitledBorder.LEFT, TitledBorder.TOP, Estilo.FUENTE_SUBTITULO, Estilo.COLOR_PRINCIPAL));
        
        // Estado
        lblEstadoValor = new JLabel(t.getEstado().toString());
        lblEstadoValor.setFont(Estilo.FUENTE_TITULO);
        lblEstadoValor.setForeground(t.getEstado() == Torneo.EstadoTorneo.FINALIZADO ? Estilo.COLOR_OCUPADO : Estilo.COLOR_PRINCIPAL);
        
        // Máx Equipos
        lblMaxEquiposValor = new JLabel("Inscritos: " + t.getEquiposInscritos().size() + " / " + t.getMaxEquipos());
        
        // Ganador
        String ganadorTxt = (t.getGanador() != null) ? t.getGanador().getNombre() : "Pendiente";
        lblGanadorValor = new JLabel("Ganador: " + ganadorTxt);

        panel.add(crearEtiquetaConValor("Estado:", lblEstadoValor));
        panel.add(crearEtiquetaConValor("Participantes:", lblMaxEquiposValor));
        panel.add(crearEtiquetaConValor("Resultado:", lblGanadorValor));
        
        return panel;
    }
    
    private JPanel crearPanelListas(Torneo t) {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 15));
        Estilo.decorarPanel(panel);

        // Columna 1: Equipos Inscritos
        panel.add(crearPanelAreaEquipos(t));
        
        // Columna 2: Partidos Pendientes
        panel.add(crearPanelAreaPartidos(t, false));
        
        // Columna 3: Partidos Finalizados
        panel.add(crearPanelAreaPartidos(t, true));
        
        return panel;
    }

    // MÉTODO CORREGIDO 1: Envuelve JScrollPane en un JPanel
    private JPanel crearPanelAreaEquipos(Torneo t) {
        equiposTextArea = new JTextArea();
        equiposTextArea.setEditable(false);
        equiposTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        equiposTextArea.setBackground(Estilo.BLANCO);
        
        StringBuilder sb = new StringBuilder();
        t.getEquiposInscritos().forEach(e -> sb.append(" - ").append(e.getNombre()).append("\n"));
        equiposTextArea.setText(sb.toString());

        JScrollPane scroll = new JScrollPane(equiposTextArea);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Estilo.COLOR_TEXTO), 
            "Equipos Inscritos (" + t.getEquiposInscritos().size() + ")", TitledBorder.LEFT, TitledBorder.TOP, Estilo.FUENTE_SUBTITULO, Estilo.COLOR_TEXTO));
        
        // FIX: Se crea un contenedor JPanel para devolver en lugar del JScrollPane
        JPanel container = new JPanel(new BorderLayout());
        Estilo.decorarPanel(container);
        container.add(scroll, BorderLayout.CENTER);
        return container; // Devuelve JPanel
    }
    
    // MÉTODO CORREGIDO 2: Envuelve JScrollPane en un JPanel
    private JPanel crearPanelAreaPartidos(Torneo t, boolean finalizados) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(Estilo.BLANCO);
        
        List<Partido> partidos = t.getPartidos().stream()
            .filter(p -> finalizados == p.isFinalizado())
            .collect(Collectors.toList());
        
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        
        for (Partido p : partidos) {
            sb.append(p.getEquipoLocal().getNombre()).append(" vs ").append(p.getEquipoVisitante().getNombre()).append("\n");
            sb.append("   - Fecha: ").append(p.getFechaHora().format(dtFormatter)).append("\n");
            sb.append("   - Cancha: N°").append(p.getCancha().getNumero()).append(" (").append(p.getCancha().getTipoSuperficie()).append(")\n");
            
            if (finalizados) {
                sb.append("   - Resultado: ").append(p.getResultado()).append("\n");
            }
            sb.append("---\n");
        }
        area.setText(sb.toString());

        JScrollPane scroll = new JScrollPane(area);
        String titulo = finalizados ? "Partidos Finalizados (" + partidos.size() + ")" : "Partidos Pendientes (" + partidos.size() + ")";
        
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(finalizados ? Estilo.COLOR_OCUPADO : Estilo.COLOR_PRINCIPAL), // Color Rojo/Verde
            titulo, TitledBorder.LEFT, TitledBorder.TOP, Estilo.FUENTE_SUBTITULO, finalizados ? Estilo.COLOR_OCUPADO : Estilo.COLOR_PRINCIPAL));
        
        // FIX: Se crea un contenedor JPanel para devolver en lugar del JScrollPane
        JPanel container = new JPanel(new BorderLayout());
        Estilo.decorarPanel(container);
        container.add(scroll, BorderLayout.CENTER);
        return container; // Devuelve JPanel
    }

    private JPanel crearEtiquetaConValor(String label, JLabel valor) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        Estilo.decorarPanel(panel);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(Estilo.FUENTE_SUBTITULO);
        lbl.setForeground(Estilo.COLOR_TEXTO);
        
        valor.setFont(Estilo.FUENTE_NORMAL); // Reajustar la fuente del valor

        panel.add(lbl, BorderLayout.WEST);
        panel.add(valor, BorderLayout.CENTER);
        
        return panel;
    }
}