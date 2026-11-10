# Material adicional para el profesorado · Práctica 2 (FPRG 2026-27)

Este documento **no es para el alumnado**. Recoge dos ampliaciones opcionales de la P2. El texto de cada una está redactado para el alumnado, de modo que se puede copiar tal cual al enunciado (como apartado opcional) o usar en la prueba escrita.

| Fichero | Contenido |
|---------|-----------|
| `docs/material-adicional/solucion/` | `Lectura`, `Sonda`, `Microarray` y `Analisis` con las ampliaciones resueltas (sustituyen a las de `solucion/`) |
| `docs/material-adicional/PruebaAmpliaciones.java` | Pruebas de las dos ampliaciones (se añade al paquete `es.upm.dit.fprg26.p2`) |

`scripts/verificar.sh` compila la solución con las ampliaciones y ejecuta sus pruebas junto con las de la versión base, que siguen pasando: los datos de ejemplo se generaron de modo que buscar en las dos hebras no cambia ningún resultado (tampoco en el chip de expresión de 20x20, que sigue dibujando «ADN»: `PruebaAmpliaciones` lo comprueba).

**Carga de trabajo** (LLOC, con la convención de `NOTAS_PROFESORADO.md`):

| | LLOC | Total acumulado | Frente a 2025-26 (90) |
|---|---:|---:|---:|
| Versión base | 70 | 70 | −22 % |
| A1 · Las dos hebras | +13 | 83 | −8 % |
| A2 · Intensidades y sondas detectadas | +15 | 98 | +9 % |

---

## A1. Las dos hebras

Una lectura puede proceder de cualquiera de las dos hebras del ADN de la muestra. Si procede de la hebra contraria a la de la diana de una sonda, la lectura no contiene la diana tal cual, sino su **complementaria inversa**: la secuencia de la otra hebra, leída en el mismo sentido. Por ejemplo, la complementaria inversa de `GATTACAN` es `NTGTAATC` (se recorre la secuencia desde el final y se sustituye cada base por su complementaria; la `N` sigue siendo `N`).

1. En `Lectura`, el método `Lectura complementariaInversa() throws Exception`, que devuelve una nueva lectura con la secuencia complementaria inversa y el mismo identificador seguido de `" (inversa)"`. Use `Iupac.complementario` con cada base.
2. En `Sonda`, el método `String getSecuenciaSonda() throws Exception`, que devuelve la secuencia del fragmento de ADN que se fija en el chip: la complementaria inversa del patrón. Por ejemplo, la de una sonda con el patrón `GAYTRCA` es `TGYARTC` (el complementario de una purina cualquiera, `R`, es una pirimidina cualquiera, `Y`, y viceversa).
3. Modifique `hibridaCon` para que la sonda hibride también con las lecturas que contienen su diana en la otra hebra: si el patrón no aparece en la lectura, búsquelo en su complementaria inversa. Como `complementariaInversa` puede lanzar una excepción, captúrela: en ese caso, la sonda no hibrida.

¿Cambia algo en los resultados de la interfaz gráfica? ¿Por qué no hace falta modificar `Microarray`?

### Notas para el profesorado

- Es la continuación natural de la pregunta sobre los extremos 5'/3' de la P1: todas las lecturas se escriben de 5' a 3', pero pueden venir de cualquiera de las dos hebras.
- Los recorridos hacia atrás (`for (int i = longitud - 1; i >= 0; i--)`) no aparecen en la versión base.
- La respuesta a la pregunta final: `Microarray.hibridar` usa `hibridaCon`, así que se beneficia del cambio sin tocarlo (buen momento para hablar de la reutilización y la encapsulación). Los resultados de los datos de ejemplo no cambian (están generados para ello), pero una lectura como `GGATTCAGCTAACGGTCCAT` (con la diana del Virus A en la otra hebra) ilumina la sonda del Virus A solo con la ampliación.
- Las secuencias de las enzimas de restricción del chip de ejemplo son palíndromos (su complementaria inversa es ella misma, también con los comodines: `GTYRAC`), así que buscar en las dos hebras no cambia nada para ellas.

---

## A2. Intensidades y sondas detectadas

En un microarray real, el brillo de cada punto es más intenso cuantos más fragmentos de la muestra se unen a su sonda.

1. En `Microarray`, el método `int[][] intensidades(Muestra muestra)`, que devuelve, para cada punto del chip, con cuántas lecturas de la muestra hibrida su sonda.
2. En `Analisis`, el método `String[] sondasDetectadas() throws Exception`, que devuelve los nombres de las sondas de los puntos iluminados, recorriendo el chip por filas (de izquierda a derecha y de arriba abajo). El array debe tener exactamente tantas posiciones como puntos iluminados: cuéntelos primero (con `contarIluminados`) y rellénelo después.

### Notas para el profesorado

- `intensidades` es un triple bucle (lecturas, filas y columnas) con un contador por posición de una matriz de `int`; `sondasDetectadas` ejercita el patrón «contar y después rellenar» un array, que se retomará en la P3 con colecciones.
- Es la forma realista de leer un microarray: cada punto da una intensidad, aproximadamente proporcional al número de fragmentos marcados que se unen a su sonda, y la presencia se decide con un umbral. La versión base equivale a un umbral de una lectura.
- La interfaz gráfica no muestra las intensidades (para eso tendría que invocar un método que no existe en la versión base). Si se quisiera, `PanelMicroarray` podría graduar el brillo de cada punto.
