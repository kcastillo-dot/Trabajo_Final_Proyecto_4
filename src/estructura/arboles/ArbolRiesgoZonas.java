package estructura.arboles;

/**
 * Árbol Binario de Búsqueda (ABB) ordenado por nivel de riesgo usando
 * ZonaRiesgo.
 * Orden de inserción oficial: 5, 2, 8, 1, 3, 7, 9.
 */
public class ArbolRiesgoZonas {

    private static class NodoZona {
        final ZonaRiesgo zona;
        NodoZona izquierdo;
        NodoZona derecho;

        NodoZona(ZonaRiesgo zona) {
            this.zona = zona;
        }
    }

    private NodoZona raiz;

    public void insertar(int riesgo, String nombre) {
        insertar(new ZonaRiesgo(riesgo, nombre));
    }

    public void insertar(ZonaRiesgo zona) {
        raiz = insertarRec(raiz, zona);
    }

    private NodoZona insertarRec(NodoZona actual, ZonaRiesgo zona) {
        if (actual == null)
            return new NodoZona(zona);
        if (zona.getNivelRiesgo() < actual.zona.getNivelRiesgo()) {
            actual.izquierdo = insertarRec(actual.izquierdo, zona);
        } else if (zona.getNivelRiesgo() > actual.zona.getNivelRiesgo()) {
            actual.derecho = insertarRec(actual.derecho, zona);
        }
        return actual;
    }

    public String recorridoInorden() {
        StringBuilder sb = new StringBuilder();
        inordenRec(raiz, sb);
        return sb.toString().trim();
    }

    private void inordenRec(NodoZona nodo, StringBuilder sb) {
        if (nodo == null)
            return;
        inordenRec(nodo.izquierdo, sb);
        sb.append(nodo.zona).append(" ");
        inordenRec(nodo.derecho, sb);
    }

    public int contarNodos() {
        return contarNodosRec(raiz);
    }

    private int contarNodosRec(NodoZona n) {
        return (n == null) ? 0 : 1 + contarNodosRec(n.izquierdo) + contarNodosRec(n.derecho);
    }

    /** Peso (número de aristas del árbol = nodos - 1). */
    public int getPeso() {
        int total = contarNodos();
        return total > 0 ? total - 1 : 0;
    }

    public int getAltura() {
        return alturaRec(raiz);
    }

    private int alturaRec(NodoZona n) {
        if (n == null)
            return -1;
        int izq = alturaRec(n.izquierdo);
        int der = alturaRec(n.derecho);
        return 1 + (izq > der ? izq : der);
    }

    public int contarHojas() {
        return contarHojasRec(raiz);
    }

    private int contarHojasRec(NodoZona n) {
        if (n == null)
            return 0;
        if (n.izquierdo == null && n.derecho == null)
            return 1;
        return contarHojasRec(n.izquierdo) + contarHojasRec(n.derecho);
    }

    public boolean esCompleto() {
        return esCompletoRec(raiz);
    }

    private boolean esCompletoRec(NodoZona n) {
        if (n == null)
            return true;
        if (n.izquierdo == null && n.derecho == null)
            return true;
        if (n.izquierdo != null && n.derecho != null) {
            return esCompletoRec(n.izquierdo) && esCompletoRec(n.derecho);
        }
        return false;
    }

    /** Baja siempre a la derecha para obtener la zona más peligrosa (Bodega). */
    public ZonaRiesgo obtenerMasPeligrosa() {
        if (raiz == null)
            return null;
        NodoZona actual = raiz;
        while (actual.derecho != null) {
            actual = actual.derecho;
        }
        return actual.zona;
    }

    /**
     * Devuelve la traza de búsqueda, nivel y grado del nodo buscado (ej. riesgo 7).
     */
    public String buscarConTraza(int riesgoObjetivo) {
        StringBuilder traza = new StringBuilder();
        NodoZona actual = raiz;
        int nivel = 0;

        while (actual != null) {
            int riesgoActual = actual.zona.getNivelRiesgo();
            traza.append(riesgoActual);
            if (riesgoObjetivo == riesgoActual) {
                int grado = (actual.izquierdo != null ? 1 : 0) + (actual.derecho != null ? 1 : 0);
                return "Recorrido: " + traza + " -> " + actual.zona.getNombre()
                        + " (" + actual.zona.getCategoria() + ", nivel " + nivel + ", grado " + grado + ")";
            }
            traza.append(" -> ");
            actual = (riesgoObjetivo < riesgoActual) ? actual.izquierdo : actual.derecho;
            nivel++;
        }
        return "Riesgo " + riesgoObjetivo + " no encontrado.";
    }
}