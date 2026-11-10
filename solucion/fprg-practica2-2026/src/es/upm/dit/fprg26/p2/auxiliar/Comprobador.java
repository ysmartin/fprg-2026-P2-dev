package es.upm.dit.fprg26.p2.auxiliar;

import module java.base;

/**
 * Comprobaciones para las pruebas: compara el resultado obtenido con el esperado y
 * escribe en la consola {@code ✓} (correcto) o {@code ✗} (incorrecto, con los dos valores).
 * <p>
 * Ejemplo de uso, en una clase de prueba:
 * </p>
 * <pre>
 * Comprobador.titulo("buscarEn");
 * Comprobador.comprobar("buscarEn(\"GATACA\")", 2, sonda.buscarEn(lectura));
 * ...
 * Comprobador.resumen();
 * </pre>
 * <p>
 * Para comprobar que un constructor o un método lanza una excepción, se invoca dentro de
 * un bloque {@code try} y se llama a {@link #fallo(String)} si no la lanza, o a
 * {@link #acierto(String)} en el {@code catch}.
 * </p>
 */
public final class Comprobador {

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
    public static void titulo(String titulo) {
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
    public static void comprobar(String descripcion, Object esperado, Object obtenido) {
        if (Objects.deepEquals(esperado, obtenido)) {
            acierto(descripcion + " = " + formatear(obtenido));
        } else {
            fallo(descripcion + " -> esperado: " + formatear(esperado) + ", obtenido: " + formatear(obtenido));
        }
    }

    /**
     * Registra una comprobación correcta.
     *
     * @param descripcion descripción de la comprobación
     */
    public static void acierto(String descripcion) {
        realizadas++;
        correctas++;
        IO.println("  ✓ " + descripcion);
    }

    /**
     * Registra una comprobación incorrecta.
     *
     * @param descripcion descripción de la comprobación y del motivo del fallo
     */
    public static void fallo(String descripcion) {
        realizadas++;
        IO.println("  ✗ " + descripcion);
    }

    /**
     * Escribe cuántas comprobaciones han sido correctas.
     */
    public static void resumen() {
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
}
