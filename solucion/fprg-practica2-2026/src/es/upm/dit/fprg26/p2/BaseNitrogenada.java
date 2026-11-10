package es.upm.dit.fprg26.p2;

/**
 * Bases nitrogenadas que forman parte de los nucleótidos de los ácidos nucleicos.
 * <p>
 * La adenina, la citosina y la guanina aparecen tanto en el ADN como en el ARN;
 * la timina solo aparece en el ADN y el uracilo solo en el ARN.
 * </p>
 * <p>
 * Además de sus constantes, este enumerado ofrece tres métodos:
 * </p>
 * <ul>
 *   <li>{@link #fromChar(char)} y {@link #toChar(BaseNitrogenada)}, dos métodos estáticos
 *       que se invocan con el nombre del enumerado: {@code BaseNitrogenada.fromChar('G')},
 *       {@code BaseNitrogenada.toChar(BaseNitrogenada.GUANINA)}.</li>
 *   <li>{@link #complementaria(AcidoNucleico)}, un método de objeto que se invoca sobre
 *       una base concreta: {@code BaseNitrogenada.GUANINA.complementaria(AcidoNucleico.ADN)}.</li>
 * </ul>
 */
public enum BaseNitrogenada {
    /** Adenina (A): purina; se empareja con la timina (ADN) o con el uracilo (ARN). */
    ADENINA,
    /** Citosina (C): pirimidina; se empareja con la guanina. */
    CITOSINA,
    /** Guanina (G): purina; se empareja con la citosina. */
    GUANINA,
    /** Timina (T): pirimidina exclusiva del ADN; se empareja con la adenina. */
    TIMINA,
    /** Uracilo (U): pirimidina exclusiva del ARN; se empareja con la adenina. */
    URACILO;

    /**
     * Devuelve la base nitrogenada que representa un carácter.
     * <p>
     * Se admiten las letras {@code A}, {@code C}, {@code G}, {@code T} y {@code U},
     * tanto en mayúsculas como en minúsculas. Por ejemplo,
     * {@code BaseNitrogenada.fromChar('g')} devuelve {@link #GUANINA}.
     * </p>
     *
     * @param letra carácter que representa una base (p. ej.: {@code 'G'} o {@code 'g'})
     * @return base nitrogenada que corresponde a la letra
     * @throws Exception si el carácter no representa ninguna base nitrogenada
     *                   (por ejemplo, {@code 'N'}, con la que los secuenciadores indican
     *                   que no han podido determinar una base)
     */
    public static BaseNitrogenada fromChar(char letra) throws Exception {
        switch (letra) {
            case 'A', 'a' -> { return ADENINA; }
            case 'C', 'c' -> { return CITOSINA; }
            case 'G', 'g' -> { return GUANINA; }
            case 'T', 't' -> { return TIMINA; }
            case 'U', 'u' -> { return URACILO; }
            default -> throw new Exception("El carácter '" + letra + "' no representa ninguna base nitrogenada");
        }
    }

    /**
     * Devuelve la letra (en mayúscula) con la que se representa una base nitrogenada.
     * <p>
     * Es la operación inversa de {@link #fromChar(char)}: por ejemplo,
     * {@code BaseNitrogenada.toChar(BaseNitrogenada.GUANINA)} devuelve {@code 'G'}.
     * </p>
     *
     * @param base base nitrogenada
     * @return letra que la representa: {@code 'A'}, {@code 'C'}, {@code 'G'}, {@code 'T'} o {@code 'U'}
     */
    public static char toChar(BaseNitrogenada base) {
        switch (base) {
            case ADENINA -> { return 'A'; }
            case CITOSINA -> { return 'C'; }
            case GUANINA -> { return 'G'; }
            case TIMINA -> { return 'T'; }
            default -> { return 'U'; } // URACILO
        }
    }

    /**
     * Devuelve la base complementaria de esta base, según las reglas de emparejamiento
     * de Watson y Crick en el tipo de ácido nucleico indicado:
     * <ul>
     *   <li>La guanina se empareja con la citosina, y viceversa (en ADN y en ARN).</li>
     *   <li>En el ADN, la adenina se empareja con la timina, y viceversa.</li>
     *   <li>En el ARN, la adenina se empareja con el uracilo, y viceversa.</li>
     * </ul>
     * <p>
     * Por ejemplo, {@code BaseNitrogenada.ADENINA.complementaria(AcidoNucleico.ARN)}
     * devuelve {@link #URACILO}. La timina no forma parte del ARN ni el uracilo del ADN,
     * así que no tiene sentido pedir su complementaria en esos casos.
     * </p>
     *
     * @param tipo tipo de ácido nucleico (ADN o ARN) en el que se produce el emparejamiento
     * @return base complementaria de esta base
     * @throws Exception si se pide la complementaria de la timina en ARN,
     *                   o la del uracilo en ADN
     */
    public BaseNitrogenada complementaria(AcidoNucleico tipo) throws Exception {
        if ((this == TIMINA && tipo == AcidoNucleico.ARN) || (this == URACILO && tipo == AcidoNucleico.ADN)) {
            throw new Exception("La base " + this + " no forma parte del " + tipo);
        }
        if (this == ADENINA) {
            if (tipo == AcidoNucleico.ADN) {
                return TIMINA;
            } else {
                return URACILO;
            }
        } else if (this == GUANINA) {
            return CITOSINA;
        } else if (this == CITOSINA) {
            return GUANINA;
        } else {
            // La timina y el uracilo se emparejan con la adenina
            return ADENINA;
        }
    }
}
