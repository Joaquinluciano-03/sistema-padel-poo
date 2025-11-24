package util;

import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.*;

/**
 * Componente de calendario personalizado para seleccionar fechas
 * sin depender de librerías externas.
 */
public class SelectorFecha extends JDialog {

    private LocalDate fechaSeleccionada;
    private YearMonth mesActual;
    private JLabel lblMesAnio;
    private JPanel panelDias;

    public SelectorFecha(Window owner) {
        super(owner, "Seleccionar Fecha", ModalityType.APPLICATION_MODAL);
        
        mesActual = YearMonth.now();
        setLayout(new BorderLayout(10, 10));
        setSize(300, 300);
        setLocationRelativeTo(owner);
        setResizable(false);

        // --- CABECERA (Navegación) ---
        JPanel panelCabecera = new JPanel(new BorderLayout());
        Estilo.decorarPanel(panelCabecera);
        panelCabecera.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnPrev = new JButton("<");
        Estilo.decorarBoton(btnPrev);
        btnPrev.addActionListener(e -> cambiarMes(-1));

        JButton btnNext = new JButton(">");
        Estilo.decorarBoton(btnNext);
        btnNext.addActionListener(e -> cambiarMes(1));

        lblMesAnio = new JLabel("", SwingConstants.CENTER);
        lblMesAnio.setFont(Estilo.FUENTE_SUBTITULO);
        lblMesAnio.setForeground(Estilo.COLOR_PRINCIPAL);

        panelCabecera.add(btnPrev, BorderLayout.WEST);
        panelCabecera.add(lblMesAnio, BorderLayout.CENTER);
        panelCabecera.add(btnNext, BorderLayout.EAST);

        add(panelCabecera, BorderLayout.NORTH);

        // --- CUERPO (Días) ---
        panelDias = new JPanel(new GridLayout(0, 7, 2, 2));
        panelDias.setBackground(Estilo.BLANCO);
        panelDias.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        add(panelDias, BorderLayout.CENTER);

        actualizarCalendario();
    }

    private void cambiarMes(int meses) {
        mesActual = mesActual.plusMonths(meses);
        actualizarCalendario();
    }

    private void actualizarCalendario() {
        panelDias.removeAll();

        // CORRECCIÓN: Usamos forLanguageTag para evitar la advertencia de "deprecated"
        Locale localeEs = Locale.forLanguageTag("es-ES");
        
        String nombreMes = mesActual.getMonth().getDisplayName(TextStyle.FULL, localeEs);
        String textoCabecera = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1) + " " + mesActual.getYear();
        lblMesAnio.setText(textoCabecera);

        // Agregar nombres de días
        String[] diasSemana = {"Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do"};
        for (String dia : diasSemana) {
            JLabel lblDia = new JLabel(dia, SwingConstants.CENTER);
            lblDia.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblDia.setForeground(Color.GRAY);
            panelDias.add(lblDia);
        }

        // Calcular espacios en blanco iniciales
        LocalDate primeroDelMes = mesActual.atDay(1);
        int diaSemanaInicio = primeroDelMes.getDayOfWeek().getValue(); // 1 (Lunes) a 7 (Domingo)
        
        // Ajustar espacios (si empieza en martes (2), necesitamos 1 espacio)
        for (int i = 1; i < diaSemanaInicio; i++) {
            panelDias.add(new JLabel(""));
        }

        // Botones de días
        int diasEnMes = mesActual.lengthOfMonth();
        for (int dia = 1; dia <= diasEnMes; dia++) {
            int diaFinal = dia;
            JButton btnDia = new JButton(String.valueOf(dia));
            btnDia.setFocusPainted(false);
            btnDia.setBackground(Estilo.BLANCO);
            btnDia.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
            btnDia.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Resaltar día actual si coincide
            if (LocalDate.now().equals(mesActual.atDay(dia))) {
                btnDia.setForeground(Estilo.COLOR_AZUL);
                btnDia.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btnDia.setBorder(BorderFactory.createLineBorder(Estilo.COLOR_AZUL));
            }

            btnDia.addActionListener(e -> {
                fechaSeleccionada = mesActual.atDay(diaFinal);
                dispose(); // Cerrar diálogo
            });

            // Efecto hover simple
            btnDia.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btnDia.setBackground(Estilo.COLOR_SECUNDARIO);
                    btnDia.setForeground(Color.WHITE);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btnDia.setBackground(Estilo.BLANCO);
                    if (LocalDate.now().equals(mesActual.atDay(diaFinal))) {
                        btnDia.setForeground(Estilo.COLOR_AZUL);
                    } else {
                        btnDia.setForeground(Color.BLACK);
                    }
                }
            });

            panelDias.add(btnDia);
        }

        panelDias.revalidate();
        panelDias.repaint();
        pack(); // Ajustar tamaño al contenido
        setSize(350, 350); // Forzar un tamaño cómodo
    }

    /**
     * Método estático para mostrar el calendario y obtener la fecha.
     */
    public static LocalDate mostrar(Component parent) {
        Window window = SwingUtilities.getWindowAncestor(parent);
        SelectorFecha dialog = new SelectorFecha(window);
        dialog.setVisible(true);
        return dialog.fechaSeleccionada;
    }
}