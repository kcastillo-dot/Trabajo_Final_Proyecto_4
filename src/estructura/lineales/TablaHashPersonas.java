package estructura.lineales;

public class TablaHashPersonas {

    private static class NodoCubeta {
        Persona persona;
        NodoCubeta siguiente;

        NodoCubeta(Persona persona) {
            this.persona = persona;
        }
    }

    private static final int NUM_CUBETAS = 5;
    private final NodoCubeta[] cubetas;

    public TablaHashPersonas() {
        this.cubetas = new NodoCubeta[NUM_CUBETAS];
    }

    public int calcularCubeta(String id) {
        int suma = 0;
        for (int i = 0; i < id.length(); i++) {
            suma += id.charAt(i);
        }
        return suma % NUM_CUBETAS;
    }

    public void insertar(Persona persona) {
        int indice = calcularCubeta(persona.getId());
        NodoCubeta nuevo = new NodoCubeta(persona);
        if (cubetas[indice] == null) {
            cubetas[indice] = nuevo;
        } else {
            NodoCubeta actual = cubetas[indice];
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
    }

    public Persona buscar(String id) {
        int indice = calcularCubeta(id);
        NodoCubeta actual = cubetas[indice];
        while (actual != null) {
            if (actual.persona.getId().equals(id)) {
                return actual.persona;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    public String mostrarCubeta(int indice) {
        StringBuilder sb = new StringBuilder("Cubeta " + indice + ": ");
        NodoCubeta actual = cubetas[indice];
        if (actual == null)
            return sb.append("(vacía)").toString();
        while (actual != null) {
            sb.append(actual.persona.getId()).append(" ").append(actual.persona.getNombre());
            if (actual.siguiente != null)
                sb.append(" -> ");
            actual = actual.siguiente;
        }
        return sb.toString();
    }
}