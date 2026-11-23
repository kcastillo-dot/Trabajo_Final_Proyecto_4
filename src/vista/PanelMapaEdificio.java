package vista;

import grafo.Arista;
import grafo.GrafoEdificio;
import grafo.ResultadoRuta;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.QuadCurve2D;

/**
 * Lienzo 2D interactivo que renderiza el grafo del edificio de 2 pisos,
 * las aristas bloqueadas por humo y las rutas calculadas por Dijkstra y BFS.
 */
public class PanelMapaEdificio extends JPanel {

    private GrafoEdificio grafo;
    private ResultadoRuta rutaPrincipal; // Verde (Dijkstra)
    private ResultadoRuta rutaSecundaria; // Cian (BFS para comparación H101)
    private String tituloEstado = "Plano en tiempo real — Seleccione una habitación o bloquee una conexión";

    // Coordenadas normalizadas (0.0 a 1.0) para los 12 nodos oficiales del edificio
    private static final String[] NOMBRES_POS = {
            "H201", "H202", "H203", "P2",
            "H101", "H102", "H103", "P1",
            "ESC_A", "ESC_B", "EXIT_A", "EXIT_B"
    };

    private static final double[] POS_X = {
            0.11, 0.11, 0.11, 0.34,
            0.11, 0.11, 0.11, 0.34,
            0.63, 0.63, 0.89, 0.89
    };

    private static final double[] POS_Y = {
            0.18, 0.31, 0.44, 0.31,
            0.61, 0.74, 0.87, 0.74,
            0.35, 0.68, 0.25, 0.78
    };

    public PanelMapaEdificio(GrafoEdificio grafo) {
        this.grafo = grafo;
        setBackground(EstilosUI.BG_CONSOLA);
        setPreferredSize(new Dimension(760, 340));
    }

    public void setGrafo(GrafoEdificio grafo) {
        this.grafo = grafo;
        this.rutaPrincipal = null;
        this.rutaSecundaria = null;
        this.tituloEstado = "Simulación reiniciada — Edificio despejado";
        repaint();
    }

    public void mostrarRutas(ResultadoRuta principal, ResultadoRuta secundaria, String titulo) {
        this.rutaPrincipal = principal;
        this.rutaSecundaria = secundaria;
        this.tituloEstado = titulo;
        repaint();
    }

    public void actualizarEstado(String titulo) {
        this.tituloEstado = titulo;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        dibujarFondosPisos(g2, w, h);
        dibujarAristas(g2, w, h);
        dibujarNodos(g2, w, h);
        dibujarCabeceraYLeyenda(g2, w, h);

        g2.dispose();
    }

    private void dibujarFondosPisos(Graphics2D g2, int w, int h) {
        int margenX = 18;
        int divY = (int) (h * 0.525);

        // Banda Piso 2
        g2.setColor(new Color(30, 41, 59, 110));
        g2.fillRoundRect(margenX, 34, w - margenX * 2, divY - 42, 14, 14);
        g2.setColor(new Color(71, 85, 105, 110));
        g2.drawRoundRect(margenX, 34, w - margenX * 2, divY - 42, 14, 14);

        // Banda Piso 1
        g2.setColor(new Color(30, 41, 59, 110));
        g2.fillRoundRect(margenX, divY + 8, w - margenX * 2, h - divY - 40, 14, 14);
        g2.setColor(new Color(71, 85, 105, 110));
        g2.drawRoundRect(margenX, divY + 8, w - margenX * 2, h - divY - 40, 14, 14);

        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(new Color(148, 163, 184, 170));
        g2.drawString("PISO 2", margenX + 12, 52);
        g2.drawString("PISO 1", margenX + 12, divY + 26);
    }

    private void dibujarAristas(Graphics2D g2, int w, int h) {
        int n = grafo.getCantidadNodos();
        Stroke lineaNormal = new BasicStroke(2.2f);
        Stroke lineaRuta = new BasicStroke(4.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Stroke lineaHumo = new BasicStroke(3.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10.0f, new float[] { 8.0f, 6.0f }, 0.0f);
        Stroke lineaBfs = new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                10.0f, new float[] { 10.0f, 6.0f }, 0.0f);

        for (int u = 0; u < n; u++) {
            Arista actual = grafo.getAdyacentes(u);
            while (actual != null) {
                int v = actual.destino;
                if (u < v) {
                    String nomU = grafo.getNombreNodo(u);
                    String nomV = grafo.getNombreNodo(v);
                    Point p1 = obtenerCoordenada(nomU, w, h);
                    Point p2 = obtenerCoordenada(nomV, w, h);

                    boolean enDijkstra = aristaEnRuta(rutaPrincipal, nomU, nomV);
                    boolean enBfs = aristaEnRuta(rutaSecundaria, nomU, nomV);
                    boolean esCurvaInferior = esPar(nomU, nomV, "P1", "EXIT_B");

                    if (actual.bloqueada) {
                        g2.setColor(EstilosUI.ACENTO_ROJO);
                        g2.setStroke(lineaHumo);
                    } else if (enDijkstra) {
                        g2.setColor(EstilosUI.ACENTO_VERDE);
                        g2.setStroke(lineaRuta);
                    } else if (enBfs) {
                        g2.setColor(EstilosUI.ACENTO_CIAN);
                        g2.setStroke(lineaBfs);
                    } else {
                        g2.setColor(new Color(100, 116, 139, 175));
                        g2.setStroke(lineaNormal);
                    }

                    int labelX;
                    int labelY;
                    if (esCurvaInferior) {
                        double ctrlX = w * 0.62;
                        double ctrlY = h * 0.94;
                        g2.draw(new QuadCurve2D.Double(p1.x, p1.y, ctrlX, ctrlY, p2.x, p2.y));
                        labelX = (int) (0.25 * p1.x + 0.5 * ctrlX + 0.25 * p2.x);
                        labelY = (int) (0.25 * p1.y + 0.5 * ctrlY + 0.25 * p2.y);
                    } else {
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                        // Desplaza ligeramente las etiquetas de las diagonales cruzadas para evitar
                        // choque
                        double t = 0.5;
                        if (esPar(nomU, nomV, "P2", "ESC_B") || esPar(nomU, nomV, "P1", "ESC_A")) {
                            t = 0.33;
                        } else if (esPar(nomU, nomV, "P1", "EXIT_A")) {
                            t = 0.52;
                        }
                        labelX = (int) (p1.x + (p2.x - p1.x) * t);
                        labelY = (int) (p1.y + (p2.y - p1.y) * t);
                    }

                    // Si coincide Dijkstra y BFS en la misma arista, dibujar un destello cian
                    // paralelo
                    if (!actual.bloqueada && enDijkstra && enBfs && !esCurvaInferior) {
                        g2.setColor(EstilosUI.ACENTO_CIAN);
                        g2.setStroke(lineaBfs);
                        g2.drawLine(p1.x, p1.y - 3, p2.x, p2.y - 3);
                    }

                    dibujarEtiquetaPeso(g2, labelX, labelY,
                            actual.bloqueada ? "HUMO" : actual.segundos + "s",
                            actual.bloqueada, enDijkstra, enBfs);
                }
                actual = actual.siguiente;
            }
        }
    }

    private void dibujarEtiquetaPeso(Graphics2D g2, int x, int y, String texto,
            boolean bloqueada, boolean enDijkstra, boolean enBfs) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(texto) + 10;
        int th = 16;

        Color fondo = EstilosUI.BG_PRINCIPAL;
        Color borde = new Color(71, 85, 105);
        Color colorTexto = EstilosUI.TEXTO_SECUNDARIO;

        if (bloqueada) {
            fondo = EstilosUI.ACENTO_ROJO;
            borde = EstilosUI.ACENTO_ROJO;
            colorTexto = Color.WHITE;
        } else if (enDijkstra) {
            fondo = new Color(6, 78, 59);
            borde = EstilosUI.ACENTO_VERDE;
            colorTexto = Color.WHITE;
        } else if (enBfs) {
            fondo = new Color(22, 78, 99);
            borde = EstilosUI.ACENTO_CIAN;
            colorTexto = Color.WHITE;
        }

        g2.setColor(fondo);
        g2.fillRoundRect(x - tw / 2, y - th / 2, tw, th, 8, 8);
        g2.setStroke(new BasicStroke(1.0f));
        g2.setColor(borde);
        g2.drawRoundRect(x - tw / 2, y - th / 2, tw, th, 8, 8);

        g2.setColor(colorTexto);
        g2.drawString(texto, x - fm.stringWidth(texto) / 2, y + 4);
    }

    private void dibujarNodos(Graphics2D g2, int w, int h) {
        int anchoNodo = 66;
        int altoNodo = 28;
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));

        for (int i = 0; i < NOMBRES_POS.length; i++) {
            String nombre = NOMBRES_POS[i];
            Point p = obtenerCoordenada(nombre, w, h);
            int x = p.x - anchoNodo / 2;
            int y = p.y - altoNodo / 2;

            Color fondo;
            Color borde;
            if (nombre.startsWith("EXIT_")) {
                fondo = new Color(6, 95, 70);
                borde = EstilosUI.ACENTO_VERDE;
            } else if (nombre.startsWith("ESC_")) {
                fondo = new Color(120, 53, 15);
                borde = EstilosUI.ACENTO_AMBAR;
            } else if (nombre.startsWith("P")) {
                fondo = new Color(30, 58, 138);
                borde = new Color(96, 165, 250);
            } else {
                fondo = EstilosUI.BG_PANEL;
                borde = new Color(148, 163, 184);
            }

            boolean resaltado = nodoEnRuta(rutaPrincipal, nombre);
            if (resaltado) {
                g2.setColor(new Color(16, 185, 129, 75));
                g2.fillRoundRect(x - 4, y - 4, anchoNodo + 8, altoNodo + 8, 14, 14);
                borde = EstilosUI.ACENTO_VERDE;
            }

            g2.setColor(fondo);
            g2.fillRoundRect(x, y, anchoNodo, altoNodo, 10, 10);
            g2.setStroke(new BasicStroke(resaltado ? 2.4f : 1.4f));
            g2.setColor(borde);
            g2.drawRoundRect(x, y, anchoNodo, altoNodo, 10, 10);

            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(EstilosUI.TEXTO_PRIMARIO);
            g2.drawString(nombre, p.x - fm.stringWidth(nombre) / 2, p.y + 4);
        }
    }

    private void dibujarCabeceraYLeyenda(Graphics2D g2, int w, int h) {
        // Título dinámico arriba
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(EstilosUI.TEXTO_PRIMARIO);
        g2.drawString(tituloEstado, 20, 22);

        // Leyenda abajo
        int ly = h - 10;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        dibujarItemLeyenda(g2, 22, ly, EstilosUI.ACENTO_VERDE, "Ruta Dijkstra (menor tiempo)");
        dibujarItemLeyenda(g2, 210, ly, EstilosUI.ACENTO_CIAN, "Ruta BFS (menor pasos)");
        dibujarItemLeyenda(g2, 375, ly, EstilosUI.ACENTO_ROJO, "Bloqueo por humo");
        dibujarItemLeyenda(g2, 510, ly, EstilosUI.ACENTO_AMBAR, "Escaleras");
    }

    private void dibujarItemLeyenda(Graphics2D g2, int x, int y, Color color, String texto) {
        g2.setColor(color);
        g2.fillOval(x, y - 8, 9, 9);
        g2.setColor(EstilosUI.TEXTO_SECUNDARIO);
        g2.drawString(texto, x + 14, y);
    }

    private Point obtenerCoordenada(String nombre, int w, int h) {
        for (int i = 0; i < NOMBRES_POS.length; i++) {
            if (NOMBRES_POS[i].equals(nombre)) {
                return new Point((int) (w * POS_X[i]), (int) (h * POS_Y[i]));
            }
        }
        return new Point(w / 2, h / 2);
    }

    private boolean aristaEnRuta(ResultadoRuta ruta, String a, String b) {
        if (ruta == null)
            return false;
        for (int i = 0; i < ruta.nodos.length - 1; i++) {
            if (esPar(ruta.nodos[i], ruta.nodos[i + 1], a, b))
                return true;
        }
        return false;
    }

    private boolean nodoEnRuta(ResultadoRuta ruta, String nombre) {
        if (ruta == null)
            return false;
        for (String n : ruta.nodos) {
            if (n.equals(nombre))
                return true;
        }
        return false;
    }

    private boolean esPar(String a, String b, String x, String y) {
        return (a.equals(x) && b.equals(y)) || (a.equals(y) && b.equals(x));
    }
}