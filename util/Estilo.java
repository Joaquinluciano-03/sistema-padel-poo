package util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class Estilo {

    // --- PALETA DE COLORES REFINADA ---
    // Verde profesional/apagado (para el diseño principal)
    public static final Color COLOR_PRINCIPAL = new Color(51, 102, 51); // Verde más oscuro y menos saturado
    // Verde claro para hover/disponible (menos chillón)
    public static final Color COLOR_SECUNDARIO = new Color(102, 153, 102); 
    // Rojo para celdas ocupadas (para contrastar con el verde)
    public static final Color COLOR_OCUPADO = new Color(204, 71, 71); // Rojo suave
    
    // Color de fondo para paneles (blanco humo)
    public static final Color COLOR_FONDO = new Color(245, 245, 245);
    // Color de texto oscuro
    public static final Color COLOR_TEXTO = new Color(33, 33, 33);
    // Blanco puro
    public static final Color BLANCO = Color.WHITE;

    // --- FUENTES ---
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);

    /**
     * Aplica el estilo base a un panel.
     */
    public static void decorarPanel(JPanel panel) {
        panel.setBackground(COLOR_FONDO);
    }

    /**
     * Transforma un botón estándar en uno moderno y plano.
     */
    public static void decorarBoton(JButton boton) {
        boton.setFont(FUENTE_NORMAL);
        boton.setBackground(COLOR_PRINCIPAL);
        boton.setForeground(BLANCO);
        boton.setFocusPainted(false); 
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        // Efecto Hover simple
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(COLOR_SECUNDARIO);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Revertir a COLOR_PRINCIPAL si no es el botón "Volver" o similar
                if (boton.getBackground().equals(COLOR_SECUNDARIO)) {
                     boton.setBackground(COLOR_PRINCIPAL);
                }
            }
        });
    }

    /**
     * Estiliza una JTable para que se vea moderna.
     */
    public static void decorarTabla(JTable tabla) {
        // Estilo general
        tabla.setFont(FUENTE_NORMAL);
        tabla.setRowHeight(30); 
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(COLOR_SECUNDARIO);
        tabla.setSelectionForeground(BLANCO);

        // Estilo del Encabezado (Header)
        JTableHeader header = tabla.getTableHeader();
        header.setFont(FUENTE_SUBTITULO);
        header.setBackground(COLOR_PRINCIPAL);
        header.setForeground(BLANCO);
        header.setOpaque(true);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Centrar el contenido de las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        // Desactivamos el renderer por defecto para que el renderizador personalizado funcione:
        // tabla.setDefaultRenderer(Object.class, centerRenderer);
    }
    
    /**
     * Crea un título estándar centrado.
     */
    public static JLabel crearTitulo(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(FUENTE_TITULO);
        lbl.setForeground(COLOR_PRINCIPAL);
        lbl.setBorder(new EmptyBorder(20, 0, 20, 0));
        return lbl;
    }
}