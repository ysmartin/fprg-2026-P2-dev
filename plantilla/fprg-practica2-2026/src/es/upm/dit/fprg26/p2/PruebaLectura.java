package es.upm.dit.fprg26.p2;

/**
 * Pruebas de la clase {@link Lectura}: comprueban el método {@code contarIndeterminadas},
 * que debe programar usted, con una muestra de casos. Añada sus propios casos de prueba en
 * {@link #pruebasPropias()}.
 */
class PruebaLectura {

    /**
     * Ejecuta las pruebas.
     */
    void main() {
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
