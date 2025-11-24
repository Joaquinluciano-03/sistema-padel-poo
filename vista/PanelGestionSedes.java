package vista;

import controlador.GestorSistema;
import java.awt.*;
import javax.swing.*;
import modelo.Cancha;
import modelo.Sede;
import util.Estilo;

public class PanelGestionSedes extends JPanel {
    private GestorSistema gestor;
    private JTextArea sedesTextArea;

    public PanelGestionSedes(GestorSistema gestor, CardLayout cardLayout, JPanel mainPanel) {
        this.gestor = gestor;
        
        // --- APLICACIÓN DE ESTILO AL PANEL ---
        Estilo.decorarPanel(this);
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título del Panel
        this.add(Estilo.crearTitulo("Gestión de Sedes y Canchas"), BorderLayout.NORTH);

        // --- ÁREA DE TEXTO DE SEDES ---
        sedesTextArea = new JTextArea(15, 50);
        sedesTextArea.setEditable(false);
        sedesTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        sedesTextArea.setBackground(Estilo.BLANCO);
        sedesTextArea.setForeground(Estilo.COLOR_TEXTO); 

        JScrollPane scrollPane = new JScrollPane(sedesTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL, 1));
        this.add(scrollPane, BorderLayout.CENTER);

        // --- PANEL DE ACCIONES (GRID) ---
        // Usamos un panel contenedor para los botones de acción y el de volver
        JPanel surContainer = new JPanel(new BorderLayout(10, 10));
        Estilo.decorarPanel(surContainer);

        // Panel para los botones de gestión (Sedes y Canchas)
        JPanel accionesPanel = new JPanel(new GridLayout(2, 1, 5, 5)); // 2 filas para separar Sedes de Canchas
        Estilo.decorarPanel(accionesPanel);
        
        // Fila 1: Gestión de Sedes
        JPanel filaSedes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        Estilo.decorarPanel(filaSedes);
        filaSedes.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL), "Sedes"));
        
        JButton btnCrearSede = new JButton("Crear Sede");
        Estilo.decorarBoton(btnCrearSede);
        btnCrearSede.addActionListener(e -> crearSede());
        
        JButton btnModSede = new JButton("Modificar Sede");
        Estilo.decorarBoton(btnModSede);
        btnModSede.addActionListener(e -> modificarSede());
        
        JButton btnElimSede = new JButton("Eliminar Sede");
        Estilo.decorarBoton(btnElimSede);
        btnElimSede.setBackground(Estilo.COLOR_OCUPADO); // Rojo
        btnElimSede.addActionListener(e -> eliminarSede());
        
        filaSedes.add(btnCrearSede);
        filaSedes.add(btnModSede);
        filaSedes.add(btnElimSede); // ¡Agregado!
        
        // Fila 2: Gestión de Canchas
        JPanel filaCanchas = new JPanel(new FlowLayout(FlowLayout.CENTER));
        Estilo.decorarPanel(filaCanchas);
        filaCanchas.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL), "Canchas"));
        
        JButton btnAddCancha = new JButton("Agregar Cancha");
        Estilo.decorarBoton(btnAddCancha);
        btnAddCancha.addActionListener(e -> agregarCanchaASede());

        JButton btnModCancha = new JButton("Modificar Cancha");
        Estilo.decorarBoton(btnModCancha);
        btnModCancha.addActionListener(e -> modificarCancha());
        
        JButton btnElimCancha = new JButton("Eliminar Cancha");
        Estilo.decorarBoton(btnElimCancha);
        btnElimCancha.setBackground(Estilo.COLOR_OCUPADO); // Rojo
        btnElimCancha.addActionListener(e -> eliminarCancha());
        
        filaCanchas.add(btnAddCancha);
        filaCanchas.add(btnModCancha);
        filaCanchas.add(btnElimCancha); // ¡Agregado!

        accionesPanel.add(filaSedes);
        accionesPanel.add(filaCanchas);
        
        surContainer.add(accionesPanel, BorderLayout.CENTER);

        // --- BOTÓN VOLVER (SEPARADO AL FONDO) ---
        JPanel volverPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Estilo.decorarPanel(volverPanel);
        
        JButton btnVolver = new JButton("Volver al Menú");
        Estilo.decorarBoton(btnVolver);
        btnVolver.setBackground(Estilo.COLOR_TEXTO);
        btnVolver.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        
        volverPanel.add(btnVolver);
        surContainer.add(volverPanel, BorderLayout.SOUTH);
        
        this.add(surContainer, BorderLayout.SOUTH);
    }

    public void actualizar() {
        StringBuilder sb = new StringBuilder();
        for (Sede sede : gestor.getSedes()) {
            sb.append("SEDE: ").append(sede.getNombre()).append(" (").append(sede.getDireccion()).append(")\n");
            if (sede.getCanchas().isEmpty()) {
                sb.append("  (Sin canchas registradas)\n");
            } else {
                for (Cancha cancha : sede.getCanchas()) {
                    sb.append("  - ").append(cancha.toString()).append("\n");
                }
            }
            sb.append("\n");
        }
        sedesTextArea.setText(sb.toString());
    }

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
                    JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                gestor.agregarSede(new Sede(nombre, direccionField.getText()));
                actualizar();
                JOptionPane.showMessageDialog(this, "Sede creada exitosamente.");
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error: Duplicado", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modificarSede() {
        String nombreActual = JOptionPane.showInputDialog(this, "Ingrese el nombre de la sede a modificar:");
        if (nombreActual == null || nombreActual.trim().isEmpty()) return;

        Sede sede = gestor.buscarSedePorNombre(nombreActual);
        if (sede == null) {
            JOptionPane.showMessageDialog(this, "Sede no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nombreField = new JTextField(sede.getNombre());
        JTextField direccionField = new JTextField(sede.getDireccion());
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Nuevo Nombre:")); form.add(nombreField);
        form.add(new JLabel("Nueva Dirección:")); form.add(direccionField);

        int result = JOptionPane.showConfirmDialog(this, form, "Modificar Sede", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            boolean exito = gestor.modificarSede(nombreActual, nombreField.getText(), direccionField.getText());
            if (exito) {
                actualizar();
                JOptionPane.showMessageDialog(this, "Sede actualizada.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar (posible nombre duplicado).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void eliminarSede() {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la sede a eliminar:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        
        Sede sede = gestor.buscarSedePorNombre(nombre);
        if (sede == null) {
            JOptionPane.showMessageDialog(this, "Sede no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar la sede '" + nombre + "'?\nSe perderán todas sus canchas.", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (gestor.eliminarSede(nombre)) {
                JOptionPane.showMessageDialog(this, "Sede eliminada.");
                actualizar();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar la sede.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void agregarCanchaASede() {
        if (gestor.getSedes().isEmpty()) { JOptionPane.showMessageDialog(this, "Primero debe crear una sede."); return; }
        
        JComboBox<String> sedeCombo = new JComboBox<>();
        gestor.getSedes().forEach(s -> sedeCombo.addItem(s.getNombre()));
        
        JTextField numeroField = new JTextField();
        JTextField superficieField = new JTextField();
        JCheckBox iluminacionCheck = new JCheckBox("Tiene Iluminación");
        
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Sede:")); form.add(sedeCombo);
        form.add(new JLabel("Número de Cancha:")); form.add(numeroField);
        form.add(new JLabel("Tipo de Superficie:")); form.add(superficieField);
        form.add(iluminacionCheck);

        int result = JOptionPane.showConfirmDialog(this, form, "Agregar Cancha", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Sede sede = gestor.buscarSedePorNombre((String)sedeCombo.getSelectedItem());
                int numero = Integer.parseInt(numeroField.getText());
                
                // Validación simple de número de cancha único en la sede
                if (sede.getCanchas().stream().anyMatch(c -> c.getNumero() == numero)) {
                    JOptionPane.showMessageDialog(this, "Ya existe la cancha N°" + numero + " en esta sede.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Cancha cancha = new Cancha(numero, superficieField.getText(), iluminacionCheck.isSelected());
                sede.agregarCancha(cancha);
                gestor.agregarSede(sede); // Guarda cambios
                actualizar();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El número de cancha debe ser un valor numérico.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modificarCancha() {
        if (gestor.getSedes().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay sedes creadas.");
            return;
        }

        JComboBox<String> sedeCombo = new JComboBox<>();
        gestor.getSedes().forEach(s -> sedeCombo.addItem(s.getNombre()));
        
        JPanel panelSede = new JPanel(new GridLayout(0, 1));
        panelSede.add(new JLabel("Seleccione la Sede donde está la cancha:"));
        panelSede.add(sedeCombo);
        
        int resultSede = JOptionPane.showConfirmDialog(this, panelSede, "Seleccionar Sede", JOptionPane.OK_CANCEL_OPTION);
        if (resultSede != JOptionPane.OK_OPTION) return;

        Sede sede = gestor.buscarSedePorNombre((String)sedeCombo.getSelectedItem());
        if (sede.getCanchas().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Esta sede no tiene canchas.");
            return;
        }

        JComboBox<Integer> canchaCombo = new JComboBox<>();
        sede.getCanchas().forEach(c -> canchaCombo.addItem(c.getNumero()));
        
        JPanel panelCancha = new JPanel(new GridLayout(0, 1));
        panelCancha.add(new JLabel("Seleccione el N° de Cancha a modificar:"));
        panelCancha.add(canchaCombo);

        int resultCancha = JOptionPane.showConfirmDialog(this, panelCancha, "Seleccionar Cancha", JOptionPane.OK_CANCEL_OPTION);
        if (resultCancha != JOptionPane.OK_OPTION) return;

        int numCancha = (Integer) canchaCombo.getSelectedItem();
        Cancha cancha = sede.getCanchas().stream().filter(c -> c.getNumero() == numCancha).findFirst().orElse(null);
        
        JTextField superficieField = new JTextField(cancha.getTipoSuperficie());
        JCheckBox iluminacionCheck = new JCheckBox("Tiene Iluminación", cancha.tieneIluminacion());
        
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Superficie:")); form.add(superficieField);
        form.add(iluminacionCheck);

        int resultFinal = JOptionPane.showConfirmDialog(this, form, "Modificar Cancha N°" + numCancha, JOptionPane.OK_CANCEL_OPTION);
        if (resultFinal == JOptionPane.OK_OPTION) {
            gestor.modificarCancha(sede.getNombre(), numCancha, superficieField.getText(), iluminacionCheck.isSelected());
            actualizar();
            JOptionPane.showMessageDialog(this, "Cancha modificada correctamente.");
        }
    }
    
    private void eliminarCancha() {
        if (gestor.getSedes().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay sedes creadas.");
            return;
        }

        // 1. Seleccionar Sede
        JComboBox<String> sedeCombo = new JComboBox<>();
        gestor.getSedes().forEach(s -> sedeCombo.addItem(s.getNombre()));
        
        int resultSede = JOptionPane.showConfirmDialog(this, sedeCombo, "Seleccionar Sede", JOptionPane.OK_CANCEL_OPTION);
        if (resultSede != JOptionPane.OK_OPTION) return;

        Sede sede = gestor.buscarSedePorNombre((String)sedeCombo.getSelectedItem());
        if (sede.getCanchas().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Esta sede no tiene canchas.");
            return;
        }

        // 2. Seleccionar Cancha
        JComboBox<Integer> canchaCombo = new JComboBox<>();
        sede.getCanchas().forEach(c -> canchaCombo.addItem(c.getNumero()));
        
        int resultCancha = JOptionPane.showConfirmDialog(this, canchaCombo, "Seleccionar N° Cancha a Eliminar", JOptionPane.OK_CANCEL_OPTION);
        if (resultCancha != JOptionPane.OK_OPTION) return;

        int numCancha = (Integer) canchaCombo.getSelectedItem();
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar la cancha N°" + numCancha + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (gestor.eliminarCancha(sede, numCancha)) {
                JOptionPane.showMessageDialog(this, "Cancha eliminada.");
                actualizar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la cancha.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}