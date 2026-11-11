package es.upm.dit.fprg26.p2;

/**
 * Comprobaciones solo para el profesorado: no se entregan al alumnado.
 * <p>
 * Comprueban {@link Sonda#contarLecturasQueHibridan(Muestra)}, cuyas pruebas tiene que
 * diseñar el alumnado (apartado 6 del enunciado), y que las clases proporcionadas no
 * permiten modificar su estado desde fuera (copias de los arrays). {@code scripts/verificar.sh}
 * las añade a la solución y las ejecuta.
 * </p>
 */
class PruebaProfesorado {

    /**
     * Ejecuta las comprobaciones.
     */
    void main() {
        probarContarLecturasQueHibridan();
        probarCopiasDeArrays();
        Comprobador.resumen();
    }

    private static void probarContarLecturasQueHibridan() {
        Comprobador.titulo("contarLecturasQueHibridan");
        try {
            Muestra[] muestras = DatosEjemplo.muestras();
            Comprobador.comprobar("Virus B con M2 (dos de las tres lecturas)", 2,
                    new Sonda("Virus B", "TTGRCATCCAGT").contarLecturasQueHibridan(muestras[2]));
            Comprobador.comprobar("Virus B con M1 (ninguna)", 0,
                    new Sonda("Virus B", "TTGRCATCCAGT").contarLecturasQueHibridan(muestras[1]));
            Comprobador.comprobar("Virus A con M1 (una)", 1,
                    new Sonda("Virus A", "ACCGTTAGCTGA").contarLecturasQueHibridan(muestras[1]));
            Comprobador.comprobar("GAT con tres lecturas: directa, en la otra hebra (ATC) y sin diana", 2,
                    new Sonda("S", "GAT").contarLecturasQueHibridan(new Muestra("X", DatosEjemplo.hace(1),
                            new Lectura[] { new Lectura("L1", "CCGATCC"), new Lectura("L2", "CCATCCC"),
                                new Lectura("L3", "CCCCCCC") })));
        } catch (Exception e) {
            Comprobador.fallo("No debería lanzarse ninguna excepción: " + e);
        }
    }

    private static void probarCopiasDeArrays() {
        Comprobador.titulo("Las clases proporcionadas no se pueden modificar desde fuera");
        try {
            Lectura[] lecturas = { new Lectura("L1", "GATTACA"), new Lectura("L2", "CCCC") };
            Muestra muestra = new Muestra("X", DatosEjemplo.hace(1), lecturas);
            lecturas[0] = null;
            Comprobador.comprobar("Muestra: cambiar el array que recibió el constructor no cambia la muestra",
                    "L1", muestra.getLectura(0).getId());
            muestra.getLecturas()[0] = null;
            Comprobador.comprobar("Muestra: cambiar el array que devuelve getLecturas() no cambia la muestra",
                    "L1", muestra.getLectura(0).getId());

            Sonda[][] sondas = { { new Sonda("S1", "GAT"), new Sonda("S2", "TAC") } };
            Microarray microarray = new Microarray("Chip", sondas);
            sondas[0][0] = null;
            Comprobador.comprobar("Microarray: cambiar la matriz que recibió el constructor no cambia el chip",
                    "S1", microarray.getSonda(0, 0).getNombre());

            Analisis analisis = new Analisis(muestra, microarray);
            int antes = analisis.contarIluminados();
            boolean[][] iluminados = analisis.getIluminados();
            iluminados[0][0] = !iluminados[0][0];
            iluminados[0][1] = !iluminados[0][1];
            Comprobador.comprobar("Analisis: cambiar la matriz que devuelve getIluminados() no cambia el análisis",
                    antes, analisis.contarIluminados());
        } catch (Exception e) {
            Comprobador.fallo("No debería lanzarse ninguna excepción: " + e);
        }
    }
}
