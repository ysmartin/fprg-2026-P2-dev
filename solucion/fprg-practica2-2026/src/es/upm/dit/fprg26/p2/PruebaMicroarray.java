package es.upm.dit.fprg26.p2;

import es.upm.dit.fprg26.p2.auxiliar.Comprobador;
import es.upm.dit.fprg26.p2.auxiliar.DatosEjemplo;

/**
 * Pruebas de los métodos {@code hibridar} de la clase {@link Microarray}, con los datos
 * de ejemplo de {@link DatosEjemplo}. Los resultados se escriben con {@code #} para los
 * puntos iluminados y {@code .} para los apagados, una fila entre corchetes. Añada sus
 * propios casos de prueba en {@link #pruebasPropias()}.
 */
public class PruebaMicroarray {

    private PruebaMicroarray() {
        // Solo contiene el método main: no se instancia
    }

    /**
     * Ejecuta las pruebas.
     *
     * @param args no se usa
     */
    public static void main(String[] args) {
        probarHibridarLectura();
        probarHibridarMuestra();
        pruebasPropias();
        Comprobador.resumen();
    }

    private static void probarHibridarLectura() {
        Comprobador.titulo("hibridar(Lectura), con el chip de prueba (GATTACA | GANTACA | GAYTRCA)");
        try {
            Microarray chip = DatosEjemplo.chipDePrueba();
            Muestra muestra = DatosEjemplo.muestraDePrueba();
            Comprobador.comprobar("GATTACA", new boolean[][] { { true, true, true } },
                    chip.hibridar(muestra.getLectura(0)));
            Comprobador.comprobar("GANTACA", new boolean[][] { { false, true, false } },
                    chip.hibridar(muestra.getLectura(1)));
            Comprobador.comprobar("TTGACTGCATT", new boolean[][] { { false, false, true } },
                    chip.hibridar(muestra.getLectura(2)));
        } catch (Exception e) {
            Comprobador.fallo("No se han podido crear los datos de ejemplo: " + e.getMessage());
        }
    }

    private static void probarHibridarMuestra() {
        Comprobador.titulo("hibridar(Muestra)");
        try {
            Comprobador.comprobar("muestra de prueba con el chip de prueba", new boolean[][] { { true, true, true } },
                    DatosEjemplo.chipDePrueba().hibridar(DatosEjemplo.muestraDePrueba()));
            Muestra[] muestras = DatosEjemplo.muestras();
            Microarray panel = DatosEjemplo.panelDePatogenos();
            Comprobador.comprobar("M1 con el panel de patógenos (Virus A y control positivo)",
                    new boolean[][] { { true, false, false }, { false, false, false }, { false, true, false } },
                    panel.hibridar(muestras[1]));
            Comprobador.comprobar("M3 con el panel de patógenos (lecturas con N)",
                    new boolean[][] { { false, false, false }, { true, false, false }, { true, true, false } },
                    panel.hibridar(muestras[3]));
            Comprobador.comprobar("R1 con el chip de dianas de restricción (2x4)",
                    new boolean[][] { { true, false, false, false }, { true, true, true, true } },
                    DatosEjemplo.chipDeDianasDeRestriccion().hibridar(muestras[5]));
        } catch (Exception e) {
            Comprobador.fallo("No se han podido crear los datos de ejemplo: " + e.getMessage());
        }
    }

    /**
     * Sus propios casos de prueba.
     */
    private static void pruebasPropias() {
        Comprobador.titulo("Pruebas propias");
        // TODO: añada aquí sus propios casos de prueba
    }
}
