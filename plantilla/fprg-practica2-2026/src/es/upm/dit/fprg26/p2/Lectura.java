package es.upm.dit.fprg26.p2;

/**
 * Una lectura: un fragmento de ADN de una muestra, tal como lo ha leído el secuenciador
 * (en un fichero FASTQ, la primera y la segunda línea de cada registro).
 * <p>
 * La secuencia solo puede contener los símbolos {@code A}, {@code C}, {@code G},
 * {@code T} y {@code N} (en mayúsculas). La {@code N} indica que el secuenciador no ha
 * podido determinar la base de esa posición. Las posiciones se numeran desde 0.
 * </p>
 * <p>
 * Una lectura no se puede modificar una vez creada.
 * </p>
 */
class Lectura {

    /** Identificador de la lectura (en un fichero FASTQ, lo que sigue a la {@code @} de la cabecera). */
    private final String id;

    /** Secuencia de bases leídas, por ejemplo, {@code "GATTACANGATTACA"}. */
    private final String secuencia;

    /**
     * Crea una lectura a partir de su identificador y su secuencia.
     *
     * @param id identificador de la lectura; no puede ser nulo ni estar en blanco
     * @param secuencia secuencia de bases leídas; no puede ser nula ni vacía, y solo
     *                  puede contener {@code A}, {@code C}, {@code G}, {@code T} y {@code N}
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
            if (!Iupac.esSimboloDeLectura(secuencia.charAt(i))) {
                throw new Exception("La lectura " + id + " contiene el carácter '" + secuencia.charAt(i)
                        + "' en la posición " + i + ", que no es " + Iupac.SIMBOLOS_DE_LECTURA);
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
     * @return base de esa posición ({@code A}, {@code C}, {@code G}, {@code T} o {@code N})
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
     * Cuenta cuántas bases indeterminadas ({@code N}) tiene la lectura.
     * <p>
     * Por ejemplo, la lectura {@code "GANTACNA"} tiene 2 bases indeterminadas.
     * </p>
     *
     * @return número de posiciones de la lectura que contienen {@code N}
     */
    int contarIndeterminadas() {
        // TODO: desarrollar
        return 0;
    }

    /**
     * Devuelve la lectura con el formato de las dos primeras líneas de un registro FASTQ:
     * la cabecera ({@code @} seguida del identificador) y la secuencia.
     *
     * @return representación de la lectura, por ejemplo, {@code "@L1\nGATTACA"}
     */
    @Override
    public String toString() {
        return "@" + id + "\n" + secuencia;
    }
}
