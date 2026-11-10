package es.upm.dit.fprg26.p2;

/**
 * Un microarray: un chip con sondas fijadas en una cuadrícula de filas y columnas
 * (una matriz de {@link Sonda}).
 * <p>
 * Al analizar una muestra, cada punto del chip se ilumina si su sonda hibrida con
 * alguna de las lecturas de la muestra. El resultado se representa con una matriz de
 * {@code boolean} del mismo tamaño que la cuadrícula: {@code true} en los puntos
 * iluminados.
 * </p>
 */
class Microarray {

    /** Nombre del microarray. */
    private final String nombre;

    /** Sondas del chip, por filas: {@code sondas[fila][columna]}. */
    private final Sonda[][] sondas;

    /**
     * Crea un microarray.
     *
     * @param nombre nombre del microarray; no puede ser nulo ni estar en blanco
     * @param sondas sondas del chip, por filas; la matriz tiene que tener al menos una
     *               fila y una columna, todas las filas tienen que tener el mismo número
     *               de sondas, y ninguna sonda puede ser nula
     * @throws Exception si alguno de los datos no es válido
     */
    Microarray(String nombre, Sonda[][] sondas) throws Exception {
        if (nombre == null || nombre.isBlank()) {
            throw new Exception("El nombre del microarray no puede ser nulo ni estar en blanco");
        }
        if (sondas == null || sondas.length == 0 || sondas[0] == null || sondas[0].length == 0) {
            throw new Exception("El microarray " + nombre + " tiene que tener al menos una fila y una columna");
        }
        for (int fila = 0; fila < sondas.length; fila++) {
            if (sondas[fila] == null || sondas[fila].length != sondas[0].length) {
                throw new Exception("Todas las filas del microarray " + nombre
                        + " tienen que tener el mismo número de sondas");
            }
            for (int columna = 0; columna < sondas[fila].length; columna++) {
                if (sondas[fila][columna] == null) {
                    throw new Exception("La sonda (" + fila + ", " + columna + ") del microarray "
                            + nombre + " es nula");
                }
            }
        }
        this.nombre = nombre;
        this.sondas = sondas;
    }

    /**
     * Devuelve el nombre del microarray.
     *
     * @return nombre del microarray
     */
    String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el número de filas de la cuadrícula.
     *
     * @return número de filas
     */
    int getFilas() {
        return sondas.length;
    }

    /**
     * Devuelve el número de columnas de la cuadrícula.
     *
     * @return número de columnas
     */
    int getColumnas() {
        return sondas[0].length;
    }

    /**
     * Devuelve la sonda que ocupa un punto de la cuadrícula.
     *
     * @param fila fila del punto, empezando a contar en 0
     * @param columna columna del punto, empezando a contar en 0
     * @return sonda de ese punto
     * @throws Exception si el punto está fuera de la cuadrícula
     */
    Sonda getSonda(int fila, int columna) throws Exception {
        if (fila < 0 || fila >= getFilas() || columna < 0 || columna >= getColumnas()) {
            throw new Exception("El punto (" + fila + ", " + columna + ") está fuera del microarray "
                    + nombre + ", de " + getFilas() + "x" + getColumnas());
        }
        return sondas[fila][columna];
    }

    /**
     * Analiza una única lectura: indica qué puntos del chip se iluminan con ella.
     * <p>
     * {@link Sonda#hibridaCon(Lectura)} puede lanzar una excepción (con una sonda y una
     * lectura válidas, solo si hay algún error en el código de {@code Sonda}). Este método la
     * captura con un bloque {@code try}/{@code catch}: si no se puede saber si una sonda
     * hibrida con la lectura, su punto no se ilumina.
     * </p>
     *
     * @param lectura lectura que se analiza
     * @return matriz del mismo tamaño que la cuadrícula, con {@code true} en los puntos
     *         cuya sonda hibrida con la lectura
     */
    boolean[][] hibridar(Lectura lectura) {
        boolean[][] iluminados = new boolean[getFilas()][getColumnas()];
        for (int fila = 0; fila < getFilas(); fila++) {
            for (int columna = 0; columna < getColumnas(); columna++) {
                // TODO: esta línea no compila, porque hibridaCon puede lanzar una excepción:
                // captúrela (si se produce, el punto no se ilumina)
                iluminados[fila][columna] = sondas[fila][columna].hibridaCon(lectura);
            }
        }
        return iluminados;
    }

    /**
     * Analiza todas las lecturas de una muestra: un punto del chip se ilumina si su
     * sonda hibrida con <em>alguna</em> de las lecturas.
     *
     * @param muestra muestra que se analiza
     * @return matriz del mismo tamaño que la cuadrícula, con {@code true} en los puntos
     *         cuya sonda hibrida con al menos una lectura de la muestra
     */
    boolean[][] hibridar(Muestra muestra) {
        // TODO: desarrollar
        return null;
    }

    /**
     * Devuelve una descripción del microarray: su nombre y su tamaño.
     *
     * @return descripción del microarray, por ejemplo, {@code "Panel A (3x3)"}
     */
    @Override
    public String toString() {
        return nombre + " (" + getFilas() + "x" + getColumnas() + ")";
    }
}
