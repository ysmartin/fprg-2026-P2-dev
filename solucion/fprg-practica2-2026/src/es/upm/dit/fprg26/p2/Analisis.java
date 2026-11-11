package es.upm.dit.fprg26.p2;

import module java.base;

/**
 * El resultado de analizar una muestra con un microarray.
 * <p>
 * Al crear el análisis se hibridan todas las lecturas de la muestra con el microarray
 * y se guarda qué puntos del chip se han iluminado. Además, se registra un dato de
 * procedencia: la fecha del análisis, que es la del día en que se hace (la fecha actual).
 * </p>
 */
class Analisis {

    /** Muestra analizada. */
    private final Muestra muestra;

    /** Microarray con el que se ha analizado la muestra. */
    private final Microarray microarray;

    /** Fecha en que se hizo el análisis. */
    private final LocalDate fechaAnalisis;

    /** Puntos del chip iluminados: iluminados[fila][columna]. */
    private final boolean[][] iluminados;

    /**
     * Analiza una muestra con un microarray en la fecha actual: hibrida todas las lecturas
     * de la muestra con el chip (ver {@link Microarray#hibridar(Muestra)}) y guarda el
     * resultado.
     * <p>
     * La fecha del análisis es la fecha actual, LocalDate.now(). No puede ser anterior a la
     * de captura de la muestra, porque {@link Muestra} no admite fechas de captura futuras.
     * </p>
     *
     * @param muestra muestra que se analiza; no puede ser nula
     * @param microarray microarray con el que se analiza; no puede ser nulo
     * @throws Exception si la muestra o el microarray son nulos, o si
     *         {@link Microarray#hibridar(Muestra)} no devuelve una matriz del mismo tamaño
     *         que la cuadrícula del microarray (por ejemplo, si devuelve null)
     */
    Analisis(Muestra muestra, Microarray microarray) throws Exception {
        if (muestra == null || microarray == null) {
            throw new Exception("La muestra y el microarray del análisis no pueden ser nulos");
        }
        this.muestra = muestra;
        this.microarray = microarray;
        this.fechaAnalisis = LocalDate.now();
        this.iluminados = microarray.hibridar(muestra);
        if (iluminados == null) {
            throw new Exception("Microarray.hibridar(Muestra) devuelve null, y no la matriz de puntos iluminados");
        }
        if (iluminados.length != microarray.getFilas()) {
            throw new Exception("Microarray.hibridar(Muestra) devuelve una matriz de " + iluminados.length
                    + " filas, pero el microarray tiene " + microarray.getFilas());
        }
        for (boolean[] fila : iluminados) {
            if (fila == null || fila.length != microarray.getColumnas()) {
                throw new Exception("Alguna fila de la matriz que devuelve Microarray.hibridar(Muestra) no tiene "
                        + microarray.getColumnas() + " columnas, como el microarray");
            }
        }
    }

    /**
     * Devuelve la muestra analizada.
     *
     * @return muestra analizada
     */
    Muestra getMuestra() {
        return muestra;
    }

    /**
     * Devuelve el microarray con el que se ha analizado la muestra.
     *
     * @return microarray del análisis
     */
    Microarray getMicroarray() {
        return microarray;
    }

    /**
     * Devuelve la fecha del análisis (la del día en que se creó este objeto).
     *
     * @return fecha del análisis
     */
    LocalDate getFechaAnalisis() {
        return fechaAnalisis;
    }

    /**
     * Devuelve los puntos del chip que se han iluminado.
     * <p>
     * Devuelve una copia de la matriz (copiando también cada fila): modificarla no cambia el
     * resultado del análisis.
     * </p>
     *
     * @return copia de la matriz del mismo tamaño que la cuadrícula del microarray, con true
     *         en los puntos iluminados
     */
    boolean[][] getIluminados() {
        boolean[][] copia = new boolean[iluminados.length][];
        for (int fila = 0; fila < iluminados.length; fila++) {
            copia[fila] = Arrays.copyOf(iluminados[fila], iluminados[fila].length);
        }
        return copia;
    }

    /**
     * Devuelve el número de días transcurridos entre la captura de la muestra y el análisis.
     * <p>
     * Por ejemplo, si la muestra se capturó el 2026-10-30 y se analiza el 2026-11-02,
     * devuelve 3. Puede calcularse con ChronoUnit.DAYS.between(fechaInicial, fechaFinal).
     * </p>
     *
     * @return días entre la fecha de captura y la del análisis (0 si son el mismo día)
     */
    long getDiasDesdeCaptura() {
        return ChronoUnit.DAYS.between(muestra.getFechaCaptura(), fechaAnalisis);
    }

    /**
     * Cuenta cuántos puntos del chip se han iluminado.
     *
     * @return número de puntos iluminados
     */
    int contarIluminados() {
        int iluminadosEnTotal = 0;
        for (int fila = 0; fila < iluminados.length; fila++) {
            for (int columna = 0; columna < iluminados[fila].length; columna++) {
                if (iluminados[fila][columna]) {
                    iluminadosEnTotal++;
                }
            }
        }
        return iluminadosEnTotal;
    }

    /**
     * Devuelve un resumen del análisis: la muestra, el microarray, las fechas y el chip, con
     * `#` en los puntos iluminados y `.` en el resto.
     *
     * @return resumen del análisis en varias líneas
     */
    @Override
    public String toString() {
        String texto = "Análisis de " + muestra + " con " + microarray + "\n"
                + "Fecha del análisis: " + fechaAnalisis + " (días desde la captura: " + getDiasDesdeCaptura()
                + ")\n"
                + "Puntos iluminados: " + contarIluminados() + "\n";
        for (boolean[] fila : iluminados) {
            for (boolean iluminado : fila) {
                if (iluminado) {
                    texto += "#";
                } else {
                    texto += ".";
                }
            }
            texto += "\n";
        }
        return texto;
    }
}
