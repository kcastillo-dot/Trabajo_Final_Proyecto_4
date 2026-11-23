package grafo;

/**
 * Grafo ponderado no dirigido mediante listas de adyacencia propias.
 * Permite bloquear y reabrir aristas sin eliminarlas del edificio.
 */
public class GrafoEdificio {
    private final String[] nombres;
    private final Arista[] adyacencia;
    private int cantidadNodos;

    public GrafoEdificio(int capacidadMaxima) {
        this.nombres = new String[capacidadMaxima];
        this.adyacencia = new Arista[capacidadMaxima];
        this.cantidadNodos = 0;
    }

    public void agregarNodo(String nombre) {
        if (indiceDe(nombre) != -1)
            return;
        nombres[cantidadNodos++] = nombre;
    }

    public void agregarArista(String origen, String destino, int segundos) {
        int u = indiceDe(origen);
        int v = indiceDe(destino);
        if (u == -1 || v == -1)
            return;

        insertarAristaDirigida(u, v, segundos);
        insertarAristaDirigida(v, u, segundos);
    }

    private void insertarAristaDirigida(int u, int v, int segundos) {
        Arista nueva = new Arista(v, segundos);
        if (adyacencia[u] == null) {
            adyacencia[u] = nueva;
        } else {
            Arista actual = adyacencia[u];
            while (actual.siguiente != null)
                actual = actual.siguiente;
            actual.siguiente = nueva;
        }
    }

    public boolean bloquearArista(String a, String b) {
        return cambiarEstadoBloqueo(a, b, true);
    }

    public boolean reabrirArista(String a, String b) {
        return cambiarEstadoBloqueo(a, b, false);
    }

    private boolean cambiarEstadoBloqueo(String a, String b, boolean bloquear) {
        int u = indiceDe(a);
        int v = indiceDe(b);
        if (u == -1 || v == -1)
            return false;
        boolean ok1 = marcarSentido(u, v, bloquear);
        boolean ok2 = marcarSentido(v, u, bloquear);
        return ok1 && ok2;
    }

    private boolean marcarSentido(int u, int v, boolean bloquear) {
        Arista actual = adyacencia[u];
        while (actual != null) {
            if (actual.destino == v) {
                actual.bloqueada = bloquear;
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    public boolean existeNodo(String nombre) {
        return indiceDe(nombre) != -1;
    }

    public int getCantidadNodos() {
        return cantidadNodos;
    }

    public String getNombreNodo(int indice) {
        return nombres[indice];
    }

    public Arista getAdyacentes(int indice) {
        return adyacencia[indice];
    }

    public int indiceDe(String nombre) {
        for (int i = 0; i < cantidadNodos; i++) {
            if (nombres[i].equals(nombre))
                return i;
        }
        return -1;
    }

    public int pesoEntre(int u, int v) {
        Arista actual = adyacencia[u];
        while (actual != null) {
            if (actual.destino == v && !actual.bloqueada)
                return actual.segundos;
            actual = actual.siguiente;
        }
        return -1;
    }
}
