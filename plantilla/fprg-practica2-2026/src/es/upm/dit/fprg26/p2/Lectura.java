package es.upm.dit.fprg26.p2;

/**
 * Una lectura: un fragmento de ADN de una muestra, tal como lo ha leído el secuenciador
 * (en un fichero FASTQ, la primera y la segunda línea de cada registro).
 * <p>
 * La secuencia solo puede contener los símbolos A, C, G, T y N (en mayúsculas). La N indica
 * que el secuenciador no ha podido determinar la base de esa posición. Las posiciones se
 * numeran desde 0.
 * </p>
 * <p>
 * Una lectura no se puede modificar una vez creada.
 * </p>
 */
class Lectura {

    /** Símbolos que pueden aparecer en una lectura: las cuatro bases y la base indeterminada. */
    static final String SIMBOLOS = "ACGTN";

    /** Identificador de la lectura (en un fichero FASTQ, lo que sigue a la `@` de la cabecera). */
    private final String id;

    /** Secuencia de bases leídas, por ejemplo, "GATTACANGATTACA". */
    private final String secuencia;

    /**
     * Crea una lectura a partir de su identificador y su secuencia.
     *
     * @param id identificador de la lectura; no puede ser nulo ni estar en blanco
     * @param secuencia secuencia de bases leídas; no puede ser nula ni vacía, y solo puede
     *                  contener A, C, G, T y N
     * @throws Exception si el identificador o la secuencia no son válidos
     */
    Lectura(String id, String secuencia) throws Exception {
        if (id == null || id.isBlank()) {
            throw new Exception("El identificador de la lectura no puede ser nulo ni estar en blanco");
        }
        if (secuencia == null || secuencia.isEmpty()) {
            throw new Exception("La secuencia de la lectura " + id + " no puede ser nula ni vacía");
        }
        for (int i = 0; i < secuencia.length(); i++) {
            if (!esSimboloValido(secuencia.charAt(i))) {
                throw new Exception("La lectura " + id + " contiene el carácter '" + secuencia.charAt(i)
                        + "' en la posición " + i + ", que no es " + SIMBOLOS);
            }
        }
        this.id = id;
        this.secuencia = secuencia;
    }

    /**
     * Devuelve el identificador de la lectura.
     *
     * @return identificador de la lectura
     */
    String getId() {
        return id;
    }

    /**
     * Devuelve la secuencia de bases leídas.
     *
     * @return secuencia de la lectura
     */
    String getSecuencia() {
        return secuencia;
    }

    /**
     * Devuelve la longitud de la lectura (su número de bases).
     *
     * @return número de bases de la lectura
     */
    int getLongitud() {
        return secuencia.length();
    }

    /**
     * Devuelve la base que ocupa una posición de la lectura.
     *
     * @param posicion posición de la base, empezando a contar en 0
     * @return base de esa posición (A, C, G, T o N)
     * @throws Exception si la posición está fuera de la lectura
     */
    char getBase(int posicion) throws Exception {
        if (posicion < 0 || posicion >= getLongitud()) {
            throw new Exception("La posición " + posicion + " está fuera de la lectura " + id
                    + ", de " + getLongitud() + " bases");
        }
        return secuencia.charAt(posicion);
    }

    /**
     * Indica si un carácter puede aparecer en una lectura.
     *
     * @param simbolo carácter que se comprueba
     * @return true si es A, C, G, T o N (en mayúsculas); false en otro caso
     */
    static boolean esSimboloValido(char simbolo) {
        return SIMBOLOS.indexOf(simbolo) >= 0;
    }

    /**
     * Devuelve la base complementaria de una base de una lectura: la que tiene enfrente en la
     * otra hebra del ADN. La A se empareja con la T, y la C con la G; la complementaria de
     * una base indeterminada (N) también es N.
     * <p>
     * Por ejemplo, Lectura.complementaria('G') devuelve 'C'.
     * </p>
     *
     * @param base base de una lectura (A, C, G, T o N)
     * @return base complementaria
     * @throws Exception si el carácter no es un símbolo de lectura
     */
    static char complementaria(char base) throws Exception {
        switch (base) {
            case 'A' -> { return 'T'; }
            case 'T' -> { return 'A'; }
            case 'C' -> { return 'G'; }
            case 'G' -> { return 'C'; }
            case 'N' -> { return 'N'; }
            default -> throw new Exception("El carácter '" + base + "' no es un símbolo de lectura ("
                    + SIMBOLOS + ")");
        }
    }

    /**
     * Devuelve la lectura con el formato de las dos primeras líneas de un registro FASTQ: la
     * cabecera (`@` seguida del identificador) y la secuencia.
     *
     * @return representación de la lectura, por ejemplo, "@L1\nGATTACA"
     */
    @Override
    public String toString() {
        return "@" + id + "\n" + secuencia;
    }
}
