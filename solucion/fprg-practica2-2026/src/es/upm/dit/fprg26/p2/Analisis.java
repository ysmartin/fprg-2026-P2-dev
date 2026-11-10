package es.upm.dit.fprg26.p2;

import module java.base;

/**
 * El resultado de analizar una muestra con un microarray en una fecha determinada.
 * <p>
 * Al crear el análisis se hibridan todas las lecturas de la muestra con el microarray
 * y se guarda qué puntos del chip se han iluminado. Además, se registran los datos de
 * procedencia: la fecha del análisis, que no puede ser anterior a la de captura de la
 * muestra.
 * </p>
 */
public class Analisis {

    /** Muestra analizada. */
    private final Muestra muestra;

    /** Microarray con el que se ha analizado la muestra. */
    private final Microarray microarray;

    /** Fecha en que se hizo el análisis. */
    private final LocalDate fechaAnalisis;

    /** Puntos del chip iluminados: {@code iluminados[fila][columna]}. */
    private final boolean[][] iluminados;

    /**
     * Analiza una muestra con un microarray: hibrida todas las lecturas de la muestra
     * con el chip (ver {@link Microarray#hibridar(Muestra)}) y guarda el resultado.
     *
     * @param muestra muestra que se analiza; no puede ser nula
     * @param microarray microarray con el que se analiza; no puede ser nulo
     * @param fechaAnalisis fecha del análisis; no puede ser nula ni anterior a la fecha
     *                      de captura de la muestra
     * @throws Exception si alguno de los datos no es válido
     */
    public Analisis(Muestra muestra, Microarray microarray, LocalDate fechaAnalisis) throws Exception {
        if (muestra == null || microarray == null || fechaAnalisis == null) {
            throw new Exception("La muestra, el microarray y la fecha del análisis no pueden ser nulos");
        }
        if (fechaAnalisis.isBefore(muestra.getFechaCaptura())) {
            throw new Exception("La fecha del análisis (" + fechaAnalisis + ") no puede ser anterior "
                    + "a la de captura de la muestra (" + muestra.getFechaCaptura() + ")");
        }
        this.muestra = muestra;
        this.microarray = microarray;
        this.fechaAnalisis = fechaAnalisis;
        this.iluminados = microarray.hibridar(muestra);
    }

    /**
     * Devuelve la muestra analizada.
     *
     * @return muestra analizada
     */
    public Muestra getMuestra() {
        return muestra;
    }

    /**
     * Devuelve el microarray con el que se ha analizado la muestra.
     *
     * @return microarray del análisis
     */
    public Microarray getMicroarray() {
        return microarray;
    }

    /**
     * Devuelve la fecha del análisis.
     *
     * @return fecha del análisis
     */
    public LocalDate getFechaAnalisis() {
        return fechaAnalisis;
    }

    /**
     * Devuelve los puntos del chip que se han iluminado.
     *
     * @return matriz del mismo tamaño que la cuadrícula del microarray, con {@code true}
     *         en los puntos iluminados
     */
    public boolean[][] getIluminados() {
        return iluminados;
    }

    /**
     * Devuelve el número de días transcurridos entre la captura de la muestra y el análisis.
     * <p>
     * Por ejemplo, si la muestra se capturó el 2026-10-30 y se analizó el 2026-11-02,
     * devuelve 3. Puede calcularse con
     * {@code ChronoUnit.DAYS.between(fechaInicial, fechaFinal)}.
     * </p>
     *
     * @return días entre la fecha de captura y la del análisis (0 si son el mismo día)
     */
    public long getDiasDesdeCaptura() {
        return ChronoUnit.DAYS.between(muestra.getFechaCaptura(), fechaAnalisis);
    }

    /**
     * Cuenta cuántos puntos del chip se han iluminado.
     *
     * @return número de puntos iluminados
     */
    public int contarIluminados() {
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
     * Devuelve un resumen del análisis: la muestra, el microarray, las fechas y el chip,
     * con {@code #} en los puntos iluminados y {@code .} en el resto.
     *
     * @return resumen del análisis en varias líneas
     */
    @Override
    public String toString() {
        String texto = "Análisis de " + muestra + " con " + microarray + "\n"
                + "Fecha del análisis: " + fechaAnalisis + " (" + getDiasDesdeCaptura()
                + " días después de la captura)\n"
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
