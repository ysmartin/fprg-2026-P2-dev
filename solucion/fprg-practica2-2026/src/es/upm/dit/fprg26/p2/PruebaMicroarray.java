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
            Muestra[] muestras = DatosEjemplo.muestras();
            Microarray panel = DatosEjemplo.panelDePatogenos();
            probarHibridar("muestra de prueba con el chip de prueba", DatosEjemplo.chipDePrueba(), muestras[0],
                    new boolean[][] { { true, true, true } });
            probarHibridar("M1 con el panel de patógenos (Virus A y control positivo)", panel, muestras[1],
                    new boolean[][] { { true, false, false }, { false, false, false }, { false, true, false } });
            probarHibridar("M3 con el panel de patógenos (lecturas con N)", panel, muestras[3],
                    new boolean[][] { { false, false, false }, { true, false, false }, { true, true, false } });
            probarHibridar("R1 con el chip de dianas de restricción (2x4)", DatosEjemplo.chipDeDianasDeRestriccion(),
                    muestras[5], new boolean[][] { { true, false, false, false }, { true, true, true, true } });
            probarLetras(DatosEjemplo.chipDeCribado(), muestras[6]);
        } catch (Exception e) {
            Comprobador.fallo("Se ha producido una excepción al crear los datos de ejemplo "
                    + "(¿lanza el constructor de Sonda una excepción con datos válidos?): " + e);
        }
    }

    /** Comprueba hibridar(Muestra) con un chip y una muestra: una excepción solo hace fallar este caso. */
    private static void probarHibridar(String descripcion, Microarray chip, Muestra muestra, boolean[][] esperado) {
        try {
            Comprobador.comprobar(descripcion, esperado, chip.hibridar(muestra));
        } catch (Exception e) {
            Comprobador.fallo(descripcion + " -> se ha producido una excepción en hibridar: " + e);
        }
    }

    /** Comprueba, letra a letra, la palabra que dibuja la muestra de cribado en el chip de cribado. */
    private static void probarLetras(Microarray chip, Muestra muestra) {
        String descripcion = "M5 con el chip de cribado de 20x20, letra ";
        boolean[][] obtenidos;
        try {
            obtenidos = chip.hibridar(muestra);
        } catch (Exception e) {
            Comprobador.fallo(descripcion + "A, D y N -> se ha producido una excepción en hibridar: " + e);
            return;
        }
        boolean[][] esperados = aMatriz(DatosEjemplo.FORMA_MUESTRA_DE_CRIBADO);
        int d = DatosEjemplo.COLUMNA_D;
        int n = DatosEjemplo.COLUMNA_N;
        int lado = DatosEjemplo.LADO_CHIP_DE_CRIBADO;
        Comprobador.comprobar(descripcion + "A (sondas sin comodines)",
                columnas(esperados, 0, d), columnas(obtenidos, 0, d));
        Comprobador.comprobar(descripcion + "D (sondas con comodines)",
                columnas(esperados, d, n), columnas(obtenidos, d, n));
        Comprobador.comprobar(descripcion + "N (dianas en la otra hebra)",
                columnas(esperados, n, lado), columnas(obtenidos, n, lado));
    }

    /**
     * Devuelve las columnas de una matriz desde {@code desde} (incluida) hasta {@code hasta}
     * (excluida). Si alguna fila es nula o no llega hasta {@code hasta}, se copia tal como
     * esté (nula o más corta), para que la comprobación falle sin lanzar una excepción.
     */
    private static boolean[][] columnas(boolean[][] matriz, int desde, int hasta) {
        if (matriz == null) {
            return null;
        }
        boolean[][] parte = new boolean[matriz.length][];
        for (int fila = 0; fila < matriz.length; fila++) {
            if (matriz[fila] != null) {
                int fin = Math.min(hasta, matriz[fila].length);
                parte[fila] = new boolean[Math.max(0, fin - desde)];
                for (int columna = desde; columna < fin; columna++) {
                    parte[fila][columna - desde] = matriz[fila][columna];
                }
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
