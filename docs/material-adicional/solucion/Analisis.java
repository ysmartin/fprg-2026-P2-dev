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

    /** Puntos del chip iluminados: {@code iluminados[fila][columna]}. */
    private final boolean[][] iluminados;

    /**
     * Analiza una muestra con un microarray en la fecha actual: hibrida todas las lecturas
     * de la muestra con el chip (ver {@link Microarray#hibridar(Muestra)}) y guarda el
     * resultado.
     * <p>
     * La fecha del análisis es la fecha actual, {@code LocalDate.now()}. No puede ser anterior
     * a la de captura de la muestra, porque {@link Muestra} no admite fechas de captura futuras.
     * </p>
     *
     * @param muestra muestra que se analiza; no puede ser nula
     * @param microarray microarray con el que se analiza; no puede ser nulo
     * @throws Exception si la muestra o el microarray son nulos
     */
    Analisis(Muestra muestra, Microarray microarray) throws Exception {
        if (muestra == null || microarray == null) {
            throw new Exception("La muestra y el microarray del análisis no pueden ser nulos");
        }
        this.muestra = muestra;
        this.microarray = microarray;
        this.fechaAnalisis = LocalDate.now();
        this.iluminados = microarray.hibridar(muestra);
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
     * @return copia de la matriz del mismo tamaño que la cuadrícula del microarray, con
     *         {@code true} en los puntos iluminados
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
     * devuelve 3. Puede calcularse con
     * {@code ChronoUnit.DAYS.between(fechaInicial, fechaFinal)}.
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
     * <em>(Ampliación A2)</em> Devuelve los nombres de las sondas de los puntos iluminados,
     * recorriendo el chip por filas (de izquierda a derecha y de arriba abajo).
     *
     * @return array con los nombres de las sondas detectadas (vacío si no se ha iluminado
     *         ningún punto)
     * @throws Exception si no se puede obtener alguna sonda del microarray
     */
    String[] sondasDetectadas() throws Exception {
        String[] nombres = new String[contarIluminados()];
        int siguiente = 0;
        for (int fila = 0; fila < iluminados.length; fila++) {
            for (int columna = 0; columna < iluminados[fila].length; columna++) {
                if (iluminados[fila][columna]) {
                    nombres[siguiente] = microarray.getSonda(fila, columna).getNombre();
                    siguiente++;
                }
            }
        }
        return nombres;
    }

    /**
     * Devuelve un resumen del análisis: la muestra, el microarray, las fechas y el chip,
     * con {@code #} en los puntos iluminados y {@code .} en el resto.
     *
     * @return resumen del análisis en varias líneas
     */
    @Override
    public String toString() {
        String texto = "Análisis de " + muestra + " con " + microarray + "\n"
                + "Fecha del análisis: " + fechaAnalisis + " (días desde la captura: " + getDiasDesdeCaptura()
                + ")\n"
                + "Puntos iluminados: " + contarIluminados() + "\n";
        if (iluminados == null) {
            return texto + "(no hay matriz de puntos iluminados)\n";
        }
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
