package es.upm.dit.fprg26.p2;

/**
 * Comprobaciones solo para el profesorado: no se entregan al alumnado.
 * <p>
 * Comprueban que las clases proporcionadas no permiten modificar su estado desde fuera
 * (copias de los arrays). {@code scripts/verificar.sh} las añade a la solución y las
 * ejecuta.
 * </p>
 */
class PruebaProfesorado {

    /**
     * Ejecuta las comprobaciones.
     */
    void main() {
        probarCopiasDeArrays();
        Comprobador.resumen();
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
