package es.upm.dit.fprg26.p2;

import module java.base;

import es.upm.dit.fprg26.p2.AnimacionFotograma.Argumento;
import es.upm.dit.fprg26.p2.AnimacionFotograma.Casilla;
import es.upm.dit.fprg26.p2.AnimacionFotograma.Llamada;
import es.upm.dit.fprg26.p2.AnimacionFotograma.Variable;
import es.upm.dit.fprg26.p2.AnimacionFotograma.Ventana;

/**
 * Ejecuta Microarray.hibridar(Muestra) paso a paso y crea los fotogramas de la animación.
 * <p>
 * Reproduce los bucles y las llamadas de la solución de referencia (hibridar(Muestra),
 * hibridar(Lectura), hibridaCon, encontrarEn, encajaEn y encajaInverso), y en cada paso anota
 * el estado, como un depurador: la pila de llamadas con sus variables locales, la sonda y la
 * lectura que se comparan, y la ventana deslizante. Las operaciones elementales
 * (Sonda.encaja y Lectura.complementaria) se delegan en la solución, y el resultado de cada
 * método reproducido se compara con el del método real: si la solución cambia y la animación
 * deja de reflejarla, se lanza una excepción en lugar de generar una animación equivocada.
 * </p>
 * <p>
 * Como mostrar todos los pasos con todas las sondas haría la animación demasiado larga, el
 * nivel de detalle se decide para cada sonda y cada lectura (ver {@link Plan}).
 * </p>
 */
final class AnimacionTraza {

    /** Nivel de detalle con que se muestra la comprobación de una sonda con una lectura. */
    enum Detalle {
        /** Un fotograma por sonda: solo el resultado de hibridaCon. */
        RESUMEN(1, "Avance rápido: un fotograma por sonda"),
        /** Un fotograma por posición de la ventana: el resultado de cada encajaEn. */
        POSICIONES(2, "Avance rápido: un fotograma por posición"),
        /** Un fotograma por comparación: el resultado de cada encaja y encajaInverso. */
        COMPARACIONES(3, null),
        /** Todos los pasos: antes de cada comparación, otro fotograma indica qué se compara. */
        PASO_A_PASO(4, null);

        /** Granularidad máxima de los fotogramas que se muestran con este nivel. */
        private final int nivel;

        /** Aviso que se muestra con este nivel, o null si no se omite ningún paso importante. */
        private final String aviso;

        Detalle(int nivel, String aviso) {
            this.nivel = nivel;
            this.aviso = aviso;
        }
    }

    /** Decide el nivel de detalle con que se muestra cada sonda con cada lectura. */
    interface Plan {

        /**
         * Devuelve el nivel de detalle de una sonda con una lectura.
         *
         * @param lectura índice de la lectura en la muestra (empezando en 0)
         * @param fila fila de la sonda en el chip
         * @param columna columna de la sonda en el chip
         * @return nivel de detalle
         */
        Detalle detalle(int lectura, int fila, int columna);
    }

    // Granularidad de cada fotograma: se muestra si no supera el nivel del detalle actual
    /** Fotogramas de hibridar(Muestra) y de hibridar(Lectura): se muestran siempre. */
    private static final int GENERAL = 0;
    /** Resultado de cada sonda. */
    private static final int SONDA = 1;
    /** Resultado de cada posición de la ventana (y comienzo y final de cada sonda). */
    private static final int POSICION = 2;
    /** Resultado de cada comparación de un símbolo con una base. */
    private static final int COMPARACION = 3;
    /** Pasos intermedios: qué se va a comparar. */
    private static final int SUBPASO = 4;

    /** Duración de los fotogramas de cada granularidad, en centésimas de segundo. */
    private static final int[] DURACION = { 350, 250, 200, 150, 110 };

    /** Microarray que se analiza. */
    private final Microarray microarray;

    /** Plan de detalle. */
    private final Plan plan;

    /** Fotogramas creados hasta el momento. */
    private final List<AnimacionFotograma> fotogramas = new ArrayList<>();

    /** Llamadas en curso, de la más externa a la más interna. */
    private final List<LlamadaEnCurso> pila = new ArrayList<>();

    /** Pila del último fotograma creado, para resaltar las variables que cambian. */
    private List<Llamada> pilaAnterior = List.of();

    /** Nivel de detalle actual. */
    private Detalle detalle = Detalle.PASO_A_PASO;

    /** Índice de la lectura que se analiza, o -1. */
    private int indiceLectura = -1;

    /** Lectura que se analiza, o null. */
    private Lectura lectura;

    /** Fila y columna de la sonda que se comprueba, o -1. */
    private int filaSonda = -1;
    private int columnaSonda = -1;

    /** Sonda que se comprueba, o null. */
    private Sonda sonda;

    /** Posición que devolvió la última llamada a encontrarEn. */
    private int posicionEncontrada = -1;

    /** Puntos iluminados al terminar el análisis, o null mientras no ha terminado. */
    private boolean[][] luces;

    // Estado de la ventana deslizante (ver AnimacionFotograma.Ventana)
    private int inicio = -1;
    private int ultimoInicio = -1;
    private Casilla[] directo;
    private Casilla[] inverso;
    private String notaDirecto;
    private String notaInverso;

    /** Una llamada en curso, con sus variables locales (que pueden cambiar). */
    private static final class LlamadaEnCurso {
        private final String receptor;
        private final String metodo;
        private final List<Argumento> argumentos;
        /** La llamada como se escribiría en Java (por ejemplo, "s1.encajaEn(l1, 2)"). */
        private final String texto;
        private final Map<String, Object> variables = new LinkedHashMap<>();
        private final Map<String, String> tipos = new HashMap<>();
        private String devuelve;
        /** Elemento que se resalta en sus matrices (fila y columna), o null. */
        private int[] marca;

        LlamadaEnCurso(String receptor, String metodo, List<Argumento> argumentos) {
            this.receptor = receptor;
            this.metodo = metodo;
            this.argumentos = argumentos;
            this.texto = new Llamada(receptor, metodo, argumentos, List.of(), null).texto();
        }
    }

    private AnimacionTraza(Microarray microarray, Plan plan) {
        this.microarray = microarray;
        this.plan = plan;
    }

    /**
     * Analiza una muestra con un microarray y devuelve los fotogramas que muestran el proceso.
     *
     * @param microarray microarray con que se analiza la muestra
     * @param muestra muestra que se analiza
     * @param plan nivel de detalle de cada sonda con cada lectura
     * @return fotogramas de la animación
     * @throws Exception si la animación no reproduce la solución, o si esta lanza alguna
     *                   excepción
     */
    static List<AnimacionFotograma> generar(Microarray microarray, Muestra muestra, Plan plan) throws Exception {
        AnimacionTraza traza = new AnimacionTraza(microarray, plan);
        traza.analizar(muestra);
        return List.copyOf(traza.fotogramas);
    }

    // ------------------------------------------------------------------------------------
    // Reproducción del algoritmo de la solución
    // ------------------------------------------------------------------------------------

    private void analizar(Muestra muestra) throws Exception {
        fotograma(GENERAL, 450, "Se analiza la muestra " + codigo(muestra.getId()) + ", de " + muestra.getNumeroLecturas()
                + " lecturas, con el microarray " + codigo(microarray.getNombre()) + ", de " + microarray.getFilas() + "x"
                + microarray.getColumnas() + " sondas: se invoca "
                + codigo(microarray.getNombre() + ".hibridar(" + muestra.getId() + ")"));
        boolean[][] iluminados = hibridar(muestra);
        luces = iluminados;
        boolean[][] apagados = new boolean[iluminados.length][iluminados[0].length];
        for (int fila = 0; fila < iluminados.length; fila++) {
            for (int columna = 0; columna < iluminados[0].length; columna++) {
                apagados[fila][columna] = !iluminados[fila][columna];
            }
        }
        String resultado = cuantos(iluminados) == 0 ? "no se ilumina ningún punto del chip"
                : "se iluminan los puntos de " + puntos(iluminados, "y");
        if (cuantos(apagados) > 0 && cuantos(iluminados) > 0) {
            resultado += "; " + puntos(apagados, "y") + (cuantos(apagados) == 1 ? " no hibrida" : " no hibridan")
                    + " con ninguna lectura";
        }
        fotograma(GENERAL, 800, "Resultado del análisis: " + resultado);
    }

    /** Reproduce Microarray.hibridar(Muestra). */
    private boolean[][] hibridar(Muestra muestra) throws Exception {
        String llamada = entrar(microarray.getNombre(), "hibridar", argumento("Muestra", "muestra", muestra.getId()));
        boolean[][] iluminados = new boolean[microarray.getFilas()][microarray.getColumnas()];
        variable("boolean[][]", "iluminados", iluminados);
        fotograma(GENERAL, codigo(llamada) + " crea la matriz " + codigo("iluminados") + ", del tamaño del chip ("
                + microarray.getFilas() + "x" + microarray.getColumnas() + "), con todos los puntos apagados ("
                + codigo(false) + ")");
        Lectura[] lecturas = muestra.getLecturas();
        for (int k = 0; k < lecturas.length; k++) {
            indiceLectura = k;
            lectura = lecturas[k];
            quitarVariable("conLectura");      // se declara dentro del bucle
            variable("Lectura", "lectura", lectura.getId());
            fotograma(GENERAL, "Vuelta " + (k + 1) + " del bucle for-each: " + codigo("lectura = " + lectura.getId())
                    + ". Para saber qué sondas hibridan con ella, se invoca "
                    + codigo(microarray.getNombre() + ".hibridar(" + lectura.getId() + ")"));
            boolean[][] conLectura = hibridar(lectura);
            variable("boolean[][]", "conLectura", conLectura);
            fotograma(GENERAL, codigo(microarray.getNombre() + ".hibridar(" + lectura.getId() + ")")
                    + " ha devuelto su matriz, que se guarda en " + codigo("conLectura"));
            boolean[][] antes = AnimacionFotograma.copiar(iluminados);
            for (int fila = 0; fila < microarray.getFilas(); fila++) {
                for (int columna = 0; columna < microarray.getColumnas(); columna++) {
                    if (conLectura[fila][columna]) {
                        iluminados[fila][columna] = true;
                    }
                }
            }
            fotograma(GENERAL, 500, mensajeAcumulacion(antes, conLectura, iluminados));
        }
        indiceLectura = -1;
        lectura = null;
        quitarVariable("lectura");
        quitarVariable("conLectura");
        comprobar(Arrays.deepEquals(iluminados, microarray.hibridar(muestra)), llamada);
        devolver("iluminados");
        fotograma(GENERAL, "No quedan más lecturas: " + codigo(llamada) + " termina y devuelve " + codigo("iluminados"));
        salir();
        return iluminados;
    }

    /** Reproduce Microarray.hibridar(Lectura). */
    private boolean[][] hibridar(Lectura lectura) throws Exception {
        String id = lectura.getId();
        String llamada = entrar(microarray.getNombre(), "hibridar", argumento("Lectura", "lectura", id));
        boolean[][] iluminados = new boolean[microarray.getFilas()][microarray.getColumnas()];
        variable("boolean[][]", "iluminados", iluminados);
        fotograma(GENERAL, codigo(llamada) + " crea su propia matriz " + codigo("iluminados")
                + ", con todos los puntos apagados, y recorre el chip fila a fila con dos bucles anidados");
        for (int fila = 0; fila < microarray.getFilas(); fila++) {
            variable("int", "fila", fila);
            for (int columna = 0; columna < microarray.getColumnas(); columna++) {
                variable("int", "columna", columna);
                filaSonda = fila;
                columnaSonda = columna;
                pila.getLast().marca = new int[] { fila, columna };
                sonda = microarray.getSonda(fila, columna);
                detalle = plan.detalle(indiceLectura, fila, columna);
                vaciarVentana();
                boolean hibrida;
                String porque;
                int profundidad = pila.size();
                try {
                    hibrida = hibridaCon(sonda, lectura);
                    porque = " devuelve " + codigo(hibrida) + " (" + codigo("encontrarEn") + " ha devuelto "
                            + codigo(posicionEncontrada) + ")";
                } catch (Exception e) {
                    // Como en la solución: si no se puede comprobar la sonda, su punto no se ilumina
                    hibrida = false;
                    porque = " lanza una excepción, que se captura (" + e.getMessage() + ")";
                    pila.subList(profundidad, pila.size()).clear();
                }
                iluminados[fila][columna] = hibrida;
                fotograma(SONDA, codigo(sonda.getNombre() + ".hibridaCon(" + id + ")") + porque + ": "
                        + codigo("iluminados[" + fila + "][" + columna + "] = " + hibrida));
            }
            quitarVariable("columna");
        }
        quitarVariable("fila");
        pila.getLast().marca = null;
        filaSonda = -1;
        columnaSonda = -1;
        sonda = null;
        detalle = Detalle.PASO_A_PASO;
        vaciarVentana();
        comprobar(Arrays.deepEquals(iluminados, microarray.hibridar(lectura)), llamada);
        devolver("iluminados");
        fotograma(GENERAL, "Se han comprobado todas las sondas: " + codigo(llamada) + " termina y devuelve su matriz "
                + codigo("iluminados"));
        salir();
        return iluminados;
    }

    /** Reproduce Sonda.hibridaCon(Lectura). */
    private boolean hibridaCon(Sonda sonda, Lectura lectura) throws Exception {
        String llamada = entrar(sonda.getNombre(), "hibridaCon", argumento("Lectura", "lectura", lectura.getId()));
        fotograma(POSICION, codigo("fila = " + filaSonda) + ", " + codigo("columna = " + columnaSonda)
                + ": ¿hibrida la sonda " + codigo(sonda.getNombre()) + ", de patrón " + sonda.getPatron() + ", con la lectura "
                + codigo(lectura.getId()) + "? " + codigo("hibridaCon") + " busca el patrón con " + codigo("encontrarEn"));
        int posicion = encontrarEn(sonda, lectura);
        boolean resultado = posicion >= 0;
        comprobar(resultado == sonda.hibridaCon(lectura), llamada);
        devolver(resultado);
        salir();
        return resultado;
    }

    /** Reproduce Sonda.encontrarEn(Lectura). */
    private int encontrarEn(Sonda sonda, Lectura lectura) throws Exception {
        String llamada = entrar(sonda.getNombre(), "encontrarEn", argumento("Lectura", "lectura", lectura.getId()));
        int ultima = lectura.getLongitud() - sonda.getLongitud();
        ultimoInicio = ultima;
        fotograma(SUBPASO, codigo("encontrarEn") + " desliza el patrón por la lectura: prueba con " + codigo("inicio")
                + " desde 0 hasta "
                + lectura.getLongitud() + " − " + sonda.getLongitud() + " = " + ultima
                + ", la última posición en la que cabe el patrón");
        for (int inicio = 0; inicio <= ultima; inicio++) {
            variable("int", "inicio", inicio);
            if (encajaEn(sonda, lectura, inicio)) {
                devolver(inicio);
                fotograma(POSICION, codigo("encajaEn") + " ha devuelto " + codigo(true) + ": " + codigo("encontrarEn")
                        + " termina y devuelve " + codigo(inicio) + ", sin probar más posiciones");
                comprobar(inicio == sonda.encontrarEn(lectura), llamada);
                posicionEncontrada = inicio;
                ultimoInicio = -1;
                salir();
                return inicio;
            }
        }
        quitarVariable("inicio");
        vaciarVentana();
        notaDirecto = sonda.getPatron() + " no encaja en ninguna posición, en ningún sentido";
        devolver(-1);
        fotograma(POSICION, "El patrón " + sonda.getPatron() + " no encaja en ninguna posición, en ningún sentido: "
                + codigo("encontrarEn") + " devuelve " + codigo(-1));
        comprobar(sonda.encontrarEn(lectura) == -1, llamada);
        posicionEncontrada = -1;
        salir();
        return -1;
    }

    /** Reproduce Sonda.encajaEn(Lectura, int). */
    private boolean encajaEn(Sonda sonda, Lectura lectura, int inicio) throws Exception {
        String patron = sonda.getPatron();
        String llamada = entrar(sonda.getNombre(), "encajaEn", argumento("Lectura", "lectura", lectura.getId()),
                argumento("int", "inicio", inicio));
        if (inicio < 0 || inicio + patron.length() > lectura.getLongitud()) {
            throw new Exception("El patrón no cabe en la lectura a partir de la posición " + inicio);
        }
        this.inicio = inicio;
        directo = pendientes(patron.length());
        inverso = null;
        notaDirecto = null;
        notaInverso = null;
        fotograma(COMPARACION, codigo(llamada) + ": se coloca el patrón a partir de la posición " + inicio + " (cabe, porque "
                + inicio + " + " + patron.length() + " ≤ " + lectura.getLongitud() + ")");

        boolean encajaDirecto = true;
        variable("boolean", "directo", true);
        for (int i = 0; i < patron.length(); i++) {
            variable("int", "i", i);
            char simbolo = patron.charAt(i);
            char base = lectura.getBase(inicio + i);
            if (!compararDirecto(i, simbolo, base)) {
                encajaDirecto = false;
                variable("boolean", "directo", false);
                notaDirecto = explicacion(simbolo, base, false);
                break;
            }
        }
        quitarVariable("i");
        if (encajaDirecto) {
            notaDirecto = parejas(patron, lectura, inicio, false) + ": encaja";
            devolver(true);
            fotograma(POSICION, "Encajan todos los símbolos en sentido directo: " + codigo(llamada) + " devuelve "
                    + codigo(true));
            comprobar(sonda.encajaEn(lectura, inicio), llamada);
            salir();
            return true;
        }

        inverso = pendientes(patron.length());
        fotograma(COMPARACION, "No encaja en sentido directo (" + codigo("directo = false") + "): se prueba en sentido inverso, "
                + "con el patrón de derecha a izquierda frente a las complementarias de la lectura");
        for (int i = 0; i < patron.length(); i++) {
            variable("int", "i", i);
            char simbolo = patron.charAt(patron.length() - 1 - i);
            char base = lectura.getBase(inicio + i);
            if (!compararInverso(i, simbolo, base, patron.length())) {
                notaInverso = explicacion(simbolo, Lectura.complementaria(base), false)
                        + " (complementaria de " + base + ")";
                devolver(false);
                boolean quedan = inicio < lectura.getLongitud() - patron.length();
                fotograma(POSICION, "Tampoco encaja en sentido inverso: " + codigo(llamada) + " devuelve " + codigo(false)
                        + (quedan ? ", y " + codigo("encontrarEn") + " prueba la posición siguiente" : ""));
                comprobar(!sonda.encajaEn(lectura, inicio), llamada);
                salir();
                return false;
            }
        }
        quitarVariable("i");
        notaInverso = parejas(patron, lectura, inicio, true) + ": encaja";
        devolver(true);
        fotograma(POSICION, "Encajan todos los símbolos en sentido inverso: " + codigo(llamada) + " devuelve "
                + codigo(true) + " (la lectura contiene la diana en la otra hebra)");
        comprobar(sonda.encajaEn(lectura, inicio), llamada);
        salir();
        return true;
    }

    /** Compara en sentido directo el símbolo i del patrón con su base (Sonda.encaja). */
    private boolean compararDirecto(int i, char simbolo, char base) throws Exception {
        directo[i] = Casilla.COMPARANDO;
        fotograma(SUBPASO, "Sentido directo, " + codigo("i = " + i) + ": el símbolo " + i + " del patrón (" + simbolo
                + ") se compara con la base " + inicio + " + " + i + " = " + (inicio + i) + " de la lectura (" + base + ")");
        String llamada = entrar(null, "encaja", argumento("char", "simbolo", simbolo), argumento("char", "base", base));
        boolean resultado = Sonda.encaja(simbolo, base);
        devolver(resultado);
        directo[i] = resultado ? Casilla.ENCAJA : Casilla.NO_ENCAJA;
        fotograma(COMPARACION, codigo(llamada) + " devuelve " + codigo(resultado) + ": "
                + explicacion(simbolo, base, resultado));
        salir();
        return resultado;
    }

    /**
     * Compara en sentido inverso el símbolo n - 1 - i del patrón con la complementaria de la
     * base inicio + i (Sonda.encajaInverso, que invoca a Sonda.encaja).
     */
    private boolean compararInverso(int i, char simbolo, char base, int n) throws Exception {
        inverso[i] = Casilla.COMPARANDO;
        fotograma(SUBPASO, "Sentido inverso, " + codigo("i = " + i) + ": el símbolo " + n + " − 1 − " + i + " = " + (n - 1 - i)
                + " del patrón (" + simbolo + ") se compara con la complementaria de la base " + (inicio + i)
                + " de la lectura (" + base + ")");
        String llamada = entrar(null, "encajaInverso", argumento("char", "simbolo", simbolo), argumento("char", "base", base));
        char complementaria = Lectura.complementaria(base);
        entrar(null, "encaja", argumento("char", "simbolo", simbolo), argumento("char", "base", complementaria));
        boolean resultado = Sonda.encaja(simbolo, complementaria);
        devolver(resultado);
        pila.get(pila.size() - 2).devuelve = texto(resultado);
        inverso[i] = resultado ? Casilla.ENCAJA : Casilla.NO_ENCAJA;
        fotograma(COMPARACION, codigo(llamada) + " devuelve " + codigo(resultado) + ": la complementaria de " + base + " es "
                + complementaria + ", y " + explicacion(simbolo, complementaria, resultado));
        salir();
        salir();
        comprobar(resultado == Sonda.encajaInverso(simbolo, base), llamada);
        return resultado;
    }

    // ------------------------------------------------------------------------------------
    // Textos
    // ------------------------------------------------------------------------------------

    /** Explica por qué un símbolo del patrón encaja o no con una base (ver Sonda.encaja). */
    private static String explicacion(char simbolo, char base, boolean encaja) {
        return switch (simbolo) {
            case 'N' -> "N encaja con cualquier base, también con " + base;
            case 'R' -> encaja ? "R (purina) encaja con " + base : "R (purina: A o G) no encaja con " + base;
            case 'Y' -> encaja ? "Y (pirimidina) encaja con " + base : "Y (pirimidina: C o T) no encaja con " + base;
            default -> base == 'N' && !encaja ? simbolo + " no encaja con N (base desconocida)"
                    : simbolo + (encaja ? " encaja con " : " no encaja con ") + base;
        };
    }

    /** Parejas de símbolo y base de una ventana que encaja: por ejemplo, "T con T, R con A". */
    private static String parejas(String patron, Lectura lectura, int inicio, boolean inversa) throws Exception {
        StringJoiner texto = new StringJoiner(", ");
        for (int i = 0; i < patron.length(); i++) {
            char base = lectura.getBase(inicio + i);
            char simbolo = inversa ? patron.charAt(patron.length() - 1 - i) : patron.charAt(i);
            texto.add(simbolo + " con " + (inversa ? Lectura.complementaria(base) : base));
        }
        return texto.toString();
    }

    /** Explica cómo cambia iluminados al acumular conLectura en hibridar(Muestra). */
    private String mensajeAcumulacion(boolean[][] antes, boolean[][] conLectura, boolean[][] despues) throws Exception {
        boolean[][] nuevos = new boolean[antes.length][antes[0].length];
        boolean[][] mantenidos = new boolean[antes.length][antes[0].length];
        for (int fila = 0; fila < antes.length; fila++) {
            for (int columna = 0; columna < antes[0].length; columna++) {
                nuevos[fila][columna] = despues[fila][columna] && !antes[fila][columna];
                mantenidos[fila][columna] = antes[fila][columna] && !conLectura[fila][columna];
            }
        }
        String texto = cuantos(nuevos) == 0
                ? "Ningún punto nuevo de " + codigo("conLectura") + " está a " + codigo(true) + ": " + codigo("iluminados")
                        + " no cambia"
                : "Se iluminan en " + codigo("iluminados") + " los puntos que están a " + codigo(true) + " en "
                        + codigo("conLectura") + " (" + puntos(nuevos, "y") + ")";
        if (cuantos(mantenidos) > 0) {
            boolean uno = cuantos(mantenidos) == 1;
            texto += "; " + puntos(mantenidos, "y") + (uno ? " sigue iluminado, aunque no hibrida" : " siguen iluminados, "
                    + "aunque no hibridan") + " con " + codigo(lectura.getId()) + ": un punto iluminado no se apaga";
        }
        return texto;
    }

    /** Nombres de las sondas de los puntos a true de una matriz, como código: por ejemplo, "`s1`, `s2` y `s3`". */
    private String puntos(boolean[][] matriz, String conjuncion) throws Exception {
        List<String> nombres = new ArrayList<>();
        for (int fila = 0; fila < matriz.length; fila++) {
            for (int columna = 0; columna < matriz[fila].length; columna++) {
                if (matriz[fila][columna]) {
                    nombres.add(codigo(microarray.getSonda(fila, columna).getNombre()));
                }
            }
        }
        if (nombres.size() == 1) {
            return nombres.getFirst();
        }
        return String.join(", ", nombres.subList(0, nombres.size() - 1)) + " " + conjuncion + " " + nombres.getLast();
    }

    private static int cuantos(boolean[][] matriz) {
        int total = 0;
        for (boolean[] fila : matriz) {
            for (boolean valor : fila) {
                if (valor) {
                    total++;
                }
            }
        }
        return total;
    }

    /** Marca un fragmento de código en un mensaje, para que se escriba en letra monoespaciada. */
    private static String codigo(Object texto) {
        return "`" + texto + "`";
    }

    private static String texto(Object valor) {
        return valor instanceof Character c ? "'" + c + "'" : String.valueOf(valor);
    }

    private static Casilla[] pendientes(int longitud) {
        Casilla[] casillas = new Casilla[longitud];
        Arrays.fill(casillas, Casilla.PENDIENTE);
        return casillas;
    }

    /**
     * Comprueba que el algoritmo reproducido da el mismo resultado que la solución.
     *
     * @throws AssertionError si no es así (es un Error y no una Exception para que no lo
     *                        capture el try/catch de hibridar(Lectura))
     */
    private static void comprobar(boolean coincide, String llamada) {
        if (!coincide) {
            throw new AssertionError("La animación no reproduce la solución: " + llamada
                    + " no da el mismo resultado que el método de la solución. Actualice AnimacionTraza.");
        }
    }

    // ------------------------------------------------------------------------------------
    // Pila, variables y fotogramas
    // ------------------------------------------------------------------------------------

    /**
     * Entra en una llamada.
     *
     * @param receptor variable del objeto sobre el que se invoca el método, o null
     * @return la llamada como se escribiría en Java (por ejemplo, "s1.encajaEn(l1, 2)")
     */
    private String entrar(String receptor, String metodo, Argumento... argumentos) {
        LlamadaEnCurso llamada = new LlamadaEnCurso(receptor, metodo, List.of(argumentos));
        pila.add(llamada);
        return llamada.texto;
    }

    private static Argumento argumento(String tipo, String nombre, Object valor) {
        return new Argumento(tipo, nombre, texto(valor));
    }

    private void salir() {
        pila.removeLast();
    }

    private void variable(String tipo, String nombre, Object valor) {
        pila.getLast().variables.put(nombre, valor);
        pila.getLast().tipos.put(nombre, tipo);
    }

    private void quitarVariable(String nombre) {
        pila.getLast().variables.remove(nombre);
    }

    private void devolver(Object valor) {
        pila.getLast().devuelve = texto(valor);
    }

    private void vaciarVentana() {
        inicio = -1;
        ultimoInicio = -1;
        directo = null;
        inverso = null;
        notaDirecto = null;
        notaInverso = null;
    }

    private void fotograma(int granularidad, String mensaje) {
        fotograma(granularidad, DURACION[granularidad], mensaje);
    }

    /** Crea un fotograma con el estado actual, si el nivel de detalle actual lo muestra. */
    private void fotograma(int granularidad, int centesimas, String mensaje) {
        if (granularidad > detalle.nivel) {
            return;
        }
        List<Llamada> llamadas = new ArrayList<>();
        for (int nivel = 0; nivel < pila.size(); nivel++) {
            LlamadaEnCurso llamada = pila.get(nivel);
            // Las variables se resaltan si cambian dentro de la misma llamada
            Llamada anterior = nivel < pilaAnterior.size() && pilaAnterior.get(nivel).texto().equals(llamada.texto)
                    ? pilaAnterior.get(nivel) : null;
            List<Variable> variables = new ArrayList<>();
            for (Map.Entry<String, Object> variable : llamada.variables.entrySet()) {
                boolean[][] matriz = variable.getValue() instanceof boolean[][] m ? m : null;
                String valor = matriz == null ? texto(variable.getValue()) : null;
                boolean cambiada = anterior != null && anterior.variables().stream()
                        .noneMatch(v -> v.nombre().equals(variable.getKey()) && Objects.equals(v.valor(), valor)
                                && Arrays.deepEquals(v.matriz(), matriz));
                int[] marca = matriz != null && llamada.marca != null ? llamada.marca : new int[] { -1, -1 };
                variables.add(new Variable(llamada.tipos.get(variable.getKey()), variable.getKey(), valor, matriz, marca[0],
                        marca[1], cambiada));
            }
            llamadas.add(new Llamada(llamada.receptor, llamada.metodo, llamada.argumentos, variables, llamada.devuelve));
        }
        pilaAnterior = llamadas;
        Ventana ventana = new Ventana(lectura == null ? null : lectura.getSecuencia(),
                lectura == null ? null : lectura.getId(), sonda == null ? null : sonda.getPatron(),
                sonda == null ? null : sonda.getNombre(), inicio, ultimoInicio, directo, inverso, notaDirecto, notaInverso);
        fotogramas.add(new AnimacionFotograma(llamadas, filaSonda, columnaSonda, luces, indiceLectura, ventana,
                mensaje, sonda == null ? null : detalle.aviso, centesimas));
    }
}
