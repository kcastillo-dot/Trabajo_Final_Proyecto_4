package vista;

import grafo.AlgoritmosGrafo;
import grafo.Arista;
import grafo.ResultadoRuta;
import servicio.SimuladorEvacuacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dashboard principal en Java Swing para el Simulador de Evacuación en
 * Edificios.
 * Incluye visualización 2D del grafo, simulación de humo en vivo y consola de
 * telemetría.
 */
public class VentanaPrincipal extends JFrame {

        private SimuladorEvacuacion sim;
        private final PanelMapaEdificio panelMapa;
        private final JTextArea consola;
        private final JTextField txtHabitacion;
        private final JComboBox<String> comboAristas;
        private final JLabel badgeCola;
        private final JLabel badgeHumo;

        private static final String[] TODAS_LAS_ARISTAS = {
                        "P2-ESC_A", "H203-P2", "P2-ESC_B", "H201-P2", "H202-P2",
                        "P1-ESC_A", "P1-ESC_B", "P1-EXIT_A", "P1-EXIT_B",
                        "H101-P1", "H102-P1", "H103-P1", "ESC_A-EXIT_A", "ESC_B-EXIT_B"
        };

        public VentanaPrincipal() {
                this.sim = new SimuladorEvacuacion();

                setTitle("Centro de Control — Evacuación de Edificios (Proyecto 4)");
                setSize(1180, 740);
                setMinimumSize(new Dimension(1020, 640));
                setDefaultCloseOperation(EXIT_ON_CLOSE);
                setLocationRelativeTo(null);
                getContentPane().setBackground(EstilosUI.BG_PRINCIPAL);
                setLayout(new BorderLayout(12, 12));

                // 1. HEADER SUPERIOR + BARRA DE COMANDOS
                JPanel contenedorNorte = new JPanel(new BorderLayout(0, 8));
                contenedorNorte.setBackground(EstilosUI.BG_PRINCIPAL);
                contenedorNorte.setBorder(new EmptyBorder(12, 14, 0, 14));

                // Fila de título e indicadores KPI
                JPanel barraTitulo = new JPanel(new BorderLayout());
                barraTitulo.setBackground(EstilosUI.BG_PANEL);
                barraTitulo.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(EstilosUI.BORDE_SUTIL, 1),
                                new EmptyBorder(10, 16, 10, 16)));

                JPanel bloqueTitulo = new JPanel(new GridLayout(2, 1));
                bloqueTitulo.setOpaque(false);
                JLabel lblTitulo = new JLabel("SIMULADOR DE EVACUACIÓN EN EDIFICIOS");
                lblTitulo.setFont(EstilosUI.FONT_TITULO);
                lblTitulo.setForeground(EstilosUI.TEXTO_PRIMARIO);
                JLabel lblSub = new JLabel("Estructuras de Datos desde cero  •  Grafo Ponderado + IA de Reapertura");
                lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
                lblSub.setForeground(EstilosUI.TEXTO_SECUNDARIO);
                bloqueTitulo.add(lblTitulo);
                bloqueTitulo.add(lblSub);

                JPanel panelBadges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
                panelBadges.setOpaque(false);
                badgeCola = EstilosUI.crearBadge("Cola ESC_A: 3", EstilosUI.ACENTO_AMBAR);
                badgeHumo = EstilosUI.crearBadge("Bloqueos: 0", EstilosUI.ACENTO_VERDE);
                panelBadges.add(badgeCola);
                panelBadges.add(badgeHumo);

                barraTitulo.add(bloqueTitulo, BorderLayout.WEST);
                barraTitulo.add(panelBadges, BorderLayout.EAST);

                // Fila de controles operativos (Evacuación + Control de Humo)
                JPanel barraControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
                barraControles.setBackground(EstilosUI.BG_PANEL);
                barraControles.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(EstilosUI.BORDE_SUTIL, 1),
                                new EmptyBorder(4, 10, 4, 10)));

                JLabel lblHab = new JLabel("Habitación:");
                lblHab.setForeground(EstilosUI.TEXTO_SECUNDARIO);
                lblHab.setFont(EstilosUI.FONT_BOTON);

                txtHabitacion = new JTextField("H201", 6);
                EstilosUI.estilizarCampoTexto(txtHabitacion);

                EstilosUI.BotonModerno btnEvacuar = new EstilosUI.BotonModerno("Evacuar (IA + Pila)",
                                EstilosUI.ACENTO_AZUL);
                EstilosUI.BotonModerno btnCompararH101 = new EstilosUI.BotonModerno("Comparar H101 (BFS vs Dijkstra)",
                                EstilosUI.ACENTO_CIAN);

                JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
                sep.setPreferredSize(new Dimension(2, 26));
                sep.setForeground(EstilosUI.BORDE_SUTIL);

                JLabel lblCon = new JLabel("Conexión:");
                lblCon.setForeground(EstilosUI.TEXTO_SECUNDARIO);
                lblCon.setFont(EstilosUI.FONT_BOTON);

                comboAristas = new JComboBox<>(TODAS_LAS_ARISTAS);
                EstilosUI.estilizarCombo(comboAristas);

                EstilosUI.BotonModerno btnBloquear = new EstilosUI.BotonModerno("Bloquear (Humo)",
                                EstilosUI.ACENTO_ROJO);
                EstilosUI.BotonModerno btnReabrir = new EstilosUI.BotonModerno("Reabrir", EstilosUI.ACENTO_VERDE);

                barraControles.add(lblHab);
                barraControles.add(txtHabitacion);
                barraControles.add(btnEvacuar);
                barraControles.add(btnCompararH101);
                barraControles.add(sep);
                barraControles.add(lblCon);
                barraControles.add(comboAristas);
                barraControles.add(btnBloquear);
                barraControles.add(btnReabrir);

                contenedorNorte.add(barraTitulo, BorderLayout.NORTH);
                contenedorNorte.add(barraControles, BorderLayout.SOUTH);
                add(contenedorNorte, BorderLayout.NORTH);

                // 2. BARRA LATERAL IZQUIERDA (Ancho fijo, sin botones gigantes)
                JPanel sidebar = new JPanel();
                sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
                sidebar.setBackground(EstilosUI.BG_PANEL);
                sidebar.setPreferredSize(new Dimension(245, 0));
                sidebar.setBorder(BorderFactory.createCompoundBorder(
                                new EmptyBorder(0, 14, 14, 0),
                                BorderFactory.createCompoundBorder(
                                                BorderFactory.createLineBorder(EstilosUI.BORDE_SUTIL, 1),
                                                new EmptyBorder(10, 12, 12, 12))));

                EstilosUI.BotonModerno btnPiso2 = crearBotonLateral("Piso 2 (Lista Doble)", EstilosUI.BOTON_NEUTRO);
                EstilosUI.BotonModerno btnPuerta = crearBotonLateral("Turno Puerta ESC_A (Cola)",
                                EstilosUI.BOTON_NEUTRO);
                EstilosUI.BotonModerno btnHash = crearBotonLateral("Tabla Hash (Cubeta 2)", EstilosUI.BOTON_NEUTRO);
                EstilosUI.BotonModerno btnZonas = crearBotonLateral("ABB Zonas de Riesgo", EstilosUI.BOTON_NEUTRO);
                EstilosUI.BotonModerno btnMst = crearBotonLateral("Red Mínima (Prim / Kruskal)",
                                EstilosUI.ACENTO_AMBAR);
                EstilosUI.BotonModerno btnCorrida = crearBotonLateral("Corrida Oficial Completa",
                                EstilosUI.ACENTO_VERDE);
                EstilosUI.BotonModerno btnLimpiar = crearBotonLateral("Limpiar Consola", EstilosUI.BG_INPUT);
                EstilosUI.BotonModerno btnReiniciar = crearBotonLateral("Reiniciar Simulación", EstilosUI.ACENTO_ROJO);

                sidebar.add(EstilosUI.crearEtiquetaSeccion("Estructuras Lineales & Hash"));
                agregarConEspacio(sidebar, btnPiso2, 6);
                agregarConEspacio(sidebar, btnPuerta, 6);
                agregarConEspacio(sidebar, btnHash, 10);

                sidebar.add(EstilosUI.crearEtiquetaSeccion("Árboles & Red del Edificio"));
                agregarConEspacio(sidebar, btnZonas, 6);
                agregarConEspacio(sidebar, btnMst, 10);

                sidebar.add(EstilosUI.crearEtiquetaSeccion("Evaluación & Control"));
                agregarConEspacio(sidebar, btnCorrida, 6);
                sidebar.add(Box.createVerticalGlue());
                agregarConEspacio(sidebar, btnLimpiar, 6);
                sidebar.add(btnReiniciar);

                add(sidebar, BorderLayout.WEST);

                // 3. ÁREA CENTRAL: MAPA 2D ARRIBA + CONSOLA TELEMETRÍA ABAJO
                panelMapa = new PanelMapaEdificio(sim.edificio);

                JPanel cardMapa = new JPanel(new BorderLayout());
                cardMapa.setBackground(EstilosUI.BG_CONSOLA);
                cardMapa.setBorder(BorderFactory.createLineBorder(EstilosUI.BORDE_SUTIL, 1));
                cardMapa.add(panelMapa, BorderLayout.CENTER);

                consola = new JTextArea();
                consola.setEditable(false);
                consola.setBackground(EstilosUI.BG_CONSOLA);
                consola.setForeground(new Color(226, 232, 240));
                consola.setFont(EstilosUI.FONT_CONSOLA);
                consola.setMargin(new Insets(12, 14, 12, 14));
                consola.setLineWrap(true);
                consola.setWrapStyleWord(true);

                JScrollPane scrollConsola = new JScrollPane(consola);
                scrollConsola.setBorder(BorderFactory.createLineBorder(EstilosUI.BORDE_SUTIL, 1));
                scrollConsola.getViewport().setBackground(EstilosUI.BG_CONSOLA);

                JPanel panelConsola = new JPanel(new BorderLayout());
                panelConsola.setBackground(EstilosUI.BG_PANEL);
                JLabel lblConsola = new JLabel("  CONSOLA DE TELEMETRÍA Y REPORTES");
                lblConsola.setFont(EstilosUI.FONT_SECCION);
                lblConsola.setForeground(EstilosUI.TEXTO_SECUNDARIO);
                lblConsola.setBorder(new EmptyBorder(6, 6, 6, 6));
                panelConsola.add(lblConsola, BorderLayout.NORTH);
                panelConsola.add(scrollConsola, BorderLayout.CENTER);

                JSplitPane splitCentro = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cardMapa, panelConsola);
                splitCentro.setResizeWeight(0.58);
                splitCentro.setDividerSize(7);
                splitCentro.setBackground(EstilosUI.BG_PRINCIPAL);
                splitCentro.setBorder(new EmptyBorder(0, 0, 14, 14));

                add(splitCentro, BorderLayout.CENTER);

                // 4. EVENTOS
                btnEvacuar.addActionListener(e -> {
                        String hab = txtHabitacion.getText().trim().toUpperCase();
                        txtHabitacion.setText(hab);
                        ResultadoRuta ruta = sim.edificio.existeNodo(hab)
                                        ? AlgoritmosGrafo.dijkstraMejorSalida(sim.edificio, hab)
                                        : null;

                        String resultado = sim.evaluarEvacuacion(hab);
                        escribir("▶ EVACUACIÓN DESDE " + hab + "\n" + resultado);

                        if (ruta != null) {
                                panelMapa.mostrarRutas(ruta, null,
                                                "Ruta óptima desde " + hab + " -> " + ruta.salida + " ("
                                                                + ruta.tiempoTotal + " s)");
                        } else {
                                panelMapa.mostrarRutas(null, null,
                                                sim.edificio.existeNodo(hab)
                                                                ? "ALERTA: " + hab
                                                                                + " aislada por humo (ver propuesta IA abajo)"
                                                                : "ERROR: La habitación " + hab
                                                                                + " no existe en el edificio");
                        }
                        actualizarIndicadores();
                });

                btnCompararH101.addActionListener(e -> {
                        ResultadoRuta bfs = AlgoritmosGrafo.bfsMejorSalida(sim.edificio, "H101");
                        ResultadoRuta dij = AlgoritmosGrafo.dijkstraMejorSalida(sim.edificio, "H101");
                        panelMapa.mostrarRutas(dij, bfs,
                                        "Comparación H101: Dijkstra (Verde: 9s, 3 pasos) vs BFS (Cian: 10s, 2 pasos)");

                        escribir("▶ COMPARACIÓN H101 (BFS vs DIJKSTRA)\n"
                                        + "  • BFS (niveles): " + bfs.caminoFormateado() + " -> " + bfs.pasos
                                        + " pasos, " + bfs.tiempoTotal + " s\n"
                                        + "  • Dijkstra:      " + dij.caminoFormateado() + " -> " + dij.pasos
                                        + " pasos, " + dij.tiempoTotal + " s\n"
                                        + "  • Análisis: menos aristas (2) NO garantiza menos tiempo (10 s vs 9 s).");
                });

                btnBloquear.addActionListener(e -> {
                        String seleccion = (String) comboAristas.getSelectedItem();
                        String[] partes = seleccion.split("-");
                        sim.edificio.bloquearArista(partes[0], partes[1]);
                        panelMapa.actualizarEstado("HUMO DETECTADO: conexión " + seleccion + " bloqueada");
                        panelMapa.repaint();
                        escribir("✖ [HUMO] Conexión bloqueada: " + seleccion);
                        actualizarIndicadores();
                });

                btnReabrir.addActionListener(e -> {
                        String seleccion = (String) comboAristas.getSelectedItem();
                        String[] partes = seleccion.split("-");
                        sim.edificio.reabrirArista(partes[0], partes[1]);
                        panelMapa.actualizarEstado("DESPEJADO: conexión " + seleccion + " reabierta");
                        panelMapa.repaint();
                        escribir("✔ [DESPEJADO] Conexión reabierta: " + seleccion);
                        actualizarIndicadores();
                });

                btnPiso2.addActionListener(e -> escribir("▶ OCUPANTES PISO 2 (LISTA DOBLE)\n"
                                + "  • Ida:     " + sim.piso2.listarIda() + "\n"
                                + "  • Regreso: " + sim.piso2.listarRegreso()));

                btnPuerta.addActionListener(e -> {
                        escribir("▶ TURNO PUERTA ESC_A\n  " + sim.atenderTurnoPuerta());
                        actualizarIndicadores();
                });

                btnHash.addActionListener(e -> escribir("▶ TABLA HASH (COLISIÓN CONTROLADA)\n  "
                                + sim.tablaPersonas.mostrarCubeta(2)));

                btnZonas.addActionListener(e -> escribir("▶ ABB ZONAS DE RIESGO\n"
                                + "  • Inorden:  " + sim.arbolZonas.recorridoInorden() + "\n"
                                + "  • Métricas: Nodos=" + sim.arbolZonas.contarNodos()
                                + " | Peso=" + sim.arbolZonas.getPeso()
                                + " | Altura=" + sim.arbolZonas.getAltura()
                                + " | Hojas=" + sim.arbolZonas.contarHojas()
                                + " | Completo=" + (sim.arbolZonas.esCompleto() ? "Sí" : "No") + "\n"
                                + "  • Riesgo 7: " + sim.arbolZonas.buscarConTraza(7) + "\n"
                                + "  • Extremo derecho (más peligrosa): "
                                + sim.arbolZonas.obtenerMasPeligrosa().getNombre()));

                btnMst.addActionListener(e -> {
                        StringBuilder detallePrim = new StringBuilder();
                        int prim = AlgoritmosGrafo.calcularPrim(sim.edificio, detallePrim);
                        int kruskal = AlgoritmosGrafo.calcularKruskal(sim.edificio);
                        escribir("▶ RED MÍNIMA DEL EDIFICIO (PRIM Y KRUSKAL)\n"
                                        + "  • Prim:    " + prim + " s -> " + detallePrim + "\n"
                                        + "  • Kruskal: " + kruskal + " s\n"
                                        + "  • Nota: El MST comunica todo el edificio al menor costo; cada persona evacua por Dijkstra.");
                });

                btnCorrida.addActionListener(e -> ejecutarCorridaOficial());
                btnLimpiar.addActionListener(e -> consola.setText(""));

                btnReiniciar.addActionListener(e -> {
                        this.sim = new SimuladorEvacuacion();
                        panelMapa.setGrafo(sim.edificio);
                        consola.setText("");
                        escribir("✔ Simulación reiniciada con los datos oficiales del edificio.");
                        actualizarIndicadores();
                });

                escribir("✔ Sistema listo. Usa el panel superior para evacuar o bloquear aristas por humo en vivo.");
                actualizarIndicadores();
        }

        private EstilosUI.BotonModerno crearBotonLateral(String texto, Color color) {
                EstilosUI.BotonModerno btn = new EstilosUI.BotonModerno(texto, color);
                btn.setAlignmentX(Component.LEFT_ALIGNMENT);
                btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
                btn.setHorizontalAlignment(SwingConstants.LEFT);
                return btn;
        }

        private void agregarConEspacio(JPanel panel, JComponent comp, int espacio) {
                panel.add(comp);
                panel.add(Box.createVerticalStrut(espacio));
        }

        private void actualizarIndicadores() {
                int enCola = sim.puertaEscA.getTamano();
                badgeCola.setText("Cola ESC_A: " + enCola);
                badgeCola.setBackground(enCola > 0 ? EstilosUI.ACENTO_AMBAR : EstilosUI.BOTON_NEUTRO);

                int bloqueadas = contarAristasBloqueadas();
                badgeHumo.setText("Bloqueos: " + bloqueadas);
                badgeHumo.setBackground(bloqueadas > 0 ? EstilosUI.ACENTO_ROJO : EstilosUI.ACENTO_VERDE);
        }

        private int contarAristasBloqueadas() {
                int total = 0;
                int n = sim.edificio.getCantidadNodos();
                for (int u = 0; u < n; u++) {
                        Arista actual = sim.edificio.getAdyacentes(u);
                        while (actual != null) {
                                if (actual.bloqueada && u < actual.destino)
                                        total++;
                                actual = actual.siguiente;
                        }
                }
                return total;
        }

        private void ejecutarCorridaOficial() {
                this.sim = new SimuladorEvacuacion();
                panelMapa.setGrafo(sim.edificio);
                consola.setText("");

                escribir("================ CORRIDA OFICIAL DE VERIFICACIÓN ================");
                escribir("1) Piso 2 -> Ida: " + sim.piso2.listarIda()
                                + " | Regreso: " + sim.piso2.listarRegreso());
                escribir("2) Tabla Hash -> " + sim.tablaPersonas.mostrarCubeta(2));
                escribir("3) Puerta ESC_A:\n   " + sim.atenderTurnoPuerta()
                                + "\n   " + sim.atenderTurnoPuerta()
                                + "\n   " + sim.atenderTurnoPuerta()
                                + "\n   " + sim.atenderTurnoPuerta());
                escribir("4) Zonas Inorden: " + sim.arbolZonas.recorridoInorden()
                                + "\n   Completo: " + (sim.arbolZonas.esCompleto() ? "Sí" : "No")
                                + " | Más peligrosa: " + sim.arbolZonas.obtenerMasPeligrosa().getNombre());
                escribir("5) H201 sin humo:\n" + sim.evaluarEvacuacion("H201"));

                ResultadoRuta bfs = AlgoritmosGrafo.bfsMejorSalida(sim.edificio, "H101");
                ResultadoRuta dij = AlgoritmosGrafo.dijkstraMejorSalida(sim.edificio, "H101");
                escribir("6) H101 -> BFS: " + bfs.tiempoTotal + " s en " + bfs.pasos
                                + " pasos | Dijkstra: " + dij.tiempoTotal + " s en " + dij.pasos + " pasos");

                sim.edificio.bloquearArista("P2", "ESC_A");
                ResultadoRuta rutaHumo = AlgoritmosGrafo.dijkstraMejorSalida(sim.edificio, "H201");
                escribir("7) Humo en P2-ESC_A -> H201:\n" + sim.evaluarEvacuacion("H201"));
                sim.edificio.reabrirArista("P2", "ESC_A");

                sim.edificio.bloquearArista("H203", "P2");
                escribir("8) H203 aislada:\n" + sim.evaluarEvacuacion("H203"));
                sim.edificio.reabrirArista("H203", "P2");

                escribir("9) Habitación inexistente:\n" + sim.evaluarEvacuacion("H999"));
                escribir("10) Red mínima -> Prim: " + AlgoritmosGrafo.calcularPrim(sim.edificio, null)
                                + " s | Kruskal: " + AlgoritmosGrafo.calcularKruskal(sim.edificio) + " s");

                // Deja resaltada en el mapa la ruta óptima de H201 como cierre visual
                panelMapa.mostrarRutas(AlgoritmosGrafo.dijkstraMejorSalida(sim.edificio, "H201"), null,
                                "Corrida oficial completada — Vista: H201 -> EXIT_A (10 s)");
                actualizarIndicadores();
        }

        private void escribir(String texto) {
                consola.append(texto + "\n\n");
                consola.setCaretPosition(consola.getDocument().getLength());
        }
}