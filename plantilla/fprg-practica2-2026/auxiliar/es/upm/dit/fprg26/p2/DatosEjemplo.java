package es.upm.dit.fprg26.p2;

import module java.base;

/**
 * Microarrays y muestras de ejemplo para las pruebas y para la interfaz gráfica.
 * <p>
 * Las secuencias del panel de patógenos y del chip de expresión son <strong>sintéticas</strong>:
 * se han inventado para la práctica y no corresponden a ningún organismo real. Las del chip
 * de dianas de restricción sí son reales: son las secuencias que reconocen esas enzimas,
 * varias de ellas con comodines del código IUPAC.
 * </p>
 * <p>
 * Las fechas de captura de las muestras son relativas a la fecha actual (de 1 a 6 días antes),
 * porque los análisis se hacen siempre en la fecha actual.
 * </p>
 * <p>
 * No es necesario entender el código de esta clase para hacer la práctica; basta con
 * saber qué datos ofrece cada método.
 * </p>
 */
final class DatosEjemplo {

    private DatosEjemplo() {
        // No se instancia: solo contiene métodos estáticos
    }

    /**
     * Devuelve un chip de prueba de 1x3 con tres sondas que se diferencian en sus comodines:
     * {@code GATTACA} (exacta), {@code GANTACA} (con {@code N}) y {@code GAYTRCA}
     * (con {@code Y} y {@code R}).
     *
     * @return chip de prueba
     * @throws Exception si alguno de los objetos no se puede crear (con su clase {@code Sonda} incompleta)
     */
    static Microarray chipDePrueba() throws Exception {
        return new Microarray("Chip de prueba", new Sonda[][] {
            { new Sonda("Exacta", "GATTACA"), new Sonda("Con N", "GANTACA"), new Sonda("Con Y y R", "GAYTRCA") }
        });
    }

    /**
     * Devuelve un panel de 3x3 con siete sondas de patógenos (sintéticas) y dos sondas de control.
     * <p>
     * La sonda de control positivo detecta una secuencia que está en todas las muestras de
     * pacientes; la de control negativo no debería iluminarse nunca. En un chip real sirven
     * para comprobar que el análisis se ha hecho correctamente.
     * </p>
     *
     * @return panel de patógenos
     * @throws Exception si alguno de los objetos no se puede crear
     */
    static Microarray panelDePatogenos() throws Exception {
        return new Microarray("Panel de patógenos (sintético)", new Sonda[][] {
            { new Sonda("Virus A", "ACCGTTAGCTGA"), new Sonda("Virus B (variantes 1 y 2)", "TTGRCATCCAGT"),
              new Sonda("Virus C", "GGTACAAGTCCA") },
            { new Sonda("Bacteria D", "CATTGGCANTTC"), new Sonda("Bacteria E (cepas 1 y 2)", "AGYTCGGATACG"),
              new Sonda("Hongo F", "TCAGGCTTAACG") },
            { new Sonda("Parásito G", "GTACCRYAGTCA"), new Sonda("Control positivo", "CTGACCTGAGTC"),
              new Sonda("Control negativo", "CGCGTATACGCG") }
        });
    }

    /**
     * Devuelve un chip de 2x4 con las secuencias que reconocen ocho enzimas de restricción
     * reales. Las de la segunda fila son degeneradas: por ejemplo, HincII reconoce
     * {@code GTYRAC} ({@code GTCGAC}, {@code GTTAAC}, {@code GTCAAC} y {@code GTTGAC}).
     *
     * @return chip de dianas de restricción
     * @throws Exception si alguno de los objetos no se puede crear
     */
    static Microarray chipDeDianasDeRestriccion() throws Exception {
        return new Microarray("Dianas de restricción", new Sonda[][] {
            { new Sonda("EcoRI", "GAATTC"), new Sonda("BamHI", "GGATCC"),
              new Sonda("HindIII", "AAGCTT"), new Sonda("NotI", "GCGGCCGC") },
            { new Sonda("HincII", "GTYRAC"), new Sonda("AvaI", "CYCGRG"),
              new Sonda("BanI", "GGYRCC"), new Sonda("Hpy188I", "TCNGA") }
        });
    }

    /** Número de filas y de columnas del chip de expresión. */
    static final int LADO_CHIP_DE_EXPRESION = 20;

    /** Primera columna de la letra D en {@link #FORMA_MUESTRA_DE_EXPRESION} (la A ocupa las anteriores). */
    static final int COLUMNA_D = 7;

    /** Primera columna de la letra N en {@link #FORMA_MUESTRA_DE_EXPRESION} (la D ocupa las anteriores). */
    static final int COLUMNA_N = 13;

    /**
     * Puntos del chip de expresión que debe iluminar la muestra de expresión ({@code #}):
     * forman la palabra «ADN». Cada letra pone a prueba una parte del reconocimiento de
     * patrones (ver {@link #chipDeExpresion()}). Como la palabra no es simétrica, si se
     * confunden las filas con las columnas, el dibujo sale traspuesto.
     */
    static final String[] FORMA_MUESTRA_DE_EXPRESION = {
        "....................",
        "....................",
        "....................",
        "....................",
        "....................",
        "....................",
        "..###..####..#...#..",
        ".#...#.#...#.##..#..",
        ".#...#.#...#.#.#.#..",
        ".#####.#...#.#..##..",
        ".#...#.#...#.#...#..",
        ".#...#.#...#.#...#..",
        ".#...#.####..#...#..",
        "....................",
        "....................",
        "....................",
        "....................",
        "....................",
        "....................",
        "...................."
    };

    /**
     * Devuelve un chip de expresión de 20x20 con 400 sondas sintéticas ({@code G001} a
     * {@code G400}), como los que miden qué genes están activos en un tejido. Algunas
     * sondas son degeneradas. En la interfaz gráfica, sus nombres y patrones no se escriben
     * en el chip: se muestran al pasar el ratón por encima de cada punto.
     * <p>
     * Con la muestra de expresión, cada letra de la palabra «ADN» se ilumina en una etapa
     * del reconocimiento de patrones:
     * </p>
     * <ul>
     *   <li>las sondas de la <strong>A</strong> no tienen comodines, y sus dianas aparecen tal
     *       cual en las lecturas: basta con buscar el patrón con {@code indexOf};</li>
     *   <li>las de la <strong>D</strong> tienen algún comodín, y sus dianas aparecen en las
     *       lecturas en sentido directo;</li>
     *   <li>las dianas de la <strong>N</strong> están en la otra hebra: aparecen en las lecturas
     *       como su complementaria inversa.</li>
     * </ul>
     *
     * @return chip de expresión
     * @throws Exception si alguno de los objetos no se puede crear
     */
    static Microarray chipDeExpresion() throws Exception {
        String[][] patrones = patronesDelChipDeExpresion();
        Sonda[][] sondas = new Sonda[LADO_CHIP_DE_EXPRESION][LADO_CHIP_DE_EXPRESION];
        for (int fila = 0; fila < LADO_CHIP_DE_EXPRESION; fila++) {
            for (int columna = 0; columna < LADO_CHIP_DE_EXPRESION; columna++) {
                String nombre = String.format("G%03d", fila * LADO_CHIP_DE_EXPRESION + columna + 1);
                sondas[fila][columna] = new Sonda(nombre, patrones[fila][columna]);
            }
        }
        return new Microarray("Chip de expresión (sintético)", sondas);
    }

    /**
     * Devuelve una muestra para el chip de expresión, cuyas lecturas contienen las dianas de
     * los puntos marcados en {@link #FORMA_MUESTRA_DE_EXPRESION} (dos dianas por lectura). Las
     * de la letra N están en la otra hebra (ver {@link #chipDeExpresion()}).
     *
     * @return muestra para el chip de expresión
     * @throws Exception si alguno de los objetos no se puede crear
     */
    static Muestra muestraDeExpresion() throws Exception {
        String[][] patrones = patronesDelChipDeExpresion();
        Random azar = new Random(7);
        String[] dianas = new String[0];
        for (int fila = 0; fila < LADO_CHIP_DE_EXPRESION; fila++) {
            for (int columna = 0; columna < LADO_CHIP_DE_EXPRESION; columna++) {
                if (FORMA_MUESTRA_DE_EXPRESION[fila].charAt(columna) == '#') {
                    String diana = concretar(patrones[fila][columna], azar);
                    if (columna >= COLUMNA_N) {
                        diana = complementariaInversa(diana);
                    }
                    dianas = Arrays.copyOf(dianas, dianas.length + 1);
                    dianas[dianas.length - 1] = diana;
                }
            }
        }
        Lectura[] lecturas = new Lectura[(dianas.length + 1) / 2];
        for (int i = 0; i < lecturas.length; i++) {
            String segunda = 2 * i + 1 < dianas.length ? dianas[2 * i + 1] : "";
            String secuencia = relleno(azar, 6) + dianas[2 * i] + relleno(azar, 5) + segunda + relleno(azar, 6);
            lecturas[i] = new Lectura(String.format("M5-L%02d", i + 1), secuencia);
        }
        return new Muestra("M5 (tejido)", hace(1), lecturas);
    }

    /**
     * Devuelve todos los microarrays de ejemplo.
     *
     * @return chip de prueba, panel de patógenos, chip de dianas de restricción y chip de expresión
     * @throws Exception si alguno de los objetos no se puede crear
     */
    static Microarray[] microarrays() throws Exception {
        return new Microarray[] { chipDePrueba(), panelDePatogenos(), chipDeDianasDeRestriccion(), chipDeExpresion() };
    }

    /**
     * Devuelve la muestra de prueba para el chip de prueba, con tres lecturas:
     * {@code GATTACA} (encaja con las tres sondas), {@code GANTACA} (su {@code N} solo encaja
     * con la {@code N} de la sonda {@code GANTACA}) y una lectura que contiene {@code GACTGCA} (solo con {@code GAYTRCA}).
     *
     * @return muestra de prueba
     * @throws Exception si alguno de los objetos no se puede crear
     */
    static Muestra muestraDePrueba() throws Exception {
        return new Muestra("P1 (prueba)", hace(1), new Lectura[] {
            new Lectura("P1-L1", "GATTACA"),
            new Lectura("P1-L2", "GANTACA"),
            new Lectura("P1-L3", "TTGACTGCATT")
        });
    }

    /**
     * Devuelve las muestras de ejemplo: la de prueba, cuatro muestras de pacientes para el
     * panel de patógenos y una para el chip de dianas de restricción.
     * <ul>
     *   <li>M1: Virus A y control positivo.</li>
     *   <li>M2: las dos variantes del Virus B, la cepa 1 de la Bacteria E y control positivo.</li>
     *   <li>M3: lecturas con bases indeterminadas ({@code N}): la diana del Virus A tiene una
     *       {@code N} y no se detecta; la de la Bacteria D tiene la {@code N} en la posición
     *       del comodín y sí se detecta. También Parásito G y control positivo.</li>
     *   <li>M4: sin patógenos; solo el control positivo.</li>
     *   <li>R1: fragmento con dianas de EcoRI, HincII, AvaI, BanI y Hpy188I.</li>
     *   <li>M5: muestra para el chip de expresión (ver {@link #muestraDeExpresion()}).</li>
     * </ul>
     *
     * @return muestras de ejemplo
     * @throws Exception si alguno de los objetos no se puede crear
     */
    static Muestra[] muestras() throws Exception {
        return new Muestra[] {
            muestraDePrueba(),
            new Muestra("M1 (paciente 1)", hace(2), new Lectura[] {
                new Lectura("M1-L1", "AGACTTTCACCGTTAGCTGAGGTAGAGGTCGAGGTTATTATTTGTTAC"),
                new Lectura("M1-L2", "AATTCTCATTGTGTTTCGGAACTTGCCTGACCTGAGTCTCTTAGTGAC"),
                new Lectura("M1-L3", "CTAAATACCAAGGCAGTCCTCGATCCGTTCCTAATAAGGAATGGTGAT")
            }),
            new Muestra("M2 (paciente 2)", hace(3), new Lectura[] {
                new Lectura("M2-L1", "TCCCTGTCATACCATTGACATCCAGTTTATGCGCGTTTGTCGTTAGAC"),
                new Lectura("M2-L2", "AACGATTTGGCATCCAGTGGGGCTCATTGCACTGACCTGAGTCCCCAG"),
                new Lectura("M2-L3", "ATGGAGTTAAGTTCGGATACGGTGGTTGCCTCGGTTCCTCTGGAGGTG")
            }),
            new Muestra("M3 (paciente 3)", hace(4), new Lectura[] {
                new Lectura("M3-L1", "GCGCAGGTTTAGTGATCTGGAACCGTTANCTGAACAGGACTGGACAAC"),
                new Lectura("M3-L2", "CTCCGATCAAGTACCTGGGGTCATTGGCANTTCCGGTGCATAGTAGTG"),
                new Lectura("M3-L3", "GTATTGGTCGTACCGTAGTCACTGACCTGAGTCTACGCAATACGAAAA")
            }),
            new Muestra("M4 (paciente 4)", hace(5), new Lectura[] {
                new Lectura("M4-L1", "GTCGTCTCGTATCTGACCTGAGTCATCGCGGTAGCAGCACTTAATTAC"),
                new Lectura("M4-L2", "AGCGGAGCACATAGTACTAGACAGCAGATGACCTAGCTTACAATTATC"),
                new Lectura("M4-L3", "CCCCGTGCTAAGACCTCGCTACATATTGGAAGCATCGCGAAGCTACTC")
            }),
            new Muestra("R1 (fragmento de ADN)", hace(6), new Lectura[] {
                new Lectura("R1-L1", "TTGTGAATTCGTTAACAGCGCCCGAGGTACTAGTAAAAAA"),
                new Lectura("R1-L2", "GGCGCCTACTCAGAGTACAGTTATTAAGTCGAAACACCAA")
            }),
            muestraDeExpresion()
        };
    }

    /**
     * Devuelve la fecha de hace un número de días.
     *
     * @param dias número de días antes de la fecha actual
     * @return fecha correspondiente
     */
    static LocalDate hace(int dias) {
        return LocalDate.now().minusDays(dias);
    }

    private static String[][] patronesDelChipDeExpresion() {
        Random azar = new Random(2026);
        String[][] patrones = new String[LADO_CHIP_DE_EXPRESION][LADO_CHIP_DE_EXPRESION];
        for (int fila = 0; fila < LADO_CHIP_DE_EXPRESION; fila++) {
            for (int columna = 0; columna < LADO_CHIP_DE_EXPRESION; columna++) {
                char[] patron = relleno(azar, 14).toCharArray();
                boolean conComodin = azar.nextInt(3) == 0;
                if (FORMA_MUESTRA_DE_EXPRESION[fila].charAt(columna) == '#' && columna < COLUMNA_N) {
                    conComodin = columna >= COLUMNA_D;   // la A, sin comodines; la D, con alguno
                }
                if (conComodin) {
                    patron[1 + azar.nextInt(12)] = "NRY".charAt(azar.nextInt(3));
                }
                patrones[fila][columna] = new String(patron);
            }
        }
        return patrones;
    }

    private static String relleno(Random azar, int longitud) {
        String bases = "";
        for (int i = 0; i < longitud; i++) {
            bases += "ACGT".charAt(azar.nextInt(4));
        }
        return bases;
    }

    /** Devuelve la complementaria inversa de una secuencia de bases concretas. */
    private static String complementariaInversa(String secuencia) {
        String inversa = "";
        for (int i = secuencia.length() - 1; i >= 0; i--) {
            inversa += "TGCA".charAt("ACGT".indexOf(secuencia.charAt(i)));
        }
        return inversa;
    }

    /** Sustituye cada comodín de un patrón por una base concreta que encaja con él. */
    private static String concretar(String patron, Random azar) {
        String diana = "";
        for (int i = 0; i < patron.length(); i++) {
            char simbolo = patron.charAt(i);
            switch (simbolo) {
                case 'N' -> diana += "ACGT".charAt(azar.nextInt(4));
                case 'R' -> diana += "AG".charAt(azar.nextInt(2));
                case 'Y' -> diana += "CT".charAt(azar.nextInt(2));
                default -> diana += simbolo;
            }
        }
        return diana;
    }
}
