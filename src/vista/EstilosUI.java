package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Paleta de colores, tipografía y fábrica de componentes modernos para Java
 * Swing.
 */
public class EstilosUI {

    // Paleta Slate Dark + Acentos Semánticos
    public static final Color BG_PRINCIPAL = new Color(15, 23, 42); // #0F172A
    public static final Color BG_PANEL = new Color(30, 41, 59); // #1E293B
    public static final Color BG_CONSOLA = new Color(11, 17, 32); // #0B1120
    public static final Color BG_INPUT = new Color(51, 65, 85); // #334155
    public static final Color BORDE_SUTIL = new Color(51, 65, 85); // #334155

    public static final Color TEXTO_PRIMARIO = new Color(248, 250, 252);
    public static final Color TEXTO_SECUNDARIO = new Color(148, 163, 184);

    public static final Color ACENTO_AZUL = new Color(37, 99, 235); // Acción principal
    public static final Color ACENTO_VERDE = new Color(16, 185, 129); // Ruta segura / Reabrir
    public static final Color ACENTO_ROJO = new Color(225, 29, 72); // Humo / Bloqueo
    public static final Color ACENTO_AMBAR = new Color(217, 119, 6); // Alertas / MST
    public static final Color ACENTO_CIAN = new Color(6, 182, 212); // BFS / Comparación
    public static final Color BOTON_NEUTRO = new Color(51, 65, 85); // Botones laterales

    public static final Font FONT_TITULO = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_SECCION = new Font("SansSerif", Font.BOLD, 11);
    public static final Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BOTON = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FONT_CONSOLA = new Font("Monospaced", Font.PLAIN, 13);

    /** Botón plano redondeado con transición suave de color al pasar el cursor. */
    public static class BotonModerno extends JButton {
        private final Color colorBase;
        private final Color colorHover;
        private Color colorActual;

        public BotonModerno(String texto, Color colorBase) {
            super(texto);
            this.colorBase = colorBase;
            this.colorHover = aclarar(colorBase, 22);
            this.colorActual = colorBase;

            setFont(FONT_BOTON);
            setForeground(TEXTO_PRIMARIO);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 14, 9, 14));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    colorActual = colorHover;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    colorActual = colorBase;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(colorActual);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }

        private static Color aclarar(Color c, int cantidad) {
            return new Color(
                    Math.min(255, c.getRed() + cantidad),
                    Math.min(255, c.getGreen() + cantidad),
                    Math.min(255, c.getBlue() + cantidad));
        }
    }

    public static JLabel crearEtiquetaSeccion(String texto) {
        JLabel lbl = new JLabel(texto.toUpperCase());
        lbl.setFont(FONT_SECCION);
        lbl.setForeground(TEXTO_SECUNDARIO);
        lbl.setBorder(new EmptyBorder(10, 4, 4, 4));
        return lbl;
    }

    public static JLabel crearBadge(String texto, Color colorFondo) {
        JLabel badge = new JLabel(texto);
        badge.setFont(new Font("SansSerif", Font.BOLD, 11));
        badge.setForeground(TEXTO_PRIMARIO);
        badge.setOpaque(true);
        badge.setBackground(colorFondo);
        badge.setBorder(new EmptyBorder(5, 10, 5, 10));
        return badge;
    }

    public static void estilizarCampoTexto(JTextField campo) {
        campo.setBackground(BG_INPUT);
        campo.setForeground(TEXTO_PRIMARIO);
        campo.setCaretColor(TEXTO_PRIMARIO);
        campo.setFont(new Font("SansSerif", Font.BOLD, 13));
        campo.setHorizontalAlignment(JTextField.CENTER);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                new EmptyBorder(6, 10, 6, 10)));
    }

    public static void estilizarCombo(JComboBox<String> combo) {
        combo.setBackground(BG_INPUT);
        combo.setForeground(TEXTO_PRIMARIO);
        combo.setFont(FONT_NORMAL);
        combo.setFocusable(false);
    }
}
