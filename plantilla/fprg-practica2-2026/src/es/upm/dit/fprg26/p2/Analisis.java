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
public class Analisis {

    /** Muestra analizada. */
    private Muestra muestra;

    /** Microarray con el que se ha analizado la muestra. */
    private Microarray microarray;

    /** Fecha en que se hizo el análisis. */
    private LocalDate fechaAnalisis;

    /** Puntos del chip iluminados: {@code iluminados[fila][columna]}. */
    private boolean[][] iluminados;

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
    public Analisis(Muestra muestra, Microarray microarray) throws Exception {
        // TODO: desarrollar
    }

    /**
     * Devuelve la muestra analizada.
     *
     * @return muestra analizada
     */
    public Muestra getMuestra() {
        // TODO: desarrollar
        return null;
    }

    /**
     * Devuelve el microarray con el que se ha analizado la muestra.
     *
     * @return microarray del análisis
     */
    public Microarray getMicroarray() {
        // TODO: desarrollar
        return null;
    }

    /**
     * Devuelve la fecha del análisis (la del día en que se creó este objeto).
     *
     * @return fecha del análisis
     */
    public LocalDate getFechaAnalisis() {
        // TODO: desarrollar
        return null;
    }

    /**
     * Devuelve los puntos del chip que se han iluminado.
     *
     * @return matriz del mismo tamaño que la cuadrícula del microarray, con {@code true}
     *         en los puntos iluminados
     */
    public boolean[][] getIluminados() {
        // TODO: desarrollar
        return null;
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
    public long getDiasDesdeCaptura() {
        // TODO: desarrollar
        return 0;
    }

    /**
     * Cuenta cuántos puntos del chip se han iluminado.
     *
     * @return número de puntos iluminados
     */
    public int contarIluminados() {
        // TODO: desarrollar
        return 0;
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
