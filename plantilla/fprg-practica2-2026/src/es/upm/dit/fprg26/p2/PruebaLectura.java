package es.upm.dit.fprg26.p2;

import es.upm.dit.fprg26.p2.auxiliar.Comprobador;

/**
 * Pruebas de la clase {@link Lectura}: comprueban el método {@code contarIndeterminadas},
 * que debe programar usted, con una muestra de casos. Añada sus propios casos de prueba en
 * {@link #pruebasPropias()}.
 */
public class PruebaLectura {

    private PruebaLectura() {
        // Solo contiene el método main: no se instancia
    }

    /**
     * Ejecuta las pruebas.
     *
     * @param args no se usa
     */
    public static void main(String[] args) {
        probarContarIndeterminadas();
        pruebasPropias();
        Comprobador.resumen();
    }

    private static void probarContarIndeterminadas() {
        Comprobador.titulo("contarIndeterminadas");
        try {
            Comprobador.comprobar("\"GATTACA\"", 0, new Lectura("L1", "GATTACA").contarIndeterminadas());
            Comprobador.comprobar("\"GANTACNA\"", 2, new Lectura("L2", "GANTACNA").contarIndeterminadas());
            Comprobador.comprobar("\"NGATTACAN\" (N al principio y al final)", 2,
                    new Lectura("L3", "NGATTACAN").contarIndeterminadas());
            Comprobador.comprobar("\"NNNN\"", 4, new Lectura("L4", "NNNN").contarIndeterminadas());
        } catch (Exception e) {
            Comprobador.fallo("No se ha podido crear una lectura válida: " + e.getMessage());
        }
    }

    /**
     * Sus propios casos de prueba.
     */
    private static void pruebasPropias() {
        Comprobador.titulo("Pruebas propias");
        // TODO: añada aquí sus propios casos de prueba
    }
}
