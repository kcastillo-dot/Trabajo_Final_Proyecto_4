package estructura.arboles;

/**
 * Árbol binario de decisión que evalúa las 3 preguntas obligatorias antes de
 * mover a una persona:
 * 1. ¿La habitación está en el edificio?
 * 2. ¿Se alcanza EXIT_A o EXIT_B?
 * 3. ¿La escalera elegida tiene gente esperando?
 */
public class ArbolDecisionEvacuacion {

    public enum Accion {
        HABITACION_NO_EXISTE,
        SIN_RUTA_REABRIR,
        ESPERAR_TURNO_COLA,
        AVANZAR_PASO
    }

    private static class NodoDecision {
        String pregunta;
        Accion accionFinal;
        NodoDecision ramaSi;
        NodoDecision ramaNo;

        NodoDecision(String pregunta) {
            this.pregunta = pregunta;
        }

        NodoDecision(Accion accionFinal) {
            this.accionFinal = accionFinal;
        }

        boolean esHoja() {
            return accionFinal != null;
        }
    }

    private final NodoDecision raiz;

    public ArbolDecisionEvacuacion() {
        raiz = new NodoDecision("¿La habitación está en el edificio?");
        raiz.ramaNo = new NodoDecision(Accion.HABITACION_NO_EXISTE);

        NodoDecision qRuta = new NodoDecision("¿Se alcanza EXIT_A o EXIT_B?");
        raiz.ramaSi = qRuta;
        qRuta.ramaNo = new NodoDecision(Accion.SIN_RUTA_REABRIR);

        NodoDecision qCola = new NodoDecision("¿La escalera elegida tiene gente esperando?");
        qRuta.ramaSi = qCola;
        qCola.ramaSi = new NodoDecision(Accion.ESPERAR_TURNO_COLA);
        qCola.ramaNo = new NodoDecision(Accion.AVANZAR_PASO);
    }

    public Accion evaluar(boolean existeHabitacion, boolean hayRuta, boolean escaleraConCola) {
        NodoDecision actual = raiz;
        actual = existeHabitacion ? actual.ramaSi : actual.ramaNo;
        if (actual.esHoja())
            return actual.accionFinal;

        actual = hayRuta ? actual.ramaSi : actual.ramaNo;
        if (actual.esHoja())
            return actual.accionFinal;

        actual = escaleraConCola ? actual.ramaSi : actual.ramaNo;
        return actual.accionFinal;
    }
}