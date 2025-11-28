package util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class Estilo {

    //PALETA DE COLORES REFINADA
    public static final Color COLOR_PRINCIPAL = new Color(51, 102, 51); // Verde
    public static final Color COLOR_SECUNDARIO = new Color(102, 153, 102); // Verde claro
    
    // Rojo para Eliminar / Ocupado
    public static final Color COLOR_OCUPADO = new Color(204, 71, 71); 
    
    // Azul para Modificar / Editar
    public static final Color COLOR_AZUL = new Color(0, 102, 204); 
    
    public static final Color COLOR_FONDO = new Color(245, 245, 245);
    public static final Color COLOR_TEXTO = new Color(33, 33, 33);
    public static final Color BLANCO = Color.WHITE;

    // FUENTES
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);

    public static void decorarPanel(JPanel panel) {
        panel.setBackground(COLOR_FONDO);
    }

    public static void decorarBoton(JButton boton) {
        boton.setFont(FUENTE_NORMAL);
        boton.setBackground(COLOR_PRINCIPAL);
        boton.setForeground(BLANCO);
        boton.setFocusPainted(false); 
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        // Efecto Hover (Respeta el color asignado)
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color colorOriginal;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                colorOriginal = boton.getBackground();
                // Si es el verde principal, usa el secundario. Si es otro (Rojo/Azul), lo aclara.
                if (colorOriginal.equals(COLOR_PRINCIPAL)) {
                    boton.setBackground(COLOR_SECUNDARIO);
                } else {
                    boton.setBackground(colorOriginal.brighter());
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (colorOriginal != null) {
                    boton.setBackground(colorOriginal);
                }
            }
        });
    }

    public static void decorarTabla(JTable tabla) {
        tabla.setFont(FUENTE_NORMAL);
        tabla.setRowHeight(30); 
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setSelectionBackground(COLOR_SECUNDARIO);
        tabla.setSelectionForeground(BLANCO);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(FUENTE_SUBTITULO);
        header.setBackground(COLOR_PRINCIPAL);
        header.setForeground(BLANCO);
        header.setOpaque(true);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    }
    
    public static JLabel crearTitulo(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(FUENTE_TITULO);
        lbl.setForeground(COLOR_PRINCIPAL);
        lbl.setBorder(new EmptyBorder(20, 0, 20, 0));
        return lbl;
    }
}