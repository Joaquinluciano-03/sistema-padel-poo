package vista;

import controlador.GestorSistema;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import modelo.Cancha;
import modelo.Partido;
import modelo.Sede;
import util.Estilo;
import util.SelectorFecha;

public class PanelDisponibilidadCanchas extends JPanel {

    private GestorSistema gestor;
    private JComboBox<String> sedeCombo;
    private JTextField dateField; 
    private JTable tablaDisponibilidad;
    private DefaultTableModel tableModel;

    private final LocalTime HORA_INICIO = LocalTime.of(8, 0);
    private final LocalTime HORA_FIN = LocalTime.of(22, 0);
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PanelDisponibilidadCanchas(GestorSistema gestor, CardLayout cardLayout, JPanel mainPanel) {
        this.gestor = gestor;
        
        Estilo.decorarPanel(this);
        this.setLayout(new BorderLayout(20, 20)); 
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        this.add(Estilo.crearTitulo("Disponibilidad de Canchas"), BorderLayout.NORTH);

        // --- Panel de Controles ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        Estilo.decorarPanel(controlPanel);
        
        JLabel lblSede = new JLabel("Sede:");
        lblSede.setFont(Estilo.FUENTE_NORMAL);
        controlPanel.add(lblSede);
        
        sedeCombo = new JComboBox<>();
        sedeCombo.setFont(Estilo.FUENTE_NORMAL);
        sedeCombo.setBackground(Estilo.BLANCO);
        controlPanel.add(sedeCombo);

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(Estilo.FUENTE_NORMAL);
        controlPanel.add(lblFecha);
        
        // Campo de fecha (Solo lectura)
        dateField = new JTextField();
        dateField.setPreferredSize(new Dimension(100, 30));
        dateField.setFont(Estilo.FUENTE_NORMAL);
        dateField.setEditable(false); 
        dateField.setText(LocalDate.now().format(dateFormat));
        dateField.setHorizontalAlignment(JTextField.CENTER);
        dateField.setBackground(Estilo.BLANCO);
        controlPanel.add(dateField);
        
        // Botón del Calendario
        JButton btnCalendario = new JButton("📅"); 
        Estilo.decorarBoton(btnCalendario);
        btnCalendario.setMargin(new Insets(2, 10, 2, 10)); 
        btnCalendario.addActionListener(e -> abrirCalendario());
        controlPanel.add(btnCalendario);
        
        JButton btnMostrar = new JButton("Consultar");
        Estilo.decorarBoton(btnMostrar); 
        btnMostrar.addActionListener(e -> mostrarDisponibilidad());
        controlPanel.add(btnMostrar);

        JPanel centerContainer = new JPanel(new BorderLayout());
        Estilo.decorarPanel(centerContainer);
        centerContainer.add(controlPanel, BorderLayout.NORTH);

        // --- Tabla de Resultados ---
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaDisponibilidad = new JTable(tableModel);
        Estilo.decorarTabla(tablaDisponibilidad);

        // --- RENDERIZADOR PERSONALIZADO MEJORADO ---
        tablaDisponibilidad.setDefaultRenderer(Object.class, new TableCellRenderer() {
            private final DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                defaultRenderer.setHorizontalAlignment(JLabel.CENTER);

                String texto = value != null ? value.toString() : "";
                // Establecer tooltip para leer contenido largo
                if (c instanceof JComponent) {
                    ((JComponent) c).setToolTipText(texto);
                }

                if (column > 0) { // Columnas de Canchas
                    if (texto.equals("Disponible")) {
                        // VERDE: Libre
                        c.setBackground(new Color(197, 227, 197)); 
                        c.setForeground(Estilo.COLOR_TEXTO);
                        c.setFont(Estilo.FUENTE_NORMAL); // Fuente normal
                    } else {
                        // ROJO: Cualquier texto que no sea "Disponible" es un partido
                        c.setBackground(new Color(255, 204, 204)); 
                        c.setForeground(Estilo.COLOR_OCUPADO); 
                        // Negrita para destacar los equipos
                        c.setFont(Estilo.FUENTE_NORMAL.deriveFont(Font.BOLD));
                    }
                } else {
                    // Columna HORA (Gris)
                    c.setBackground(new Color(230, 230, 230)); 
                    c.setForeground(Estilo.COLOR_TEXTO);
                    c.setFont(Estilo.FUENTE_NORMAL);
                }

                // Respetar selección
                if (isSelected) {
                    c.setBackground(Estilo.COLOR_SECUNDARIO); 
                    c.setForeground(Estilo.BLANCO);
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaDisponibilidad);
        scrollPane.getViewport().setBackground(Estilo.BLANCO); 
        scrollPane.setBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL, 1));
        
        centerContainer.add(scrollPane, BorderLayout.CENTER);
        this.add(centerContainer, BorderLayout.CENTER);
        
        // --- Botón Volver ---
        JPanel surPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Estilo.decorarPanel(surPanel);
        
        JButton btnVolver = new JButton("Volver al Menú");
        Estilo.decorarBoton(btnVolver); 
        btnVolver.setBackground(Estilo.COLOR_TEXTO); 
        btnVolver.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        
        surPanel.add(btnVolver);
        this.add(surPanel, BorderLayout.SOUTH);
    }
    
    public void actualizar() {
        sedeCombo.removeAllItems();
        gestor.getSedes().forEach(s -> sedeCombo.addItem(s.getNombre()));
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
    }
    
    private void abrirCalendario() {
        LocalDate fechaSeleccionada = SelectorFecha.mostrar(this);
        if (fechaSeleccionada != null) {
            dateField.setText(fechaSeleccionada.format(dateFormat));
        }
    }
    
    private void mostrarDisponibilidad() {
        if (sedeCombo.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una sede.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombreSede = (String) sedeCombo.getSelectedItem();
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(dateField.getText(), dateFormat);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fecha inválida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Sede sede = gestor.buscarSedePorNombre(nombreSede);
        if (sede.getCanchas().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La sede seleccionada no tiene canchas registradas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);
            return;
        }

        // Obtener datos
        List<Cancha> canchas = sede.getCanchas();
        List<Partido> partidos = gestor.getPartidosPorSedeYFecha(nombreSede, fecha);

        // Configurar Columnas
        String[] columnNames = new String[canchas.size() + 1];
        columnNames[0] = "HORA";
        for (int i = 0; i < canchas.size(); i++) {
            columnNames[i + 1] = "Cancha N°" + canchas.get(i).getNumero() + " (" + canchas.get(i).getTipoSuperficie() + ")";
        }
        tableModel.setColumnIdentifiers(columnNames);
        tableModel.setRowCount(0); 

        // Mapa de ocupación
        Map<Integer, Map<LocalTime, Partido>> horarioOcupado = new HashMap<>();
        for (Cancha cancha : canchas) {
            horarioOcupado.put(cancha.getNumero(), new HashMap<>());
        }

        // Llenar mapa con los partidos
        for (Partido p : partidos) {
            LocalTime horaInicio = p.getFechaHora().toLocalTime().withSecond(0).withNano(0);
            int duracion = p.getDuracionMinutos() > 0 ? p.getDuracionMinutos() : 90; 
            
            LocalTime horaActual = horaInicio;
            LocalTime horaFin = horaInicio.plusMinutes(duracion);
            
            // Marcar todos los bloques de 30 minutos que ocupa el partido
            while (horaActual.isBefore(horaFin)) {
                 if (p.getCancha() != null && horarioOcupado.containsKey(p.getCancha().getNumero())) {
                    horarioOcupado.get(p.getCancha().getNumero()).put(horaActual, p);
                }
                horaActual = horaActual.plusMinutes(30); 
            }
        }

        // Llenar tabla fila por fila (cada 30 min)
        LocalTime horaActual = HORA_INICIO;
        while (horaActual.isBefore(HORA_FIN)) {
            Object[] row = new Object[columnNames.length];
            row[0] = horaActual.format(timeFormat);
            
            for (int i = 0; i < canchas.size(); i++) {
                int numCancha = canchas.get(i).getNumero();
                Partido partido = horarioOcupado.get(numCancha).get(horaActual);
                
                if (partido != null) {
                    // Construir string informativo
                    String torneoInfo = "";
                    if (partido.getTorneoPerteneciente() != null) {
                        torneoInfo = " [" + partido.getTorneoPerteneciente().getNombre() + "]";
                    } else {
                        torneoInfo = " [Amistoso]";
                    }
                    
                    // Formato: "Local vs Visita [Torneo]"
                    String info = partido.getEquipoLocal().getNombre() + " vs " + partido.getEquipoVisitante().getNombre() + torneoInfo;
                    row[i + 1] = info;
                } else {
                    row[i + 1] = "Disponible";
                }
            }
            
            tableModel.addRow(row);
            horaActual = horaActual.plusMinutes(30); 
        }
    }
}