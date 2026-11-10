package es.upm.dit.fprg26.p2;

/**
 * Pruebas de la clase {@link Sonda}, con una muestra de casos de cada método. Superarlas
 * no garantiza que su clase sea correcta: añada sus propios casos de prueba en
 * {@link #pruebasPropias()}.
 */
class PruebaSonda {

    /**
     * Ejecuta las pruebas.
     */
    void main() {
        probarConstructor();
        probarEncaja();
        probarEncajaEnDirecto();
        probarEncontrarEnSinComodines();
        probarEncontrarEnConComodines();
        probarEncajaInverso();
        probarEncajaEnInverso();
        probarEncontrarEnHebraInversa();
        probarHibridaCon();
        pruebasPropias();
        Comprobador.resumen();
    }

    private static void probarConstructor() {
        Comprobador.titulo("Constructor y métodos de acceso");
        try {
            Sonda sonda = new Sonda("Virus B", "TTGRCATCCAGT");
            Comprobador.comprobar("getNombre()", "Virus B", sonda.getNombre());
            Comprobador.comprobar("getPatron()", "TTGRCATCCAGT", sonda.getPatron());
            Comprobador.comprobar("getLongitud()", 12, sonda.getLongitud());
        } catch (Exception e) {
            Comprobador.fallo("new Sonda(\"Virus B\", \"TTGRCATCCAGT\") no debería lanzar excepción: " + e.getMessage());
        }
        probarSondaNoValida(null, "GATTACA");
        probarSondaNoValida("  ", "GATTACA");
        probarSondaNoValida("S", null);
        probarSondaNoValida("S", "");
        probarSondaNoValida("S", "GATZACA");
        probarSondaNoValida("S", "gattaca");
        probarSondaNoValida("S", "GAUUACA");
    }

    private static void probarSondaNoValida(String nombre, String patron) {
        String descripcion = "new Sonda(" + entreComillas(nombre) + ", " + entreComillas(patron) + ")";
        try {
            new Sonda(nombre, patron);
            Comprobador.fallo(descripcion + " debería lanzar una excepción");
        } catch (Exception e) {
            Comprobador.acierto(descripcion + " lanza excepción: " + e.getMessage());
        }
    }

    private static String entreComillas(String texto) {
        return texto == null ? "null" : "\"" + texto + "\"";
    }

    private static void probarEncaja() {
        Comprobador.titulo("encaja");
        probarEncaja('A', 'A', true);
        probarEncaja('A', 'C', false);
        probarEncaja('N', 'G', true);
        probarEncaja('N', 'N', true);
        probarEncaja('A', 'N', false);
        probarEncaja('R', 'G', true);
        probarEncaja('R', 'C', false);
        probarEncaja('R', 'N', false);
        probarEncaja('Y', 'T', true);
        probarEncaja('Y', 'A', false);
        probarEncajaNoValido('X', 'A');
        probarEncajaNoValido('a', 'A');
        probarEncajaNoValido('A', 'R');
        probarEncajaNoValido('A', 'U');
    }

    private static void probarEncaja(char simbolo, char base, boolean esperado) {
        String descripcion = "encaja('" + simbolo + "', '" + base + "')";
        try {
            Comprobador.comprobar(descripcion, esperado, Sonda.encaja(simbolo, base));
        } catch (Exception e) {
            Comprobador.fallo(descripcion + " no debería lanzar excepción: " + e.getMessage());
        }
    }

    private static void probarEncajaNoValido(char simbolo, char base) {
        String descripcion = "encaja('" + simbolo + "', '" + base + "')";
        try {
            Sonda.encaja(simbolo, base);
            Comprobador.fallo(descripcion + " debería lanzar una excepción");
        } catch (Exception e) {
            Comprobador.acierto(descripcion + " lanza excepción: " + e.getMessage());
        }
    }

    private static void probarEncajaEnDirecto() {
        Comprobador.titulo("encajaEn, en sentido directo");
        probarEncajaEn("TRC", "GATACA", 2, true, "");
        probarEncajaEn("TRC", "GATACA", 0, false, "");
        probarEncajaEn("GANTACA", "GANTACA", 0, true, " (N con N)");
        probarEncajaEn("GATTACA", "GANTACA", 0, false, " (T con N)");
        probarEncajaEnNoCabe("TRC", "GATACA", 4);
        probarEncajaEnNoCabe("TRC", "GATACA", -1);
    }

    private static void probarEncontrarEnSinComodines() {
        Comprobador.titulo("encontrarEn, con patrones sin comodines");
        probarEncontrarEn("GAT", "GATACATGCA", 0, " (al principio)");
        probarEncontrarEn("AGC", "GATTACATTAGC", 9, " (al final)");
        probarEncontrarEn("CCC", "GATACATGCA", -1, " (no está)");
        probarEncontrarEn("GATACATGCAT", "GATACATGCA", -1, " (más largo que la lectura)");
        probarEncontrarEn("CAGCT", "GATTACAGCTTAGC", 5, "");
        probarEncontrarEn("CAGCT", "GATTACANCTTAGC", -1, " (la N de la lectura no encaja con G)");
    }

    private static void probarEncontrarEnConComodines() {
        Comprobador.titulo("encontrarEn, con patrones con comodines");
        probarEncontrarEn("TRC", "GATACATGCA", 2, " (la primera aparición)");
        probarEncontrarEn("CAGYT", "GATTACAGCTTAGC", 5, "");
        probarEncontrarEn("CAGYT", "GATTACAGTTTAGC", 5, "");
        probarEncontrarEn("CANYT", "GATTACANCTTAGC", 5, " (N con N)");
        probarEncontrarEn("CAGYT", "GATTACANCTTAGC", -1, " (G con N)");
        probarEncontrarEn("NNNN", "GATACATGCA", 0, " (al principio)");
    }

    private static void probarEncajaInverso() {
        Comprobador.titulo("encajaInverso");
        probarEncajaInverso('A', 'T', true);
        probarEncajaInverso('A', 'A', false);
        probarEncajaInverso('G', 'C', true);
        probarEncajaInverso('R', 'T', true);
        probarEncajaInverso('R', 'G', false);
        probarEncajaInverso('Y', 'A', true);
        probarEncajaInverso('N', 'N', true);
        probarEncajaInverso('A', 'N', false);
        String descripcion = "encajaInverso('A', 'R')";
        try {
            Sonda.encajaInverso('A', 'R');
            Comprobador.fallo(descripcion + " debería lanzar una excepción");
        } catch (Exception e) {
            Comprobador.acierto(descripcion + " lanza excepción: " + e.getMessage());
        }
    }

    private static void probarEncajaInverso(char simbolo, char base, boolean esperado) {
        String descripcion = "encajaInverso('" + simbolo + "', '" + base + "')";
        try {
            Comprobador.comprobar(descripcion, esperado, Sonda.encajaInverso(simbolo, base));
        } catch (Exception e) {
            Comprobador.fallo(descripcion + " no debería lanzar excepción: " + e.getMessage());
        }
    }

    private static void probarEncajaEnInverso() {
        Comprobador.titulo("encajaEn, en sentido inverso");
        probarEncajaEn("CAGCT", "GATTAGCTGTTAGC", 4, true, " (AGCTG)");
        probarEncajaEn("CAGCT", "GATTAGCTGTTAGC", 3, false, "");
        probarEncajaEn("CAGYT", "GATTAACTGTTAGC", 4, true, " (AACTG)");
        probarEncajaEn("CANYT", "GATTAGNTGTTAGC", 4, true, " (N con N)");
        probarEncajaEn("CAGYT", "GATTAGNTGTTAGC", 4, false, " (C con N)");
    }

    private static void probarEncontrarEnHebraInversa() {
        Comprobador.titulo("encontrarEn, con la diana en la otra hebra");
        probarEncontrarEn("CAGCT", "GATTAGCTGTTAGC", 4, "");
        probarEncontrarEn("GCA", "GATACATGCA", 6, " (en sentido inverso en la 6, antes que en directo en la 7)");
        probarEncontrarEn("CAGYT", "GATTAACTGTTAGC", 4, "");
        probarEncontrarEn("CANYT", "GATTAGNTGTTAGC", 4, "");
    }

    private static void probarHibridaCon() {
        Comprobador.titulo("hibridaCon");
        probarHibridaCon("ACCGTTAGCTGA", "GGAACCGTTAGCTGACCAT", true, " (Virus A, con su diana)");
        probarHibridaCon("ACCGTTAGCTGA", "GGAACCGTTAGGTGACCAT", false, " (Virus A, sin su diana)");
        probarHibridaCon("ACCGTTAGCTGA", "GGATTCAGCTAACGGTCCAT", true, " (Virus A, con su diana en la otra hebra)");
        probarHibridaCon("TTGRCATCCAGT", "CATTGACATCCAGTTT", true, " (Virus B, variante 1)");
        probarHibridaCon("TTGRCATCCAGT", "CATTGGCATCCAGTTT", true, " (Virus B, variante 2)");
        probarHibridaCon("TTGRCATCCAGT", "CATTGCCATCCAGTTT", false, " (Virus B, otra variante)");
    }

    // Métodos auxiliares: cada uno comprueba un caso, y también le pueden servir para sus pruebas

    private static void probarEncajaEn(String patron, String lectura, int inicio, boolean esperado, String nota) {
        String descripcion = "\"" + patron + "\" en \"" + lectura + "\" desde " + inicio + nota;
        try {
            Comprobador.comprobar(descripcion, esperado, new Sonda("S", patron).encajaEn(new Lectura("L", lectura), inicio));
        } catch (Exception e) {
            Comprobador.fallo(descripcion + " no debería lanzar excepción: " + e.getMessage());
        }
    }

    private static void probarEncajaEnNoCabe(String patron, String lectura, int inicio) {
        String descripcion = "\"" + patron + "\" en \"" + lectura + "\" desde " + inicio + " (no cabe)";
        try {
            new Sonda("S", patron).encajaEn(new Lectura("L", lectura), inicio);
            Comprobador.fallo(descripcion + " debería lanzar una excepción");
        } catch (Exception e) {
            Comprobador.acierto(descripcion + " lanza excepción: " + e.getMessage());
        }
    }

    private static void probarEncontrarEn(String patron, String lectura, int esperado, String nota) {
        String descripcion = "\"" + patron + "\" en \"" + lectura + "\"" + nota;
        try {
            Comprobador.comprobar(descripcion, esperado, new Sonda("S", patron).encontrarEn(new Lectura("L", lectura)));
        } catch (Exception e) {
            Comprobador.fallo(descripcion + " no debería lanzar excepción: " + e.getMessage());
        }
    }

    private static void probarHibridaCon(String patron, String lectura, boolean esperado, String nota) {
        String descripcion = "\"" + patron + "\" con \"" + lectura + "\"" + nota;
        try {
            Comprobador.comprobar(descripcion, esperado, new Sonda("S", patron).hibridaCon(new Lectura("L", lectura)));
        } catch (Exception e) {
            Comprobador.fallo(descripcion + " no debería lanzar excepción: " + e.getMessage());
        }
    }

    /**
     * Sus propios casos de prueba.
     */
    private static void pruebasPropias() {
        Comprobador.titulo("Pruebas propias");
        // TODO: añada aquí sus propios casos de prueba. Puede usar los métodos auxiliares de
        // esta clase; por ejemplo: probarEncontrarEn("GAT", "CCGATCC", 2, "");
    }
}
