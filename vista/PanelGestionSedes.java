package vista;

import controlador.GestorSistema;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.Cancha;
import modelo.Sede;
import util.Estilo;

public class PanelGestionSedes extends JPanel {
    
    private GestorSistema gestor;
    private JTable tablaSedes;
    private JTable tablaCanchas;
    private DefaultTableModel modeloSedes;
    private DefaultTableModel modeloCanchas;
    
    // Mantenemos referencia a la sede seleccionada actualmente
    private Sede sedeSeleccionadaActual = null;

    public PanelGestionSedes(GestorSistema gestor, CardLayout cardLayout, JPanel mainPanel) {
        this.gestor = gestor;
        
        Estilo.decorarPanel(this);
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        this.add(Estilo.crearTitulo("Gestión de Sedes y Canchas"), BorderLayout.NORTH);

        // --- PANEL CENTRAL (TABLAS MAESTRO-DETALLE) ---
        JPanel centroPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        Estilo.decorarPanel(centroPanel);

        // 1. TABLA SEDES
        JPanel panelSedes = new JPanel(new BorderLayout(5, 5));
        Estilo.decorarPanel(panelSedes);
        panelSedes.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL), "1. Listado de Sedes (Seleccione una)"));
        
        String[] colSedes = {"Nombre", "Dirección", "Cant. Canchas"};
        modeloSedes = new DefaultTableModel(colSedes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaSedes = new JTable(modeloSedes);
        Estilo.decorarTabla(tablaSedes);
        
        // Listener para actualizar canchas al hacer clic
        tablaSedes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarTablaCanchas();
        });

        JScrollPane scrollSedes = new JScrollPane(tablaSedes);
        scrollSedes.getViewport().setBackground(Estilo.BLANCO);
        panelSedes.add(scrollSedes, BorderLayout.CENTER);

        // 2. TABLA CANCHAS
        JPanel panelCanchas = new JPanel(new BorderLayout(5, 5));
        Estilo.decorarPanel(panelCanchas);
        panelCanchas.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL), "2. Canchas de la Sede"));

        String[] colCanchas = {"Número", "Superficie", "Iluminación"};
        modeloCanchas = new DefaultTableModel(colCanchas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCanchas = new JTable(modeloCanchas);
        Estilo.decorarTabla(tablaCanchas);
        
        JScrollPane scrollCanchas = new JScrollPane(tablaCanchas);
        scrollCanchas.getViewport().setBackground(Estilo.BLANCO);
        panelCanchas.add(scrollCanchas, BorderLayout.CENTER);

        centroPanel.add(panelSedes);
        centroPanel.add(panelCanchas);
        this.add(centroPanel, BorderLayout.CENTER);


        // --- PANEL LATERAL DE BOTONES ---
        JPanel botonesPanel = new JPanel(new GridLayout(2, 1, 10, 20)); 
        Estilo.decorarPanel(botonesPanel);
        botonesPanel.setPreferredSize(new Dimension(200, 0));

        // Acciones Sede
        JPanel accionesSede = new JPanel(new GridLayout(4, 1, 5, 10)); 
        Estilo.decorarPanel(accionesSede);
        accionesSede.setBorder(BorderFactory.createTitledBorder("Acciones Sede"));
        
        JButton btnCrearSede = new JButton("Crear Sede");
        Estilo.decorarBoton(btnCrearSede);
        btnCrearSede.addActionListener(e -> crearSede());
        
        JButton btnModSede = new JButton("Modificar Sede");
        Estilo.decorarBoton(btnModSede);
        btnModSede.setBackground(Estilo.COLOR_AZUL); // AZUL
        btnModSede.addActionListener(e -> modificarSede());
        
        JButton btnElimSede = new JButton("Eliminar Sede");
        Estilo.decorarBoton(btnElimSede);
        btnElimSede.setBackground(Estilo.COLOR_OCUPADO); // ROJO
        btnElimSede.addActionListener(e -> eliminarSede());

        accionesSede.add(btnCrearSede);
        accionesSede.add(btnModSede);
        accionesSede.add(btnElimSede);

        // Acciones Cancha
        JPanel accionesCancha = new JPanel(new GridLayout(4, 1, 5, 10));
        Estilo.decorarPanel(accionesCancha);
        accionesCancha.setBorder(BorderFactory.createTitledBorder("Acciones Cancha"));

        JButton btnAddCancha = new JButton("Agregar Cancha");
        Estilo.decorarBoton(btnAddCancha);
        btnAddCancha.addActionListener(e -> agregarCancha());

        JButton btnModCancha = new JButton("Modificar Cancha");
        Estilo.decorarBoton(btnModCancha);
        btnModCancha.setBackground(Estilo.COLOR_AZUL); // AZUL
        btnModCancha.addActionListener(e -> modificarCancha());
        
        JButton btnElimCancha = new JButton("Eliminar Cancha");
        Estilo.decorarBoton(btnElimCancha);
        btnElimCancha.setBackground(Estilo.COLOR_OCUPADO); // ROJO
        btnElimCancha.addActionListener(e -> eliminarCancha());
        
        accionesCancha.add(btnAddCancha);
        accionesCancha.add(btnModCancha);
        accionesCancha.add(btnElimCancha);

        botonesPanel.add(accionesSede);
        botonesPanel.add(accionesCancha);
        
        JPanel lateralContainer = new JPanel(new BorderLayout(10, 10));
        Estilo.decorarPanel(lateralContainer);
        lateralContainer.add(botonesPanel, BorderLayout.CENTER);
        
        JButton btnVolver = new JButton("Volver al Menú");
        Estilo.decorarBoton(btnVolver);
        btnVolver.setBackground(Estilo.COLOR_TEXTO);
        btnVolver.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        
        lateralContainer.add(btnVolver, BorderLayout.SOUTH);
        
        this.add(lateralContainer, BorderLayout.EAST);
    }

    public void actualizar() {
        int selectedRow = tablaSedes.getSelectedRow();
        String nombreSeleccionado = (selectedRow >= 0) ? (String) modeloSedes.getValueAt(selectedRow, 0) : null;

        modeloSedes.setRowCount(0);
        for (Sede s : gestor.getSedes()) {
            Object[] fila = { s.getNombre(), s.getDireccion(), s.getCanchas().size() };
            modeloSedes.addRow(fila);
        }

        // Restaurar selección
        if (nombreSeleccionado != null) {
            for (int i = 0; i < modeloSedes.getRowCount(); i++) {
                if (modeloSedes.getValueAt(i, 0).equals(nombreSeleccionado)) {
                    tablaSedes.setRowSelectionInterval(i, i);
                    break;
                }
            }
        } else {
            modeloCanchas.setRowCount(0);
            sedeSeleccionadaActual = null;
        }
    }
    
    private void actualizarTablaCanchas() {
        int row = tablaSedes.getSelectedRow();
        if (row == -1) {
            modeloCanchas.setRowCount(0);
            sedeSeleccionadaActual = null;
            return;
        }

        String nombreSede = (String) modeloSedes.getValueAt(row, 0);
        sedeSeleccionadaActual = gestor.buscarSedePorNombre(nombreSede);
        
        modeloCanchas.setRowCount(0);
        if (sedeSeleccionadaActual != null) {
            for (Cancha c : sedeSeleccionadaActual.getCanchas()) {
                Object[] fila = { c.getNumero(), c.getTipoSuperficie(), c.tieneIluminacion() ? "Sí" : "No" };
                modeloCanchas.addRow(fila);
            }
        }
    }

    // --- MÉTODOS DE ACCIÓN ---
    
    private void crearSede() {
        JTextField nombreField = new JTextField();
        JTextField direccionField = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Nombre Sede:")); form.add(nombreField);
        form.add(new JLabel("Dirección:")); form.add(direccionField);

        int result = JOptionPane.showConfirmDialog(this, form, "Crear Sede", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String nombre = nombreField.getText().trim();
                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío."); return;
                }
                gestor.agregarSede(new Sede(nombre, direccionField.getText()));
                actualizar();
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error: Duplicado", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modificarSede() {
        if (sedeSeleccionadaActual == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una sede de la tabla."); return;
        }
        String nombreActual = sedeSeleccionadaActual.getNombre();
        JTextField nombreField = new JTextField(sedeSeleccionadaActual.getNombre());
        JTextField direccionField = new JTextField(sedeSeleccionadaActual.getDireccion());
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Nuevo Nombre:")); form.add(nombreField);
        form.add(new JLabel("Nueva Dirección:")); form.add(direccionField);

        if (JOptionPane.showConfirmDialog(this, form, "Modificar Sede", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            boolean exito = gestor.modificarSede(nombreActual, nombreField.getText(), direccionField.getText());
            if (exito) actualizar();
            else JOptionPane.showMessageDialog(this, "Error al modificar (¿Nombre duplicado?).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarSede() {
        if (sedeSeleccionadaActual == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una sede de la tabla."); return;
        }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar '" + sedeSeleccionadaActual.getNombre() + "' y sus canchas?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            gestor.eliminarSede(sedeSeleccionadaActual.getNombre());
            actualizar();
        }
    }

    private void agregarCancha() {
        if (sedeSeleccionadaActual == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una sede primero."); return;
        }
        JTextField numeroField = new JTextField();
        JTextField superficieField = new JTextField();
        JCheckBox iluminacionCheck = new JCheckBox("Tiene Iluminación");
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Número:")); form.add(numeroField);
        form.add(new JLabel("Superficie:")); form.add(superficieField);
        form.add(new JLabel("")); form.add(iluminacionCheck);

        if (JOptionPane.showConfirmDialog(this, form, "Agregar Cancha", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                int numero = Integer.parseInt(numeroField.getText());
                if (sedeSeleccionadaActual.getCanchas().stream().anyMatch(c -> c.getNumero() == numero)) {
                    JOptionPane.showMessageDialog(this, "La cancha N°" + numero + " ya existe.", "Error", JOptionPane.ERROR_MESSAGE); return;
                }
                Cancha cancha = new Cancha(numero, superficieField.getText(), iluminacionCheck.isSelected());
                sedeSeleccionadaActual.agregarCancha(cancha);
                gestor.agregarSede(sedeSeleccionadaActual); 
                actualizarTablaCanchas();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El número debe ser numérico.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modificarCancha() {
        if (sedeSeleccionadaActual == null) return;
        int row = tablaCanchas.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione una cancha."); return; }
        
        int numCancha = (Integer) modeloCanchas.getValueAt(row, 0);
        Cancha cancha = sedeSeleccionadaActual.getCanchas().stream().filter(c -> c.getNumero() == numCancha).findFirst().orElse(null);
        if (cancha == null) return;

        JTextField superficieField = new JTextField(cancha.getTipoSuperficie());
        JCheckBox iluminacionCheck = new JCheckBox("Tiene Iluminación", cancha.tieneIluminacion());
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Superficie:")); form.add(superficieField);
        form.add(iluminacionCheck);

        if (JOptionPane.showConfirmDialog(this, form, "Modificar Cancha", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            gestor.modificarCancha(sedeSeleccionadaActual.getNombre(), numCancha, superficieField.getText(), iluminacionCheck.isSelected());
            actualizarTablaCanchas();
        }
    }
    
    private void eliminarCancha() {
        if (sedeSeleccionadaActual == null) return;
        int row = tablaCanchas.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seleccione una cancha."); return; }
        
        int numCancha = (Integer) modeloCanchas.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar cancha N°" + numCancha + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            gestor.eliminarCancha(sedeSeleccionadaActual, numCancha);
            actualizarTablaCanchas();
        }
    }
}