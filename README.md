# 🏢 Simulador de Rutas de Evacuación en Edificios

Proyecto final para la asignatura **Estructura de Datos (Proyecto 4)**. El sistema modela y simula la evacuación de emergencia de un edificio de dos pisos (12 ubicaciones y 14 conexiones ponderadas en segundos), incorporando bloqueo reversible por humo en tiempo real, atención por turnos en puertas estrechas, análisis de zonas de riesgo, un módulo de **IA de recuperación de rutas** y una **interfaz gráfica 2D interactiva**.

> **Restricción académica cumplida al 100%:** Todas las estructuras de datos (lista doblemente enlazada, cola, pila, tabla hash, árboles binarios y grafo por listas de adyacencia) fueron implementadas desde cero con nodos propios, sin utilizar colecciones nativas de `java.util`.

---

## 🎥 Video de Sustentación y Manual de Uso

* **Enlace en YouTube (duración ≥ 7 min):** `[PEGAR AQUÍ EL LINK DEL VIDEO DE YOUTUBE]`
* **Contenido del video:** Demostración de la interfaz gráfica y bloqueo por humo en vivo, recorrido por las estructuras lineales y tabla hash, explicación del ABB de zonas e IA de decisión, comparación BFS vs. Dijkstra en `H101` y verificación de Prim/Kruskal.

---

## 🧩 Estructuras de Datos y Algoritmos Implementados

| Estructura / Algoritmo | Clase(s) | Función dentro de la simulación |
| :--- | :--- | :--- |
| **Lista Doblemente Enlazada** | `ListaDobleOcupantes` | Gestiona los ocupantes del Piso 2 (`Nora`, `Teo`, `Lina`, `Omar`), permitiendo recorrido de ida/regreso y retiro sin huecos. |
| **Cola (FIFO) sobre lista simple** | `MiCola<T>` | Administra los turnos de salida en la puerta estrecha de `ESC_A` (`Sara`, `Iker`, `Vera`) y sirve de soporte para BFS. |
| **Pila (LIFO) sobre lista simple** | `MiPila<T>` | Almacena los pasos de la ruta apilados desde la salida hacia el origen para desapilar en el orden exacto de caminata. |
| **Tabla Hash (5 cubetas)** | `TablaHashPersonas` | Localiza ocupantes por ID usando suma ASCII módulo 5 con encadenamiento simple (colisión verificada en cubeta 2: `A01` y `A06`). |
| **Árbol Binario de Búsqueda (ABB)** | `ArbolRiesgoZonas`, `ZonaRiesgo` | Ordena las 7 zonas del edificio por riesgo (`1` a `9`). El inorden entrega el reporte ordenado y el extremo derecho identifica la zona más peligrosa (`Bodega`). |
| **Árbol Binario de Decisión** | `ArbolDecisionEvacuacion` | Evalúa en cada turno si la habitación existe, si hay ruta hacia alguna salida y si la escalera tiene cola antes de autorizar un paso. |
| **IA de Diagnóstico (Punto Extra)** | `AsesorIAEvacuacion` | Ante habitaciones aisladas por humo, simula la reapertura de cada arista bloqueada real del grafo y propone la intervención de menor tiempo. |
| **Grafo Ponderado + Recorridos** | `GrafoEdificio`, `AlgoritmosGrafo` | Representación por listas de adyacencia con bloqueo reversible. Incluye **Dijkstra** (menor tiempo), **BFS** (menor pasos), **Prim** y **Kruskal** (red mínima). |

---

## 🗂️ Estructura del Repositorio

```text
evacuacion-edificio-ed/
├── README.md                              # Documentación principal del proyecto
├── evacuacion.txt                         # Registro generado al evacuar habitaciones
└── src/
    ├── Main.java                          # Punto de entrada de la aplicación
    ├── estructuras/
    │   ├── lineales/                      # Módulo Integrante 1
    │   │   ├── Persona.java
    │   │   ├── ListaDobleOcupantes.java
    │   │   ├── MiCola.java
    │   │   ├── MiPila.java
    │   │   └── TablaHashPersonas.java
    │   └── arboles/                       # Módulo Integrante 2
    │       ├── ZonaRiesgo.java
    │       ├── ArbolRiesgoZonas.java
    │       ├── ArbolDecisionEvacuacion.java
    │       └── AsesorIAEvacuacion.java
    ├── grafo/                             # Módulo Integrante 3
    │   ├── Arista.java
    │   ├── ResultadoRuta.java
    │   ├── GrafoEdificio.java
    │   └── AlgoritmosGrafo.java
    ├── servicio/                          # Módulo Integrante 4
    │   └── SimuladorEvacuacion.java
    └── vista/                             # Módulo Integrante 4
        ├── EstilosUI.java
        ├── PanelMapaEdificio.java
        └── VentanaPrincipal.java-Karen Castillo:Lo que hice fue hacer las bases del codigo y completar mi parte de completar todo el codigo lineal 