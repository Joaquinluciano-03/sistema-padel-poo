package vista;

import controlador.GestorSistema;
import excepciones.JugadorYaExisteException;
import java.awt.*;
import javax.swing.*;
import modelo.Arbitro;
import util.Estilo;

public class PanelGestionArbitros extends JPanel {

    private GestorSistema gestor;
    private JTextArea arbitrosTextArea;

    public PanelGestionArbitros(GestorSistema gestor, CardLayout cardLayout, JPanel mainPanel) {
        this.gestor = gestor;
        
        // --- APLICACIÓN DE ESTILO AL PANEL ---
        Estilo.decorarPanel(this);
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título del Panel
        this.add(Estilo.crearTitulo("Gestión de Árbitros"), BorderLayout.NORTH);

        // --- ÁREA DE TEXTO DE ÁRBITROS ---
        arbitrosTextArea = new JTextArea(15, 50);
        arbitrosTextArea.setEditable(false);
        arbitrosTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        arbitrosTextArea.setBackground(Estilo.BLANCO); 
        arbitrosTextArea.setForeground(Estilo.COLOR_TEXTO); 
        
        JScrollPane scrollPane = new JScrollPane(arbitrosTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL, 1));
        this.add(scrollPane, BorderLayout.CENTER);

        // --- PANEL DE BOTONES ---
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        Estilo.decorarPanel(botonesPanel);

        JButton btnRegistrar = new JButton("Registrar Árbitro");
        Estilo.decorarBoton(btnRegistrar);
        btnRegistrar.addActionListener(e -> registrarNuevoArbitro());
        
        JButton btnModificar = new JButton("Modificar Árbitro");
        Estilo.decorarBoton(btnModificar);
        btnModificar.addActionListener(e -> modificarArbitro());

        JButton btnEliminar = new JButton("Eliminar Árbitro");
        Estilo.decorarBoton(btnEliminar);
        btnEliminar.addActionListener(e -> eliminarArbitro());
        
        JButton btnVolver = new JButton("Volver al Menú Principal");
        Estilo.decorarBoton(btnVolver);
        btnVolver.setBackground(Estilo.COLOR_TEXTO); // Color secundario para "Volver"
        btnVolver.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        
        botonesPanel.add(btnRegistrar);
        botonesPanel.add(btnModificar);
        botonesPanel.add(btnEliminar);
        botonesPanel.add(btnVolver);
        
        this.add(botonesPanel, BorderLayout.SOUTH);
    }

    public void actualizar() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-25s %-12s %-15s\n", "Nombre y Apellido", "DNI", "Licencia"));
        sb.append("--------------------------------------------------------------------\n");
        for (Arbitro a : gestor.getArbitrosRegistrados()) {
            String nombreCompleto = a.getNombre() + " " + a.getApellido();
            sb.append(String.format("%-25.25s %-12s %-15s\n", nombreCompleto, a.getDni(), a.getLicencia()));
        }
        arbitrosTextArea.setText(sb.toString());
    }

    private void registrarNuevoArbitro() {
        JTextField nombreField = new JTextField(10);
        JTextField apellidoField = new JTextField(10);
        JTextField dniField = new JTextField(10);
        JTextField licenciaField = new JTextField(10);
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Nombre:")); formPanel.add(nombreField);
        formPanel.add(new JLabel("Apellido:")); formPanel.add(apellidoField);
        formPanel.add(new JLabel("DNI:")); formPanel.add(dniField);
        formPanel.add(new JLabel("Licencia:")); formPanel.add(licenciaField);

        int result = JOptionPane.showConfirmDialog(this, formPanel, "Registrar Árbitro", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validación: Evitar duplicado de DNI con jugadores (aunque JugadorServicio ya lo hace)
                if (gestor.buscarJugadorPorDni(dniField.getText()) != null) {
                    throw new JugadorYaExisteException("El DNI ya existe en el sistema.", dniField.getText());
                }
                
                Arbitro nuevo = new Arbitro(nombreField.getText(), apellidoField.getText(), dniField.getText(), licenciaField.getText());
                gestor.registrarArbitro(nuevo);
                actualizar();
                JOptionPane.showMessageDialog(this, "Árbitro registrado y guardado.");
            } catch (JugadorYaExisteException e) {
                 JOptionPane.showMessageDialog(this, "Motivo: El DNI ya está registrado como jugador o árbitro.", "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modificarArbitro() {
        String dni = JOptionPane.showInputDialog(this, "Ingrese el DNI del árbitro a modificar:");
        if (dni == null || dni.trim().isEmpty()) return;

        Arbitro a = gestor.buscarArbitroPorDni(dni);
        if (a == null) {
            JOptionPane.showMessageDialog(this, "No existe un árbitro con ese DNI.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nombreField = new JTextField(a.getNombre(), 10);
        JTextField apellidoField = new JTextField(a.getApellido(), 10);
        JTextField licenciaField = new JTextField(a.getLicencia(), 10);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Nombre:")); formPanel.add(nombreField);
        formPanel.add(new JLabel("Apellido:")); formPanel.add(apellidoField);
        formPanel.add(new JLabel("Licencia:")); formPanel.add(licenciaField);

        int result = JOptionPane.showConfirmDialog(this, formPanel, "Modificar Árbitro: " + a.getNombre(), JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            boolean exito = gestor.modificarArbitro(dni, nombreField.getText(), apellidoField.getText(), licenciaField.getText());
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Árbitro actualizado exitosamente.");
                actualizar();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarArbitro() {
        String dni = JOptionPane.showInputDialog(this, "Ingrese el DNI del árbitro a eliminar:");
        if (dni != null && !dni.trim().isEmpty()) {
            Arbitro a = gestor.buscarArbitroPorDni(dni);
            if (a == null) {
                JOptionPane.showMessageDialog(this, "No se encontró ningún árbitro con ese DNI.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar a " + a.getNombre() + " " + a.getApellido() + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (gestor.eliminarArbitro(dni)) {
                    JOptionPane.showMessageDialog(this, "Árbitro eliminado.");
                    actualizar();
                }
            }
        }
    }
}