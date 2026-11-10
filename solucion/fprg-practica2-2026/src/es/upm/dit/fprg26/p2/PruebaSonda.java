package es.upm.dit.fprg26.p2;

import es.upm.dit.fprg26.p2.auxiliar.Comprobador;

/**
 * Pruebas de la clase {@link Sonda}, con una muestra de casos de cada método. Superarlas
 * no garantiza que su clase sea correcta: añada sus propios casos de prueba en
 * {@link #pruebasPropias()}.
 */
public class PruebaSonda {

    private PruebaSonda() {
        // Solo contiene el método main: no se instancia
    }

    /**
     * Ejecuta las pruebas.
     *
     * @param args no se usa
     */
    public static void main(String[] args) {
        probarConstructor();
        probarEncaja();
        probarEncajaEn();
        probarBuscarEn();
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
        Comprobador.comprobar("encaja('A', 'A')", true, Sonda.encaja('A', 'A'));
        Comprobador.comprobar("encaja('A', 'C')", false, Sonda.encaja('A', 'C'));
        Comprobador.comprobar("encaja('N', 'G')", true, Sonda.encaja('N', 'G'));
        Comprobador.comprobar("encaja('N', 'N')", true, Sonda.encaja('N', 'N'));
        Comprobador.comprobar("encaja('A', 'N')", false, Sonda.encaja('A', 'N'));
        Comprobador.comprobar("encaja('R', 'G')", true, Sonda.encaja('R', 'G'));
        Comprobador.comprobar("encaja('R', 'C')", false, Sonda.encaja('R', 'C'));
        Comprobador.comprobar("encaja('R', 'N')", false, Sonda.encaja('R', 'N'));
        Comprobador.comprobar("encaja('Y', 'T')", true, Sonda.encaja('Y', 'T'));
        Comprobador.comprobar("encaja('Y', 'A')", false, Sonda.encaja('Y', 'A'));
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

    private static void probarBuscarEn() {
        Comprobador.titulo("buscarEn");
        try {
            Lectura lectura = new Lectura("L", "GATACATGCA");
            Comprobador.comprobar("\"TRC\" en \"GATACATGCA\" (primera de dos)", 2,
                    new Sonda("S", "TRC").buscarEn(lectura));
            Comprobador.comprobar("\"GAT\" en \"GATACATGCA\" (al principio)", 0,
                    new Sonda("S", "GAT").buscarEn(lectura));
            Comprobador.comprobar("\"GCA\" en \"GATACATGCA\" (al final)", 7,
                    new Sonda("S", "GCA").buscarEn(lectura));
            Comprobador.comprobar("\"CCC\" en \"GATACATGCA\" (no está)", -1,
                    new Sonda("S", "CCC").buscarEn(lectura));
            Comprobador.comprobar("\"GATACATGCAT\" en \"GATACATGCA\" (más larga)", -1,
                    new Sonda("S", "GATACATGCAT").buscarEn(lectura));
        } catch (Exception e) {
            Comprobador.fallo("No se ha podido crear una sonda o una lectura válida: " + e.getMessage());
        }
    }

    private static void probarHibridaCon() {
        Comprobador.titulo("hibridaCon");
        try {
            Sonda sonda = new Sonda("Virus B (variantes 1 y 2)", "TTGRCATCCAGT");
            Comprobador.comprobar("variante 1 (TTGACATCCAGT)", true,
                    sonda.hibridaCon(new Lectura("L1", "CATTGACATCCAGTTT")));
            Comprobador.comprobar("variante 2 (TTGGCATCCAGT)", true,
                    sonda.hibridaCon(new Lectura("L2", "CATTGGCATCCAGTTT")));
            Comprobador.comprobar("otra variante (TTGCCATCCAGT)", false,
                    sonda.hibridaCon(new Lectura("L3", "CATTGCCATCCAGTTT")));
        } catch (Exception e) {
            Comprobador.fallo("No se ha podido crear una sonda o una lectura válida: " + e.getMessage());
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
