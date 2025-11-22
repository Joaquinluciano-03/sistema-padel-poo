package vista;

import controlador.GestorSistema;
import excepciones.EquipoYaExisteException;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import modelo.Equipo;
import modelo.Jugador;
import util.Estilo;

public class PanelGestionEquipos extends JPanel {

    private GestorSistema gestor;
    private JTextArea equiposTextArea;

    public PanelGestionEquipos(GestorSistema gestor, CardLayout cardLayout, JPanel mainPanel) {
        this.gestor = gestor;
        
        // --- APLICACIÓN DE ESTILO AL PANEL ---
        Estilo.decorarPanel(this);
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título del Panel
        this.add(Estilo.crearTitulo("Gestión de Equipos"), BorderLayout.NORTH);

        // --- ÁREA DE TEXTO DE EQUIPOS ---
        equiposTextArea = new JTextArea(15, 50);
        equiposTextArea.setEditable(false);
        equiposTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        equiposTextArea.setBackground(Estilo.BLANCO); 
        equiposTextArea.setForeground(Estilo.COLOR_TEXTO); 
        
        JScrollPane scrollPane = new JScrollPane(equiposTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL, 1));
        this.add(scrollPane, BorderLayout.CENTER);

        // --- PANEL DE BOTONES ---
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        Estilo.decorarPanel(botonesPanel);

        // BOTÓN CREAR
        JButton btnCrear = new JButton("Crear Equipo con 2 Jugadores");
        Estilo.decorarBoton(btnCrear);
        btnCrear.addActionListener(e -> crearNuevoEquipo());
        
        // BOTÓN EDITAR (Centralizado)
        JButton btnEditar = new JButton("Editar Equipo (Nombre/Jugadores)");
        Estilo.decorarBoton(btnEditar);
        btnEditar.addActionListener(e -> editarEquipo());
        
        // BOTÓN ELIMINAR
        JButton btnEliminar = new JButton("Eliminar Equipo");
        Estilo.decorarBoton(btnEliminar);
        btnEliminar.addActionListener(e -> eliminarEquipo());
        
        // BOTÓN VOLVER
        JButton btnVolver = new JButton("Volver al Menú Principal");
        Estilo.decorarBoton(btnVolver);
        btnVolver.setBackground(Estilo.COLOR_TEXTO);
        btnVolver.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        
        botonesPanel.add(btnCrear);
        botonesPanel.add(btnEditar);
        botonesPanel.add(btnEliminar);
        botonesPanel.add(btnVolver);

        this.add(botonesPanel, BorderLayout.SOUTH);
    }

    public void actualizar() {
        StringBuilder sb = new StringBuilder();
        for (Equipo e : gestor.getEquiposRegistrados()) {
            sb.append(e.toString());
            sb.append("--------------------------------\n");
        }
        equiposTextArea.setText(sb.toString());
    }

    private void crearNuevoEquipo() {
        JTextField nombreField = new JTextField();
        JTextField dni1Field = new JTextField();
        JTextField dni2Field = new JTextField();
        
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Nombre del Equipo:")); form.add(nombreField);
        form.add(new JLabel("DNI Jugador 1:")); form.add(dni1Field);
        form.add(new JLabel("DNI Jugador 2:")); form.add(dni2Field);

        int result = JOptionPane.showConfirmDialog(this, form, "Crear Equipo (2 Jugadores)", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // 1. Validar y buscar jugadores
                Jugador j1 = gestor.buscarJugadorPorDni(dni1Field.getText());
                Jugador j2 = gestor.buscarJugadorPorDni(dni2Field.getText());
                
                if (j1 == null || j2 == null) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar el DNI de 2 jugadores registrados.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (j1.equals(j2)) {
                    JOptionPane.showMessageDialog(this, "Un jugador no puede jugar contra sí mismo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                List<Jugador> jugadores = Arrays.asList(j1, j2);
                
                // 2. Registrar equipo con los 2 jugadores
                gestor.registrarEquipo(new Equipo(nombreField.getText().trim(), jugadores));
                actualizar();
                JOptionPane.showMessageDialog(this, "Equipo creado y guardado.");

            } catch (EquipoYaExisteException e) {
                JOptionPane.showMessageDialog(this, "Motivo: " + e.getMessage(), "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                 JOptionPane.showMessageDialog(this, "Error de creación: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarEquipo() {
        String nombreActual = JOptionPane.showInputDialog(this, "Nombre del equipo a editar:");
        if (nombreActual == null || nombreActual.trim().isEmpty()) return;

        Equipo equipo = gestor.buscarEquipoPorNombre(nombreActual);
        if (equipo == null) {
            JOptionPane.showMessageDialog(this, "Equipo no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Precargar datos
        JTextField nombreField = new JTextField(equipo.getNombre());
        JTextField dni1Field = new JTextField(equipo.getJugadores().size() > 0 ? equipo.getJugadores().get(0).getDni() : "");
        JTextField dni2Field = new JTextField(equipo.getJugadores().size() > 1 ? equipo.getJugadores().get(1).getDni() : "");
        
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Nuevo Nombre:")); form.add(nombreField);
        form.add(new JLabel("DNI Jugador 1:")); form.add(dni1Field);
        form.add(new JLabel("DNI Jugador 2:")); form.add(dni2Field);

        int result = JOptionPane.showConfirmDialog(this, form, "Editar Equipo: " + equipo.getNombre(), JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String nuevoNombre = nombreField.getText().trim();
                
                // 1. Validar y buscar nuevos jugadores
                Jugador j1 = gestor.buscarJugadorPorDni(dni1Field.getText());
                Jugador j2 = gestor.buscarJugadorPorDni(dni2Field.getText());
                
                if (j1 == null || j2 == null) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar el DNI de 2 jugadores registrados.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (j1.equals(j2)) {
                    JOptionPane.showMessageDialog(this, "Un jugador no puede estar dos veces en el mismo equipo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                List<Jugador> nuevosJugadores = Arrays.asList(j1, j2);

                // 2. Llamar al método centralizado de edición
                gestor.editarEquipo(nombreActual, nuevoNombre, nuevosJugadores);
                
                actualizar();
                JOptionPane.showMessageDialog(this, "Equipo actualizado exitosamente.");
            } catch (EquipoYaExisteException e) {
                JOptionPane.showMessageDialog(this, "No se pudo renombrar. " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Error de edición: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void eliminarEquipo() {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre del equipo a eliminar:");
        if (nombre != null && !nombre.trim().isEmpty()) {
            if (gestor.buscarEquipoPorNombre(nombre) == null) {
                 JOptionPane.showMessageDialog(this, "No se encontró ningún equipo con ese nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro? Se cancelarán sus partidos pendientes.", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                gestor.eliminarEquipo(nombre);
                JOptionPane.showMessageDialog(this, "Equipo eliminado.");
                actualizar();
            }
        }
    }
}