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

        // --- PANEL DE BOTONES ---
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        Estilo.decorarPanel(botonesPanel);
        
        // BOTONES DE ACCIÓN
        JButton btnCrearSede = new JButton("Crear Nueva Sede");
        Estilo.decorarBoton(btnCrearSede);
        btnCrearSede.addActionListener(e -> crearSede());
        
        JButton btnModSede = new JButton("Modificar Sede");
        Estilo.decorarBoton(btnModSede);
        btnModSede.addActionListener(e -> modificarSede());
        
        JButton btnAddCancha = new JButton("Agregar Cancha a Sede");
        Estilo.decorarBoton(btnAddCancha);
        btnAddCancha.addActionListener(e -> agregarCanchaASede());

        JButton btnModCancha = new JButton("Modificar Cancha");
        Estilo.decorarBoton(btnModCancha);
        btnModCancha.addActionListener(e -> modificarCancha());
        
        // BOTÓN VOLVER
        JButton btnVolver = new JButton("Volver al Menú Principal");
        Estilo.decorarBoton(btnVolver);
        btnVolver.setBackground(Estilo.COLOR_TEXTO);
        btnVolver.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        
        botonesPanel.add(btnCrearSede);
        botonesPanel.add(btnModSede);
        botonesPanel.add(btnAddCancha);
        botonesPanel.add(btnModCancha);
        botonesPanel.add(btnVolver);
        
        this.add(botonesPanel, BorderLayout.SOUTH);
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
            gestor.agregarSede(new Sede(nombreField.getText(), direccionField.getText()));
            actualizar();
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
            gestor.modificarSede(nombreActual, nombreField.getText(), direccionField.getText());
            actualizar();
            JOptionPane.showMessageDialog(this, "Sede actualizada.");
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
                Cancha cancha = new Cancha(numero, superficieField.getText(), iluminacionCheck.isSelected());
                sede.agregarCancha(cancha);
                gestor.agregarSede(sede);
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
}