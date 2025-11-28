package vista;

import controlador.GestorSistema;
import excepciones.EquipoYaExisteException;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modelo.Equipo;
import modelo.Jugador;
import util.Estilo;

public class PanelGestionEquipos extends JPanel {

    private GestorSistema gestor;
    private JTable tablaEquipos;
    private DefaultTableModel tableModel;

    public PanelGestionEquipos(GestorSistema gestor, CardLayout cardLayout, JPanel mainPanel) {
        this.gestor = gestor;
        
        Estilo.decorarPanel(this);
        this.setLayout(new BorderLayout(15, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título del Panel
        this.add(Estilo.crearTitulo("Gestión de Equipos"), BorderLayout.NORTH);

        // TABLA DE EQUIPOS
        String[] columnas = {"Nombre Equipo", "Jugador 1", "Jugador 2", "Partidos (G/P)", "Torneos G."};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaEquipos = new JTable(tableModel);
        Estilo.decorarTabla(tablaEquipos);
        
        tablaEquipos.getColumnModel().getColumn(0).setPreferredWidth(150); // Nombre equipo
        tablaEquipos.getColumnModel().getColumn(1).setPreferredWidth(150); // Jugador 1
        tablaEquipos.getColumnModel().getColumn(2).setPreferredWidth(150); // Jugador 2
        
        JScrollPane scrollPane = new JScrollPane(tablaEquipos);
        scrollPane.getViewport().setBackground(Estilo.BLANCO);
        scrollPane.setBorder(BorderFactory.createLineBorder(Estilo.COLOR_PRINCIPAL, 1));
        
        this.add(scrollPane, BorderLayout.CENTER);

        // PANEL DE BOTONES
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        Estilo.decorarPanel(botonesPanel);

        // BOTÓN CREAR (Verde por defecto)
        JButton btnCrear = new JButton("Crear Equipo");
        Estilo.decorarBoton(btnCrear);
        btnCrear.addActionListener(e -> crearNuevoEquipo());
        
        // BOTÓN EDITAR (AZUL)
        JButton btnEditar = new JButton("Editar Equipo");
        Estilo.decorarBoton(btnEditar);
        btnEditar.setBackground(Estilo.COLOR_AZUL); // Color Azul
        btnEditar.addActionListener(e -> editarEquipo());
        
        // BOTÓN ELIMINAR (ROJO)
        JButton btnEliminar = new JButton("Eliminar Equipo");
        Estilo.decorarBoton(btnEliminar);
        btnEliminar.setBackground(Estilo.COLOR_OCUPADO); // Color Rojo
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
        // Limpiar tabla
        tableModel.setRowCount(0);
        
        // Llenar con datos
        for (Equipo e : gestor.getEquiposRegistrados()) {
            String j1 = "---";
            String j2 = "---";
            
            List<Jugador> jugadores = e.getJugadores();
            if (jugadores.size() > 0 && jugadores.get(0) != null) {
                j1 = jugadores.get(0).getNombre() + " " + jugadores.get(0).getApellido();
            }
            if (jugadores.size() > 1 && jugadores.get(1) != null) {
                j2 = jugadores.get(1).getNombre() + " " + jugadores.get(1).getApellido();
            }
            
            String stats = e.getPartidosGanados() + " / " + e.getPartidosPerdidos();
            
            Object[] fila = {
                e.getNombre(),
                j1,
                j2,
                stats,
                e.getTorneosGanados()
            };
            tableModel.addRow(fila);
        }
    }

    private void crearNuevoEquipo() {
        JTextField nombreField = new JTextField();
        JTextField dni1Field = new JTextField();
        JTextField dni2Field = new JTextField();
        
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Nombre del Equipo:")); form.add(nombreField);
        form.add(new JLabel("DNI Jugador 1:")); form.add(dni1Field);
        form.add(new JLabel("DNI Jugador 2:")); form.add(dni2Field);

        int result = JOptionPane.showConfirmDialog(this, form, "Crear Equipo (2 Jugadores)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validar y buscar jugadores
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
                
                // Registrar equipo con los 2 jugadores
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
        
        // Precargar datos actuales
        String dniJ1 = "";
        String dniJ2 = "";
        if (equipo.getJugadores().size() > 0) dniJ1 = equipo.getJugadores().get(0).getDni();
        if (equipo.getJugadores().size() > 1) dniJ2 = equipo.getJugadores().get(1).getDni();

        JTextField nombreField = new JTextField(equipo.getNombre());
        JTextField dni1Field = new JTextField(dniJ1);
        JTextField dni2Field = new JTextField(dniJ2);
        
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.add(new JLabel("Nuevo Nombre:")); form.add(nombreField);
        form.add(new JLabel("DNI Jugador 1:")); form.add(dni1Field);
        form.add(new JLabel("DNI Jugador 2:")); form.add(dni2Field);

        int result = JOptionPane.showConfirmDialog(this, form, "Editar Equipo: " + equipo.getNombre(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String nuevoNombre = nombreField.getText().trim();
                
                // Validar y buscar nuevos jugadores
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

                // Llamar al método centralizado de edición
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