# Notas para el profesorado · Práctica 2 (FPRG 2026-27)

Este documento **no es para el alumnado**. Recoge las decisiones de diseño de la P2, la comparación de su carga de trabajo con la P2 de 2025-26, los errores previsibles y algunas ideas para la prueba escrita. Las ampliaciones opcionales (A1 y A2) están en [`MATERIAL_ADICIONAL.md`](MATERIAL_ADICIONAL.md).

## 1. Estructura de este repositorio

| Ruta | Contenido | Destino |
|------|-----------|---------|
| `README.md`, `logos/`, `img/` | Enunciado de la P2 y sus imágenes (`img/ventana.png`: captura de la interfaz gráfica) | Repositorio del alumnado |
| `plantilla/fprg-practica2-2026/` | Proyecto Eclipse (JavaSE-25, paquete `es.upm.dit.fprg26.p2`) que reciben los alumnos, con la documentación HTML ya generada (`doc/`) | Repositorio del alumnado |
| `solucion/fprg-practica2-2026/` | Mismo proyecto, con la solución de referencia | Repositorio de la solución |
| `docs/NOTAS_PROFESORADO.md` | Este documento | Solo desarrollo |
| `docs/MATERIAL_ADICIONAL.md`, `docs/material-adicional/` | Ampliaciones A1 y A2: enunciado, solución (clases ampliadas) y pruebas | Solo profesorado |
| `scripts/derivar-plantilla.py` | Deriva la plantilla a partir de la solución (vacía los métodos que completa el alumnado) | Solo desarrollo |
| `scripts/generar-doc.sh` | Regenera la documentación HTML de la plantilla | Solo desarrollo |
| `scripts/verificar.sh` | Comprueba que la plantilla y `doc/` están al día, compila (javac y, con `ECJ_JAR`, ECJ), ejecuta las pruebas de la solución, de la plantilla y de la solución con ampliaciones, y revisa el Javadoc | Solo desarrollo |

Para publicar, basta con copiar `README.md`, `logos/`, `img/` y `plantilla/fprg-practica2-2026/` al repositorio del alumnado (con la carpeta del proyecto en la raíz, como en la P1), y `README.md`, `logos/`, `img/` y `solucion/fprg-practica2-2026/` al de la solución. Los proyectos se llaman `Practica2` (plantilla) y `Practica2-solucion` (solución).

**Flujo de trabajo**: se edita la solución; después, `python3 scripts/derivar-plantilla.py`, `scripts/generar-doc.sh` y `scripts/verificar.sh`.

## 2. Decisiones de diseño

| Decisión | Motivo / consecuencia |
|----------|-----------------------|
| **Escenario**: lecturas de una muestra (como las de un fichero FASTQ) analizadas con un microarray de sondas | Continúa la P1 (el alfabeto) y el plan global: en la P2, clases y recorridos de cadenas y arrays; en la P3, ficheros, colecciones y varios reconocedores. Los *arrays* aparecen en los dos sentidos: el microarray es una matriz `Sonda[][]` y el resultado, una matriz `boolean[][]`; las *cadenas* son las secuencias de las lecturas y de los patrones. |
| **Patrones con comodines del código IUPAC**: `N` (cualquier base), `R` (purina) e `Y` (pirimidina) en el patrón | Es la notación estándar de las posiciones degeneradas, y se usa en dianas de enzimas de restricción (HincII: `GTYRAC`), cebadores y sondas degeneradas, y búsqueda de motivos (por ejemplo, `matchPattern` de Biostrings admite ambigüedades en el patrón, en la secuencia o en ambos). Equivale a los píxeles `INDEFINIDO` del patrón de 2025-26, pero con tres tipos de comodín. Se presentan las sondas como «degeneradas» (detectan variantes de un patógeno), porque las sondas de un microarray real son secuencias concretas. |
| **Una `N` de la lectura solo encaja con la `N` del patrón** | Ambigüedad solo en el patrón (opción conservadora): una base desconocida no puede confirmar ninguna coincidencia, igual que los alineadores (Bowtie2 penaliza las posiciones con `N` como desajustes). La `N` del patrón significa «esta posición no importa», así que acepta cualquier cosa, incluida una `N`. |
| **La sonda guarda el patrón de su diana** y la comparación es base a base (`A` con `A`) | Es lo más sencillo para el alumnado y evita complementar en cada comparación. El enunciado explica que la sonda real es complementaria a su diana. La ampliación A1 añade la secuencia real de la sonda (complementaria inversa del patrón) y la búsqueda en las dos hebras. |
| **Sin marcas 5'/3'** | Todas las lecturas de un FASTQ se escriben de 5' a 3', así que no hace falta mencionarlo en la versión base. Sí hay que tener en cuenta que una lectura puede proceder de cualquiera de las dos hebras (la diana puede aparecer como su complementaria inversa): es la ampliación A1. |
| **Solo ADN**: las lecturas admiten `A`, `C`, `G`, `T` y `N` | El ARN se secuencia como ADN copia (con `T`), así que las lecturas de un FASTQ son siempre de ADN. Los patrones admiten además `R` e `Y`: dos validaciones parecidas con alfabetos distintos (como `Muestra` y `Patogeno` en 2025-26), pero una se proporciona hecha (`Lectura`) y la otra la escribe el alumnado (`Sonda`). |
| **Proteínas, para la P3** | El algoritmo sería el mismo con otro alfabeto: en la P2 solo añadiría código duplicado. Su valor didáctico propio (aplicar el mismo reconocedor a distintas extensiones de una clase) encaja en la P3. |
| **Clases proporcionadas como modelo** (`Lectura`, `Muestra`, el constructor de `Microarray`) y clases que se completan (`Sonda`, `Microarray.hibridar`, `Analisis`) | El punto de partida es más rico que en la P1: el alumnado estudia clases completas antes de escribir la suya. Los atributos de las clases que se completan vienen declarados (como en 2025-26). |
| **Enumerados de la P1** (`BaseNitrogenada`, `AcidoNucleico`) y utilidades `Iupac` | `Iupac.complementario` reutiliza `BaseNitrogenada.fromChar`, `complementaria` y `toChar` de la P1. `Iupac` reúne las constantes y los métodos de utilidad (`esSimboloDeLectura`, `esSimboloDePatron`, `esPurina`, `esPirimidina`, `complementario`) para que el alumnado se centre en las clases y los bucles. |
| **`try`/`catch`** en `encajaEn` | `Lectura.getBase` lanza una excepción comprobada si la posición está fuera de la lectura; `encajaEn` la captura y devuelve `false` (el patrón no cabe a partir de esa posición), igual que `coincideEn` en 2025-26. `buscarEn` solo prueba las posiciones en las que cabe el patrón, así que la captura protege, sobre todo, las invocaciones directas a `encajaEn` con posiciones no válidas (las pruebas lo comprueban). |
| **Procedencia con `LocalDate`**: fecha de captura (en `Muestra`) y de análisis (en `Analisis`) | Uso de una biblioteca de Java distinta de `java.lang`: `LocalDate.of`, `now`, `isAfter`, `isBefore` y `ChronoUnit.DAYS.between`. Las fechas de los datos de ejemplo son de 2026 (de abril a junio), anteriores a cualquier fecha de uso, porque `Muestra` rechaza las fechas futuras. |
| **`import module java.base;`** (y `java.desktop` en la interfaz) | Importaciones de módulo (Java 25). En la interfaz se combinan `java.base` y `java.desktop`; no se usa `List`, que sería ambiguo (`java.util.List` y `java.awt.List`). Probado con javac 25 y ECJ 3.44. |
| **`switch` con flechas, como sentencia** (en `encaja` e `Iupac.complementario`) | Igual que en la P1. Las constantes de `Iupac` (`INDETERMINADA`, `PURINA`, `PIRIMIDINA`) sirven como etiquetas de `case`. |
| **Datos de ejemplo** (`DatosEjemplo`) | Un chip de prueba de 1x3 (`GATTACA`, `GANTACA`, `GAYTRCA`) con lecturas que distinguen las reglas de los comodines; un panel de patógenos de 3x3 con secuencias **sintéticas** (inventadas, y así se indica), con sondas de control positivo y negativo; y un chip de 2x4 con las secuencias **reales** de ocho enzimas de restricción (EcoRI, BamHI, HindIII, NotI y, degeneradas, HincII `GTYRAC`, AvaI `CYCGRG`, BanI `GGYRCC` y Hpy188I `TCNGA`, según el catálogo de NEB). Las lecturas se generaron con un script que comprueba que cada sonda se ilumina solo donde debe, también en la otra hebra (para que A1 no cambie los resultados). |
| **Interfaz gráfica opaca** (`VentanaMicroarray`, `PanelMicroarray`) | Dibuja el chip con los puntos iluminados (con el mismo estilo que la figura de la P1) y, para cada lectura, marca con `^` dónde encaja cada sonda según `buscarEn`. Permite escribir lecturas a mano. Tolera el código incompleto: con la plantilla muestra `(null)` y un aviso en lugar de fallar. |
| **Pruebas legibles** (`Comprobador` y `Prueba*`) | El alumnado ya conoce las clases y `try`/`catch`, así que las pruebas ya no son opacas como en la P1: `Comprobador.comprobar(descripción, esperado, obtenido)` escribe `✓`/`✗` (también con matrices), y las excepciones se comprueban con `try`/`catch`. Cada clase de prueba tiene un método `pruebasPropias` para las del alumnado. |
| **Plantilla derivada por script** | `scripts/derivar-plantilla.py` vacía los métodos que completa el alumnado (con `TODO` y un valor provisional) y quita `final` de los atributos de `Sonda` y `Analisis`, para que la plantilla compile. `verificar.sh` comprueba que la plantilla está al día. |

## 3. Carga de trabajo comparada con la P2 de 2025-26

Se usa la misma convención que en la P1 (líneas lógicas de código, LLOC, en la línea de NCSS):
- **Cuenta 1** cada declaración (de atributo, de método o constructor que se escribe desde cero, de variable local) y cada sentencia (expresión, `return`, `throw`, `if`, cada `else`, `for`, `switch` y cada una de sus etiquetas, `try`, cada `catch`).
- **No cuentan**: el código proporcionado, las cabeceras ya escritas, las líneas en blanco, los comentarios, las llaves y las líneas de continuación.
- **Pesos**: trivial 0,5 (atributos, `this.x = x`, métodos de acceso), normal 1 y compleja 2 (composición, condiciones compuestas, fórmulas con más de un operador).

| | P2 2025-26 | P2 2026-27 |
|---|---:|---:|
| LLOC triviales | 11 | 12 |
| LLOC normales | 64 | 50 |
| LLOC complejas | 15 | 8 |
| **Total de LLOC de código** | **90** | **70 (−22 %)** |
| Unidades ponderadas (trivial = 0,5) | ≈ 100 | ≈ 72 (−28 %) |
| Unidades ponderadas (trivial = 0,25) | ≈ 97 | ≈ 69 (−29 %) |

Detalle de 2025-26: `Muestra` 26 LLOC, `Patogeno` 30 y `Reconocedor` 34 (diferencia entre su plantilla y su solución).

Detalle de 2026-27 (LLOC que escribe el alumnado):

| Clase | Método | LLOC | Qué ejercita |
|---|---|---:|---|
| `Lectura` | `contarIndeterminadas` | 5 | Primer recorrido de una cadena |
| `Sonda` | constructor | 9 | Validación con condiciones compuestas y recorrido del patrón; excepciones |
| | `getNombre`, `getPatron`, `getLongitud` | 3 | Métodos de acceso (triviales) |
| | `encaja` | 9 | `switch` con flechas sobre los comodines (reutiliza `Iupac.esPurina`/`esPirimidina`) |
| | `encajaEn` | 7 | Bucle con salida anticipada; `try`/`catch` |
| | `buscarEn` | 4 | Ventana deslizante (límite del bucle) |
| | `hibridaCon` | 1 | Reutilización |
| `Microarray` | `hibridar(Lectura)` | 5 | Recorrido de una matriz con bucles anidados |
| | `hibridar(Muestra)` | 8 | `for` mejorado sobre un array y combinación (O lógico) de matrices |
| `Analisis` | constructor | 8 | Validación con `LocalDate.isBefore`; cálculo en el constructor |
| | métodos de acceso | 4 | Triviales |
| | `getDiasDesdeCaptura` | 1 | `ChronoUnit.DAYS.between` |
| | `contarIluminados` | 6 | Recorrido de una matriz con contador |
| **Total** | | **70** | |

**Interpretación**: la carga baja un 22 % en LLOC y algo más en unidades ponderadas, porque hay menos líneas complejas (las validaciones de matrices de 2025-26 se proporcionan hechas en `Microarray`, y la de `Lectura` sirve de modelo para la de `Sonda`). A cambio, el alumnado lee más código proporcionado (`Lectura`, `Muestra`, `Microarray`, `Iupac`) y aparecen contenidos nuevos: recorrido de cadenas, `switch`, fechas y bucle `for` mejorado.

**Con las ampliaciones** (detalle en `MATERIAL_ADICIONAL.md`): A1 añade 13 LLOC y A2, 15, hasta 98 LLOC (un 9 % más que en 2025-26). Si se quiere llegar al 20 % más, puede añadirse una tercera, por ejemplo, contar todas las apariciones de un patrón (solapadas) o filtrar las lecturas con demasiadas bases indeterminadas (unas 8 a 12 LLOC cada una).

## 4. Errores previsibles

- **Límite de la ventana en `buscarEn`**: `inicio < lectura.getLongitud() - patron.length()` (falta el `=`) no encuentra el patrón al final de la lectura; la prueba `"GCA"` en `"GATACATGCA"` (posición 7) lo detecta. Gracias al `try`/`catch` de `encajaEn`, el error contrario (recorrer de más) no se nota en el resultado.
- **`encaja` con la `N` de la lectura**: comparar primero `base == 'N'` y devolver `true` (ambigüedad en los dos lados) hace que `GATTACA` encaje con `GANTACA`; las pruebas `encaja('A', 'N')` y la de `encajaEn` con `"GATTACA"` en `"GANTACA"` lo detectan (comprobado con una versión de la solución con ese error).
- **`encajaEn` sin salida anticipada**: devolver el resultado de la última comparación en vez de `false` en cuanto una falla.
- **`try`/`catch` mal colocado**: dentro del bucle, sin `return false` en el `catch`, el método sigue comparando y puede devolver `true`.
- **`hibridar(Muestra)` que sobrescribe** el resultado de cada lectura (`iluminados[f][c] = conLectura[f][c]`) en lugar de acumularlo: solo cuenta la última lectura. La prueba con M1 lo detecta (el Virus A está en la primera lectura y la última no tiene ninguna diana).
- **Filas y columnas cambiadas**: `new boolean[getColumnas()][getFilas()]`; la prueba con el chip de 2x4 lo detecta.
- **Fechas**: usar `isAfter` en lugar de `isBefore` (o al revés), o invertir los argumentos de `ChronoUnit.DAYS.between` (días negativos). Las pruebas con el mismo día (0) y con cambio de mes (9) lo detectan.
- **Atributos sin asignar en el constructor de `Analisis`** (o asignados después de llamar a métodos que los usan): `getIluminados()` devuelve `null`; la interfaz lo avisa.

## 5. Ideas para la prueba escrita

- Ampliar `encaja` con otros códigos IUPAC: `S` (`G` o `C`), `W` (`A` o `T`), `K` (`G` o `T`), `M` (`A` o `C`).
- Contar todas las apariciones de un patrón en una lectura, incluidas las solapadas (`ANA` aparece dos veces en `BANANA`), o devolver todas sus posiciones en un array (contar primero y rellenar después).
- El contenido GC de una lectura, ahora con un bucle (en la P1 se resolvía con `replace`, y se reservó para el examen).
- `Muestra.contarLecturasConIndeterminadas()`, o la fracción de lecturas que hibridan con una sonda.
- `Microarray.filasConAlgunPunto(boolean[][])`, o la sonda de un microarray con el patrón más largo.
- `Analisis.estaCaducada(int dias)`: si han pasado más de esos días entre la captura y el análisis (`ChronoUnit`), o la fecha límite de análisis con `plusDays`.
- Predecir el resultado de `buscarEn` para un patrón y una lectura dados, o localizar el error de un `buscarEn` con el límite del bucle mal calculado.
- La complementaria inversa de una secuencia (recorrido hacia atrás), si no se ha usado la ampliación A1.

## 6. Verificación técnica realizada

- **JDK 25** (Temurin 25.0.5): la solución, la plantilla y la solución con ampliaciones compilan sin avisos con `javac -Xlint:all -Werror`, y también con **ECJ 3.44** (Eclipse 2025-12) y la configuración de `.settings`. Las pruebas de la solución superan todas sus comprobaciones (4, 34, 7 y 13), y las de las ampliaciones, las suyas (11). La plantilla se ejecuta sin excepciones, con las comprobaciones pendientes marcadas con `✗`. `javadoc -Xdoclint:all` no da avisos (en la plantilla, sin el grupo `missing`). Todo esto lo repite `scripts/verificar.sh`.
- **Interfaz gráfica**: probada con la solución y con la plantilla en un servidor X virtual (Xvfb), con muestras de ejemplo y con lecturas escritas a mano (`img/ventana.png` es una de esas capturas).
- **Errores típicos**: con versiones de la solución que contienen los errores de la sección 4 (límite de `buscarEn`, `N` de la lectura como comodín, `hibridar(Muestra)` que sobrescribe, `isAfter` por `isBefore` y argumentos de `ChronoUnit` invertidos), las pruebas proporcionadas señalan cada uno de ellos.
- **Pendiente de comprobar en la imagen de los laboratorios**: la ejecución desde la interfaz de Eclipse (_Run As ➤ Java Application_), y la apertura de `doc/index.html` con _Open With ➤ Web Browser_.
