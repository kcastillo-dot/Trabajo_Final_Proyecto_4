package estructura.lineales;

public class ListaDobleOcupantes {
    private static class NodoDoble {
        Persona persona;
        NodoDoble anterior;
        NodoDoble siguiente;

        NodoDoble(Persona persona) {
            this.persona = persona;
        }
    }

    private NodoDoble cabeza;
    private NodoDoble cola;

    public void insertarAlFinal(Persona persona) {
        NodoDoble nuevo = new NodoDoble(persona);
        if (cabeza == null) {
            cabeza = cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            nuevo.anterior = cola;
            cola = nuevo;
        }
    }

    public boolean eliminarPorId(String id) {
        NodoDoble actual = cabeza;
        while (actual != null) {
            if (actual.persona.getId().equals(id)) {
                if (actual.anterior != null) {
                    actual.anterior.siguiente = actual.siguiente;
                } else {
                    cabeza = actual.siguiente;
                }
                if (actual.siguiente != null) {
                    actual.siguiente.anterior = actual.anterior;
                } else {
                    cola = actual.anterior;
                }
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    public String listarIda() {
        StringBuilder sb = new StringBuilder();
        NodoDoble actual = cabeza;
        while (actual != null) {
            sb.append(actual.persona.getNombre());
            if (actual.siguiente != null)
                sb.append(" -> ");
            actual = actual.siguiente;
        }
        return sb.toString();
    }

    public String listarRegreso() {
        StringBuilder sb = new StringBuilder();
        NodoDoble actual = cola;
        while (actual != null) {
            sb.append(actual.persona.getNombre());
            if (actual.anterior != null)
                sb.append(" -> ");
            actual = actual.anterior;
        }
        return sb.toString();
    }
}
