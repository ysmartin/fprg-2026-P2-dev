package es.upm.dit.fprg26.p2;

/**
 * Pruebas de las ampliaciones A1 (todas las apariciones) y A2 (intensidades y sondas
 * detectadas). Se añade a la carpeta src del proyecto, en el paquete es.upm.dit.fprg26.p2,
 * cuando se trabajan las ampliaciones.
 */
class PruebaAmpliaciones {

    /**
     * Ejecuta las pruebas.
     */
    void main() {
        probarTodasLasApariciones();
        probarIntensidadesYDetectadas();
        Comprobador.resumen();
    }

    private static void probarTodasLasApariciones() {
        Comprobador.titulo("A1 · Todas las apariciones");
        try {
            Lectura lectura = new Lectura("L", "GATACATGCA");
            Comprobador.comprobar("contarEn: \"TRC\" en \"GATACATGCA\" (2 y 6 en directo, 7 en inverso)", 3,
                    new Sonda("S", "TRC").contarEn(lectura));
            Comprobador.comprobar("contarEn: \"AAA\" en \"AAAAA\" (solapadas)", 3,
                    new Sonda("S", "AAA").contarEn(new Lectura("L", "AAAAA")));
            Comprobador.comprobar("contarEn: \"CCC\" en \"GATACATGCA\"", 0,
                    new Sonda("S", "CCC").contarEn(lectura));
            Comprobador.comprobar("encontrarTodasEn: \"TRC\" en \"GATACATGCA\"", new int[] { 2, 6, 7 },
                    new Sonda("S", "TRC").encontrarTodasEn(lectura));
            Comprobador.comprobar("encontrarTodasEn: \"CAGCT\" en \"GATTAGCTGTTAGC\" (en sentido inverso)",
                    new int[] { 4 }, new Sonda("S", "CAGCT").encontrarTodasEn(new Lectura("L", "GATTAGCTGTTAGC")));
            Comprobador.comprobar("encontrarTodasEn: \"CCC\" en \"GATACATGCA\" (ninguna)", new int[0],
                    new Sonda("S", "CCC").encontrarTodasEn(lectura));
        } catch (Exception e) {
            Comprobador.fallo("Se ha producido una excepción: " + e);
        }
    }

    private static void probarIntensidadesYDetectadas() {
        Comprobador.titulo("A2 · Intensidades y sondas detectadas");
        try {
            Muestra[] muestras = DatosEjemplo.muestras();
            Microarray panel = DatosEjemplo.panelDePatogenos();
            Comprobador.comprobar("intensidades (M2: el Virus B está en dos lecturas)",
                    new int[][] { { 0, 2, 0 }, { 0, 1, 0 }, { 0, 1, 0 } }, panel.intensidades(muestras[2]));
            Comprobador.comprobar("sondasDetectadas (M2)",
                    new String[] { "Virus B (variantes 1 y 2)", "Bacteria E (cepas 1 y 2)", "Control positivo" },
                    new Analisis(muestras[2], panel).sondasDetectadas());
            Comprobador.comprobar("sondasDetectadas (M4)", new String[] { "Control positivo" },
                    new Analisis(muestras[4], panel).sondasDetectadas());
            Comprobador.comprobar("sondasDetectadas (chip de prueba)", new String[] { "Exacta", "Con N", "Con Y y R" },
                    new Analisis(DatosEjemplo.muestraDePrueba(), DatosEjemplo.chipDePrueba()).sondasDetectadas());
        } catch (Exception e) {
            Comprobador.fallo("No debería lanzarse ninguna excepción: " + e);
        }
    }
}
