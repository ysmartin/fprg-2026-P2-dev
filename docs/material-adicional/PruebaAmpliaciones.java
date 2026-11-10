package es.upm.dit.fprg26.p2;

/**
 * Pruebas de las ampliaciones A1 (las dos hebras) y A2 (intensidades y sondas detectadas).
 * Se añade a la carpeta {@code src} del proyecto, en el paquete {@code es.upm.dit.fprg26.p2},
 * cuando se trabajan las ampliaciones.
 */
class PruebaAmpliaciones {

    /**
     * Ejecuta las pruebas.
     */
    void main() {
        probarDosHebras();
        probarIntensidadesYDetectadas();
        Comprobador.resumen();
    }

    private static void probarDosHebras() {
        Comprobador.titulo("A1 · Las dos hebras");
        try {
            Lectura lectura = new Lectura("L", "GATTACAN");
            Lectura inversa = lectura.complementariaInversa();
            Comprobador.comprobar("complementariaInversa() de \"GATTACAN\"", "NTGTAATC", inversa.getSecuencia());
            Comprobador.comprobar("identificador de la complementaria inversa", "L (inversa)", inversa.getId());
            Comprobador.comprobar("getSecuenciaSonda() de \"GAYTRCA\"", "TGYARTC",
                    new Sonda("S", "GAYTRCA").getSecuenciaSonda());
            Sonda virusA = new Sonda("Virus A", "ACCGTTAGCTGA");
            Comprobador.comprobar("hibridaCon (diana en la misma hebra)", true,
                    virusA.hibridaCon(new Lectura("L1", "GGAACCGTTAGCTGACCAT")));
            Comprobador.comprobar("hibridaCon (diana en la otra hebra: TCAGCTAACGGT)", true,
                    virusA.hibridaCon(new Lectura("L2", "GGATTCAGCTAACGGTCCAT")));
            Comprobador.comprobar("hibridaCon (sin diana)", false,
                    virusA.hibridaCon(new Lectura("L3", "GGATTCAGCTTACGGTCCAT")));
            Muestra otraHebra = new Muestra("X1 (otra hebra)", DatosEjemplo.hace(1),
                    new Lectura[] { new Lectura("X1-L1", "GGATTCAGCTAACGGTCCAT") });
            Comprobador.comprobar("panel de patógenos con una lectura de la otra hebra",
                    new boolean[][] { { true, false, false }, { false, false, false }, { false, false, false } },
                    DatosEjemplo.panelDePatogenos().hibridar(otraHebra));
            Comprobador.comprobar("chip de expresión con M5 (con las dos hebras, sigue dibujando «ADN»)",
                    aMatriz(DatosEjemplo.FORMA_MUESTRA_DE_EXPRESION),
                    DatosEjemplo.chipDeExpresion().hibridar(DatosEjemplo.muestraDeExpresion()));
        } catch (Exception e) {
            Comprobador.fallo("No debería lanzarse ninguna excepción: " + e.getMessage());
        }
    }

    private static void probarIntensidadesYDetectadas() {
        Comprobador.titulo("A2 · Intensidades y sondas detectadas");
        try {
            Muestra[] muestras = DatosEjemplo.muestras();
            Microarray panel = DatosEjemplo.panelDePatogenos();
            Comprobador.comprobar("intensidades (M2: el Virus B está en dos lecturas)",
                    new int[][] { { 0, 2, 0 }, { 0, 1, 0 }, { 0, 1, 0 } }, panel.intensidades(muestras[2]));
            Comprobador.comprobar("sondasDetectadas (M2)",
                    new String[] { "Virus B (variantes 1 y 2)", "Bacteria E (cepas 1 y 2)", "Control positivo" },
                    new Analisis(muestras[2], panel).sondasDetectadas());
            Comprobador.comprobar("sondasDetectadas (M4)", new String[] { "Control positivo" },
                    new Analisis(muestras[4], panel).sondasDetectadas());
            Comprobador.comprobar("sondasDetectadas (chip de prueba)", new String[] { "Exacta", "Con N", "Con Y y R" },
                    new Analisis(DatosEjemplo.muestraDePrueba(), DatosEjemplo.chipDePrueba()).sondasDetectadas());
        } catch (Exception e) {
            Comprobador.fallo("No debería lanzarse ninguna excepción: " + e.getMessage());
        }
    }

    /** Convierte un dibujo hecho con {@code #} y {@code .} en una matriz de booleanos. */
    private static boolean[][] aMatriz(String[] dibujo) {
        boolean[][] matriz = new boolean[dibujo.length][];
        for (int fila = 0; fila < dibujo.length; fila++) {
            matriz[fila] = new boolean[dibujo[fila].length()];
            for (int columna = 0; columna < dibujo[fila].length(); columna++) {
                matriz[fila][columna] = dibujo[fila].charAt(columna) == '#';
            }
        }
        return matriz;
    }
}
