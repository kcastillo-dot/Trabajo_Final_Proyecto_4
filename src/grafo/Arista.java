package grafo;

public class Arista {
    public final int destino;
    public final int segundos;
    public boolean bloqueada;
    public Arista siguiente;

    public Arista(int destino, int segundos) {
        this.destino = destino;
        this.segundos = segundos;
        this.bloqueada = false;
        this.siguiente = null;
    }
}