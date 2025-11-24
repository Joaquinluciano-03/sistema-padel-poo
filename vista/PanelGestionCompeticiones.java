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
import javax.swing.text.DateFormatter;
import modelo.*;
import util.Estilo;

public class PanelGestionCompeticiones extends JPanel {

    private GestorSistema gestor;
    private JComboBox<String> allTorneosCombo, partidoPendienteCombo;
    
    private final LocalTime HORA_APERTURA = LocalTime.of(8, 0);
    private final LocalTime HORA_CIERRE = LocalTime.of(22, 0);

    public PanelGestionCompeticiones(GestorSistema gestor, CardLayout cardLayout, JPanel mainPanel) {
        this.gestor = gestor;
        
        // --- APLICACIÓN DE ESTILO AL PANEL PRINCIPAL ---
        Estilo.decorarPanel(this);
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título del Panel
        JLabel titulo = Estilo.crearTitulo("Gestión de Competiciones Activas");
        this.add(titulo, BorderLayout.NORTH);
        
        JPanel accionesPanel = new JPanel(new GridLayout(0, 1, 15, 15));
        Estilo.decorarPanel(accionesPanel);
        
        // --- SECCIÓN CREAR ---
        JPanel crearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        Estilo.decorarPanel(crearPanel);
        crearPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL), "Crear"));
        
        JButton btnCrearTorneo = new JButton("Crear Torneo");
        Estilo.decorarBoton(btnCrearTorneo);
        btnCrearTorneo.addActionListener(e -> crearTorneo());
        crearPanel.add(btnCrearTorneo);
        
        JButton btnCrearPartido = new JButton("Crear Partido");
        Estilo.decorarBoton(btnCrearPartido);
        btnCrearPartido.addActionListener(e -> crearPartidoSuelto());
        crearPanel.add(btnCrearPartido);
        

        // --- SECCIÓN GESTIONAR PARTIDOS PENDIENTES ---
        JPanel finalizarPartidoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        Estilo.decorarPanel(finalizarPartidoPanel);
        finalizarPartidoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL), "Gestionar Partidos Pendientes"));
        
        finalizarPartidoPanel.add(new JLabel("Partido:"));
        partidoPendienteCombo = new JComboBox<>();
        partidoPendienteCombo.setPreferredSize(new Dimension(250, 30));
        partidoPendienteCombo.setBackground(Estilo.BLANCO);
        finalizarPartidoPanel.add(partidoPendienteCombo);
        
        JButton btnFinalizar = new JButton("Registrar Resultado");
        Estilo.decorarBoton(btnFinalizar);
        btnFinalizar.addActionListener(e -> finalizarPartido());
        finalizarPartidoPanel.add(btnFinalizar);
        
        JButton btnEliminarPartido = new JButton("Eliminar Partido");
        Estilo.decorarBoton(btnEliminarPartido);
        finalizarPartidoPanel.add(btnEliminarPartido);
        btnEliminarPartido.addActionListener(e -> eliminarPartido());
        
        
        // --- SECCIÓN GESTIONAR TORNEOS ACTIVOS ---
        JPanel gestionarTorneoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        Estilo.decorarPanel(gestionarTorneoPanel);
        gestionarTorneoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL), "Gestionar Torneos Activos"));
        
        gestionarTorneoPanel.add(new JLabel("Torneo:"));
        allTorneosCombo = new JComboBox<>();
        allTorneosCombo.setPreferredSize(new Dimension(250, 30));
        allTorneosCombo.setBackground(Estilo.BLANCO);
        gestionarTorneoPanel.add(allTorneosCombo);
        
        JButton btnInscribir = new JButton("Inscribir Equipo");
        Estilo.decorarBoton(btnInscribir);
        btnInscribir.addActionListener(e -> inscribirEquipo());
        gestionarTorneoPanel.add(btnInscribir);
        
        JButton btnFinalizarTorneo = new JButton("Finalizar Torneo");
        Estilo.decorarBoton(btnFinalizarTorneo);
        btnFinalizarTorneo.addActionListener(e -> finalizarTorneo());
        gestionarTorneoPanel.add(btnFinalizarTorneo);
        
        JButton btnEliminarTorneo = new JButton("Eliminar Torneo");
        Estilo.decorarBoton(btnEliminarTorneo);
        btnEliminarTorneo.addActionListener(e -> eliminarTorneo());
        gestionarTorneoPanel.add(btnEliminarTorneo);

        accionesPanel.add(crearPanel);
        accionesPanel.add(finalizarPartidoPanel);
        accionesPanel.add(gestionarTorneoPanel);
        
        this.add(accionesPanel, BorderLayout.CENTER);
        
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
        partidoPendienteCombo.removeAllItems();
        gestor.getPartidosPendientes().forEach(p -> partidoPendienteCombo.addItem(p.getDescripcion()));

        allTorneosCombo.removeAllItems();
        gestor.getTodosLosTorneos().stream()
              .filter(t -> t.getEstado() != Torneo.EstadoTorneo.FINALIZADO)
              .forEach(t -> allTorneosCombo.addItem(t.getNombre()));
    }

    private void crearTorneo() {
        JTextField nombreField = new JTextField();
        JTextField maxEquiposField = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        
        form.add(new JLabel("Nombre del Torneo:")); form.add(nombreField);
        form.add(new JLabel("Máximo de Equipos:")); form.add(maxEquiposField);
        
        int result = JOptionPane.showConfirmDialog(this, form, "Crear Torneo", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int maxEquipos = Integer.parseInt(maxEquiposField.getText());
                Torneo torneo = new Torneo(nombreField.getText(), maxEquipos);
                
                gestor.registrarTorneo(torneo);
                
                JOptionPane.showMessageDialog(this, "Torneo '" + torneo.getNombre() + "' creado exitosamente.");
                actualizar();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El máximo de equipos debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void inscribirEquipo() {
        if (allTorneosCombo.getSelectedIndex() == -1) { 
            JOptionPane.showMessageDialog(this, "Debe seleccionar un torneo activo.", "Error", JOptionPane.ERROR_MESSAGE); 
            return; 
        }

        String nombreTorneo = (String) allTorneosCombo.getSelectedItem();
        Torneo torneo = gestor.buscarTorneoPorNombre(nombreTorneo);

        String nombreEquipo = JOptionPane.showInputDialog(this, "Nombre del equipo a inscribir en " + torneo.getNombre() + ":");
        if (nombreEquipo == null || nombreEquipo.trim().isEmpty()) return;

        Equipo equipo = gestor.buscarEquipoPorNombre(nombreEquipo);
        if (equipo == null) {
            JOptionPane.showMessageDialog(this, "Equipo no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            gestor.inscribirEquipoEnTorneo(torneo, equipo);
            JOptionPane.showMessageDialog(this, "Equipo '" + equipo.getNombre() + "' inscrito con éxito en " + torneo.getNombre() + ".");
            actualizar();
        } catch (InscripcionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Inscripción", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearPartidoSuelto() {
        if (gestor.getEquiposRegistrados().size() < 2) { 
            JOptionPane.showMessageDialog(this, "Necesita al menos 2 equipos para crear un partido."); return; 
        }
        if (gestor.getSedes().isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Necesita al menos 1 sede registrada."); return; 
        }
        if (gestor.getArbitrosRegistrados().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Necesita al menos 1 árbitro registrado."); return; 
        }

        // --- Paso 1: Equipos, Árbitro y TORNEO ---
        JComboBox<String> equipoLocalCombo = new JComboBox<>();
        JComboBox<String> equipoVisitanteCombo = new JComboBox<>();
        JComboBox<String> arbitroCombo = new JComboBox<>();
        JComboBox<String> torneoCombo = new JComboBox<>();
        
        gestor.getEquiposRegistrados().forEach(e -> {
            equipoLocalCombo.addItem(e.getNombre());
            equipoVisitanteCombo.addItem(e.getNombre());
        });
        gestor.getArbitrosRegistrados().forEach(a -> arbitroCombo.addItem(a.getNombre() + " " + a.getApellido()));
        
        torneoCombo.addItem("Partido Suelto (Ninguno)");
        gestor.getTodosLosTorneos().stream()
              .filter(t -> t.getEstado() != Torneo.EstadoTorneo.FINALIZADO)
              .forEach(t -> torneoCombo.addItem(t.getNombre()));

        // --- Paso 2: Sede y Cancha (Carga dinámica) ---
        
        JComboBox<String> sedeCombo = new JComboBox<>();
        gestor.getSedes().forEach(s -> sedeCombo.addItem(s.getNombre()));
        
        JComboBox<String> canchaCombo = new JComboBox<>();
        
        if (!gestor.getSedes().isEmpty() && gestor.getSedes().get(0).getCanchas().isEmpty()) {
             JOptionPane.showMessageDialog(this, "La sede inicial no tiene canchas. Agregue canchas primero.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }
        if (!gestor.getSedes().isEmpty()) {
             gestor.getSedes().get(0).getCanchas().forEach(c -> canchaCombo.addItem(c.toString()));
        }

        sedeCombo.addActionListener(e -> {
            Sede sede = gestor.buscarSedePorNombre((String) sedeCombo.getSelectedItem());
            canchaCombo.removeAllItems();
            if (sede != null) {
                sede.getCanchas().forEach(c -> canchaCombo.addItem(c.toString()));
            }
        });

        // --- Paso 3: Fecha y Horario Restringido ---
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
        
        // --- Formulario Completo ---
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Torneo al que Pertenece:")); form.add(torneoCombo);
        form.add(new JLabel("Equipo Local:")); form.add(equipoLocalCombo);
        form.add(new JLabel("Equipo Visitante:")); form.add(equipoVisitanteCombo);
        form.add(new JLabel("Árbitro:")); form.add(arbitroCombo);
        form.add(new JLabel("Sede:")); form.add(sedeCombo);
        form.add(new JLabel("Cancha:")); form.add(canchaCombo);
        form.add(new JLabel("Fecha (dd/MM/yyyy):")); form.add(fechaField);
        form.add(new JLabel("Hora Inicio (HH:mm):")); form.add(horaInicioCombo);
        form.add(new JLabel("Duración (min):")); form.add(duracionCombo);
        
        int result = JOptionPane.showConfirmDialog(this, form, "Crear Partido", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // 1. Obtener datos y validar equipos y torneo
                Equipo local = gestor.buscarEquipoPorNombre((String)equipoLocalCombo.getSelectedItem());
                Equipo visitante = gestor.buscarEquipoPorNombre((String)equipoVisitanteCombo.getSelectedItem());
                String nombreTorneo = (String) torneoCombo.getSelectedItem();
                
                if(local.equals(visitante)) { 
                    JOptionPane.showMessageDialog(this, "Un equipo no puede jugar contra sí mismo."); return; 
                }

                Torneo torneo = nombreTorneo.equals("Partido Suelto (Ninguno)") ? null : gestor.buscarTorneoPorNombre(nombreTorneo);
                
                // VALIDACIÓN CRUCIAL: Verificar inscripción si el partido es de torneo
                if (torneo != null) {
                    List<Equipo> inscritos = torneo.getEquiposInscritos();
                    if (!inscritos.contains(local)) {
                         JOptionPane.showMessageDialog(this, "Error: El equipo local (" + local.getNombre() + ") no está inscrito en este torneo.", "Error de Inscripción", JOptionPane.ERROR_MESSAGE); return;
                    }
                    if (!inscritos.contains(visitante)) {
                         JOptionPane.showMessageDialog(this, "Error: El equipo visitante (" + visitante.getNombre() + ") no está inscrito en este torneo.", "Error de Inscripción", JOptionPane.ERROR_MESSAGE); return;
                    }
                }
                
                // Obtener objetos Cancha, Arbitro
                Sede sedeSeleccionada = gestor.buscarSedePorNombre((String) sedeCombo.getSelectedItem());
                String canchaDescripcion = (String) canchaCombo.getSelectedItem();
                
                int start = canchaDescripcion.indexOf("N°") + 2;
                int end = canchaDescripcion.indexOf('(');
                int numeroCancha = Integer.parseInt(canchaDescripcion.substring(start, end).trim());
                Cancha cancha = sedeSeleccionada.getCanchas().stream()
                                    .filter(c -> c.getNumero() == numeroCancha).findFirst().orElse(null);

                String nombreArbitroCompleto = (String) arbitroCombo.getSelectedItem();
                Arbitro arbitro = gestor.getArbitrosRegistrados().stream()
                                    .filter(a -> (a.getNombre() + " " + a.getApellido()).equals(nombreArbitroCompleto)).findFirst().orElse(null);

                if (cancha == null || arbitro == null) {
                    JOptionPane.showMessageDialog(this, "Error al encontrar la cancha o árbitro. Revise la selección.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Parsear Fecha y Hora
                LocalDate fecha = ((Date) fechaField.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalTime horaInicio = LocalTime.parse((String) horaInicioCombo.getSelectedItem());
                LocalDateTime fechaHoraInicio = LocalDateTime.of(fecha, horaInicio);
                int duracionMinutos = (Integer) duracionCombo.getSelectedItem();

                // 3. Validación de Disponibilidad
                if (!gestor.validarDisponibilidadCancha(cancha, fechaHoraInicio, duracionMinutos)) {
                    JOptionPane.showMessageDialog(this, 
                        "Horario no disponible. La cancha está ocupada en ese rango de tiempo.", 
                        "Error de Disponibilidad", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // 4. Validación de cierre
                if (fechaHoraInicio.plusMinutes(duracionMinutos).toLocalTime().isAfter(HORA_CIERRE)) {
                    JOptionPane.showMessageDialog(this, 
                        "El partido excede el horario de cierre (" + HORA_CIERRE.format(DateTimeFormatter.ofPattern("HH:mm")) + ").", 
                        "Error de Horario", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // 5. Creación y registro
                // CORREGIDO: Pasamos la duración al constructor
                Partido partido = new Partido(local, visitante, fechaHoraInicio, duracionMinutos, cancha, arbitro, torneo);
                gestor.registrarPartidoSuelto(partido);
                actualizar();
                JOptionPane.showMessageDialog(this, "Partido creado y programado.");
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error en el formato de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al crear el partido: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void finalizarPartido() {
        if (partidoPendienteCombo.getSelectedIndex() == -1) { JOptionPane.showMessageDialog(this, "No hay partidos pendientes."); return; }
        
        Partido partido = gestor.getPartidosPendientes().get(partidoPendienteCombo.getSelectedIndex());

        JTextField resultadoField = new JTextField(10);
        JComboBox<String> ganadorCombo = new JComboBox<>(new String[]{partido.getEquipoLocal().getNombre(), partido.getEquipoVisitante().getNombre()});
        
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Resultado (ej: 6-2, 6-4):")); form.add(resultadoField);
        form.add(new JLabel("Equipo Ganador:")); form.add(ganadorCombo);

        int result = JOptionPane.showConfirmDialog(this, form, "Finalizar Partido", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String res = resultadoField.getText();
            Equipo ganador = gestor.buscarEquipoPorNombre((String) ganadorCombo.getSelectedItem());
            
            if (res.trim().isEmpty()) { JOptionPane.showMessageDialog(this, "El resultado no puede estar vacío."); return; }

            gestor.finalizarPartido(partido, res, ganador);
            JOptionPane.showMessageDialog(this, "Partido finalizado y estadísticas actualizadas.");
            actualizar();
        }
    }
    
    private void eliminarPartido() {
        if (partidoPendienteCombo.getSelectedIndex() == -1) { JOptionPane.showMessageDialog(this, "No hay partidos para eliminar."); return; }
        
        Partido partido = gestor.getPartidosPendientes().get(partidoPendienteCombo.getSelectedIndex());
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar el partido " + partido.getDescripcion() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            gestor.eliminarPartido(partido);
            JOptionPane.showMessageDialog(this, "Partido eliminado.");
            actualizar();
        }
    }
    
    private void finalizarTorneo() {
        if (allTorneosCombo.getSelectedIndex() == -1) { JOptionPane.showMessageDialog(this, "No hay torneos para finalizar."); return; }
        
        List<Torneo> torneosActivos = gestor.getTodosLosTorneos().stream()
                            .filter(t -> t.getEstado() != Torneo.EstadoTorneo.FINALIZADO)
                            .collect(Collectors.toList());
        Torneo torneo = torneosActivos.get(allTorneosCombo.getSelectedIndex());
        
        if (torneo.getEquiposInscritos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El torneo no tiene equipos inscritos para elegir un ganador.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<String> ganadorCombo = new JComboBox<>();
        torneo.getEquiposInscritos().forEach(e -> ganadorCombo.addItem(e.getNombre()));

        int result = JOptionPane.showConfirmDialog(this, ganadorCombo, "Seleccione el equipo ganador para '" + torneo.getNombre() + "'", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Equipo ganador = gestor.buscarEquipoPorNombre((String)ganadorCombo.getSelectedItem());
            gestor.finalizarTorneo(torneo, ganador);
            JOptionPane.showMessageDialog(this, "¡Torneo finalizado! Estadísticas actualizadas.");
            actualizar();
        }
    }
    
    private void eliminarTorneo() {
        if (allTorneosCombo.getSelectedIndex() == -1) { JOptionPane.showMessageDialog(this, "No hay torneos para eliminar."); return; }
        
        List<Torneo> torneosActivos = gestor.getTodosLosTorneos().stream()
                            .filter(t -> t.getEstado() != Torneo.EstadoTorneo.FINALIZADO)
                            .collect(Collectors.toList());
        Torneo torneo = torneosActivos.get(allTorneosCombo.getSelectedIndex());
                            
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar el torneo " + torneo.getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            gestor.eliminarTorneo(torneo);
            JOptionPane.showMessageDialog(this, "Torneo eliminado.");
            actualizar();
        }
    }
}