package estructura.arboles;

import grafo.AlgoritmosGrafo;
import grafo.Arista;
import grafo.GrafoEdificio;
import grafo.ResultadoRuta;

/**
 * Módulo de IA específica del proyecto (punto adicional):
 * Cuando una habitación queda aislada por humo, simula temporalmente la
 * reapertura
 * de cada arista bloqueada real del edificio y recomienda la intervención de
 * menor tiempo.
 */
public class AsesorIAEvacuacion {

    public String proponerAlternativaReal(GrafoEdificio grafo, String habitacionOrigen) {
        int mejorTiempo = Integer.MAX_VALUE;
        String mejorArista = null;
        int pesoMejorArista = 0;
        ResultadoRuta mejorRutaRecuperada = null;

        int n = grafo.getCantidadNodos();
        for (int u = 0; u < n; u++) {
            Arista actual = grafo.getAdyacentes(u);
            while (actual != null) {
                int v = actual.destino;
                // Revisamos cada arista bloqueada una sola vez (u < v)
                if (actual.bloqueada && u < v) {
                    String nombreU = grafo.getNombreNodo(u);
                    String nombreV = grafo.getNombreNodo(v);

                    grafo.reabrirArista(nombreU, nombreV);
                    ResultadoRuta candidata = AlgoritmosGrafo.dijkstraMejorSalida(grafo, habitacionOrigen);
                    grafo.bloquearArista(nombreU, nombreV);

                    if (candidata != null && candidata.tiempoTotal < mejorTiempo) {
                        mejorTiempo = candidata.tiempoTotal;
                        mejorArista = nombreU + "-" + nombreV;
                        pesoMejorArista = actual.segundos;
                        mejorRutaRecuperada = candidata;
                    }
                }
                actual = actual.siguiente;
            }
        }

        if (mejorRutaRecuperada == null) {
            return "Sin ruta desde " + habitacionOrigen
                    + ". Se requiere reabrir múltiples conexiones bloqueadas.";
        }

        return "Sin ruta desde " + habitacionOrigen + ". [IA] Alternativa concreta: reabrir "
                + mejorArista + " (" + pesoMejorArista + " s) y salir por "
                + mejorRutaRecuperada.salida + " en " + mejorRutaRecuperada.tiempoTotal
                + " s (" + mejorRutaRecuperada.caminoFormateado() + ").";
    }
}