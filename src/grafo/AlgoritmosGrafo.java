package grafo;

import estructura.lineales.MiCola;

/**
 * Algoritmos de recorrido y árboles de expansión mínima del edificio:
 * Dijkstra (menor tiempo), BFS (menor número de pasos), Prim y Kruskal.
 */
public class AlgoritmosGrafo {

    public static ResultadoRuta dijkstraHasta(GrafoEdificio g, String origen, String destino) {
        int n = g.getCantidadNodos();
        int idxOrigen = g.indiceDe(origen);
        int idxDestino = g.indiceDe(destino);
        if (idxOrigen == -1 || idxDestino == -1)
            return null;

        int[] dist = new int[n];
        int[] previo = new int[n];
        boolean[] visitado = new boolean[n];

        for (int i = 0; i < n; i++) {
            dist[i] = Integer.MAX_VALUE;
            previo[i] = -1;
        }
        dist[idxOrigen] = 0;

        for (int iter = 0; iter < n; iter++) {
            int u = -1;
            int minDist = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (!visitado[i] && dist[i] < minDist) {
                    minDist = dist[i];
                    u = i;
                }
            }
            if (u == -1)
                break;
            visitado[u] = true;

            Arista arista = g.getAdyacentes(u);
            while (arista != null) {
                if (!arista.bloqueada && !visitado[arista.destino]) {
                    int nuevaDist = dist[u] + arista.segundos;
                    if (nuevaDist < dist[arista.destino]) {
                        dist[arista.destino] = nuevaDist;
                        previo[arista.destino] = u;
                    }
                }
                arista = arista.siguiente;
            }
        }

        if (dist[idxDestino] == Integer.MAX_VALUE)
            return null;
        return construirResultado(g, previo, idxDestino, dist[idxDestino]);
    }

    public static ResultadoRuta dijkstraMejorSalida(GrafoEdificio g, String origen) {
        ResultadoRuta a = dijkstraHasta(g, origen, "EXIT_A");
        ResultadoRuta b = dijkstraHasta(g, origen, "EXIT_B");
        if (a == null)
            return b;
        if (b == null)
            return a;
        return (a.tiempoTotal <= b.tiempoTotal) ? a : b;
    }

    /**
     * Búsqueda por niveles (BFS): minimiza el número de pasos y desempata por
     * tiempo.
     */
    public static ResultadoRuta bfsMejorSalida(GrafoEdificio g, String origen) {
        ResultadoRuta a = bfsHasta(g, origen, "EXIT_A");
        ResultadoRuta b = bfsHasta(g, origen, "EXIT_B");
        if (a == null)
            return b;
        if (b == null)
            return a;
        if (a.pasos != b.pasos)
            return (a.pasos < b.pasos) ? a : b;
        return (a.tiempoTotal <= b.tiempoTotal) ? a : b;
    }

    private static ResultadoRuta bfsHasta(GrafoEdificio g, String origen, String destino) {
        int n = g.getCantidadNodos();
        int idxOrigen = g.indiceDe(origen);
        int idxDestino = g.indiceDe(destino);
        if (idxOrigen == -1 || idxDestino == -1)
            return null;

        boolean[] visitado = new boolean[n];
        int[] previo = new int[n];
        for (int i = 0; i < n; i++)
            previo[i] = -1;

        MiCola<Integer> cola = new MiCola<>();
        visitado[idxOrigen] = true;
        cola.encolar(idxOrigen);

        while (!cola.estaVacia()) {
            int u = cola.desencolar();
            if (u == idxDestino)
                break;

            Arista arista = g.getAdyacentes(u);
            while (arista != null) {
                if (!arista.bloqueada && !visitado[arista.destino]) {
                    visitado[arista.destino] = true;
                    previo[arista.destino] = u;
                    cola.encolar(arista.destino);
                }
                arista = arista.siguiente;
            }
        }

        if (!visitado[idxDestino])
            return null;
        int tiempo = calcularTiempoCamino(g, previo, idxDestino);
        return construirResultado(g, previo, idxDestino, tiempo);
    }

    /**
     * Prim: conecta todos los lugares del edificio al menor tiempo total (34 s).
     */
    public static int calcularPrim(GrafoEdificio g, StringBuilder detalle) {
        int n = g.getCantidadNodos();
        int[] clave = new int[n];
        int[] padre = new int[n];
        boolean[] enMst = new boolean[n];

        for (int i = 0; i < n; i++) {
            clave[i] = Integer.MAX_VALUE;
            padre[i] = -1;
        }
        clave[0] = 0;

        for (int count = 0; count < n; count++) {
            int u = -1;
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (!enMst[i] && clave[i] < min) {
                    min = clave[i];
                    u = i;
                }
            }
            if (u == -1)
                break;
            enMst[u] = true;

            Arista arista = g.getAdyacentes(u);
            while (arista != null) {
                int v = arista.destino;
                if (!arista.bloqueada && !enMst[v] && arista.segundos < clave[v]) {
                    clave[v] = arista.segundos;
                    padre[v] = u;
                }
                arista = arista.siguiente;
            }
        }

        int costoTotal = 0;
        for (int i = 1; i < n; i++) {
            if (padre[i] != -1) {
                costoTotal += clave[i];
                if (detalle != null) {
                    detalle.append(g.getNombreNodo(padre[i])).append("-")
                            .append(g.getNombreNodo(i)).append(" (").append(clave[i]).append("s), ");
                }
            }
        }
        return costoTotal;
    }

    /**
     * Kruskal con ordenamiento propio y Union-Find: debe coincidir en 34 s con
     * Prim.
     */
    public static int calcularKruskal(GrafoEdificio g) {
        int n = g.getCantidadNodos();
        int[][] aristas = new int[n * n][3]; // [u, v, peso]
        int totalAristas = 0;

        for (int u = 0; u < n; u++) {
            Arista actual = g.getAdyacentes(u);
            while (actual != null) {
                if (!actual.bloqueada && u < actual.destino) {
                    aristas[totalAristas][0] = u;
                    aristas[totalAristas][1] = actual.destino;
                    aristas[totalAristas][2] = actual.segundos;
                    totalAristas++;
                }
                actual = actual.siguiente;
            }
        }

        // Ordenamiento por inserción según peso
        for (int i = 1; i < totalAristas; i++) {
            int[] clave = aristas[i];
            int j = i - 1;
            while (j >= 0 && aristas[j][2] > clave[2]) {
                aristas[j + 1] = aristas[j];
                j--;
            }
            aristas[j + 1] = clave;
        }

        int[] padre = new int[n];
        for (int i = 0; i < n; i++)
            padre[i] = i;

        int costoTotal = 0;
        for (int i = 0; i < totalAristas; i++) {
            int raizU = encontrarRaiz(padre, aristas[i][0]);
            int raizV = encontrarRaiz(padre, aristas[i][1]);
            if (raizU != raizV) {
                padre[raizU] = raizV;
                costoTotal += aristas[i][2];
            }
        }
        return costoTotal;
    }

    private static int encontrarRaiz(int[] padre, int x) {
        while (padre[x] != x)
            x = padre[x];
        return x;
    }

    private static int calcularTiempoCamino(GrafoEdificio g, int[] previo, int destino) {
        int total = 0;
        int actual = destino;
        while (previo[actual] != -1) {
            total += g.pesoEntre(previo[actual], actual);
            actual = previo[actual];
        }
        return total;
    }

    private static ResultadoRuta construirResultado(GrafoEdificio g, int[] previo, int destino, int tiempo) {
        int longitud = 0;
        for (int x = destino; x != -1; x = previo[x])
            longitud++;

        String[] camino = new String[longitud];
        int actual = destino;
        for (int i = longitud - 1; i >= 0; i--) {
            camino[i] = g.getNombreNodo(actual);
            actual = previo[actual];
        }
        return new ResultadoRuta(camino, tiempo);
    }
}