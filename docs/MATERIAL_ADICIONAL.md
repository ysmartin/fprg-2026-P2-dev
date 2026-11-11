# Material adicional para el profesorado · Práctica 2 (FPRG 2026-27)

Este documento **no es para el alumnado**. Recoge dos ampliaciones opcionales de la P2. El texto de cada una está redactado para el alumnado, de modo que se puede copiar tal cual al enunciado (como apartado opcional) o usar en la prueba escrita.

| Fichero | Contenido |
|---------|-----------|
| `docs/material-adicional/solucion/` | `Sonda`, `Microarray` y `Analisis` con las ampliaciones resueltas (sustituyen a las de `solucion/`) |
| `docs/material-adicional/PruebaAmpliaciones.java` | Pruebas de las dos ampliaciones (se añade al paquete `es.upm.dit.fprg26.p2`) |

`scripts/verificar.sh` compila la solución con las ampliaciones y ejecuta sus pruebas junto con las de la versión base, que siguen pasando.

**Carga de trabajo** (LLOC, con la convención de `NOTAS_PROFESORADO.md`):

| | LLOC | Total acumulado | Frente a 2025-26 (90) |
|---|---:|---:|---:|
| Versión base | 55 | 55 | −39 % |
| A1 · Todas las apariciones | +12 | 67 | −26 % |
| A2 · Intensidades y sondas detectadas | +16 | 83 | −8 % |

---

## A1. Todas las apariciones

El método `encontrarEn` devuelve solo la primera posición en la que encaja el patrón de una sonda. A veces interesa saber cuántas veces aparece la diana en una lectura, y dónde.

1. En `Sonda`, el método `int contarEn(Lectura lectura) throws Exception`, que devuelve en cuántas posiciones de la lectura encaja el patrón, en cualquiera de los dos sentidos. Cuente también las apariciones que se solapan: por ejemplo, el patrón `AAA` aparece tres veces en la lectura `AAAAA`.
2. En `Sonda`, el método `int[] encontrarTodasEn(Lectura lectura) throws Exception`, que devuelve un array con todas esas posiciones, de menor a mayor. Por ejemplo, para el patrón `TRC` y la lectura `GATACATGCA`, devuelve un array con 2, 6 y 7 (en la 7 encaja en sentido inverso). El array debe tener exactamente tantas posiciones como apariciones: cuéntelas primero (con `contarEn`) y rellénelo después.

### Notas para el profesorado

- Reutiliza la ventana deslizante de `encontrarEn` sin salida anticipada: se recorren todas las posiciones. Es un buen momento para comparar las dos formas de recorrido (buscar el primero frente a recorrerlo todo).
- `encontrarTodasEn` ejercita el patrón «contar y después rellenar» un array, con un índice de escritura distinto del de lectura, que se retomará en la P3 con colecciones.
- Sustituye a la antigua A1 («Las dos hebras»), que ha pasado a la versión base.

---

## A2. Intensidades y sondas detectadas

En un microarray real, el brillo de cada punto es más intenso cuantos más fragmentos de la muestra se unen a su sonda.

1. En `Microarray`, el método `int[][] intensidades(Muestra muestra)`, que devuelve, para cada punto del chip, con cuántas lecturas de la muestra hibrida su sonda. Reutilice `hibridar(Lectura)`, como en `hibridar(Muestra)`.
2. En `Analisis` (que se proporciona hecha), el método `String[] sondasDetectadas() throws Exception`, que devuelve los nombres de las sondas de los puntos iluminados, recorriendo el chip por filas (de izquierda a derecha y de arriba abajo). El array debe tener exactamente tantas posiciones como puntos iluminados: cuéntelos primero (con `contarIluminados`) y rellénelo después.

### Notas para el profesorado

- `intensidades` generaliza a todo el chip el método `contarLecturasQueHibridan` de la versión base (apartado 6), pero recorriendo la muestra una vez por lectura con `hibridar(Lectura)` en lugar de una vez por sonda.
- `intensidades` es un triple bucle (lecturas, filas y columnas) con un contador por posición de una matriz de `int`; `sondasDetectadas` ejercita el patrón «contar y después rellenar» un array, que se retomará en la P3 con colecciones.
- Es la forma realista de leer un microarray: cada punto da una intensidad, aproximadamente proporcional al número de fragmentos marcados que se unen a su sonda, y la presencia se decide con un umbral. La versión base equivale a un umbral de una lectura.
- La interfaz gráfica no muestra las intensidades (para eso tendría que invocar un método que no existe en la versión base). Si se quisiera, `PanelMicroarray` podría graduar el brillo de cada punto.
