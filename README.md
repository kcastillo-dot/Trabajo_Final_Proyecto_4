# 🏢 Simulador de Rutas de Evacuación en Edificios

Proyecto final para la asignatura **Estructura de Datos (Proyecto 4)**. El sistema modela y simula la evacuación de emergencia de un edificio de dos pisos (12 ubicaciones y 14 conexiones ponderadas en segundos), incorporando bloqueo reversible por humo en tiempo real, atención por turnos en puertas estrechas, análisis de zonas de riesgo, un módulo de **IA de recuperación de rutas** y una **interfaz gráfica 2D interactiva**.

> **Restricción académica cumplida al 100%:** Todas las estructuras de datos (lista doblemente enlazada, cola, pila, tabla hash, árboles binarios y grafo por listas de adyacencia) fueron implementadas desde cero con nodos propios, sin utilizar colecciones nativas de `java.util`.

---

## 🎥 Video de Sustentación y Manual de Uso

* **Enlace en YouTube (duración ≥ 7 min):** `[PEGAR AQUÍ EL LINK DEL VIDEO DE YOUTUBE]`
* **Contenido del video:** Demostración de la interfaz gráfica y bloqueo por humo en vivo, recorrido por las estructuras lineales y tabla hash, explicación del ABB de zonas e IA de decisión, comparación BFS vs. Dijkstra en `H101` y verificación de Prim/Kruskal.

---

## ⚙️ Cómo Funciona el Código (Arquitectura y Flujo Interno)

El sistema sigue una **arquitectura modular en capas** que separa la interfaz gráfica, la lógica de orquestación, los algoritmos de grafos y las estructuras de datos propias:

```text
[Vista: Java Swing 2D]  ──▶  [Servicio: SimuladorEvacuacion]  ──▶  [Árboles e IA] / [Grafo] / [Estructuras Lineales]
```

### 1. Inicialización del escenario (`Main` → `SimuladorEvacuacion`)
Al iniciar `Main.java`, se instancia `VentanaPrincipal`, la cual crea un objeto `SimuladorEvacuacion`. Su constructor ejecuta `cargarDatosOficiales()`, encargándose de:
1. Crear los **12 nodos** y las **14 aristas bidireccionales** en `GrafoEdificio` mediante listas de adyacencia propias.
2. Registrar a las **8 personas** en `TablaHashPersonas` (5 cubetas con encadenamiento simple).
3. Poblar la lista doble `ListaDobleOcupantes` con el orden inicial del Piso 2 (`Nora`, `Teo`, `Lina`, `Omar`).
4. Encolar en `MiCola` a quienes esperan en la puerta estrecha de `ESC_A` (`Sara`, `Iker`, `Vera`).
5. Insertar las 7 zonas de riesgo en `ArbolRiesgoZonas` en el orden exacto `5, 2, 8, 1, 3, 7, 9`.

### 2. Flujo interno al evacuar una habitación (`evaluarEvacuacion`)
Cada vez que el usuario ingresa una habitación y presiona **"Evacuar (IA + Pila)"**, el código ejecuta la siguiente cadena de pasos:
1. **Validación en el grafo:** Consulta `edificio.existeNodo(habitacion)` y, si existe, ejecuta `AlgoritmosGrafo.dijkstraMejorSalida(...)` comparando el costo en segundos hacia `EXIT_A` y `EXIT_B` (ignorando las aristas cuya bandera `bloqueada` sea `true`).
2. **Evaluación en el Árbol de Decisión (`ArbolDecisionEvacuacion`):** Recorre sus 3 niveles de decisión binaria:
   * *¿Existe la habitación?* → Si es `false`, retorna `HABITACION_NO_EXISTE` (ej. `H999`).
   * *¿Existe ruta a alguna salida?* → Si es `false`, retorna `SIN_RUTA_REABRIR` e invoca a `AsesorIAEvacuacion`.
   * *¿La ruta usa `ESC_A` y hay cola esperando?* → Si es `true`, retorna `ESPERAR_TURNO_COLA`, retira a la persona al frente de `MiCola` y la marca como `EVACUADA` en la tabla hash.
   * *En caso despejado:* Retorna `AVANZAR_PASO`.
3. **Generación de instrucciones en orden de caminata (`MiPila`):** `ResultadoRuta.construirPilaInstrucciones()` recorre el camino **desde la salida hacia la habitación de origen** y apila cada instrucción. Así, al desapilar (`desapilar()`), el primer paso entregado es salir de la habitación y el último es cruzar la salida.
4. **Renderizado 2D y registro:** `PanelMapaEdificio` redibuja el grafo con `Graphics2D` iluminando la ruta en verde (o comparando Dijkstra en verde vs. BFS en cian) y `SimuladorEvacuacion` guarda la salida en `evacuacion.txt`.

### 3. Mecánicas clave implementadas desde cero
* **Bloqueo por humo reversible (`GrafoEdificio`):** Bloquear una arista no la elimina de la memoria; solo cambia `arista.bloqueada = true` en ambos sentidos (`u -> v` y `v -> u`). Al reabrirla vuelve a `false`, permitiendo pruebas en vivo sin reconstruir el edificio.
* **IA de Diagnóstico (`AsesorIAEvacuacion`):** Cuando una habitación queda aislada (ej. `H203` al bloquear `H203-P2`), la IA recorre las aristas bloqueadas reales del grafo, simula temporalmente reabrir cada una, ejecuta Dijkstra y selecciona la reapertura que logra el menor tiempo real de evacuación sin inventar conexiones ficticias.
* **Tabla Hash con colisión controlada (`TablaHashPersonas`):** Calcula la cubeta sumando el código ASCII de cada carácter del ID y aplicando `% 5`. Si dos IDs caen en la misma cubeta (`A01` = 162 % 5 = 2 y `A06` = 167 % 5 = 2), se encadenan en una lista enlazada simple dentro de esa cubeta.
* **Árboles de expansión mínima (`AlgoritmosGrafo`):** `calcularPrim` selecciona iterativamente el vértice no incluido de menor costo; `calcularKruskal` extrae las aristas despejadas en un arreglo propio, las ordena por inserción y detecta ciclos con un arreglo de padres (*Union-Find*).

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
        └── VentanaPrincipal.java
```

---

## 👥 Integrantes y División del Trabajo (Git Flow)

El desarrollo se dividió en 4 ramas independientes integradas mediante *Pull Requests* hacia `develop` y `main`:

| Integrante | Usuario GitHub | Rama | Responsabilidad |
| :--- | :--- | :--- | :--- |
| `Karen Castillo` | `@kcastillo-dot` | `feature/estructuras-lineales` | `Persona`, `ListaDobleOcupantes`, `MiCola`, `MiPila` y `TablaHashPersonas`. |
| `Alex Santiago Perez Illera` | `@ASPERI2026 - @aillera91` | `feature/arboles-ia` | `ZonaRiesgo`, `ArbolRiesgoZonas`, `ArbolDecisionEvacuacion` y `AsesorIAEvacuacion`. |
| `Nicole Henao` | `@nicoval18` | `feature/grafo-algoritmos` | `GrafoEdificio`, `Arista`, `ResultadoRuta`, Dijkstra, BFS, Prim y Kruskal. |
| `Juan Jose Hoyos` | `@jjhoyosc-tech` | `feature/gui-integracion` | `SimuladorEvacuacion`, interfaz Swing 2D (`EstilosUI`, `PanelMapaEdificio`, `VentanaPrincipal`), `evacuacion.txt` . |

---

## 🚀 Guía Paso a Paso para Ejecutar el Proyecto

### 1. Requisitos previos
Verifica que tienes instalado **Java JDK 17 o superior** (requerido por las expresiones `switch` modernas) y **Git**:

```bash
java -version
javac -version
```

> Si alguno de los comandos no responde, instala el **JDK 17+** (OpenJDK / Temurin / Oracle JDK) y agrega la carpeta `bin` del JDK a la variable de entorno `PATH`. El proyecto no requiere librerías externas ni Maven/Gradle.

### 2. Clonar el repositorio

```bash
git clone [URL_DEL_REPOSITORIO]
cd evacuacion-edificio-ed
```

### 3. Opción A: Compilar y ejecutar desde la Terminal

#### En Windows (PowerShell o CMD)
1. Crea la carpeta de salida para los archivos compilados:
   ```powershell
   mkdir bin
   ```
2. Compila todo el proyecto con codificación UTF-8 (evita errores con tildes y símbolos):
   ```powershell
   javac -encoding UTF-8 -d bin -sourcepath src src\Main.java
   ```
3. Ejecuta la interfaz gráfica:
   ```powershell
   java -cp bin Main
   ```

#### En Linux / macOS
1. Crea la carpeta de salida:
   ```bash
   mkdir -p bin
   ```
2. Compila el proyecto:
   ```bash
   javac -encoding UTF-8 -d bin -sourcepath src src/Main.java
   ```
3. Ejecuta la interfaz gráfica:
   ```bash
   java -cp bin Main
   ```

### 4. Opción B: Ejecutar desde un IDE

* **IntelliJ IDEA:** **File → Open...** sobre la carpeta `evacuacion-edificio-ed` → configura **Project SDK** en Java 17+ → marca `src` como **Sources Root** si no aparece en azul → abre `src/Main.java` y presiona **Run (`▶`)**.
* **Visual Studio Code:** instala **Extension Pack for Java** → **File → Open Folder...** sobre la raíz → abre `src/Main.java` y presiona **Run** o `F5`.
* **NetBeans / Eclipse:** importa el proyecto a partir de los fuentes en `src` con JDK 17+ → clic derecho en `Main.java` → **Run File** / **Run As → Java Application**.

### 5. Verificación rápida al abrir la aplicación
1. Presiona **"Corrida Oficial Completa"** para validar los 10 casos de prueba y generar `evacuacion.txt`.
2. Presiona **"Reiniciar Simulación"** para restaurar el edificio despejado y la cola inicial.
3. Haz clic en **"Comparar H101 (BFS vs Dijkstra)"** para ver ambas rutas simultáneamente en el plano 2D.
4. Selecciona `P2-ESC_A`, presiona **"Bloquear (Humo)"**, escribe `H201` y haz clic en **"Evacuar (IA + Pila)"** para comprobar el desvío en vivo hacia `EXIT_B` (12 s).
5. Selecciona `H203-P2`, presiona **"Bloquear (Humo)"** e intenta evacuar `H203` para ver la propuesta de reapertura de la IA.

### 🛠️ Solución de problemas comunes

| Error | Causa | Solución |
| :--- | :--- | :--- |
| `unmappable character for encoding Cp1252` | Compilación en Windows sin UTF-8. | Agrega `-encoding UTF-8` al comando `javac`. |
| `switch expressions are not supported` | JDK inferior a la versión 14/17. | Actualiza el JDK activo a **Java 17 o superior**. |
| `Error: Could not find or load main class Main` | Ejecución fuera de la carpeta raíz. | Verifica que estás en `evacuacion-edificio-ed` y corre `java -cp bin Main`. |

---

## ✅ Corrida Oficial de Verificación

Al presionar **"Corrida Oficial Completa"** en la interfaz, el sistema reproduce los resultados oficiales de referencia:

| Caso evaluado | Resultado esperado y verificado |
| :--- | :--- |
| **Piso 2 (Lista doble)** | Ida: `Nora -> Teo -> Lina -> Omar` \| Regreso: `Omar -> Lina -> Teo -> Nora` |
| **Puerta `ESC_A` (Cola)** | Turno 1: `Sara` (evacuada), Turno 2: `Iker`, Turno 3: `Vera`, Turno 4: aviso de puerta vacía |
| **Tabla Hash (Cubeta 2)** | Colisión controlada: `A01 Nora` (162 % 5 = 2) y `A06 Dana` (167 % 5 = 2) |
| **ABB Zonas de Riesgo** | Inorden del `1` al `9`, 7 nodos, peso `6`, altura `2`, 4 hojas, completo (`Sí`), riesgo `7` en `5 -> 8 -> 7` (nivel 2, grado 0), más peligrosa: `Bodega` |
| **Evacuación `H201` (sin humo)** | `EXIT_A` en **10 s** (`H201 -> P2 -> ESC_A -> EXIT_A`) con 3 instrucciones retiradas de la pila |
| **Comparación `H101`** | **BFS:** `10 s` en 2 pasos (`H101 -> P1 -> EXIT_A`) vs. **Dijkstra:** `9 s` en 3 pasos (`H101 -> P1 -> ESC_A -> EXIT_A`) |
| **Humo en `P2-ESC_A`** | Desde `H201` cambia a `EXIT_B` en **12 s** (`H201 -> P2 -> ESC_B -> EXIT_B`) |
| **Habitación aislada `H203`** | Bloqueando `H203-P2`: la IA propone reabrir `H203-P2` (3 s) para salir por `EXIT_A` en **11 s** |
| **Habitación inexistente** | `H999` informa que la habitación no pertenece al edificio |
| **Red mínima (MST)** | **Prim** y **Kruskal** coinciden en **34 s** |

---

## 💡 Nota Técnica: ¿Por qué en `H101` menos pasos no significa menos tiempo?

* **Búsqueda por niveles (BFS)** trata todas las conexiones como si tuvieran el mismo costo, por lo que selecciona `H101 -> P1 -> EXIT_A` (2 pasos). Sin embargo, el tramo directo `P1-EXIT_A` cuesta **8 s**, sumando **10 s** en total.
* **Dijkstra** evalúa el peso real en segundos y descubre que desviarse por `ESC_A` (`P1 -> ESC_A` = 3 s y `ESC_A -> EXIT_A` = 4 s) suma solo 7 s adicionales al tramo inicial, reduciendo el tiempo total a **9 s** aunque implique 3 pasos.
* Del mismo modo, el árbol de expansión mínima obtenido por **Prim y Kruskal (34 s)** indica cómo mantener conectados los 12 lugares del edificio al menor costo global, pero **no** sustituye la ruta individual de evacuación de una persona (que siempre se calcula con Dijkstra).