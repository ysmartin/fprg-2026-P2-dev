package es.upm.dit.fprg26.p2;

/**
 * Una sonda de un microarray: un fragmento corto de ADN que reconoce las lecturas que
 * contienen su <em>diana</em>.
 * <p>
 * En el laboratorio, una sonda se une a los fragmentos de la muestra que son
 * complementarios a ella. Para simplificar, cada sonda guarda directamente el
 * <strong>patrón</strong> de su diana, tal como aparece en las lecturas, y la búsqueda
 * compara cada símbolo del patrón con la base que ocupa la misma posición de la lectura.
 * </p>
 * <p>
 * Las sondas pueden ser <em>degeneradas</em>: su patrón puede contener comodines del
 * código IUPAC (ver {@link Iupac}), que sirven, por ejemplo, para detectar a la vez las
 * variantes de un patógeno que se diferencian en una posición conocida:
 * </p>
 * <ul>
 *   <li>{@code N} encaja con cualquier base de la lectura, incluida una {@code N}.</li>
 *   <li>{@code R} encaja con cualquier purina ({@code A} o {@code G}).</li>
 *   <li>{@code Y} encaja con cualquier pirimidina ({@code C} o {@code T}).</li>
 *   <li>{@code A}, {@code C}, {@code G} y {@code T} solo encajan con la misma base.</li>
 * </ul>
 * <p>
 * Una base indeterminada ({@code N}) de la lectura solo encaja con el comodín {@code N}
 * del patrón: como no se sabe qué base es, no puede asegurarse que coincida con ninguna
 * otra.
 * </p>
 */
public class Sonda {

    /** Nombre de la sonda (por ejemplo, el del patógeno o el gen que detecta). */
    private final String nombre;

    /** Patrón de la diana de la sonda, con símbolos de {@link Iupac#SIMBOLOS_DE_PATRON}. */
    private final String patron;

    /**
     * Crea una sonda.
     *
     * @param nombre nombre de la sonda; no puede ser nulo ni estar en blanco
     * @param patron patrón de la diana; no puede ser nulo ni vacío, y solo puede contener
     *               los símbolos {@code A}, {@code C}, {@code G}, {@code T}, {@code N},
     *               {@code R} e {@code Y} (ver {@link Iupac#esSimboloDePatron(char)})
     * @throws Exception si el nombre o el patrón no son válidos
     */
    public Sonda(String nombre, String patron) throws Exception {
        if (nombre == null || nombre.isBlank()) {
            throw new Exception("El nombre de la sonda no puede ser nulo ni estar en blanco");
        }
        if (patron == null || patron.isEmpty()) {
            throw new Exception("El patrón de la sonda " + nombre + " no puede ser nulo ni vacío");
        }
        for (int i = 0; i < patron.length(); i++) {
            if (!Iupac.esSimboloDePatron(patron.charAt(i))) {
                throw new Exception("El patrón de la sonda " + nombre + " contiene el carácter '"
                        + patron.charAt(i) + "', que no es " + Iupac.SIMBOLOS_DE_PATRON);
            }
        }
        this.nombre = nombre;
        this.patron = patron;
    }

    /**
     * Devuelve el nombre de la sonda.
     *
     * @return nombre de la sonda
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el patrón de la diana de la sonda.
     *
     * @return patrón de la sonda
     */
    public String getPatron() {
        return patron;
    }

    /**
     * Devuelve la longitud del patrón (su número de símbolos).
     *
     * @return número de símbolos del patrón
     */
    public int getLongitud() {
        return patron.length();
    }

    /**
     * Indica si un símbolo de un patrón encaja con una base de una lectura, según las
     * reglas descritas en la documentación de la clase.
     * <p>
     * Por ejemplo, {@code encaja('R', 'G')} y {@code encaja('N', 'N')} devuelven
     * {@code true}; {@code encaja('R', 'C')} y {@code encaja('A', 'N')}, {@code false}.
     * </p>
     *
     * @param simbolo símbolo del patrón ({@code A}, {@code C}, {@code G}, {@code T},
     *                {@code N}, {@code R} o {@code Y})
     * @param base base de la lectura ({@code A}, {@code C}, {@code G}, {@code T} o {@code N})
     * @return {@code true} si el símbolo encaja con la base; {@code false} en otro caso
     * @throws Exception si el símbolo no es un símbolo de patrón o la base no es un
     *                   símbolo de lectura (ver {@link Iupac})
     */
    public static boolean encaja(char simbolo, char base) throws Exception {
        if (!Iupac.esSimboloDePatron(simbolo) || !Iupac.esSimboloDeLectura(base)) {
            throw new Exception("No se puede comparar el símbolo '" + simbolo + "' con la base '" + base
                    + "': los símbolos de patrón son " + Iupac.SIMBOLOS_DE_PATRON
                    + " y los de lectura, " + Iupac.SIMBOLOS_DE_LECTURA);
        }
        switch (simbolo) {
            case Iupac.INDETERMINADA -> { return true; }
            case Iupac.PURINA -> { return Iupac.esPurina(base); }
            case Iupac.PIRIMIDINA -> { return Iupac.esPirimidina(base); }
            default -> { return simbolo == base; }
        }
    }

    /**
     * Indica si el patrón de la sonda encaja con una lectura a partir de una posición
     * de la lectura: es decir, si cada símbolo del patrón encaja con la base que ocupa
     * la posición correspondiente de la lectura.
     * <p>
     * Por ejemplo, el patrón {@code "TRC"} encaja con la lectura {@code "GATACA"} a partir
     * de la posición 2 ({@code T} con {@code T}, {@code R} con {@code A}, {@code C} con
     * {@code C}), pero no a partir de la posición 0. Si el patrón no cabe en la lectura
     * a partir de esa posición (o la posición es negativa), no encaja. Para ello, captura
     * las excepciones que lanzan {@link Lectura#getBase(int)} y {@link #encaja(char, char)}.
     * </p>
     *
     * @param lectura lectura en la que se busca el patrón
     * @param inicio posición de la lectura en la que se coloca el primer símbolo del patrón
     * @return {@code true} si el patrón encaja con la lectura a partir de esa posición;
     *         {@code false} en otro caso
     */
    public boolean encajaEn(Lectura lectura, int inicio) {
        try {
            for (int i = 0; i < patron.length(); i++) {
                if (!encaja(patron.charAt(i), lectura.getBase(inicio + i))) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            // El patrón se sale de la lectura (o algún símbolo no es válido): no encaja
            return false;
        }
    }

    /**
     * Busca la primera posición de una lectura a partir de la cual encaja el patrón de
     * la sonda.
     * <p>
     * Por ejemplo, el patrón {@code "TRC"} se encuentra en la lectura {@code "GATACATGCA"}
     * en la posición 2 (también encajaría en la 6, pero se devuelve la primera).
     * </p>
     *
     * @param lectura lectura en la que se busca el patrón
     * @return primera posición de la lectura en la que encaja el patrón, o -1 si no
     *         encaja en ninguna
     */
    public int buscarEn(Lectura lectura) {
        for (int inicio = 0; inicio <= lectura.getLongitud() - patron.length(); inicio++) {
            if (encajaEn(lectura, inicio)) {
                return inicio;
            }
        }
        return -1;
    }

    /**
     * Indica si la sonda hibrida con una lectura, es decir, si su patrón encaja con la
     * lectura en alguna posición.
     *
     * @param lectura lectura que se analiza
     * @return {@code true} si el patrón aparece en la lectura; {@code false} en otro caso
     */
    public boolean hibridaCon(Lectura lectura) {
        return buscarEn(lectura) >= 0;
    }

    /**
     * Devuelve una descripción de la sonda: su nombre y su patrón.
     *
     * @return descripción de la sonda, por ejemplo, {@code "Virus B [GATRCA]"}
     */
    @Override
    public String toString() {
        return nombre + " [" + patron + "]";
    }
}
