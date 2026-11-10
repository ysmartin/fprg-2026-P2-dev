package es.upm.dit.fprg26.p2;

import module java.base;

import es.upm.dit.fprg26.p2.auxiliar.DatosEjemplo;
import es.upm.dit.fprg26.p2.auxiliar.VentanaMicroarray;

/**
 * Abre la ventana del microarray virtual con los microarrays y las muestras de ejemplo,
 * más los que usted añada en este método {@code main}.
 */
public class PruebaInteractiva {

    private PruebaInteractiva() {
        // Solo contiene el método main: no se instancia
    }

    /**
     * Abre la ventana.
     *
     * @param args no se usa
     * @throws Exception si alguno de los microarrays o de las muestras no se puede crear
     */
    public static void main(String[] args) throws Exception {
        Microarray[] ejemplos = DatosEjemplo.microarrays();

        // Puede añadir sus propios microarrays y muestras aquí. Por ejemplo:
        Microarray miChip = new Microarray("Mi chip", new Sonda[][] {
            { new Sonda("Sonda 1", "GATTACA"), new Sonda("Sonda 2", "CANNTG") }
        });
        Muestra miMuestra = new Muestra("Mi muestra", LocalDate.of(2026, 1, 15), new Lectura[] {
            new Lectura("L1", "TTGATTACATT"),
            new Lectura("L2", "ACACGTGCA")
        });

        Microarray[] microarrays = { ejemplos[0], ejemplos[1], ejemplos[2], miChip };
        Muestra[] muestras = DatosEjemplo.muestras();
        Muestra[] todas = Arrays.copyOf(muestras, muestras.length + 1);
        todas[muestras.length] = miMuestra;

        new VentanaMicroarray(microarrays, todas).mostrar();
    }
}
