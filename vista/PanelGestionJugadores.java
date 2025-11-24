package vista;

import controlador.GestorSistema;
import excepciones.JugadorYaExisteException;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.Jugador;
import util.Estilo;

public class PanelGestionJugadores extends JPanel {

    private GestorSistema gestor;
    private JTable tablaJugadores;
    private DefaultTableModel tableModel;

    public PanelGestionJugadores(GestorSistema gestor, CardLayout cardLayout, JPanel mainPanel) {
        this.gestor = gestor;
        
        // --- APLICACIÓN DE ESTILO AL PANEL ---
        Estilo.decorarPanel(this);
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título del Panel
        this.add(Estilo.crearTitulo("Gestión de Jugadores"), BorderLayout.NORTH);

        // --- TABLA DE JUGADORES ---
        String[] columnas = {"Nombre", "Apellido", "DNI", "Posición", "Nivel", "Partidos (G/P)"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // La tabla no es editable directamente
            }
        };
        
        tablaJugadores = new JTable(tableModel);
        Estilo.decorarTabla(tablaJugadores); // Aplicar estilo personalizado
        
        JScrollPane scrollPane = new JScrollPane(tablaJugadores);
        scrollPane.getViewport().setBackground(Estilo.BLANCO);
        scrollPane.setBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL, 1));
        
        this.add(scrollPane, BorderLayout.CENTER);

        // --- PANEL DE BOTONES ---
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        Estilo.decorarPanel(botonesPanel);

        JButton btnRegistrar = new JButton("Registrar Jugador");
        Estilo.decorarBoton(btnRegistrar);
        btnRegistrar.addActionListener(e -> registrarNuevoJugador());
        
        // BOTÓN MODIFICAR (AZUL)
        JButton btnModificar = new JButton("Modificar Jugador");
        Estilo.decorarBoton(btnModificar);
        btnModificar.setBackground(Estilo.COLOR_AZUL); // Aplicamos color específico
        btnModificar.addActionListener(e -> modificarJugador());

        // BOTÓN ELIMINAR (ROJO)
        JButton btnEliminar = new JButton("Eliminar Jugador");
        Estilo.decorarBoton(btnEliminar);
        btnEliminar.setBackground(Estilo.COLOR_OCUPADO); // Aplicamos color específico
        btnEliminar.addActionListener(e -> eliminarJugador());
        
        JButton btnVolver = new JButton("Volver al Menú Principal");
        Estilo.decorarBoton(btnVolver);
        btnVolver.setBackground(Estilo.COLOR_TEXTO); 
        btnVolver.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        
        botonesPanel.add(btnRegistrar);
        botonesPanel.add(btnModificar);
        botonesPanel.add(btnEliminar);
        botonesPanel.add(btnVolver);
        
        this.add(botonesPanel, BorderLayout.SOUTH);
    }

    public void actualizar() {
        // Limpiar tabla
        tableModel.setRowCount(0);
        
        // Llenar con datos actualizados
        for (Jugador j : gestor.getJugadoresRegistrados()) {
            String stats = j.getPartidosGanados() + " / " + j.getPartidosPerdidos();
            Object[] fila = {
                j.getNombre(),
                j.getApellido(),
                j.getDni(),
                j.getPosicion(),
                j.getNivel(),
                stats
            };
            tableModel.addRow(fila);
        }
    }

    private void registrarNuevoJugador() {
        JTextField nombreField = new JTextField(10);
        JTextField apellidoField = new JTextField(10);
        JTextField dniField = new JTextField(10);
        JTextField posField = new JTextField(10);
        JTextField nivelField = new JTextField(5);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Nombre:")); formPanel.add(nombreField);
        formPanel.add(new JLabel("Apellido:")); formPanel.add(apellidoField);
        formPanel.add(new JLabel("DNI:")); formPanel.add(dniField);
        formPanel.add(new JLabel("Posición:")); formPanel.add(posField);
        formPanel.add(new JLabel("Nivel (1-10):")); formPanel.add(nivelField);

        int result = JOptionPane.showConfirmDialog(this, formPanel, "Registrar Jugador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Jugador nuevo = new Jugador(nombreField.getText(), apellidoField.getText(), dniField.getText(), posField.getText(), Integer.parseInt(nivelField.getText()));
                gestor.registrarJugador(nuevo);
                actualizar();
                JOptionPane.showMessageDialog(this, "Jugador registrado y guardado.");
            } catch (JugadorYaExisteException e) {
                JOptionPane.showMessageDialog(this, "Motivo: " + e.getMessage(), "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: El nivel debe ser un número.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modificarJugador() {
        String dni = JOptionPane.showInputDialog(this, "Ingrese el DNI del jugador a modificar:");
        if (dni == null || dni.trim().isEmpty()) return;

        Jugador j = gestor.buscarJugadorPorDni(dni);
        if (j == null) {
            JOptionPane.showMessageDialog(this, "No existe un jugador con ese DNI.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nombreField = new JTextField(j.getNombre(), 10);
        JTextField apellidoField = new JTextField(j.getApellido(), 10);
        JTextField posField = new JTextField(j.getPosicion(), 10);
        JTextField nivelField = new JTextField(String.valueOf(j.getNivel()), 5);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Nombre:")); formPanel.add(nombreField);
        formPanel.add(new JLabel("Apellido:")); formPanel.add(apellidoField);
        formPanel.add(new JLabel("Posición:")); formPanel.add(posField);
        formPanel.add(new JLabel("Nivel (1-10):")); formPanel.add(nivelField);

        int result = JOptionPane.showConfirmDialog(this, formPanel, "Modificar Jugador: " + j.getNombre(), JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int nuevoNivel = Integer.parseInt(nivelField.getText());
                boolean exito = gestor.modificarJugador(dni, nombreField.getText(), apellidoField.getText(), posField.getText(), nuevoNivel);
                
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Jugador actualizado exitosamente.");
                    actualizar();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El nivel debe ser numérico.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarJugador() {
        String dni = JOptionPane.showInputDialog(this, "Ingrese el DNI del jugador a eliminar:");
        if (dni != null && !dni.trim().isEmpty()) {
            Jugador j = gestor.buscarJugadorPorDni(dni);
            if (j == null) {
                JOptionPane.showMessageDialog(this, "No se encontró ningún jugador con ese DNI.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar a " + j.getNombre() + " " + j.getApellido() + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (gestor.eliminarJugador(dni)) {
                    JOptionPane.showMessageDialog(this, "Jugador eliminado.");
                    actualizar();
                }
            }
        }
    }
}