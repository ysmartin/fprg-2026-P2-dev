package es.upm.dit.fprg26.p2;

/**
 * Utilidades sobre los símbolos que usamos para escribir secuencias de ADN, tomados
 * del código de ambigüedad de la IUPAC (Unión Internacional de Química Pura y Aplicada).
 * <p>
 * Las <strong>lecturas</strong> que obtiene un secuenciador solo contienen las cuatro
 * bases del ADN y la letra {@code N}, con la que el secuenciador indica que no ha podido
 * determinar una base. Los <strong>patrones</strong> de las sondas pueden contener,
 * además, dos símbolos que representan una base de un grupo (son <em>comodines</em>):
 * </p>
 * <table>
 *   <caption>Símbolos que admite cada tipo de secuencia</caption>
 *   <tr><th>Símbolo</th><th>Significado</th><th>En lecturas</th><th>En patrones</th></tr>
 *   <tr><td>{@code A}, {@code C}, {@code G}, {@code T}</td><td>Una base concreta</td><td>Sí</td><td>Sí</td></tr>
 *   <tr><td>{@code N}</td><td>En una lectura, base indeterminada; en un patrón, cualquier base</td><td>Sí</td><td>Sí</td></tr>
 *   <tr><td>{@code R}</td><td>Cualquier purina ({@code A} o {@code G})</td><td>No</td><td>Sí</td></tr>
 *   <tr><td>{@code Y}</td><td>Cualquier pirimidina ({@code C} o {@code T})</td><td>No</td><td>Sí</td></tr>
 * </table>
 * <p>
 * Esta clase no se instancia: todos sus métodos son estáticos y se invocan con el nombre
 * de la clase, por ejemplo, {@code Iupac.esPurina('G')}.
 * </p>
 */
final class Iupac {

    /** Símbolos que pueden aparecer en una lectura: las cuatro bases y la base indeterminada. */
    static final String SIMBOLOS_DE_LECTURA = "ACGTN";

    /** Símbolos que pueden aparecer en el patrón de una sonda: los de las lecturas y los comodines. */
    static final String SIMBOLOS_DE_PATRON = "ACGTNRY";

    /** Base indeterminada (en una lectura) o comodín de cualquier base (en un patrón). */
    static final char INDETERMINADA = 'N';

    /** Comodín de cualquier purina ({@code A} o {@code G}). */
    static final char PURINA = 'R';

    /** Comodín de cualquier pirimidina ({@code C} o {@code T}). */
    static final char PIRIMIDINA = 'Y';

    private Iupac() {
        // No se instancia: solo contiene métodos estáticos
    }

    /**
     * Indica si un carácter puede aparecer en una lectura.
     *
     * @param simbolo carácter que se quiere comprobar
     * @return {@code true} si es {@code A}, {@code C}, {@code G}, {@code T} o {@code N}
     *         (en mayúsculas); {@code false} en otro caso
     */
    static boolean esSimboloDeLectura(char simbolo) {
        return SIMBOLOS_DE_LECTURA.indexOf(simbolo) >= 0;
    }

    /**
     * Indica si un carácter puede aparecer en el patrón de una sonda.
     *
     * @param simbolo carácter que se quiere comprobar
     * @return {@code true} si es {@code A}, {@code C}, {@code G}, {@code T}, {@code N},
     *         {@code R} o {@code Y} (en mayúsculas); {@code false} en otro caso
     */
    static boolean esSimboloDePatron(char simbolo) {
        return SIMBOLOS_DE_PATRON.indexOf(simbolo) >= 0;
    }

    /**
     * Indica si una base es una purina.
     *
     * @param base base que se quiere clasificar
     * @return {@code true} si es {@code A} o {@code G}; {@code false} en otro caso
     *         (también si es {@code N}, porque no se sabe qué base es)
     */
    static boolean esPurina(char base) {
        return base == 'A' || base == 'G';
    }

    /**
     * Indica si una base es una pirimidina.
     *
     * @param base base que se quiere clasificar
     * @return {@code true} si es {@code C} o {@code T}; {@code false} en otro caso
     *         (también si es {@code N}, porque no se sabe qué base es)
     */
    static boolean esPirimidina(char base) {
        return base == 'C' || base == 'T';
    }

    /**
     * Devuelve el símbolo complementario de un símbolo de patrón o de lectura.
     * <p>
     * Para las bases se usa el enumerado {@link BaseNitrogenada} de la práctica 1
     * ({@code A} con {@code T}, {@code C} con {@code G}). El complementario de una purina
     * cualquiera ({@code R}) es una pirimidina cualquiera ({@code Y}), y viceversa; y el
     * de una base cualquiera o indeterminada ({@code N}) es también {@code N}.
     * </p>
     *
     * @param simbolo símbolo de patrón o de lectura (ver {@link #esSimboloDePatron(char)})
     * @return símbolo complementario
     * @throws Exception si el carácter no es un símbolo de patrón ni de lectura
     */
    static char complementario(char simbolo) throws Exception {
        switch (simbolo) {
            case INDETERMINADA -> { return INDETERMINADA; }
            case PURINA -> { return PIRIMIDINA; }
            case PIRIMIDINA -> { return PURINA; }
            case 'A', 'C', 'G', 'T' -> {
                BaseNitrogenada base = BaseNitrogenada.fromChar(simbolo);
                return BaseNitrogenada.toChar(base.complementaria(AcidoNucleico.ADN));
            }
            default -> throw new Exception("El carácter '" + simbolo + "' no es un símbolo de patrón ni de lectura");
        }
    }
}
