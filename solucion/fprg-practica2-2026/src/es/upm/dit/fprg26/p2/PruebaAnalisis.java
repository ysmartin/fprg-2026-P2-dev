package es.upm.dit.fprg26.p2;

import module java.base;

import es.upm.dit.fprg26.p2.auxiliar.Comprobador;
import es.upm.dit.fprg26.p2.auxiliar.DatosEjemplo;

/**
 * Pruebas de la clase {@link Analisis}, con los datos de ejemplo de {@link DatosEjemplo}.
 * Añada sus propios casos de prueba en {@link #pruebasPropias()}.
 */
public class PruebaAnalisis {

    private PruebaAnalisis() {
        // Solo contiene el método main: no se instancia
    }

    /**
     * Ejecuta las pruebas.
     *
     * @param args no se usa
     */
    public static void main(String[] args) {
        probarConstructorYAcceso();
        probarAnalisisNoValidos();
        probarDiasYPuntos();
        pruebasPropias();
        Comprobador.resumen();
    }

    private static void probarConstructorYAcceso() {
        Comprobador.titulo("Constructor y métodos de acceso");
        try {
            Muestra muestra = DatosEjemplo.muestras()[1];
            Microarray panel = DatosEjemplo.panelDePatogenos();
            LocalDate fecha = LocalDate.of(2026, 5, 11);
            Analisis analisis = new Analisis(muestra, panel, fecha);
            Comprobador.comprobar("getMuestra()", muestra, analisis.getMuestra());
            Comprobador.comprobar("getMicroarray()", panel, analisis.getMicroarray());
            Comprobador.comprobar("getFechaAnalisis()", fecha, analisis.getFechaAnalisis());
            Comprobador.comprobar("getIluminados() (M1: Virus A y control positivo)",
                    new boolean[][] { { true, false, false }, { false, false, false }, { false, true, false } },
                    analisis.getIluminados());
        } catch (Exception e) {
            Comprobador.fallo("No debería lanzarse ninguna excepción: " + e.getMessage());
        }
    }

    private static void probarAnalisisNoValidos() {
        Comprobador.titulo("Análisis no válidos");
        try {
            Muestra muestra = DatosEjemplo.muestras()[1];
            Microarray panel = DatosEjemplo.panelDePatogenos();
            probarAnalisisNoValido("muestra nula", null, panel, LocalDate.of(2026, 5, 11));
            probarAnalisisNoValido("microarray nulo", muestra, null, LocalDate.of(2026, 5, 11));
            probarAnalisisNoValido("fecha nula", muestra, panel, null);
            probarAnalisisNoValido("fecha anterior a la captura (2026-05-03)", muestra, panel, LocalDate.of(2026, 5, 3));
        } catch (Exception e) {
            Comprobador.fallo("No se han podido crear los datos de ejemplo: " + e.getMessage());
        }
    }

    private static void probarAnalisisNoValido(String caso, Muestra muestra, Microarray microarray, LocalDate fecha) {
        try {
            new Analisis(muestra, microarray, fecha);
            Comprobador.fallo(caso + ": debería lanzar una excepción");
        } catch (Exception e) {
            Comprobador.acierto(caso + ": lanza excepción: " + e.getMessage());
        }
    }

    private static void probarDiasYPuntos() {
        Comprobador.titulo("getDiasDesdeCaptura y contarIluminados");
        try {
            Muestra[] muestras = DatosEjemplo.muestras();
            Microarray panel = DatosEjemplo.panelDePatogenos();
            Analisis mismoDia = new Analisis(muestras[1], panel, LocalDate.of(2026, 5, 4));
            Comprobador.comprobar("getDiasDesdeCaptura() (mismo día)", 0L, mismoDia.getDiasDesdeCaptura());
            Analisis cambioDeMes = new Analisis(muestras[4], panel, LocalDate.of(2026, 6, 3));
            Comprobador.comprobar("getDiasDesdeCaptura() (2026-05-25 a 2026-06-03)", 9L,
                    cambioDeMes.getDiasDesdeCaptura());
            Comprobador.comprobar("contarIluminados() (M1)", 2, mismoDia.contarIluminados());
            Comprobador.comprobar("contarIluminados() (M2)", 3,
                    new Analisis(muestras[2], panel, LocalDate.of(2026, 5, 12)).contarIluminados());
            Comprobador.comprobar("contarIluminados() (M4: solo el control positivo)", 1,
                    cambioDeMes.contarIluminados());
        } catch (Exception e) {
            Comprobador.fallo("No debería lanzarse ninguna excepción: " + e.getMessage());
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
