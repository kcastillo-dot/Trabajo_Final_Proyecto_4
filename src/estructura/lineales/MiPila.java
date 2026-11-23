package estructura.lineales;

public class MiPila<T> {

    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo<T> cima;
    private int tamano;

    public void apilar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = cima;
        cima = nuevo;
        tamano++;
    }

    public T desapilar() {
        if (estaVacia())
            return null;
        T dato = cima.dato;
        cima = cima.siguiente;
        tamano--;
        return dato;
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int getTamano() {
        return tamano;
    }
}