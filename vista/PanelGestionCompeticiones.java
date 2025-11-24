package vista;

import controlador.GestorSistema;
import excepciones.InscripcionException;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;
import modelo.*;
import util.Estilo;

public class PanelGestionCompeticiones extends JPanel {

    private GestorSistema gestor;
    
    // Tablas
    private JTable tablaTorneos;
    private DefaultTableModel modeloTorneos;
    private JTable tablaPartidos;
    private DefaultTableModel modeloPartidos;
    
    // Listas auxiliares para mapear filas a objetos reales
    private List<Torneo> listaTorneosActuales;
    private List<Partido> listaPartidosActuales;

    private final LocalTime HORA_APERTURA = LocalTime.of(8, 0);
    private final LocalTime HORA_CIERRE = LocalTime.of(22, 0);

    public PanelGestionCompeticiones(GestorSistema gestor, CardLayout cardLayout, JPanel mainPanel) {
        this.gestor = gestor;
        
        Estilo.decorarPanel(this);
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        this.add(Estilo.crearTitulo("Gestión de Competiciones Activas"), BorderLayout.NORTH);

        // --- CENTRO: TABLAS ---
        JPanel centroPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        Estilo.decorarPanel(centroPanel);

        // 1. TABLA TORNEOS ACTIVOS
        JPanel panelTorneos = new JPanel(new BorderLayout(5, 5));
        Estilo.decorarPanel(panelTorneos);
        panelTorneos.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL), "Torneos Activos (Seleccione para gestionar)"));
        
        String[] colTorneos = {"Nombre", "Equipos Inscritos", "Estado"};
        modeloTorneos = new DefaultTableModel(colTorneos, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaTorneos = new JTable(modeloTorneos);
        Estilo.decorarTabla(tablaTorneos);
        panelTorneos.add(new JScrollPane(tablaTorneos), BorderLayout.CENTER);

        // 2. TABLA PARTIDOS PENDIENTES
        JPanel panelPartidos = new JPanel(new BorderLayout(5, 5));
        Estilo.decorarPanel(panelPartidos);
        panelPartidos.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL), "Partidos Pendientes (Seleccione para finalizar)"));

        String[] colPartidos = {"Enfrentamiento", "Fecha/Hora", "Sede - Cancha", "Torneo"};
        modeloPartidos = new DefaultTableModel(colPartidos, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPartidos = new JTable(modeloPartidos);
        Estilo.decorarTabla(tablaPartidos);
        panelPartidos.add(new JScrollPane(tablaPartidos), BorderLayout.CENTER);

        centroPanel.add(panelTorneos);
        centroPanel.add(panelPartidos);
        this.add(centroPanel, BorderLayout.CENTER);

        // --- DERECHA: BOTONES DE ACCIÓN ---
        JPanel lateralPanel = new JPanel(new BorderLayout(10, 10));
        Estilo.decorarPanel(lateralPanel);
        lateralPanel.setPreferredSize(new Dimension(220, 0));

        JPanel botonesPanel = new JPanel(new GridLayout(9, 1, 5, 10)); // 9 espacios
        Estilo.decorarPanel(botonesPanel);
        
        // Grupo CREAR
        JButton btnCrearTorneo = new JButton("Crear Torneo");
        Estilo.decorarBoton(btnCrearTorneo);
        btnCrearTorneo.addActionListener(e -> crearTorneo());
        
        JButton btnCrearPartido = new JButton("Crear Partido");
        Estilo.decorarBoton(btnCrearPartido);
        btnCrearPartido.addActionListener(e -> crearPartidoSueltoCompleto()); // Usamos el método completo

        // Grupo TORNEO (Actúan sobre la selección de tablaTorneos)
        JButton btnInscribir = new JButton("Inscribir Equipo");
        Estilo.decorarBoton(btnInscribir);
        btnInscribir.setBackground(Estilo.COLOR_AZUL);
        btnInscribir.addActionListener(e -> inscribirEquipo());
        
        JButton btnFinalizarTorneo = new JButton("Finalizar Torneo");
        Estilo.decorarBoton(btnFinalizarTorneo);
        btnFinalizarTorneo.setBackground(Estilo.COLOR_AZUL);
        btnFinalizarTorneo.addActionListener(e -> finalizarTorneo());

        JButton btnEliminarTorneo = new JButton("Eliminar Torneo");
        Estilo.decorarBoton(btnEliminarTorneo);
        btnEliminarTorneo.setBackground(Estilo.COLOR_OCUPADO);
        btnEliminarTorneo.addActionListener(e -> eliminarTorneo());

        // Grupo PARTIDO (Actúan sobre selección de tablaPartidos)
        JButton btnFinalizarPartido = new JButton("Registrar Resultado");
        Estilo.decorarBoton(btnFinalizarPartido);
        btnFinalizarPartido.setBackground(Estilo.COLOR_AZUL);
        btnFinalizarPartido.addActionListener(e -> finalizarPartido());

        JButton btnEliminarPartido = new JButton("Eliminar Partido");
        Estilo.decorarBoton(btnEliminarPartido);
        btnEliminarPartido.setBackground(Estilo.COLOR_OCUPADO);
        btnEliminarPartido.addActionListener(e -> eliminarPartido());

        botonesPanel.add(btnCrearTorneo);
        botonesPanel.add(btnCrearPartido);
        botonesPanel.add(new JSeparator()); // Separador visual
        botonesPanel.add(btnInscribir);
        botonesPanel.add(btnFinalizarTorneo);
        botonesPanel.add(btnEliminarTorneo);
        botonesPanel.add(new JSeparator()); // Separador visual
        botonesPanel.add(btnFinalizarPartido);
        botonesPanel.add(btnEliminarPartido);

        lateralPanel.add(botonesPanel, BorderLayout.NORTH);

        JButton btnVolver = new JButton("Volver al Menú");
        Estilo.decorarBoton(btnVolver);
        btnVolver.setBackground(Estilo.COLOR_TEXTO);
        btnVolver.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        
        lateralPanel.add(btnVolver, BorderLayout.SOUTH);

        this.add(lateralPanel, BorderLayout.EAST);
    }
    
    public void actualizar() {
        // Actualizar Torneos
        modeloTorneos.setRowCount(0);
        listaTorneosActuales = gestor.getTodosLosTorneos().stream()
                .filter(t -> t.getEstado() != Torneo.EstadoTorneo.FINALIZADO)
                .collect(Collectors.toList());
        
        for (Torneo t : listaTorneosActuales) {
            modeloTorneos.addRow(new Object[]{
                t.getNombre(),
                t.getEquiposInscritos().size() + " / " + t.getMaxEquipos(),
                t.getEstado()
            });
        }

        // Actualizar Partidos
        modeloPartidos.setRowCount(0);
        listaPartidosActuales = gestor.getPartidosPendientes();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        
        for (Partido p : listaPartidosActuales) {
            String torneoInfo = (p.getTorneoPerteneciente() != null) ? p.getTorneoPerteneciente().getNombre() : "-";
            modeloPartidos.addRow(new Object[]{
                p.getEquipoLocal().getNombre() + " vs " + p.getEquipoVisitante().getNombre(),
                p.getFechaHora().format(dtf),
                "Cancha " + p.getCancha().getNumero(),
                torneoInfo
            });
        }
    }

    // --- LÓGICA DE BOTONES ---

    private void crearTorneo() {
        JTextField nombreField = new JTextField();
        JTextField maxEquiposField = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Nombre:")); form.add(nombreField);
        form.add(new JLabel("Máx. Equipos:")); form.add(maxEquiposField);
        
        if (JOptionPane.showConfirmDialog(this, form, "Crear Torneo", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                int max = Integer.parseInt(maxEquiposField.getText());
                gestor.registrarTorneo(new Torneo(nombreField.getText(), max));
                actualizar();
                JOptionPane.showMessageDialog(this, "Torneo creado.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Datos inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void inscribirEquipo() {
        int row = tablaTorneos.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione un torneo de la lista."); return; }
        
        Torneo torneo = listaTorneosActuales.get(row);
        
        String nombreEquipo = JOptionPane.showInputDialog(this, "Nombre del equipo a inscribir en " + torneo.getNombre() + ":");
        if (nombreEquipo == null) return;

        Equipo equipo = gestor.buscarEquipoPorNombre(nombreEquipo);
        if (equipo == null) { JOptionPane.showMessageDialog(this, "Equipo no encontrado."); return; }

        try {
            gestor.inscribirEquipoEnTorneo(torneo, equipo);
            actualizar();
            JOptionPane.showMessageDialog(this, "Inscripción exitosa.");
        } catch (InscripcionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void finalizarTorneo() {
        int row = tablaTorneos.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione un torneo."); return; }
        
        Torneo torneo = listaTorneosActuales.get(row);
        if (torneo.getEquiposInscritos().isEmpty()) { JOptionPane.showMessageDialog(this, "Sin equipos."); return; }

        JComboBox<String> ganadorCombo = new JComboBox<>();
        torneo.getEquiposInscritos().forEach(e -> ganadorCombo.addItem(e.getNombre()));

        if (JOptionPane.showConfirmDialog(this, ganadorCombo, "Elegir Ganador", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Equipo ganador = gestor.buscarEquipoPorNombre((String)ganadorCombo.getSelectedItem());
            gestor.finalizarTorneo(torneo, ganador);
            actualizar();
            JOptionPane.showMessageDialog(this, "Torneo finalizado.");
        }
    }

    private void eliminarTorneo() {
        int row = tablaTorneos.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione un torneo."); return; }
        
        Torneo torneo = listaTorneosActuales.get(row);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar torneo " + torneo.getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            gestor.eliminarTorneo(torneo);
            actualizar();
        }
    }

    private void finalizarPartido() {
        int row = tablaPartidos.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione un partido pendiente."); return; }
        
        Partido partido = listaPartidosActuales.get(row);
        JTextField resultadoField = new JTextField();
        JComboBox<String> ganadorCombo = new JComboBox<>(new String[]{partido.getEquipoLocal().getNombre(), partido.getEquipoVisitante().getNombre()});
        
        JPanel form = new JPanel(new GridLayout(0, 2));
        form.add(new JLabel("Resultado:")); form.add(resultadoField);
        form.add(new JLabel("Ganador:")); form.add(ganadorCombo);

        if (JOptionPane.showConfirmDialog(this, form, "Finalizar Partido", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String res = resultadoField.getText();
            if (res.trim().isEmpty()) return;
            Equipo ganador = gestor.buscarEquipoPorNombre((String) ganadorCombo.getSelectedItem());
            gestor.finalizarPartido(partido, res, ganador);
            actualizar();
        }
    }

    private void eliminarPartido() {
        int row = tablaPartidos.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione un partido."); return; }
        
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar partido?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            gestor.eliminarPartido(listaPartidosActuales.get(row));
            actualizar();
        }
    }
    
    // --- MÉTODO COMPLETO DE CREACIÓN DE PARTIDO (CON VALIDACIÓN) ---
    private void crearPartidoSueltoCompleto() {
        if (gestor.getEquiposRegistrados().size() < 2 || gestor.getSedes().isEmpty() || gestor.getArbitrosRegistrados().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Faltan datos (equipos/sedes/arbitros)."); return;
        }

        // Componentes del formulario
        JComboBox<String> localCombo = new JComboBox<>();
        JComboBox<String> visitCombo = new JComboBox<>();
        JComboBox<String> arbitroCombo = new JComboBox<>();
        JComboBox<String> torneoCombo = new JComboBox<>();
        JComboBox<String> sedeCombo = new JComboBox<>();
        JComboBox<String> canchaCombo = new JComboBox<>();
        
        // Llenar combos
        gestor.getEquiposRegistrados().forEach(e -> {
            localCombo.addItem(e.getNombre());
            visitCombo.addItem(e.getNombre());
        });
        gestor.getArbitrosRegistrados().forEach(a -> arbitroCombo.addItem(a.getNombre() + " " + a.getApellido()));
        
        torneoCombo.addItem("Partido Suelto (Ninguno)");
        gestor.getTodosLosTorneos().stream()
              .filter(t -> t.getEstado() != Torneo.EstadoTorneo.FINALIZADO)
              .forEach(t -> torneoCombo.addItem(t.getNombre()));
        
        gestor.getSedes().forEach(s -> sedeCombo.addItem(s.getNombre()));
        
        // Actualizar canchas al cambiar sede
        sedeCombo.addActionListener(e -> {
            Sede s = gestor.buscarSedePorNombre((String)sedeCombo.getSelectedItem());
            canchaCombo.removeAllItems();
            if (s != null) s.getCanchas().forEach(c -> canchaCombo.addItem(c.toString()));
        });
        // Cargar canchas iniciales
        if (sedeCombo.getItemCount() > 0) {
            Sede s = gestor.buscarSedePorNombre((String)sedeCombo.getSelectedItem());
            if (s != null) s.getCanchas().forEach(c -> canchaCombo.addItem(c.toString()));
        }

        // Fecha y Hora
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DateFormatter dateFormat = new DateFormatter(sdf);
        JFormattedTextField fechaField = new JFormattedTextField(dateFormat);
        fechaField.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        JComboBox<String> horaInicioCombo = new JComboBox<>();
        LocalTime hora = HORA_APERTURA;
        while (hora.isBefore(HORA_CIERRE)) {
            horaInicioCombo.addItem(hora.format(DateTimeFormatter.ofPattern("HH:mm")));
            hora = hora.plusMinutes(30);
        }
        
        JComboBox<Integer> duracionCombo = new JComboBox<>();
        duracionCombo.addItem(60); 
        duracionCombo.addItem(90);
        duracionCombo.addItem(120);

        // Panel del formulario
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Torneo:")); form.add(torneoCombo);
        form.add(new JLabel("Local:")); form.add(localCombo);
        form.add(new JLabel("Visitante:")); form.add(visitCombo);
        form.add(new JLabel("Árbitro:")); form.add(arbitroCombo);
        form.add(new JLabel("Sede:")); form.add(sedeCombo);
        form.add(new JLabel("Cancha:")); form.add(canchaCombo);
        form.add(new JLabel("Fecha:")); form.add(fechaField);
        form.add(new JLabel("Hora:")); form.add(horaInicioCombo);
        form.add(new JLabel("Duración:")); form.add(duracionCombo);
        
        int result = JOptionPane.showConfirmDialog(this, form, "Crear Partido", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Obtener y validar datos
                Equipo local = gestor.buscarEquipoPorNombre((String)localCombo.getSelectedItem());
                Equipo visit = gestor.buscarEquipoPorNombre((String)visitCombo.getSelectedItem());
                
                if (local.equals(visit)) { JOptionPane.showMessageDialog(this, "Equipos iguales."); return; }
                
                // Validar inscripción si es torneo
                String nomTorneo = (String) torneoCombo.getSelectedItem();
                Torneo torneo = nomTorneo.equals("Partido Suelto (Ninguno)") ? null : gestor.buscarTorneoPorNombre(nomTorneo);
                
                if (torneo != null) {
                    List<Equipo> inscritos = torneo.getEquiposInscritos();
                    if (!inscritos.contains(local) || !inscritos.contains(visit)) {
                        JOptionPane.showMessageDialog(this, "Equipos no inscritos en el torneo."); return;
                    }
                }
                
                // Obtener cancha y arbitro
                String descCancha = (String) canchaCombo.getSelectedItem();
                if (descCancha == null) { JOptionPane.showMessageDialog(this, "Seleccione una cancha."); return; }
                int numCancha = Integer.parseInt(descCancha.substring(descCancha.indexOf("N°") + 2, descCancha.indexOf('(')).trim());
                Sede sedeSel = gestor.buscarSedePorNombre((String)sedeCombo.getSelectedItem());
                Cancha cancha = sedeSel.getCanchas().stream().filter(c -> c.getNumero() == numCancha).findFirst().orElse(null);
                
                String nomArbitro = (String) arbitroCombo.getSelectedItem();
                Arbitro arbitro = gestor.getArbitrosRegistrados().stream().filter(a -> (a.getNombre() + " " + a.getApellido()).equals(nomArbitro)).findFirst().orElse(null);

                // Parsear fecha y validar
                LocalDate fecha = ((Date) fechaField.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalTime horaInicio = LocalTime.parse((String) horaInicioCombo.getSelectedItem());
                LocalDateTime fechaHora = LocalDateTime.of(fecha, horaInicio);
                int duracion = (Integer) duracionCombo.getSelectedItem();
                
                if (!gestor.validarDisponibilidadCancha(cancha, fechaHora, duracion)) {
                    JOptionPane.showMessageDialog(this, "Horario no disponible.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Crear partido
                Partido p = new Partido(local, visit, fechaHora, duracion, cancha, arbitro, torneo);
                gestor.registrarPartidoSuelto(p);
                actualizar();
                JOptionPane.showMessageDialog(this, "Partido creado.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}