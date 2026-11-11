package es.upm.dit.fprg26.p2;

import module java.base;

/**
 * Abre la ventana del microarray virtual con los microarrays y las muestras de ejemplo, más
 * los que usted añada en este método main.
 */
class PruebaInteractiva {

    /**
     * Abre la ventana.
     *
     * @throws Exception si alguno de los microarrays o de las muestras no se puede crear
     */
    void main() throws Exception {
        Microarray[] ejemplos = DatosEjemplo.microarrays();

        // Puede añadir sus propios microarrays y muestras aquí. Por ejemplo:
        Microarray miChip = new Microarray("Mi chip", new Sonda[][] {
            { new Sonda("Sonda 1", "GATTACA"), new Sonda("Sonda 2", "CANNTG") }
        });
        Muestra miMuestra = new Muestra("Mi muestra", LocalDate.now().minusDays(3), new Lectura[] {
            new Lectura("L1", "TTGATTACATT"),
            new Lectura("L2", "ACACGTGCA")
        });

        Microarray[] microarrays = { ejemplos[0], ejemplos[1], ejemplos[2], ejemplos[3], miChip };
        Muestra[] muestras = DatosEjemplo.muestras();
        Muestra[] todas = Arrays.copyOf(muestras, muestras.length + 1);
        todas[muestras.length] = miMuestra;

        new VentanaMicroarray(microarrays, todas).mostrar();
    }
}
