package estructura.lineales;

public class MiCola<T> {

    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamano;

    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (fin == null) {
            frente = fin = nuevo;
        } else {
            fin.siguiente = nuevo;
            fin = nuevo;
        }
        tamano++;
    }

    public T desencolar() {
        if (estaVacia())
            return null;
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null)
            fin = null;
        tamano--;
        return dato;
    }

    public T verFrente() {
        return estaVacia() ? null : frente.dato;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int getTamano() {
        return tamano;
    }
}