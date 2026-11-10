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
        probarEncajaEn();
        probarEncontrarEnSinComodines();
        probarEncontrarEnConComodines();
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

    private static void probarEncajaEn() {
        Comprobador.titulo("encajaEn");
        try {
            Sonda sonda = new Sonda("S", "TRC");
            Lectura lectura = new Lectura("L", "GATACA");
            Comprobador.comprobar("\"TRC\" en \"GATACA\" desde 2", true, sonda.encajaEn(lectura, 2));
            Comprobador.comprobar("\"TRC\" en \"GATACA\" desde 0", false, sonda.encajaEn(lectura, 0));
            Comprobador.comprobar("\"TRC\" en \"GATACA\" desde 4 (no cabe)", false, sonda.encajaEn(lectura, 4));
            Comprobador.comprobar("\"TRC\" en \"GATACA\" desde -1", false, sonda.encajaEn(lectura, -1));
            Comprobador.comprobar("\"GANTACA\" en \"GANTACA\" desde 0 (N con N)", true,
                    new Sonda("S", "GANTACA").encajaEn(new Lectura("L", "GANTACA"), 0));
            Comprobador.comprobar("\"GATTACA\" en \"GANTACA\" desde 0 (T con N)", false,
                    new Sonda("S", "GATTACA").encajaEn(new Lectura("L", "GANTACA"), 0));
        } catch (Exception e) {
            Comprobador.fallo("No se ha podido crear una sonda o una lectura válida: " + e.getMessage());
        }
    }

    private static void probarEncontrarEnSinComodines() {
        Comprobador.titulo("encontrarEn, con patrones sin comodines");
        try {
            Lectura lectura = new Lectura("L", "GATACATGCA");
            Comprobador.comprobar("\"GAT\" en \"GATACATGCA\" (al principio)", 0,
                    new Sonda("S", "GAT").encontrarEn(lectura));
            Comprobador.comprobar("\"GCA\" en \"GATACATGCA\" (al final)", 7,
                    new Sonda("S", "GCA").encontrarEn(lectura));
            Comprobador.comprobar("\"CCC\" en \"GATACATGCA\" (no está)", -1,
                    new Sonda("S", "CCC").encontrarEn(lectura));
            Comprobador.comprobar("\"GATACATGCAT\" en \"GATACATGCA\" (más larga)", -1,
                    new Sonda("S", "GATACATGCAT").encontrarEn(lectura));
            Comprobador.comprobar("\"CAGCT\" en \"GATTACAGCTTAGC\"", 5,
                    new Sonda("S", "CAGCT").encontrarEn(new Lectura("L", "GATTACAGCTTAGC")));
            Comprobador.comprobar("\"CAGCT\" en \"GATTACANCTTAGC\" (la N de la lectura no encaja con G)", -1,
                    new Sonda("S", "CAGCT").encontrarEn(new Lectura("L", "GATTACANCTTAGC")));
        } catch (Exception e) {
            Comprobador.fallo("Se ha producido una excepción: " + e);
        }
    }

    private static void probarEncontrarEnConComodines() {
        Comprobador.titulo("encontrarEn, con patrones con comodines");
        try {
            Comprobador.comprobar("\"TRC\" en \"GATACATGCA\" (primera de dos)", 2,
                    new Sonda("S", "TRC").encontrarEn(new Lectura("L", "GATACATGCA")));
            Comprobador.comprobar("\"CAGYT\" en \"GATTACAGCTTAGC\"", 5,
                    new Sonda("S", "CAGYT").encontrarEn(new Lectura("L", "GATTACAGCTTAGC")));
            Comprobador.comprobar("\"CAGYT\" en \"GATTACAGTTTAGC\"", 5,
                    new Sonda("S", "CAGYT").encontrarEn(new Lectura("L", "GATTACAGTTTAGC")));
            Comprobador.comprobar("\"CANYT\" en \"GATTACANCTTAGC\" (N con N)", 5,
                    new Sonda("S", "CANYT").encontrarEn(new Lectura("L", "GATTACANCTTAGC")));
            Comprobador.comprobar("\"CAGYT\" en \"GATTACANCTTAGC\" (G con N)", -1,
                    new Sonda("S", "CAGYT").encontrarEn(new Lectura("L", "GATTACANCTTAGC")));
            Comprobador.comprobar("\"NNNN\" en \"GATACATGCA\" (al principio)", 0,
                    new Sonda("S", "NNNN").encontrarEn(new Lectura("L", "GATACATGCA")));
        } catch (Exception e) {
            Comprobador.fallo("Se ha producido una excepción: " + e);
        }
    }

    private static void probarHibridaCon() {
        Comprobador.titulo("hibridaCon");
        try {
            Sonda virusA = new Sonda("Virus A", "ACCGTTAGCTGA");
            Comprobador.comprobar("Virus A, con su diana (ACCGTTAGCTGA)", true,
                    virusA.hibridaCon(new Lectura("L1", "GGAACCGTTAGCTGACCAT")));
            Comprobador.comprobar("Virus A, sin su diana", false,
                    virusA.hibridaCon(new Lectura("L2", "GGAACCGTTAGGTGACCAT")));
            Sonda virusB = new Sonda("Virus B (variantes 1 y 2)", "TTGRCATCCAGT");
            Comprobador.comprobar("Virus B, variante 1 (TTGACATCCAGT)", true,
                    virusB.hibridaCon(new Lectura("L3", "CATTGACATCCAGTTT")));
            Comprobador.comprobar("Virus B, variante 2 (TTGGCATCCAGT)", true,
                    virusB.hibridaCon(new Lectura("L4", "CATTGGCATCCAGTTT")));
            Comprobador.comprobar("Virus B, otra variante (TTGCCATCCAGT)", false,
                    virusB.hibridaCon(new Lectura("L5", "CATTGCCATCCAGTTT")));
        } catch (Exception e) {
            Comprobador.fallo("Se ha producido una excepción: " + e);
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
