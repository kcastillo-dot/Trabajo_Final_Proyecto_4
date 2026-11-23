package grafo;

import estructura.lineales.MiPila;

public class ResultadoRuta {
    public final String[] nodos;
    public final int pasos;
    public final int tiempoTotal;
    public final String salida;

    public ResultadoRuta(String[] nodos, int tiempoTotal) {
        this.nodos = nodos;
        this.pasos = nodos.length - 1;
        this.tiempoTotal = tiempoTotal;
        this.salida = nodos[nodos.length - 1];
    }

    /**
     * Apila las instrucciones desde la salida hacia el origen
     * para que al desapilar salgan en el orden exacto de caminata.
     */
    public MiPila<String> construirPilaInstrucciones() {
        MiPila<String> pila = new MiPila<>();
        for (int i = nodos.length - 1; i >= 1; i--) {
            String desde = nodos[i - 1];
            String hacia = nodos[i];
            String texto;
            if (hacia.startsWith("EXIT_")) {
                texto = "Salga por " + hacia;
            } else if (hacia.startsWith("ESC_")) {
                texto = "Tome " + hacia;
            } else {
                texto = "Salga de " + desde + " hacia " + hacia;
            }
            pila.apilar(texto);
        }
        return pila;
    }

    public boolean usaEscalera(String escalera) {
        for (String n : nodos) {
            if (n.equals(escalera)) return true;
        }
        return false;
    }

    public String caminoFormateado() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodos.length; i++) {
            sb.append(nodos[i]);
            if (i < nodos.length - 1) sb.append(" -> ");
        }
        return sb.toString();
    }
}
