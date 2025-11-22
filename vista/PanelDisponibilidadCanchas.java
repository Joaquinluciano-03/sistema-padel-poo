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

public class PanelDisponibilidadCanchas extends JPanel {

    private GestorSistema gestor;
    private JComboBox<String> sedeCombo;
    private JFormattedTextField dateField;
    private JTable tablaDisponibilidad;
    private DefaultTableModel tableModel;

    private final LocalTime HORA_INICIO = LocalTime.of(8, 0);
    private final LocalTime HORA_FIN = LocalTime.of(22, 0);
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    public PanelDisponibilidadCanchas(GestorSistema gestor, CardLayout cardLayout, JPanel mainPanel) {
        this.gestor = gestor;
        
        // --- APLICACIÓN DE ESTILO AL PANEL PRINCIPAL ---
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

        JLabel lblFecha = new JLabel("Fecha (dd/MM/yyyy):");
        lblFecha.setFont(Estilo.FUENTE_NORMAL);
        controlPanel.add(lblFecha);
        
        dateField = new JFormattedTextField(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        dateField.setPreferredSize(new Dimension(120, 30));
        dateField.setFont(Estilo.FUENTE_NORMAL);
        dateField.setValue(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        controlPanel.add(dateField);
        
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

        // Implementación del Renderizador Personalizado
        tablaDisponibilidad.setDefaultRenderer(Object.class, new TableCellRenderer() {
            private final DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Centrar el texto en todas las celdas
                defaultRenderer.setHorizontalAlignment(JLabel.CENTER);

                if (column > 0) { // Solo aplicar color a las columnas de Canchas
                    String texto = value != null ? value.toString() : "";
                    
                    if (texto.startsWith("Disponible")) {
                        c.setBackground(new Color(197, 227, 197)); // Verde claro para disponible
                        c.setForeground(Estilo.COLOR_TEXTO);
                    } else if (texto.startsWith("Ocupada")) {
                        c.setBackground(new Color(255, 204, 204)); // Rojo muy suave para ocupado
                        c.setForeground(Estilo.COLOR_OCUPADO); // Texto en el rojo más oscuro
                    } else {
                        // Columna Hora
                        c.setBackground(Estilo.BLANCO);
                        c.setForeground(Estilo.COLOR_TEXTO);
                    }
                } else {
                    // Columna Hora
                    c.setBackground(new Color(230, 230, 230)); // Fondo ligeramente gris para la hora
                    c.setForeground(Estilo.COLOR_TEXTO);
                }

                if (isSelected) {
                    c.setBackground(Estilo.COLOR_SECUNDARIO); // Mantener el color de selección
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
    
    private void mostrarDisponibilidad() {
        if (sedeCombo.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una sede.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombreSede = (String) sedeCombo.getSelectedItem();
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Sede sede = gestor.buscarSedePorNombre(nombreSede);
        if (sede.getCanchas().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La sede seleccionada no tiene canchas registradas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);
            return;
        }

        // 1. Obtener datos
        List<Cancha> canchas = sede.getCanchas();
        List<Partido> partidos = gestor.getPartidosPorSedeYFecha(nombreSede, fecha);

        // 2. Preparar el modelo de la tabla
        String[] columnNames = new String[canchas.size() + 1];
        columnNames[0] = "HORA";
        for (int i = 0; i < canchas.size(); i++) {
            columnNames[i + 1] = "Cancha N°" + canchas.get(i).getNumero();
        }
        tableModel.setColumnIdentifiers(columnNames);
        tableModel.setRowCount(0); 

        // Mapeo: Cancha.numero -> Horario -> Partido (o null)
        Map<Integer, Map<LocalTime, Partido>> horarioOcupado = new HashMap<>();
        
        for (Cancha cancha : canchas) {
            horarioOcupado.put(cancha.getNumero(), new HashMap<>());
        }

        // Llenar el mapa de ocupación
        for (Partido p : partidos) {
            LocalTime hora = p.getFechaHora().toLocalTime().withMinute(0).withSecond(0).withNano(0);
            if (p.getCancha() != null && horarioOcupado.containsKey(p.getCancha().getNumero())) {
                horarioOcupado.get(p.getCancha().getNumero()).put(hora, p);
            }
        }

        // 3. Llenar la tabla por hora
        LocalTime horaActual = HORA_INICIO;
        while (horaActual.isBefore(HORA_FIN)) {
            Object[] row = new Object[columnNames.length];
            row[0] = horaActual.format(timeFormat) + " - " + horaActual.plusHours(1).format(timeFormat);
            
            for (int i = 0; i < canchas.size(); i++) {
                int numCancha = canchas.get(i).getNumero();
                Partido partido = horarioOcupado.get(numCancha).get(horaActual);
                
                if (partido != null) {
                    String infoTorneo = "";
                    if (partido.getTorneoPerteneciente() != null) { 
                        infoTorneo = " (Torneo: " + partido.getTorneoPerteneciente().getNombre() + ")";
                    }
                    row[i + 1] = "Ocupada: " + partido.getEquipoLocal().getNombre() + " vs " + partido.getEquipoVisitante().getNombre() + infoTorneo;
                } else {
                    row[i + 1] = "Disponible";
                }
            }
            
            tableModel.addRow(row);
            horaActual = horaActual.plusHours(1);
        }
        
        if (tableModel.getRowCount() > 0) {
            tablaDisponibilidad.setModel(tableModel);
        }
    }
}