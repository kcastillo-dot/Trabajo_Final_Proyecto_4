package estructura.arboles;

/**
 * Representa una zona del edificio con su nivel numérico de riesgo (1 a 9).
 * Se utiliza como objeto de dominio dentro del Árbol Binario de Búsqueda.
 */
public class ZonaRiesgo {
    private final int nivelRiesgo;
    private final String nombre;

    public ZonaRiesgo(int nivelRiesgo, String nombre) {
        this.nivelRiesgo = nivelRiesgo;
        this.nombre = nombre;
    }

    public int getNivelRiesgo() {
        return nivelRiesgo;
    }

    public String getNombre() {
        return nombre;
    }

    /** Clasificación útil para los reportes del informe y la interfaz gráfica. */
    public String getCategoria() {
        if (nivelRiesgo <= 3)
            return "BAJO";
        if (nivelRiesgo <= 6)
            return "MEDIO";
        return "ALTO";
    }

    @Override
    public String toString() {
        return "[" + nivelRiesgo + " " + nombre + "]";
    }
}