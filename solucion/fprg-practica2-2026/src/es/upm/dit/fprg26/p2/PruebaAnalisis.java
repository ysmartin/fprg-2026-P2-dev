package es.upm.dit.fprg26.p2;

import module java.base;

/**
 * Pruebas de la clase {@link Analisis}, con los datos de ejemplo de {@link DatosEjemplo}.
 * Añada sus propios casos de prueba en {@link #pruebasPropias()}.
 */
class PruebaAnalisis {

    /**
     * Ejecuta las pruebas.
     */
    void main() {
        probarConstructorYAcceso();
        probarAnalisisNoValidos();
        probarDias();
        probarContarIluminados();
        pruebasPropias();
        Comprobador.resumen();
    }

    private static void probarConstructorYAcceso() {
        Comprobador.titulo("Constructor y métodos de acceso");
        try {
            Muestra muestra = DatosEjemplo.muestras()[1];
            Microarray panel = DatosEjemplo.panelDePatogenos();
            Analisis analisis = new Analisis(muestra, panel);
            Comprobador.comprobar("getMuestra()", muestra, analisis.getMuestra());
            Comprobador.comprobar("getMicroarray()", panel, analisis.getMicroarray());
            Comprobador.comprobar("getFechaAnalisis() (hoy)", LocalDate.now(), analisis.getFechaAnalisis());
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
            probarAnalisisNoValido("muestra nula", null, panel);
            probarAnalisisNoValido("microarray nulo", muestra, null);
        } catch (Exception e) {
            Comprobador.fallo("No se han podido crear los datos de ejemplo: " + e.getMessage());
        }
    }

    private static void probarAnalisisNoValido(String caso, Muestra muestra, Microarray microarray) {
        try {
            new Analisis(muestra, microarray);
            Comprobador.fallo(caso + ": debería lanzar una excepción");
        } catch (Exception e) {
            Comprobador.acierto(caso + ": lanza excepción: " + e.getMessage());
        }
    }

    private static void probarDias() {
        Comprobador.titulo("getDiasDesdeCaptura");
        try {
            Microarray panel = DatosEjemplo.panelDePatogenos();
            Lectura[] lecturas = DatosEjemplo.muestras()[1].getLecturas();
            probarDias(new Muestra("Capturada hoy", LocalDate.now(), lecturas), panel, 0);
            probarDias(DatosEjemplo.muestras()[1], panel, 2);
            probarDias(new Muestra("Capturada hace 40 días (cambio de mes)", DatosEjemplo.hace(40), lecturas),
                    panel, 40);
            probarDias(new Muestra("Capturada hace 400 días (cambio de año)", DatosEjemplo.hace(400), lecturas),
                    panel, 400);
        } catch (Exception e) {
            Comprobador.fallo("No debería lanzarse ninguna excepción: " + e.getMessage());
        }
    }

    private static void probarDias(Muestra muestra, Microarray microarray, long esperado) throws Exception {
        Comprobador.comprobar("getDiasDesdeCaptura() de " + muestra.getId() + " (" + muestra.getFechaCaptura() + ")",
                esperado, new Analisis(muestra, microarray).getDiasDesdeCaptura());
    }

    private static void probarContarIluminados() {
        Comprobador.titulo("contarIluminados");
        try {
            Muestra[] muestras = DatosEjemplo.muestras();
            Microarray panel = DatosEjemplo.panelDePatogenos();
            Comprobador.comprobar("contarIluminados() (M1)", 2, new Analisis(muestras[1], panel).contarIluminados());
            Comprobador.comprobar("contarIluminados() (M2)", 3, new Analisis(muestras[2], panel).contarIluminados());
            Comprobador.comprobar("contarIluminados() (M4: solo el control positivo)", 1,
                    new Analisis(muestras[4], panel).contarIluminados());
            Comprobador.comprobar("contarIluminados() (M5 con el chip de expresión: la palabra «ADN»)", 53,
                    new Analisis(muestras[6], DatosEjemplo.chipDeExpresion()).contarIluminados());
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
