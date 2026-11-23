package vista;

import grafo.AlgoritmosGrafo;
import grafo.ResultadoRuta;
import servicio.SimuladorEvacuacion;

import javax.swing.*;
import java.awt.*;
public class VentanaPrincipal extends JFrame {
private final SimuladorEvacuacion sim;
private final JTextArea consola;
private final JTextField txtHabitacion;
private final JComboBox<String> comboAristas;

public VentanaPrincipal() {
this.sim = new SimuladorEvacuacion();
setTitle("Simulador de Evacuación en Edificios — Proyecto 4");
setSize(920, 620);
setDefaultCloseOperation(EXIT_ON_CLOSE);
setLocationRelativeTo(null);
setLayout(new BorderLayout(10, 10));
JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
txtHabitacion = new JTextField("H201", 6);
JButton btnEvacuar = new JButton("Evacuar (IA + Pila)");
JButton btnCompararH101 = new JButton("Comparar H101 (BFS vs Dijkstra)");

String[] listaAristas = {
"P2-ESC_A", "H203-P2", "P2-ESC_B", "H201-P2", "P1-ESC_A", "P1-EXIT_A"
};
comboAristas = new JComboBox<>(listaAristas);
JButton btnBloquear = new JButton("Bloquear (Humo)");
JButton btnReabrir = new JButton("Reabrir");

panelTop.add(new JLabel("Habitación:"));
panelTop.add(txtHabitacion);
panelTop.add(btnEvacuar);
panelTop.add(btnCompararH101);
panelTop.add(new JLabel("Conexión:"));
panelTop.add(comboAristas);
panelTop.add(btnBloquear);
panelTop.add(btnReabrir);
add(panelTop, BorderLayout.NORTH);
JPanel panelBotones = new JPanel(new GridLayout(6, 1, 6, 6));
panelBotones.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
JButton btnPiso2 = new JButton("Piso 2 (Ida / Regreso)");
JButton btnPuerta = new JButton("Turno Puerta ESC_A");
JButton btnHash = new JButton("Tabla Hash (Cubeta 2)");
JButton btnZonas = new JButton("ABB Zonas de Riesgo");
JButton btnMst = new JButton("Red Mínima (Prim y Kruskal)");
JButton btnCorrida = new JButton("Corrida Oficial Completa");
panelBotones.add(btnPiso2);
panelBotones.add(btnPuerta);
panelBotones.add(btnHash);
panelBotones.add(btnZonas);
panelBotones.add(btnMst);
panelBotones.add(btnCorrida);
add(panelBotones, BorderLayout.WEST);
consola = new JTextArea();
consola.setEditable(false);
consola.setFont(new Font("Monospaced", Font.PLAIN, 13));
add(new JScrollPane(consola), BorderLayout.CENTER);
btnEvacuar.addActionListener(e -> escribir(sim.evaluarEvacuacion(txtHabitacion.getText().trim())));

btnCompararH101.addActionListener(e -> {
ResultadoRuta bfs = AlgoritmosGrafo.bfsMejorSalida(sim.edificio, "H101");
ResultadoRuta dij = AlgoritmosGrafo.dijkstraMejorSalida(sim.edificio, "H101");
escribir("=== COMPARACIÓN H101 ===\n"
+ "BFS (niveles): " + bfs.caminoFormateado() + " -> " + bfs.pasos + " pasos, " + bfs.tiempoTotal + " s\n"
+ "Dijkstra: " + dij.caminoFormateado() + " -> " + dij.pasos + " pasos, " + dij.tiempoTotal + " s\n"
+ "Conclusión: menos pasos (2) NO significa menos tiempo (10 s vs 9 s).");
});

btnBloquear.addActionListener(e -> {
String[] partes = ((String) comboAristas.getSelectedItem()).split("-");
sim.edificio.bloquearArista(partes[0], partes[1]);
escribir("[HUMO] Arista bloqueada: " + partes[0] + "-" + partes[1]);
});

btnReabrir.addActionListener(e -> {
String[] partes = ((String) comboAristas.getSelectedItem()).split("-");
sim.edificio.reabrirArista(partes[0], partes[1]);
escribir("[DESPEJADO] Arista reabierta: " + partes[0] + "-" + partes[1]);
});
btnPiso2.addActionListener(e -> escribir("Piso 2 ida: " + sim.piso2.listarIda()
+ "\nPiso 2 regreso: " + sim.piso2.listarRegreso()));
btnPuerta.addActionListener(e -> escribir(sim.atenderTurnoPuerta()));
btnHash.addActionListener(e -> escribir(sim.tablaPersonas.mostrarCubeta(2)));

btnZonas.addActionListener(e -> escribir("=== ABB ZONAS DE RIESGO ===\n"
+ "Inorden: " + sim.arbolZonas.recorridoInorden() + "\n"
+ "Nodos: " + sim.arbolZonas.contarNodos()
+ " | Peso: " + sim.arbolZonas.getPeso()
+ " | Altura: " + sim.arbolZonas.getAltura()
+ " | Hojas: " + sim.arbolZonas.contarHojas()
+ " | Completo: " + (sim.arbolZonas.esCompleto() ? "Sí" : "No") + "\n"
+ sim.arbolZonas.buscarConTraza(7) + "\n"
+ "Zona más peligrosa: " + sim.arbolZonas.obtenerMasPeligrosa().nombre));
btnMst.addActionListener(e -> {
StringBuilder detallePrim = new StringBuilder();
int prim = AlgoritmosGrafo.calcularPrim(sim.edificio, detallePrim);
int kruskal = AlgoritmosGrafo.calcularKruskal(sim.edificio);
escribir("=== RED MÍNIMA DEL EDIFICIO ===\n"
+ "Prim: " + prim + " s -> " + detallePrim + "\n"
+ "Kruskal: " + kruskal + " s\n"
+ "Nota: El MST conecta todo el edificio al menor costo total; una persona evacua por Dijkstra.");
});
btnCorrida.addActionListener(e -> ejecutarCorridaOficial());
}
private void ejecutarCorridaOficial() {
consola.setText("");
escribir("=== CORRIDA OFICIAL DE VERIFICACIÓN ===");
escribir("1) Piso 2 ida: " + sim.piso2.listarIda() + " | regreso: " + sim.piso2.listarRegreso());
escribir("2) Tabla Hash -> " + sim.tablaPersonas.mostrarCubeta(2));
escribir("3) Puerta ESC_A:\n " + sim.atenderTurnoPuerta()
+ "\n " + sim.atenderTurnoPuerta()
+ "\n " + sim.atenderTurnoPuerta()
+ "\n " + sim.atenderTurnoPuerta());
escribir("4) Zonas Inorden: " + sim.arbolZonas.recorridoInorden()
+ " | Más peligrosa: " + sim.arbolZonas.obtenerMasPeligrosa().nombre);
escribir("5) H201 sin humo:\n" + sim.evaluarEvacuacion("H201"));

ResultadoRuta bfs = AlgoritmosGrafo.bfsMejorSalida(sim.edificio, "H101");
ResultadoRuta dij = AlgoritmosGrafo.dijkstraMejorSalida(sim.edificio, "H101");
escribir("6) H101 -> BFS: " + bfs.tiempoTotal + " s en " + bfs.pasos + " pasos | Dijkstra: "
+ dij.tiempoTotal + " s en " + dij.pasos + " pasos");

sim.edificio.bloquearArista("P2", "ESC_A");
escribir("7) Humo en P2-ESC_A -> H201:\n" + sim.evaluarEvacuacion("H201"));
sim.edificio.reabrirArista("P2", "ESC_A");

sim.edificio.bloquearArista("H203", "P2");
escribir("8) H203 aislada:\n" + sim.evaluarEvacuacion("H203"));
sim.edificio.reabrirArista("H203", "P2");

escribir("9) Habitación inexistente:\n" + sim.evaluarEvacuacion("H999"));
escribir("10) Red mínima -> Prim: " + AlgoritmosGrafo.calcularPrim(sim.edificio, null)
+ " s | Kruskal: " + AlgoritmosGrafo.calcularKruskal(sim.edificio) + " s");
}

private void escribir(String texto) {
consola.append(texto + "\n\n");
 }
}
