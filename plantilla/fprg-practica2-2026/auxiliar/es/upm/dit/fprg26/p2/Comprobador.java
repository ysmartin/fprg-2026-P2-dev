package es.upm.dit.fprg26.p2;

import module java.base;

/**
 * Comprobaciones para las pruebas: compara el resultado obtenido con el esperado y
 * escribe en la consola {@code ✓} (correcto) o {@code ✗} (incorrecto, con los dos valores).
 * <p>
 * Ejemplo de uso, en una clase de prueba:
 * </p>
 * <pre>
 * Comprobador.titulo("encontrarEn");
 * Comprobador.comprobar("encontrarEn(\"GATACA\")", 2, sonda.encontrarEn(lectura));
 * ...
 * Comprobador.resumen();
 * </pre>
 * <p>
 * Para comprobar que un constructor o un método lanza una excepción, se invoca dentro de
 * un bloque {@code try} y se llama a {@link #fallo(String)} si no la lanza, o a
 * {@link #acierto(String)} en el {@code catch}.
 * </p>
 */
final class Comprobador {

    private static int realizadas = 0;
    private static int correctas = 0;

    private Comprobador() {
        // No se instancia: solo contiene métodos estáticos
    }

    /**
     * Escribe en la consola un título que separa un grupo de comprobaciones.
     *
     * @param titulo texto del título
     */
    static void titulo(String titulo) {
        IO.println();
        IO.println("[" + titulo + "]");
    }

    /**
     * Compara un resultado con el esperado y escribe el resultado de la comprobación.
     * Sirve para cualquier tipo de valor, incluidos {@code String}, números, {@code char},
     * {@code boolean} y arrays (de una o de dos dimensiones).
     *
     * @param descripcion qué se comprueba (por ejemplo, la invocación que se ha hecho)
     * @param esperado resultado esperado
     * @param obtenido resultado obtenido
     */
    static void comprobar(String descripcion, Object esperado, Object obtenido) {
        if (Objects.deepEquals(esperado, obtenido)) {
            acierto(descripcion + " = " + formatear(obtenido));
        } else if (esperado instanceof boolean[][] matrizEsperada && obtenido instanceof boolean[][] matrizObtenida
                && esGrande(matrizEsperada)) {
            fallo(descripcion + " -> esperado (izquierda) y obtenido (derecha):"
                    + enParalelo(matrizEsperada, matrizObtenida));
        } else {
            fallo(descripcion + " -> esperado: " + formatear(esperado) + ", obtenido: " + formatear(obtenido));
        }
    }

    /**
     * Registra una comprobación correcta.
     *
     * @param descripcion descripción de la comprobación
     */
    static void acierto(String descripcion) {
        realizadas++;
        correctas++;
        IO.println("  ✓ " + descripcion);
    }

    /**
     * Registra una comprobación incorrecta.
     *
     * @param descripcion descripción de la comprobación y del motivo del fallo
     */
    static void fallo(String descripcion) {
        realizadas++;
        IO.println("  ✗ " + descripcion);
    }

    /**
     * Escribe cuántas comprobaciones han sido correctas.
     */
    static void resumen() {
        IO.println();
        IO.println("Resumen: " + correctas + " de " + realizadas + " comprobaciones correctas");
    }

    private static String formatear(Object valor) {
        if (valor instanceof String texto) {
            return "\"" + texto + "\"";
        }
        if (valor instanceof Character caracter) {
            return "'" + caracter + "'";
        }
        if (valor instanceof boolean[][] matriz) {
            String texto = "";
            for (boolean[] fila : matriz) {
                if (esGrande(matriz)) {
                    texto += "\n      ";
                }
                texto += fila == null ? "null" : formatear(fila);
            }
            return texto;
        }
        if (valor instanceof boolean[] fila) {
            String texto = "[";
            for (boolean b : fila) {
                texto += b ? "#" : ".";
            }
            return texto + "]";
        }
        if (valor instanceof Object[] array) {
            return Arrays.deepToString(array);
        }
        return String.valueOf(valor);
    }

    /** Las matrices de más de 4 filas se escriben con una fila en cada línea. */
    private static boolean esGrande(boolean[][] matriz) {
        return matriz.length > 4;
    }

    private static String enParalelo(boolean[][] izquierda, boolean[][] derecha) {
        String texto = "";
        for (int i = 0; i < Math.max(izquierda.length, derecha.length); i++) {
            String filaIzquierda = i < izquierda.length ? formatearFila(izquierda[i]) : "";
            String filaDerecha = i < derecha.length ? formatearFila(derecha[i]) : "";
            String marca = filaIzquierda.equals(filaDerecha) ? "   " : " ≠ ";
            texto += "\n      " + filaIzquierda + " ".repeat(Math.max(0, 24 - filaIzquierda.length()))
                    + marca + filaDerecha;
        }
        return texto;
    }

    private static String formatearFila(boolean[] fila) {
        return fila == null ? "null" : formatear(fila);
    }
}
