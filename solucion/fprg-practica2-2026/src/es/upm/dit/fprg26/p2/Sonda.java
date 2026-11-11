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
 * código de ambigüedad de la IUPAC, que sirven, por ejemplo, para detectar a la vez las
 * variantes de un patógeno que se diferencian en una posición conocida:
 * </p>
 * <ul>
 *   <li>El comodín N encaja con cualquier base de la lectura, incluida una N.</li>
 *   <li>El comodín R encaja con cualquier purina (A o G).</li>
 *   <li>El comodín Y encaja con cualquier pirimidina (C o T).</li>
 *   <li>Las bases A, C, G y T solo encajan con la misma base.</li>
 * </ul>
 * <p>
 * Una base indeterminada (N) de la lectura solo encaja con el comodín N del patrón: como no
 * se sabe qué base es, no puede asegurarse que coincida con ninguna otra.
 * </p>
 * <p>
 * Además, una lectura puede proceder de cualquiera de las dos hebras del ADN de la muestra.
 * Si procede de la otra hebra, no contiene la diana tal cual, sino su <em>complementaria
 * inversa</em>: la secuencia de la otra hebra, leída en el mismo sentido. Por eso, el patrón
 * se busca en los dos sentidos (ver {@link #encajaEn(Lectura, int)}).
 * </p>
 */
class Sonda {

    /** Nombre de la sonda (por ejemplo, el del patógeno o el gen que detecta). */
    private final String nombre;

    /** Símbolos que pueden aparecer en un patrón: los de las lecturas y los comodines R e Y. */
    static final String SIMBOLOS = "ACGTNRY";

    /** Patrón de la diana de la sonda, con los símbolos de {@link #SIMBOLOS}. */
    private final String patron;

    /**
     * Crea una sonda.
     *
     * @param nombre nombre de la sonda; no puede ser nulo ni estar en blanco
     * @param patron patrón de la diana; no puede ser nulo ni vacío, y solo puede contener los
     *               símbolos A, C, G, T, N, R e Y (ver {@link #esSimboloValido(char)})
     * @throws Exception si el nombre o el patrón no son válidos
     */
    Sonda(String nombre, String patron) throws Exception {
        if (nombre == null || nombre.isBlank()) {
            throw new Exception("El nombre de la sonda no puede ser nulo ni estar en blanco");
        }
        if (patron == null || patron.isEmpty()) {
            throw new Exception("El patrón de la sonda " + nombre + " no puede ser nulo ni vacío");
        }
        for (int i = 0; i < patron.length(); i++) {
            if (!esSimboloValido(patron.charAt(i))) {
                throw new Exception("El patrón de la sonda " + nombre + " contiene el carácter '"
                        + patron.charAt(i) + "', que no es " + SIMBOLOS);
            }
        }
        this.nombre = nombre;
        this.patron = patron;
    }

    /**
     * Indica si un carácter puede aparecer en el patrón de una sonda.
     *
     * @param simbolo carácter que se comprueba
     * @return true si es A, C, G, T, N, R o Y (en mayúsculas); false en otro caso
     */
    static boolean esSimboloValido(char simbolo) {
        return SIMBOLOS.indexOf(simbolo) >= 0;
    }

    /**
     * Devuelve el nombre de la sonda.
     *
     * @return nombre de la sonda
     */
    String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el patrón de la diana de la sonda.
     *
     * @return patrón de la sonda
     */
    String getPatron() {
        return patron;
    }

    /**
     * Devuelve la longitud del patrón (su número de símbolos).
     *
     * @return número de símbolos del patrón
     */
    int getLongitud() {
        return patron.length();
    }

    /**
     * Indica si un símbolo de un patrón encaja con una base de una lectura, según las
     * reglas descritas en la documentación de la clase.
     * <p>
     * Por ejemplo, encaja('R', 'G') y encaja('N', 'N') devuelven true; encaja('R', 'C') y
     * encaja('A', 'N'), false.
     * </p>
     *
     * @param simbolo símbolo del patrón (A, C, G, T, N, R o Y)
     * @param base base de la lectura (A, C, G, T o N)
     * @return true si el símbolo encaja con la base; false en otro caso
     * @throws Exception si el símbolo no es un símbolo de patrón (ver
     *                   {@link #esSimboloValido(char)}) o la base no es un símbolo de lectura
     *                   (ver {@link Lectura#esSimboloValido(char)})
     */
    static boolean encaja(char simbolo, char base) throws Exception {
        if (!esSimboloValido(simbolo) || !Lectura.esSimboloValido(base)) {
            throw new Exception("No se puede comparar el símbolo '" + simbolo + "' con la base '" + base
                    + "': los símbolos de patrón son " + SIMBOLOS + " y los de lectura, " + Lectura.SIMBOLOS);
        }
        switch (simbolo) {
            case 'N' -> { return true; }
            case 'R' -> { return base == 'A' || base == 'G'; }
            case 'Y' -> { return base == 'C' || base == 'T'; }
            default -> { return simbolo == base; }
        }
    }

    /**
     * Indica si un símbolo de un patrón encaja con la base complementaria de una base de
     * una lectura (la que tiene enfrente en la otra hebra; ver
     * {@link Lectura#complementaria(char)}), según las reglas de {@link #encaja(char, char)}.
     * <p>
     * Por ejemplo, encajaInverso('A', 'T') y encajaInverso('R', 'C') devuelven true (la
     * complementaria de T es A, y la de C es G, que es una purina); encajaInverso('A', 'A') y
     * encajaInverso('A', 'N'), false.
     * </p>
     *
     * @param simbolo símbolo del patrón (A, C, G, T, N, R o Y)
     * @param base base de la lectura (A, C, G, T o N)
     * @return true si el símbolo encaja con la complementaria de la base; false en otro caso
     * @throws Exception si el símbolo no es un símbolo de patrón o la base no es un
     *                   símbolo de lectura
     */
    static boolean encajaInverso(char simbolo, char base) throws Exception {
        return encaja(simbolo, Lectura.complementaria(base));
    }

    /**
     * Indica si el patrón de la sonda encaja con una lectura a partir de una posición de la
     * lectura, en cualquiera de los dos sentidos:
     * <ul>
     *   <li>en <strong>sentido directo</strong>, si cada símbolo del patrón encaja con la base
     *       que ocupa la posición correspondiente de la lectura (ver
     *       {@link #encaja(char, char)});</li>
     *   <li>en <strong>sentido inverso</strong>, si el patrón, leído de derecha a izquierda,
     *       encaja con las complementarias de las bases de esas mismas posiciones (ver
     *       {@link #encajaInverso(char, char)}); es decir, si la lectura contiene la diana en
     *       la otra hebra.</li>
     * </ul>
     * <p>
     * Por ejemplo, el patrón "TRC" encaja en sentido directo con la lectura "GATACA" a partir
     * de la posición 2 (T con T, R con A, C con C), pero no a partir de la posición 0. Y el
     * patrón "CAGCT" encaja en sentido inverso con la lectura "GATTAGCTGTTAGC" a partir de la
     * posición 4: leído de derecha a izquierda (TCGAC), encaja con las complementarias de
     * AGCTG (TCGAC).
     * </p>
     *
     * @param lectura lectura en la que se busca el patrón (puede asumir que nunca es null)
     * @param inicio posición de la lectura en la que empieza la comparación
     * @return true si el patrón encaja con la lectura a partir de esa posición, en sentido
     *         directo o inverso; false en otro caso
     * @throws Exception si el patrón no cabe en la lectura a partir de esa posición (porque
     *                   `inicio` es negativa o porque el patrón se saldría por el final)
     */
    boolean encajaEn(Lectura lectura, int inicio) throws Exception {
        if (inicio < 0 || inicio + patron.length() > lectura.getLongitud()) {
            throw new Exception("El patrón de la sonda " + nombre + " no cabe en la lectura " + lectura.getId()
                    + " a partir de la posición " + inicio);
        }
        boolean directo = true;
        for (int i = 0; i < patron.length(); i++) {
            if (!encaja(patron.charAt(i), lectura.getBase(inicio + i))) {
                directo = false;
                break;
            }
        }
        if (directo) {
            return true;
        }
        for (int i = 0; i < patron.length(); i++) {
            if (!encajaInverso(patron.charAt(patron.length() - 1 - i), lectura.getBase(inicio + i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Busca la primera posición de una lectura a partir de la cual encaja el patrón de
     * la sonda, teniendo en cuenta sus comodines y los dos sentidos (ver
     * {@link #encajaEn(Lectura, int)}).
     * <p>
     * Por ejemplo, el patrón "TRC" se encuentra en la lectura "GATACATGCA" en la posición 2
     * (también encaja en la 6, y en sentido inverso en la 7, pero se devuelve la primera).
     * </p>
     *
     * @param lectura lectura en la que se busca el patrón (puede asumir que nunca es null)
     * @return primera posición de la lectura en la que encaja el patrón, o -1 si no
     *         encaja en ninguna
     * @throws Exception si se produce algún error al comparar el patrón con la lectura (no
     *                   debería ocurrir con una sonda y una lectura válidas)
     */
    int encontrarEn(Lectura lectura) throws Exception {
        for (int inicio = 0; inicio <= lectura.getLongitud() - patron.length(); inicio++) {
            if (encajaEn(lectura, inicio)) {
                return inicio;
            }
        }
        return -1;
    }

    /**
     * Indica si la sonda hibrida con una lectura, es decir, si su patrón encaja con la
     * lectura en alguna posición, en cualquiera de los dos sentidos.
     *
     * @param lectura lectura que se analiza (puede asumir que nunca es null)
     * @return true si el patrón aparece en la lectura; false en otro caso
     * @throws Exception si se produce algún error al buscar el patrón en la lectura
     */
    boolean hibridaCon(Lectura lectura) throws Exception {
        return encontrarEn(lectura) >= 0;
    }

    /**
     * Devuelve una descripción de la sonda: su nombre y su patrón.
     *
     * @return descripción de la sonda, por ejemplo, "Virus B [GATRCA]"
     */
    @Override
    public String toString() {
        return nombre + " [" + patron + "]";
    }
}
