package es.upm.dit.fprg26.p2.auxiliar;

import module java.base;

import es.upm.dit.fprg26.p2.Lectura;
import es.upm.dit.fprg26.p2.Microarray;
import es.upm.dit.fprg26.p2.Muestra;
import es.upm.dit.fprg26.p2.Sonda;

/**
 * Microarrays y muestras de ejemplo para las pruebas y para la interfaz gráfica.
 * <p>
 * Las secuencias del panel de patógenos son <strong>sintéticas</strong>: se han inventado
 * para la práctica y no corresponden a ningún organismo real. Las del chip de dianas de
 * restricción sí son reales: son las secuencias que reconocen esas enzimas, varias de ellas
 * con comodines del código IUPAC.
 * </p>
 * <p>
 * No es necesario entender el código de esta clase para hacer la práctica; basta con
 * saber qué datos ofrece cada método.
 * </p>
 */
public final class DatosEjemplo {

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
    public static Microarray chipDePrueba() throws Exception {
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
    public static Microarray panelDePatogenos() throws Exception {
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
    public static Microarray chipDeDianasDeRestriccion() throws Exception {
        return new Microarray("Dianas de restricción", new Sonda[][] {
            { new Sonda("EcoRI", "GAATTC"), new Sonda("BamHI", "GGATCC"),
              new Sonda("HindIII", "AAGCTT"), new Sonda("NotI", "GCGGCCGC") },
            { new Sonda("HincII", "GTYRAC"), new Sonda("AvaI", "CYCGRG"),
              new Sonda("BanI", "GGYRCC"), new Sonda("Hpy188I", "TCNGA") }
        });
    }

    /**
     * Devuelve todos los microarrays de ejemplo.
     *
     * @return chip de prueba, panel de patógenos y chip de dianas de restricción
     * @throws Exception si alguno de los objetos no se puede crear
     */
    public static Microarray[] microarrays() throws Exception {
        return new Microarray[] { chipDePrueba(), panelDePatogenos(), chipDeDianasDeRestriccion() };
    }

    /**
     * Devuelve la muestra de prueba para el chip de prueba, con tres lecturas:
     * {@code GATTACA} (encaja con las tres sondas), {@code GANTACA} (su {@code N} solo encaja
     * con la {@code N} de la sonda {@code GANTACA}) y una lectura que contiene {@code GACTGCA} (solo con {@code GAYTRCA}).
     *
     * @return muestra de prueba
     * @throws Exception si alguno de los objetos no se puede crear
     */
    public static Muestra muestraDePrueba() throws Exception {
        return new Muestra("P1 (prueba)", LocalDate.of(2026, 4, 27), new Lectura[] {
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
     * </ul>
     *
     * @return muestras de ejemplo
     * @throws Exception si alguno de los objetos no se puede crear
     */
    public static Muestra[] muestras() throws Exception {
        return new Muestra[] {
            muestraDePrueba(),
            new Muestra("M1 (paciente 1)", LocalDate.of(2026, 5, 4), new Lectura[] {
                new Lectura("M1-L1", "AGACTTTCACCGTTAGCTGAGGTAGAGGTCGAGGTTATTATTTGTTAC"),
                new Lectura("M1-L2", "AATTCTCATTGTGTTTCGGAACTTGCCTGACCTGAGTCTCTTAGTGAC"),
                new Lectura("M1-L3", "CTAAATACCAAGGCAGTCCTCGATCCGTTCCTAATAAGGAATGGTGAT")
            }),
            new Muestra("M2 (paciente 2)", LocalDate.of(2026, 5, 11), new Lectura[] {
                new Lectura("M2-L1", "TCCCTGTCATACCATTGACATCCAGTTTATGCGCGTTTGTCGTTAGAC"),
                new Lectura("M2-L2", "AACGATTTGGCATCCAGTGGGGCTCATTGCACTGACCTGAGTCCCCAG"),
                new Lectura("M2-L3", "ATGGAGTTAAGTTCGGATACGGTGGTTGCCTCGGTTCCTCTGGAGGTG")
            }),
            new Muestra("M3 (paciente 3)", LocalDate.of(2026, 5, 18), new Lectura[] {
                new Lectura("M3-L1", "GCGCAGGTTTAGTGATCTGGAACCGTTANCTGAACAGGACTGGACAAC"),
                new Lectura("M3-L2", "CTCCGATCAAGTACCTGGGGTCATTGGCANTTCCGGTGCATAGTAGTG"),
                new Lectura("M3-L3", "GTATTGGTCGTACCGTAGTCACTGACCTGAGTCTACGCAATACGAAAA")
            }),
            new Muestra("M4 (paciente 4)", LocalDate.of(2026, 5, 25), new Lectura[] {
                new Lectura("M4-L1", "GTCGTCTCGTATCTGACCTGAGTCATCGCGGTAGCAGCACTTAATTAC"),
                new Lectura("M4-L2", "AGCGGAGCACATAGTACTAGACAGCAGATGACCTAGCTTACAATTATC"),
                new Lectura("M4-L3", "CCCCGTGCTAAGACCTCGCTACATATTGGAAGCATCGCGAAGCTACTC")
            }),
            new Muestra("R1 (fragmento de ADN)", LocalDate.of(2026, 6, 1), new Lectura[] {
                new Lectura("R1-L1", "TTGTGAATTCGTTAACAGCGCCCGAGGTACTAGTAAAAAA"),
                new Lectura("R1-L2", "GGCGCCTACTCAGAGTACAGTTATTAAGTCGAAACACCAA")
            })
        };
    }
}
