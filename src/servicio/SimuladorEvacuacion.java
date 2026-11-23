package servicio;

import estructura.arboles.*;
import estructura.lineales.*;
import grafo.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SimuladorEvacuacion {
  public final GrafoEdificio edificio;
  public final ListaDobleOcupantes piso2;
  public final MiCola<Persona> puertaEscA;
  public final TablaHashPersonas tablaPersonas;
  public final ArbolRiesgoZonas arbolZonas;
  public final ArbolDecisionEvacuacion arbolDecision;
  public final AsesorIAEvacuacion asesorIA;

  public SimuladorEvacuacion() {
    edificio = new GrafoEdificio(12);
    piso2 = new ListaDobleOcupantes();
    puertaEscA = new MiCola<>();
    tablaPersonas = new TablaHashPersonas();
    arbolZonas = new ArbolRiesgoZonas();
    arbolDecision = new ArbolDecisionEvacuacion();
    asesorIA = new AsesorIAEvacuacion();
    cargarDatosOficiales();
  }

  private void cargarDatosOficiales() {
    String[] lugares = {
        "H201", "H202", "H203", "P2",
        "H101", "H102", "H103", "P1",
        "ESC_A", "ESC_B", "EXIT_A", "EXIT_B"
    };
    for (String lugar : lugares)
      edificio.agregarNodo(lugar);
    edificio.agregarArista("H201", "P2", 2);
    edificio.agregarArista("H202", "P2", 2);
    edificio.agregarArista("H203", "P2", 3);
    edificio.agregarArista("P2", "ESC_A", 4);
    edificio.agregarArista("P2", "ESC_B", 6);
    edificio.agregarArista("H101", "P1", 2);
    edificio.agregarArista("H102", "P1", 2);
    edificio.agregarArista("H103", "P1", 3);
    edificio.agregarArista("P1", "ESC_A", 3);
    edificio.agregarArista("P1", "ESC_B", 5);
    edificio.agregarArista("P1", "EXIT_A", 8);
    edificio.agregarArista("ESC_A", "EXIT_A", 4);
    edificio.agregarArista("ESC_B", "EXIT_B", 4);
    edificio.agregarArista("P1", "EXIT_B", 9);
    Persona nora = new Persona("A01", "Nora", 2, "H201");
    Persona teo = new Persona("A02", "Teo", 2, "H202");
    Persona lina = new Persona("A03", "Lina", 2, "H203");
    Persona sara = new Persona("A04", "Sara", 1, "H101");
    Persona iker = new Persona("A05", "Iker", 1, "H102");
    Persona dana = new Persona("A06", "Dana", 1, "H103");
    Persona omar = new Persona("A07", "Omar", 2, "H201");
    Persona vera = new Persona("A08", "Vera", 1, "H101");

    Persona[] todas = { nora, teo, lina, sara, iker, dana, omar, vera };
    for (Persona p : todas)
      tablaPersonas.insertar(p);
    piso2.insertarAlFinal(nora);
    piso2.insertarAlFinal(teo);
    piso2.insertarAlFinal(lina);
    piso2.insertarAlFinal(omar);
    puertaEscA.encolar(sara);
    puertaEscA.encolar(iker);
    puertaEscA.encolar(vera);
    arbolZonas.insertar(5, "Pasillos");
    arbolZonas.insertar(2, "Archivo");
    arbolZonas.insertar(8, "Cocina");
    arbolZonas.insertar(1, "Lobby");
    arbolZonas.insertar(3, "Aulas");
    arbolZonas.insertar(7, "Laboratorio");
    arbolZonas.insertar(9, "Bodega");
  }

  public String atenderTurnoPuerta() {
    if (puertaEscA.estaVacia()) {
      return "Aviso: puerta ESC_A vacía. No se marca a nadie más como evacuado.";
    }
    Persona saliente = puertaEscA.desencolar();
    saliente.marcarEvacuado();
    return "Sale por ESC_A: " + saliente.getNombre() + " (" + saliente.getId()
        + ") -> marcada como EVACUADA en la tabla.";
  }

  public String evaluarEvacuacion(String habitacion) {
    boolean existe = edificio.existeNodo(habitacion);
    ResultadoRuta ruta = existe ? AlgoritmosGrafo.dijkstraMejorSalida(edificio, habitacion) : null;
    boolean hayRuta = (ruta != null);
    boolean colaEnEscalera = hayRuta && ruta.usaEscalera("ESC_A") && !puertaEscA.estaVacia();

    ArbolDecisionEvacuacion.Accion accion = arbolDecision.evaluar(existe, hayRuta, colaEnEscalera);

    StringBuilder reporte = new StringBuilder();
    switch (accion) {
      case HABITACION_NO_EXISTE ->
        reporte.append("La habitación ").append(habitacion).append(" no está en el edificio.");
      case SIN_RUTA_REABRIR ->
        reporte.append(asesorIA.proponerAlternativaReal(edificio, habitacion));
      case ESPERAR_TURNO_COLA -> {
        reporte.append("Ruta óptima: ").append(ruta.caminoFormateado())
            .append(" (").append(ruta.tiempoTotal).append(" s). ")
            .append("Escalera ESC_A con cola -> se espera un turno.\n")
            .append(" ").append(atenderTurnoPuerta());
      }
      case AVANZAR_PASO -> {
        reporte.append("Salida elegida: ").append(ruta.salida)
            .append(" en ").append(ruta.tiempoTotal).append(" s (")
            .append(ruta.caminoFormateado()).append(")\nInstrucciones desde la Pila:\n");
        MiPila<String> pila = ruta.construirPilaInstrucciones();
        int paso = 1;
        while (!pila.estaVacia()) {
          reporte.append(" ").append(paso++).append(". ").append(pila.desapilar()).append("\n");
        }
        guardarEvacuacionTxt(habitacion, ruta);
      }
    }
    return reporte.toString();
  }

  public void guardarEvacuacionTxt(String habitacion, ResultadoRuta ruta) {
    try (PrintWriter pw = new PrintWriter(new FileWriter("evacuacion.txt", true))) {
      pw.println("Habitación: " + habitacion + " | Camino: " + ruta.caminoFormateado()
          + " | Salida: " + ruta.salida + " | Tiempo: " + ruta.tiempoTotal + " s");
    } catch (IOException e) {
    }
  }
}
