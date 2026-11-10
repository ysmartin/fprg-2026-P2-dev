package es.upm.dit.fprg26.p2;

/**
 * Pruebas de los métodos {@code hibridar} de la clase {@link Microarray}, con los datos
 * de ejemplo de {@link DatosEjemplo}. Los resultados se escriben con {@code #} para los
 * puntos iluminados y {@code .} para los apagados, una fila entre corchetes. Añada sus
 * propios casos de prueba en {@link #pruebasPropias()}.
 */
class PruebaMicroarray {

    /**
     * Ejecuta las pruebas.
     */
    void main() {
        probarHibridarLectura();
        probarHibridarMuestra();
        pruebasPropias();
        Comprobador.resumen();
    }

    private static void probarHibridarLectura() {
        Comprobador.titulo("hibridar(Lectura), con el chip de prueba (GATTACA | GANTACA | GAYTRCA)");
        try {
            Microarray chip = DatosEjemplo.chipDePrueba();
            Muestra muestra = DatosEjemplo.muestraDePrueba();
            Comprobador.comprobar("GATTACA", new boolean[][] { { true, true, true } },
                    chip.hibridar(muestra.getLectura(0)));
            Comprobador.comprobar("GANTACA", new boolean[][] { { false, true, false } },
                    chip.hibridar(muestra.getLectura(1)));
            Comprobador.comprobar("TTGACTGCATT", new boolean[][] { { false, false, true } },
                    chip.hibridar(muestra.getLectura(2)));
        } catch (Exception e) {
            Comprobador.fallo("Se ha producido una excepción (en los datos de ejemplo o en hibridar): " + e);
        }
    }

    private static void probarHibridarMuestra() {
        Comprobador.titulo("hibridar(Muestra)");
        try {
            Comprobador.comprobar("muestra de prueba con el chip de prueba", new boolean[][] { { true, true, true } },
                    DatosEjemplo.chipDePrueba().hibridar(DatosEjemplo.muestraDePrueba()));
            Muestra[] muestras = DatosEjemplo.muestras();
            Microarray panel = DatosEjemplo.panelDePatogenos();
            Comprobador.comprobar("M1 con el panel de patógenos (Virus A y control positivo)",
                    new boolean[][] { { true, false, false }, { false, false, false }, { false, true, false } },
                    panel.hibridar(muestras[1]));
            Comprobador.comprobar("M3 con el panel de patógenos (lecturas con N)",
                    new boolean[][] { { false, false, false }, { true, false, false }, { true, true, false } },
                    panel.hibridar(muestras[3]));
            Comprobador.comprobar("R1 con el chip de dianas de restricción (2x4)",
                    new boolean[][] { { true, false, false, false }, { true, true, true, true } },
                    DatosEjemplo.chipDeDianasDeRestriccion().hibridar(muestras[5]));
            boolean[][] esperados = aMatriz(DatosEjemplo.FORMA_MUESTRA_DE_EXPRESION);
            boolean[][] obtenidos = DatosEjemplo.chipDeExpresion().hibridar(muestras[6]);
            int d = DatosEjemplo.COLUMNA_D;
            int n = DatosEjemplo.COLUMNA_N;
            int lado = DatosEjemplo.LADO_CHIP_DE_EXPRESION;
            Comprobador.comprobar("M5 con el chip de expresión de 20x20, letra A (sondas sin comodines)",
                    columnas(esperados, 0, d), columnas(obtenidos, 0, d));
            Comprobador.comprobar("M5 con el chip de expresión de 20x20, letra D (sondas con comodines)",
                    columnas(esperados, d, n), columnas(obtenidos, d, n));
            Comprobador.comprobar("M5 con el chip de expresión de 20x20, letra N (dianas en la otra hebra)",
                    columnas(esperados, n, lado), columnas(obtenidos, n, lado));
        } catch (Exception e) {
            Comprobador.fallo("Se ha producido una excepción (en los datos de ejemplo o en hibridar): " + e);
        }
    }

    /** Devuelve las columnas de una matriz desde {@code desde} (incluida) hasta {@code hasta} (excluida). */
    private static boolean[][] columnas(boolean[][] matriz, int desde, int hasta) {
        if (matriz == null) {
            return null;
        }
        boolean[][] parte = new boolean[matriz.length][hasta - desde];
        for (int fila = 0; fila < matriz.length; fila++) {
            for (int columna = desde; columna < hasta; columna++) {
                parte[fila][columna - desde] = matriz[fila][columna];
            }
        }
        return parte;
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

    /**
     * Sus propios casos de prueba.
     */
    private static void pruebasPropias() {
        Comprobador.titulo("Pruebas propias");
        // TODO: añada aquí sus propios casos de prueba
    }
}
