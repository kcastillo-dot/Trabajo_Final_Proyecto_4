package estructura.lineales;

public class Persona {

    private final String id;
    private final String nombre;
    private final int piso;
    private final String habitacion;
    private boolean evacuado;

    public Persona(String id, String nombre, int piso, String habitacion) {
        this.id = id;
        this.nombre = nombre;
        this.piso = piso;
        this.habitacion = habitacion;
        this.evacuado = false;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPiso() {
        return piso;
    }

    public String getHabitacion() {
        return habitacion;
    }

    public boolean isEvacuado() {
        return evacuado;
    }

    public void marcarEvacuado() {
        this.evacuado = true;
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (" + habitacion + ", Piso " + piso + ") ["
                + (evacuado ? "EVACUADA" : "EN EDIFICIO") + "]";
    }
}
