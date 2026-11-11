package es.upm.dit.fprg26.p2;

import module java.base;
import module java.desktop;

import java.util.List;

import es.upm.dit.fprg26.p2.AnimacionFotograma.Casilla;
import es.upm.dit.fprg26.p2.AnimacionFotograma.Llamada;
import es.upm.dit.fprg26.p2.AnimacionFotograma.Variable;
import es.upm.dit.fprg26.p2.AnimacionFotograma.Ventana;

/**
 * Dibuja los fotogramas de la animación.
 * <p>
 * Cada fotograma tiene cinco zonas: el microarray (con la sonda que se comprueba), la
 * muestra (con la lectura que se analiza), la pila de llamadas con sus variables, la ventana
 * deslizante (el patrón colocado sobre la lectura y sus complementarias) y una explicación del
 * paso. Los colores son los de las figuras del enunciado y de la ventana de la práctica.
 * </p>
 * <p>
 * Todas las coordenadas se refieren a un lienzo de {@link #ANCHO}x{@link #ALTO} (16:9); la
 * imagen se puede generar a otra escala (por ejemplo, 1,5 para 1920x1080).
 * </p>
 */
final class AnimacionLienzo {

    /** Ancho del lienzo, antes de escalar. */
    static final int ANCHO = 1280;

    /** Alto del lienzo, antes de escalar. */
    static final int ALTO = 720;

    // Colores (los de las figuras del enunciado y de PanelMicroarray)
    private static final Color FONDO = new Color(0xf6f8fa);
    private static final Color PANEL = Color.WHITE;
    private static final Color BORDE = new Color(0xd0d7de);
    private static final Color TEXTO = new Color(0x1f2328);
    private static final Color SECUNDARIO = new Color(0x57606a);
    private static final Color TENUE = new Color(0xafb8c1);
    private static final Color GRIS_CLARO = new Color(0xeaeef2);
    private static final Color CASILLA = new Color(0xf6f8fa);
    private static final Color CASILLA_BORDE = new Color(0x8c959f);
    private static final Color AZUL = new Color(0x0969da);
    private static final Color AZUL_CLARO = new Color(0xddf4ff);
    private static final Color AZUL_BORDE = new Color(0x54aeff);
    private static final Color VERDE = new Color(0x116329);
    private static final Color VERDE_BORDE = new Color(0x1a7f37);
    private static final Color VERDE_CLARO = new Color(0xdafbe1);
    private static final Color ROJO = new Color(0xa40e26);
    private static final Color ROJO_BORDE = new Color(0xcf222e);
    private static final Color ROJO_CLARO = new Color(0xffebe9);
    private static final Color AMBAR = new Color(0x7d4e00);
    private static final Color AMBAR_BORDE = new Color(0xd4a72c);
    private static final Color AMBAR_CLARO = new Color(0xfff8c5);
    private static final Color LUZ = new Color(0xffd600);
    private static final Color LUZ_BORDE = new Color(0xb08f00);

    // Fuentes: la primera familia instalada de cada lista
    private static final String SANS = familia(Font.SANS_SERIF, "Inter", "Segoe UI", "Helvetica Neue", "Arial", "DejaVu Sans");
    private static final String MONO = familia(Font.MONOSPACED, "JetBrains Mono", "Cascadia Mono", "Consolas", "Menlo",
            "DejaVu Sans Mono");

    // Zonas del lienzo: x, y, ancho, alto
    private static final Rectangle ZONA_CHIP = new Rectangle(24, 16, 500, 228);
    private static final Rectangle ZONA_MUESTRA = new Rectangle(24, 256, 500, 104);
    private static final Rectangle ZONA_PILA = new Rectangle(540, 16, 716, 344);
    private static final Rectangle ZONA_VENTANA = new Rectangle(24, 372, 1232, 280);
    private static final Rectangle ZONA_MENSAJE = new Rectangle(24, 662, 1232, 50);

    // Ventana deslizante
    private static final int PASO = 40;            // distancia entre casillas
    private static final int LADO = 36;            // lado de cada casilla
    private static final int X_CASILLAS = 236;     // x de la posición 0
    private static final int Y_INICIOS = 418;      // límites del bucle de encontrarEn
    private static final int Y_POSICIONES = 440;   // línea base de los números de posición
    private static final int Y_LECTURA = 448;
    private static final int Y_DIRECTO = 490;
    private static final int Y_COMPLEMENTARIAS = 548;
    private static final int Y_INVERSO = 590;

    /** Microarray y muestra que se analizan (no cambian de un fotograma a otro). */
    private final Microarray microarray;
    private final Muestra muestra;

    /** Escala de la imagen respecto al lienzo. */
    private final double escala;

    /**
     * Crea un lienzo.
     *
     * @param microarray microarray de la animación
     * @param muestra muestra de la animación
     * @param escala escala de las imágenes (1 para 1280x720)
     */
    AnimacionLienzo(Microarray microarray, Muestra muestra, double escala) {
        this.microarray = microarray;
        this.muestra = muestra;
        this.escala = escala;
    }

    /**
     * Dibuja un fotograma.
     *
     * @param fotograma fotograma que se dibuja
     * @return imagen del fotograma
     * @throws Exception si no se puede acceder a alguna sonda o lectura
     */
    BufferedImage dibujar(AnimacionFotograma fotograma) throws Exception {
        int ancho = (int) Math.round(ANCHO * escala);
        int alto = (int) Math.round(ALTO * escala);
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.scale(escala, escala);
            g.setColor(FONDO);
            g.fillRect(0, 0, ANCHO, ALTO);
            dibujarChip(g, fotograma);
            dibujarMuestra(g, fotograma);
            dibujarPila(g, fotograma);
            dibujarVentana(g, fotograma);
            dibujarMensaje(g, fotograma.mensaje());
        } finally {
            g.dispose();
        }
        return imagen;
    }

    // ------------------------------------------------------------------------------------
    // Microarray
    // ------------------------------------------------------------------------------------

    private void dibujarChip(Graphics2D g, AnimacionFotograma fotograma) throws Exception {
        panel(g, ZONA_CHIP, "Microarray", microarray.getNombre() + " · " + microarray.getFilas() + "x"
                + microarray.getColumnas());
        int filas = microarray.getFilas();
        int columnas = microarray.getColumnas();
        int separacion = 12;
        int x0 = ZONA_CHIP.x + 16;
        int y0 = ZONA_CHIP.y + 50;
        int anchoTarjeta = Math.min(228, (ZONA_CHIP.width - 32 - (columnas - 1) * separacion) / columnas);
        int altoTarjeta = Math.min(80, (ZONA_CHIP.height - 66 - (filas - 1) * separacion) / filas);
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                Sonda sonda = microarray.getSonda(fila, columna);
                int x = x0 + columna * (anchoTarjeta + separacion);
                int y = y0 + fila * (altoTarjeta + separacion);
                boolean actual = fila == fotograma.filaSonda() && columna == fotograma.columnaSonda();
                boolean encendida = fotograma.luces() != null && fotograma.luces()[fila][columna];
                RoundRectangle2D tarjeta = new RoundRectangle2D.Double(x, y, anchoTarjeta, altoTarjeta, 12, 12);
                g.setColor(actual ? AZUL_CLARO : PANEL);
                g.fill(tarjeta);
                g.setColor(actual ? AZUL : BORDE);
                g.setStroke(new BasicStroke(actual ? 3f : 1.5f));
                g.draw(tarjeta);
                // Punto del chip
                int diametro = 26;
                int cx = x + 14;
                int cy = y + (altoTarjeta - diametro) / 2;
                if (encendida) {
                    g.setPaint(new RadialGradientPaint(cx + diametro / 2f, cy + diametro / 2f, diametro,
                            new float[] { 0f, 0.5f, 1f },
                            new Color[] { new Color(0xfff59d), new Color(255, 214, 0, 150), new Color(255, 214, 0, 0) }));
                    g.fill(new Ellipse2D.Double(cx - diametro / 2.0, cy - diametro / 2.0, diametro * 2.0, diametro * 2.0));
                }
                g.setColor(encendida ? LUZ : GRIS_CLARO);
                g.fill(new Ellipse2D.Double(cx, cy, diametro, diametro));
                g.setColor(encendida ? LUZ_BORDE : TENUE);
                g.setStroke(new BasicStroke(1.5f));
                g.draw(new Ellipse2D.Double(cx, cy, diametro, diametro));
                // Nombre, posición y patrón
                int xt = cx + diametro + 14;
                texto(g, sonda.getNombre(), xt, y + altoTarjeta / 2 - 6, fuente(SANS, Font.BOLD, 17), TEXTO);
                texto(g, sonda.getPatron(), xt, y + altoTarjeta / 2 + 20, fuente(MONO, Font.BOLD, 19),
                        actual ? AZUL : SECUNDARIO);
                Font pequena = fuente(MONO, Font.PLAIN, 12);
                String indice = "[" + fila + "][" + columna + "]";
                texto(g, indice, x + anchoTarjeta - 10 - ancho(g, indice, pequena), y + 18, pequena, TENUE);
            }
        }
    }

    // ------------------------------------------------------------------------------------
    // Muestra
    // ------------------------------------------------------------------------------------

    private void dibujarMuestra(Graphics2D g, AnimacionFotograma fotograma) throws Exception {
        panel(g, ZONA_MUESTRA, "Muestra", muestra.getId() + " · " + muestra.getNumeroLecturas() + " lecturas");
        int n = muestra.getNumeroLecturas();
        int altoFila = Math.min(28, (ZONA_MUESTRA.height - 50) / n);
        int y = ZONA_MUESTRA.y + 44;
        for (int k = 0; k < n; k++) {
            Lectura lectura = muestra.getLectura(k);
            boolean actual = k == fotograma.lectura();
            if (actual) {
                g.setColor(AZUL_CLARO);
                g.fill(new RoundRectangle2D.Double(ZONA_MUESTRA.x + 10, y, ZONA_MUESTRA.width - 20, altoFila - 2, 8, 8));
                g.setColor(AZUL);
                g.fill(new RoundRectangle2D.Double(ZONA_MUESTRA.x + 10, y, 4, altoFila - 2, 4, 4));
            }
            int base = y + altoFila / 2 + 6;
            texto(g, lectura.getId(), ZONA_MUESTRA.x + 24, base, fuente(SANS, Font.BOLD, 16), actual ? AZUL : TEXTO);
            texto(g, espaciada(lectura.getSecuencia()), ZONA_MUESTRA.x + 72, base, fuente(MONO, Font.BOLD, 17),
                    actual ? TEXTO : SECUNDARIO);
            y += altoFila;
        }
    }

    // ------------------------------------------------------------------------------------
    // Pila de llamadas
    // ------------------------------------------------------------------------------------

    private void dibujarPila(Graphics2D g, AnimacionFotograma fotograma) {
        panel(g, ZONA_PILA, "Pila de llamadas", "con sus variables locales");
        List<Llamada> pila = fotograma.pila();
        if (pila.isEmpty()) {
            texto(g, "(ninguna llamada en curso)", ZONA_PILA.x + 20, ZONA_PILA.y + 76, fuente(SANS, Font.ITALIC, 16),
                    TENUE);
            return;
        }
        int altoLinea = Math.min(42, (ZONA_PILA.height - 52) / pila.size());
        Font metodo = fuente(MONO, Font.PLAIN, 16);
        Font metodoActual = fuente(MONO, Font.BOLD, 16);
        Font variable = fuente(MONO, Font.PLAIN, 14);
        int y = ZONA_PILA.y + 46;
        for (int nivel = 0; nivel < pila.size(); nivel++) {
            Llamada llamada = pila.get(nivel);
            boolean actual = nivel == pila.size() - 1;
            int centro = y + altoLinea / 2;
            if (actual) {
                g.setColor(AZUL_CLARO);
                g.fill(new RoundRectangle2D.Double(ZONA_PILA.x + 10, y + 2, ZONA_PILA.width - 20, altoLinea - 4, 8, 8));
            }
            int x = ZONA_PILA.x + 20 + nivel * 18;
            if (nivel > 0) {
                // Conector con la llamada que la ha invocado
                g.setColor(TENUE);
                g.setStroke(new BasicStroke(1.5f));
                g.draw(new Line2D.Double(x - 12, centro - altoLinea + 12, x - 12, centro));
                g.draw(new Line2D.Double(x - 12, centro, x - 3, centro));
            }
            Font fuenteMetodo = actual ? metodoActual : metodo;
            texto(g, llamada.metodo(), x, centro + 6, fuenteMetodo, actual ? TEXTO : SECUNDARIO);
            int xFin = x + ancho(g, llamada.metodo(), fuenteMetodo);
            if (llamada.devuelve() != null) {
                Color color = switch (llamada.devuelve()) {
                    case "true" -> VERDE;
                    case "false", "-1" -> ROJO;
                    default -> AZUL;
                };
                flecha(g, xFin + 12, centro, 18, color);
                texto(g, llamada.devuelve(), xFin + 38, centro + 6, metodoActual, color);
                xFin += 38 + ancho(g, llamada.devuelve(), metodoActual);
            }
            int xv = Math.max(ZONA_PILA.x + 270, xFin + 28);
            Font valor = fuente(MONO, Font.BOLD, 14);
            for (Variable v : llamada.variables()) {
                int anchoValor = v.matriz() != null ? anchoMiniMatriz(v.matriz(), altoLinea - 10)
                        : ancho(g, v.valor(), valor);
                String nombre = v.matriz() != null ? v.nombre() + " " : v.nombre() + " = ";
                int anchoVariable = ancho(g, nombre, variable) + anchoValor;
                if (v.cambiada()) {
                    // Como en el depurador de Eclipse: las variables que cambian se resaltan
                    g.setColor(AMBAR_CLARO);
                    g.fill(new RoundRectangle2D.Double(xv - 5, y + 5, anchoVariable + 10, altoLinea - 10, 8, 8));
                    g.setColor(AMBAR_BORDE);
                    g.setStroke(new BasicStroke(1f));
                    g.draw(new RoundRectangle2D.Double(xv - 5, y + 5, anchoVariable + 10, altoLinea - 10, 8, 8));
                }
                texto(g, nombre, xv, centro + 5, variable, SECUNDARIO);
                xv += ancho(g, nombre, variable);
                if (v.matriz() != null) {
                    miniMatriz(g, v, xv, centro, altoLinea - 10);
                } else {
                    texto(g, v.valor(), xv, centro + 5, valor, TEXTO);
                }
                xv += anchoValor + 20;
            }
            y += altoLinea;
        }
    }

    private static final int HUECO_MATRIZ = 3;

    /** Lado de cada punto de una matriz dibujada en una línea de la pila. */
    private static int ladoMiniMatriz(boolean[][] matriz, int altoMaximo) {
        int filas = matriz.length;
        return Math.max(5, Math.min(15, (altoMaximo - (filas - 1) * HUECO_MATRIZ) / filas));
    }

    private static int anchoMiniMatriz(boolean[][] matriz, int altoMaximo) {
        int lado = ladoMiniMatriz(matriz, altoMaximo);
        return matriz[0].length * (lado + HUECO_MATRIZ) - HUECO_MATRIZ;
    }

    /** Dibuja una matriz de boolean como una cuadrícula de puntos (true, encendido). */
    private static void miniMatriz(Graphics2D g, Variable variable, int x, int centro, int altoMaximo) {
        boolean[][] matriz = variable.matriz();
        int lado = ladoMiniMatriz(matriz, altoMaximo);
        int alto = matriz.length * (lado + HUECO_MATRIZ) - HUECO_MATRIZ;
        int y0 = centro - alto / 2;
        for (int fila = 0; fila < matriz.length; fila++) {
            for (int columna = 0; columna < matriz[0].length; columna++) {
                double px = x + columna * (lado + HUECO_MATRIZ);
                double py = y0 + fila * (lado + HUECO_MATRIZ);
                Ellipse2D punto = new Ellipse2D.Double(px, py, lado, lado);
                g.setColor(matriz[fila][columna] ? LUZ : GRIS_CLARO);
                g.fill(punto);
                g.setColor(matriz[fila][columna] ? LUZ_BORDE : TENUE);
                g.setStroke(new BasicStroke(1f));
                g.draw(punto);
                if (fila == variable.filaMarcada() && columna == variable.columnaMarcada()) {
                    // Elemento de la sonda que se comprueba
                    g.setColor(AZUL);
                    g.setStroke(new BasicStroke(2f));
                    g.draw(new Ellipse2D.Double(px - 2.5, py - 2.5, lado + 5, lado + 5));
                }
            }
        }
    }

    // ------------------------------------------------------------------------------------
    // Ventana deslizante
    // ------------------------------------------------------------------------------------

    private void dibujarVentana(Graphics2D g, AnimacionFotograma fotograma) throws Exception {
        Ventana ventana = fotograma.ventana();
        String detalle = ventana.lectura() == null ? null
                : ventana.patron() == null ? "lectura " + ventana.idLectura()
                : "patrón " + ventana.patron() + " de la sonda " + ventana.nombreSonda() + " sobre la lectura "
                        + ventana.idLectura();
        panel(g, ZONA_VENTANA, "Ventana deslizante", detalle);
        if (fotograma.avanceRapido() != null) {
            etiquetaAvanceRapido(g, fotograma.avanceRapido());
        }
        if (ventana.lectura() == null) {
            texto(g, "(ninguna lectura en análisis)", ZONA_VENTANA.x + 20, ZONA_VENTANA.y + 80,
                    fuente(SANS, Font.ITALIC, 16), TENUE);
            return;
        }
        String lectura = ventana.lectura();
        String patron = ventana.patron();
        int n = patron == null ? 0 : patron.length();
        boolean hayInverso = ventana.inverso() != null;
        boolean hayVentana = patron != null && ventana.inicio() >= 0 && ventana.directo() != null;

        // Banda de la ventana, detrás de las filas
        if (hayVentana) {
            int x = X_CASILLAS + ventana.inicio() * PASO - 6;
            int yFin = (hayInverso ? Y_INVERSO : Y_DIRECTO) + LADO + 18;   // incluye los índices del patrón
            RoundRectangle2D banda = new RoundRectangle2D.Double(x, Y_LECTURA - 6, (n - 1) * PASO + LADO + 12,
                    yFin - Y_LECTURA + 6, 12, 12);
            g.setColor(new Color(221, 244, 255, 150));
            g.fill(banda);
            g.setColor(AZUL_BORDE);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 6f, 5f }, 0f));
            g.draw(banda);
        }

        // Etiquetas de las filas
        Font etiqueta = fuente(SANS, Font.PLAIN, 15);
        Font etiquetaNegrita = fuente(SANS, Font.BOLD, 15);
        etiquetaFila(g, "posición", Y_POSICIONES - 5, fuente(SANS, Font.PLAIN, 13), SECUNDARIO);
        etiquetaFila(g, "lectura " + ventana.idLectura(), Y_LECTURA + LADO / 2, etiquetaNegrita, TEXTO);
        etiquetaFila(g, "complementarias", Y_COMPLEMENTARIAS + LADO / 2, etiqueta, hayInverso ? AZUL : TENUE);
        if (hayVentana) {
            etiquetaFila(g, "sentido directo", Y_DIRECTO + LADO / 2, etiqueta, TEXTO);
        }
        if (hayVentana && hayInverso) {
            etiquetaFila(g, "sentido inverso", Y_INVERSO + LADO / 2, etiqueta, AZUL);
        }

        // Posiciones, lectura y complementarias
        Font numero = fuente(MONO, Font.PLAIN, 13);
        for (int p = 0; p < lectura.length(); p++) {
            int x = X_CASILLAS + p * PASO;
            String indice = Integer.toString(p);
            texto(g, indice, x + (LADO - ancho(g, indice, numero)) / 2, Y_POSICIONES, numero, SECUNDARIO);
            casilla(g, x, Y_LECTURA, lectura.charAt(p), CASILLA, CASILLA_BORDE, TEXTO);
            char complementaria = Lectura.complementaria(lectura.charAt(p));
            if (hayInverso) {
                casilla(g, x, Y_COMPLEMENTARIAS, complementaria, AZUL_CLARO, AZUL_BORDE, AZUL);
            } else {
                casilla(g, x, Y_COMPLEMENTARIAS, complementaria, PANEL, GRIS_CLARO, TENUE);
            }
        }
        if (patron != null && ventana.ultimoInicio() >= 0) {
            rangoDeInicio(g, ventana);
        }
        if (!hayVentana) {
            if (patron != null && ventana.notaDirecto() != null) {
                nota(g, ventana.notaDirecto(), X_CASILLAS, Y_DIRECTO + LADO / 2);
            }
            return;
        }

        // Filas del patrón en los dos sentidos
        filaPatron(g, ventana, patron, false);
        if (hayInverso) {
            filaPatron(g, ventana, patron, true);
        }

        // Notas con el resultado de cada sentido
        int xNotas = X_CASILLAS + lectura.length() * PASO + 28;
        nota(g, ventana.notaDirecto(), xNotas, Y_DIRECTO + LADO / 2);
        nota(g, ventana.notaInverso(), xNotas, Y_INVERSO + LADO / 2);
    }

    /** Dibuja el patrón en sentido directo (bajo la lectura) o inverso (bajo las complementarias). */
    private void filaPatron(Graphics2D g, Ventana ventana, String patron, boolean inversa) throws Exception {
        int n = patron.length();
        Casilla[] estados = inversa ? ventana.inverso() : ventana.directo();
        int y = inversa ? Y_INVERSO : Y_DIRECTO;
        int yArriba = inversa ? Y_COMPLEMENTARIAS : Y_LECTURA;
        Font indice = fuente(MONO, Font.PLAIN, 11);
        for (int i = 0; i < n; i++) {
            int x = X_CASILLAS + (ventana.inicio() + i) * PASO;
            int simbolo = inversa ? n - 1 - i : i;
            Casilla estado = estados[i];
            if (estado == Casilla.COMPARANDO) {
                // Resalta el símbolo y la base (o la complementaria) que se comparan
                RoundRectangle2D resalte = new RoundRectangle2D.Double(x - 4, yArriba - 4, LADO + 8, y + LADO - yArriba + 8,
                        12, 12);
                g.setColor(AMBAR_CLARO);
                g.fill(resalte);
                g.setColor(AMBAR_BORDE);
                g.setStroke(new BasicStroke(2.5f));
                g.draw(resalte);
                char arriba = inversa ? Lectura.complementaria(ventana.lectura().charAt(ventana.inicio() + i))
                        : ventana.lectura().charAt(ventana.inicio() + i);
                casilla(g, x, yArriba, arriba, PANEL, AMBAR_BORDE, AMBAR);
            }
            switch (estado) {
                case PENDIENTE -> casilla(g, x, y, patron.charAt(simbolo), PANEL, BORDE, TENUE);
                case COMPARANDO -> casilla(g, x, y, patron.charAt(simbolo), AMBAR_CLARO, AMBAR_BORDE, AMBAR);
                case ENCAJA -> {
                    casilla(g, x, y, patron.charAt(simbolo), VERDE_CLARO, VERDE_BORDE, VERDE);
                    insignia(g, x + LADO - 4, y + 2, true);
                }
                case NO_ENCAJA -> {
                    casilla(g, x, y, patron.charAt(simbolo), ROJO_CLARO, ROJO_BORDE, ROJO);
                    insignia(g, x + LADO - 4, y + 2, false);
                }
            }
            // Índice del símbolo en el patrón
            String texto = Integer.toString(simbolo);
            texto(g, texto, x + (LADO - ancho(g, texto, indice)) / 2, y + LADO + 13, indice, TENUE);
        }
    }

    /**
     * Dibuja sobre las posiciones los valores que toma inicio en el bucle de encontrarEn (de 0
     * a la última posición en la que cabe el patrón), con una marca en el actual.
     */
    private void rangoDeInicio(Graphics2D g, Ventana ventana) {
        double x0 = X_CASILLAS + LADO / 2.0;
        double x1 = X_CASILLAS + ventana.ultimoInicio() * PASO + LADO / 2.0;
        g.setColor(AZUL_BORDE);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(x0, Y_INICIOS, x1, Y_INICIOS));
        for (int p = 0; p <= ventana.ultimoInicio(); p++) {
            double x = X_CASILLAS + p * PASO + LADO / 2.0;
            g.draw(new Line2D.Double(x, Y_INICIOS - 3, x, Y_INICIOS + 3));
        }
        Font fuente = fuente(SANS, Font.PLAIN, 13);
        String texto = "valores de inicio: de 0 a " + ventana.ultimoInicio();
        texto(g, texto, x1 + 14, Y_INICIOS + 5, fuente, AZUL);
        etiquetaFila(g, "inicio", Y_INICIOS, fuente, AZUL);
        if (ventana.inicio() >= 0) {
            double x = X_CASILLAS + ventana.inicio() * PASO + LADO / 2.0;
            Path2D marca = new Path2D.Double();
            marca.moveTo(x - 6, Y_INICIOS - 9);
            marca.lineTo(x + 6, Y_INICIOS - 9);
            marca.lineTo(x, Y_INICIOS + 1);
            marca.closePath();
            g.setColor(AZUL);
            g.fill(marca);
        }
    }

    private void nota(Graphics2D g, String nota, int x, int centro) {
        if (nota == null) {
            return;
        }
        boolean encaja = nota.endsWith(": encaja");
        Font fuente = fuente(SANS, Font.PLAIN, 16);
        texto(g, nota, x, centro + 6, fuente, encaja ? VERDE : ROJO);
    }

    private void etiquetaFila(Graphics2D g, String texto, int centro, Font fuente, Color color) {
        texto(g, texto, X_CASILLAS - 16 - ancho(g, texto, fuente), centro + 5, fuente, color);
    }

    private void etiquetaAvanceRapido(Graphics2D g, String aviso) {
        Font fuente = fuente(SANS, Font.BOLD, 14);
        int ancho = ancho(g, aviso, fuente) + 46;
        int x = ZONA_VENTANA.x + ZONA_VENTANA.width - 16 - ancho;
        int y = ZONA_VENTANA.y + 12;
        RoundRectangle2D etiqueta = new RoundRectangle2D.Double(x, y, ancho, 28, 28, 28);
        g.setColor(AMBAR_CLARO);
        g.fill(etiqueta);
        g.setColor(AMBAR_BORDE);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(etiqueta);
        // Dos triángulos (símbolo de avance rápido)
        g.setColor(AMBAR);
        for (int t = 0; t < 2; t++) {
            Path2D triangulo = new Path2D.Double();
            double tx = x + 12 + t * 9;
            triangulo.moveTo(tx, y + 8);
            triangulo.lineTo(tx + 9, y + 14);
            triangulo.lineTo(tx, y + 20);
            triangulo.closePath();
            g.fill(triangulo);
        }
        texto(g, aviso, x + 36, y + 19, fuente, AMBAR);
    }

    // ------------------------------------------------------------------------------------
    // Mensaje
    // ------------------------------------------------------------------------------------

    private void dibujarMensaje(Graphics2D g, String mensaje) {
        RoundRectangle2D fondo = new RoundRectangle2D.Double(ZONA_MENSAJE.x, ZONA_MENSAJE.y, ZONA_MENSAJE.width,
                ZONA_MENSAJE.height, 12, 12);
        g.setColor(PANEL);
        g.fill(fondo);
        g.setColor(BORDE);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(fondo);
        g.setColor(AZUL);
        g.fill(new RoundRectangle2D.Double(ZONA_MENSAJE.x, ZONA_MENSAJE.y, 6, ZONA_MENSAJE.height, 6, 6));
        if (mensaje == null) {
            return;
        }
        Font fuente = fuente(SANS, Font.PLAIN, 18);
        List<String> lineas = partir(g, mensaje, fuente, ZONA_MENSAJE.width - 48);
        if (lineas.size() > 1) {
            fuente = fuente(SANS, Font.PLAIN, 16);
            lineas = partir(g, mensaje, fuente, ZONA_MENSAJE.width - 48);
        }
        int altoLinea = lineas.size() > 1 ? 20 : 0;
        int y = ZONA_MENSAJE.y + ZONA_MENSAJE.height / 2 + 6 - altoLinea * (lineas.size() - 1) / 2;
        for (String linea : lineas) {
            texto(g, linea, ZONA_MENSAJE.x + 24, y, fuente, TEXTO);
            y += altoLinea;
        }
    }

    /** Parte un texto en líneas que no superen un ancho. */
    private static List<String> partir(Graphics2D g, String texto, Font fuente, int anchoMaximo) {
        List<String> lineas = new ArrayList<>();
        StringBuilder linea = new StringBuilder();
        for (String palabra : texto.split(" ")) {
            String prueba = linea.isEmpty() ? palabra : linea + " " + palabra;
            if (!linea.isEmpty() && ancho(g, prueba, fuente) > anchoMaximo) {
                lineas.add(linea.toString());
                linea = new StringBuilder(palabra);
            } else {
                linea = new StringBuilder(prueba);
            }
        }
        lineas.add(linea.toString());
        return lineas;
    }

    // ------------------------------------------------------------------------------------
    // Elementos comunes
    // ------------------------------------------------------------------------------------

    /** Dibuja el fondo y el título de una zona. */
    private static void panel(Graphics2D g, Rectangle zona, String titulo, String detalle) {
        RoundRectangle2D fondo = new RoundRectangle2D.Double(zona.x, zona.y, zona.width, zona.height, 14, 14);
        g.setColor(PANEL);
        g.fill(fondo);
        g.setColor(BORDE);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(fondo);
        Font fuenteTitulo = fuente(SANS, Font.BOLD, 16);
        texto(g, titulo, zona.x + 16, zona.y + 30, fuenteTitulo, TEXTO);
        if (detalle != null) {
            texto(g, detalle, zona.x + 16 + ancho(g, titulo, fuenteTitulo) + 10, zona.y + 30, fuente(SANS, Font.PLAIN, 15),
                    SECUNDARIO);
        }
    }

    /** Dibuja una casilla con una letra, como en las figuras del enunciado. */
    private static void casilla(Graphics2D g, int x, int y, char letra, Color relleno, Color borde, Color colorTexto) {
        RoundRectangle2D forma = new RoundRectangle2D.Double(x, y, LADO, LADO, 8, 8);
        g.setColor(relleno);
        g.fill(forma);
        g.setColor(borde);
        g.setStroke(new BasicStroke(1.6f));
        g.draw(forma);
        Font fuente = fuente(MONO, Font.BOLD, 20);
        String texto = String.valueOf(letra);
        FontMetrics medidas = g.getFontMetrics(fuente);
        texto(g, texto, x + (LADO - medidas.stringWidth(texto)) / 2, y + (LADO + medidas.getAscent()
                - medidas.getDescent()) / 2, fuente, colorTexto);
    }

    /** Dibuja una flecha horizontal hacia la derecha (sin depender de las fuentes). */
    private static void flecha(Graphics2D g, double x, double y, double largo, Color color) {
        g.setColor(color);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(x, y, x + largo, y));
        Path2D punta = new Path2D.Double();
        punta.moveTo(x + largo - 5, y - 4.5);
        punta.lineTo(x + largo, y);
        punta.lineTo(x + largo - 5, y + 4.5);
        g.draw(punta);
    }

    /** Dibuja una insignia redonda con una marca (✔) o una cruz (✖), sin depender de las fuentes. */
    private static void insignia(Graphics2D g, int cx, int cy, boolean encaja) {
        double radio = 8;
        Ellipse2D circulo = new Ellipse2D.Double(cx - radio, cy - radio, radio * 2, radio * 2);
        g.setColor(encaja ? VERDE_BORDE : ROJO_BORDE);
        g.fill(circulo);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.5f));
        g.draw(circulo);
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D marca = new Path2D.Double();
        if (encaja) {
            marca.moveTo(cx - 4, cy + 0.5);
            marca.lineTo(cx - 1, cy + 3.5);
            marca.lineTo(cx + 4.5, cy - 3);
        } else {
            marca.moveTo(cx - 3.5, cy - 3.5);
            marca.lineTo(cx + 3.5, cy + 3.5);
            marca.moveTo(cx + 3.5, cy - 3.5);
            marca.lineTo(cx - 3.5, cy + 3.5);
        }
        g.draw(marca);
    }

    private static void texto(Graphics2D g, String texto, double x, double y, Font fuente, Color color) {
        g.setFont(fuente);
        g.setColor(color);
        g.drawString(texto, (float) x, (float) y);
    }

    private static int ancho(Graphics2D g, String texto, Font fuente) {
        return (int) Math.ceil(fuente.getStringBounds(texto, g.getFontRenderContext()).getWidth());
    }

    /** Separa las bases de una secuencia con espacios finos, para que se lean mejor. */
    private static String espaciada(String secuencia) {
        return String.join(" ", secuencia.split(""));
    }

    private static final Map<String, Font> FUENTES = new HashMap<>();

    private static Font fuente(String familia, int estilo, int tamano) {
        return FUENTES.computeIfAbsent(familia + estilo + "/" + tamano, _ -> new Font(familia, estilo, tamano));
    }

    /** Devuelve la primera familia de la lista que esté instalada, o la predeterminada. */
    private static String familia(String predeterminada, String... preferidas) {
        Set<String> instaladas = Set.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String familia : preferidas) {
            if (instaladas.contains(familia)) {
                return familia;
            }
        }
        return predeterminada;
    }
}
