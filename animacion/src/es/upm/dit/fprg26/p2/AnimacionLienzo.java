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
 * La ventana deslizante está en el centro, como en una tabla de doble entrada: las lecturas
 * de la muestra entran desde arriba (alineadas, posición a posición, con la lectura de la
 * ventana) y los patrones del microarray, desde la izquierda. A la derecha está la pila de
 * llamadas con sus variables, y debajo, una explicación del paso.
 * </p>
 * <p>
 * Las secuencias se dibujan siempre igual, con una casilla por elemento: las bases de las
 * lecturas, con letras macizas y borde continuo; los símbolos de los patrones, con letras
 * huecas y borde punteado; y los comodines, además, con un fondo rayado (R en un sentido, Y en
 * el otro y N en los dos). El borde punteado solo se usa para los patrones.
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
    private static final Color PATRON_BORDE = new Color(0x424a53);
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
    // Como en una tabla de doble entrada, la muestra está encima de la ventana deslizante y el
    // microarray, a su izquierda; la leyenda ocupa la esquina. La franja superior (hasta y = 112)
    // queda libre.
    private static final Rectangle ZONA_LEYENDA = new Rectangle(24, 124, 356, 124);
    private static final Rectangle ZONA_MUESTRA = new Rectangle(392, 124, 468, 124);
    private static final Rectangle ZONA_CHIP = new Rectangle(24, 260, 266, 390);   // el panel solo ocupa su alto
    private static final Rectangle ZONA_VENTANA = new Rectangle(302, 260, 558, 390);
    private static final Rectangle ZONA_PILA = new Rectangle(876, 124, 380, 588);
    private static final Rectangle ZONA_MENSAJE = new Rectangle(24, 662, 836, 50);

    // Casillas de las lecturas (en la muestra y en la ventana deslizante), alineadas por columnas
    private static final int PASO = 40;            // distancia entre casillas
    private static final int LADO = 36;            // lado de cada casilla
    private static final int X_CASILLAS = 446;     // x de la posición 0

    // Filas de la muestra
    private static final int Y_LECTURAS = 160;
    private static final int PASO_LECTURAS = 44;

    // Filas de la ventana deslizante
    private static final int Y_INICIOS = 310;      // valores de inicio en el bucle de encontrarEn
    private static final int Y_POSICIONES = 332;   // línea base de los números de posición
    private static final int Y_LECTURA = 340;
    private static final int Y_DIRECTO = 386;
    private static final int Y_COMPLEMENTARIAS = 450;
    private static final int Y_INVERSO = 496;
    private static final int Y_NOTAS = 574;

    // Tarjetas de las sondas en el microarray
    private static final int LADO_CHIP = 30;
    private static final int PASO_CHIP = 35;

    /** Estilo de un símbolo de un patrón: fuera de la ventana (NORMAL) o en ella (los demás). */
    private enum Estilo {
        NORMAL, PENDIENTE, COMPARANDO, ENCAJA, NO_ENCAJA;

        static Estilo de(Casilla casilla) {
            return switch (casilla) {
                case PENDIENTE -> PENDIENTE;
                case COMPARANDO -> COMPARANDO;
                case ENCAJA -> ENCAJA;
                case NO_ENCAJA -> NO_ENCAJA;
            };
        }
    }

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
            dibujarMuestra(g, fotograma);
            dibujarChip(g, fotograma);
            dibujarLeyenda(g);
            dibujarVentana(g, fotograma);
            dibujarPila(g, fotograma);
            dibujarMensaje(g, fotograma.mensaje());
        } finally {
            g.dispose();
        }
        return imagen;
    }

    // ------------------------------------------------------------------------------------
    // Muestra: las lecturas, desde arriba
    // ------------------------------------------------------------------------------------

    private void dibujarMuestra(Graphics2D g, AnimacionFotograma fotograma) throws Exception {
        panel(g, ZONA_MUESTRA, "Muestra " + muestra.getId(), muestra.getNumeroLecturas() + " lecturas");
        int paso = Math.min(PASO_LECTURAS, (ZONA_MUESTRA.y + ZONA_MUESTRA.height - Y_LECTURAS - 6)
                / muestra.getNumeroLecturas());
        int lado = Math.min(LADO, paso - 8);
        for (int k = 0; k < muestra.getNumeroLecturas(); k++) {
            Lectura lectura = muestra.getLectura(k);
            int y = Y_LECTURAS + k * paso;
            boolean actual = k == fotograma.lectura();
            if (actual) {
                RoundRectangle2D resalte = new RoundRectangle2D.Double(ZONA_MUESTRA.x + 8, y - 4,
                        X_CASILLAS + (lectura.getLongitud() - 1) * PASO + LADO + 6 - ZONA_MUESTRA.x - 8, lado + 8, 12, 12);
                g.setColor(AZUL_CLARO);
                g.fill(resalte);
                g.setColor(AZUL_BORDE);
                g.setStroke(new BasicStroke(1.5f));
                g.draw(resalte);
            }
            Font fuente = fuente(SANS, Font.BOLD, 15);
            texto(g, lectura.getId(), X_CASILLAS - 14 - ancho(g, lectura.getId(), fuente), y + lado / 2 + 5, fuente,
                    actual ? AZUL : TEXTO);
            for (int p = 0; p < lectura.getLongitud(); p++) {
                base(g, X_CASILLAS + p * PASO + (LADO - lado) / 2, y, lado, lectura.getBase(p), CASILLA, CASILLA_BORDE,
                        actual ? TEXTO : SECUNDARIO);
            }
        }
    }

    // ------------------------------------------------------------------------------------
    // Microarray: los patrones, desde la izquierda
    // ------------------------------------------------------------------------------------

    private void dibujarChip(Graphics2D g, AnimacionFotograma fotograma) throws Exception {
        int filas = microarray.getFilas();
        int columnas = microarray.getColumnas();
        int separacion = 10;
        int anchoTarjeta = (ZONA_CHIP.width - 24 - (columnas - 1) * separacion) / columnas;
        int altoTarjeta = Math.min(84, (ZONA_CHIP.height - 56 - (filas - 1) * separacion) / filas);
        // El panel solo ocupa el alto de las tarjetas, que se centran a la altura de las filas del
        // patrón de la ventana deslizante (el patrón entra desde la izquierda)
        int altoChip = filas * altoTarjeta + (filas - 1) * separacion;
        int y0 = Math.clamp((Y_DIRECTO + Y_INVERSO + LADO) / 2 - altoChip / 2, ZONA_CHIP.y + 44,
                ZONA_CHIP.y + ZONA_CHIP.height - 12 - altoChip);
        panel(g, new Rectangle(ZONA_CHIP.x, y0 - 44, ZONA_CHIP.width, altoChip + 56), "Microarray",
                filas + "x" + columnas);
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                Sonda sonda = microarray.getSonda(fila, columna);
                int x = ZONA_CHIP.x + 12 + columna * (anchoTarjeta + separacion);
                int y = y0 + fila * (altoTarjeta + separacion);
                boolean actual = fila == fotograma.filaSonda() && columna == fotograma.columnaSonda();
                boolean encendida = fotograma.luces() != null && fotograma.luces()[fila][columna];
                RoundRectangle2D tarjeta = new RoundRectangle2D.Double(x, y, anchoTarjeta, altoTarjeta, 12, 12);
                g.setColor(actual ? AZUL_CLARO : PANEL);
                g.fill(tarjeta);
                g.setColor(actual ? AZUL : BORDE);
                g.setStroke(new BasicStroke(actual ? 3f : 1.5f));
                g.draw(tarjeta);
                // Nombre y posición en el chip
                Font nombre = fuente(SANS, Font.BOLD, 14);
                texto(g, sonda.getNombre(), x + 10, y + 20, nombre, actual ? AZUL : TEXTO);
                texto(g, "[" + fila + "][" + columna + "]", x + 14 + ancho(g, sonda.getNombre(), nombre), y + 20,
                        fuente(MONO, Font.PLAIN, 11), TENUE);
                // Punto del chip (se ilumina al final del análisis)
                luz(g, x + anchoTarjeta - 20, y + 9, 12, encendida);
                // Patrón
                String patron = sonda.getPatron();
                int anchoPatron = (patron.length() - 1) * PASO_CHIP + LADO_CHIP;
                int xp = x + (anchoTarjeta - anchoPatron) / 2;
                for (int i = 0; i < patron.length(); i++) {
                    simbolo(g, xp + i * PASO_CHIP, y + altoTarjeta - LADO_CHIP - 12, LADO_CHIP, patron.charAt(i),
                            Estilo.NORMAL);
                }
            }
        }
    }

    /** Dibuja un punto del chip, encendido o apagado. */
    private static void luz(Graphics2D g, double x, double y, double diametro, boolean encendida) {
        if (encendida) {
            g.setPaint(new RadialGradientPaint((float) (x + diametro / 2), (float) (y + diametro / 2), (float) diametro,
                    new float[] { 0f, 0.5f, 1f },
                    new Color[] { new Color(0xfff59d), new Color(255, 214, 0, 150), new Color(255, 214, 0, 0) }));
            g.fill(new Ellipse2D.Double(x - diametro / 2, y - diametro / 2, diametro * 2, diametro * 2));
        }
        Ellipse2D punto = new Ellipse2D.Double(x, y, diametro, diametro);
        g.setColor(encendida ? LUZ : GRIS_CLARO);
        g.fill(punto);
        g.setColor(encendida ? LUZ_BORDE : TENUE);
        g.setStroke(new BasicStroke(1.2f));
        g.draw(punto);
    }

    // ------------------------------------------------------------------------------------
    // Leyenda
    // ------------------------------------------------------------------------------------

    private void dibujarLeyenda(Graphics2D g) {
        panel(g, ZONA_LEYENDA, "Leyenda", null);
        int lado = 26;
        int paso = 30;
        Font fuente = fuente(SANS, Font.PLAIN, 13);
        int x = ZONA_LEYENDA.x + 16;
        int y = ZONA_LEYENDA.y + 34;
        base(g, x, y, lado, 'G', CASILLA, CASILLA_BORDE, TEXTO);
        texto(g, "base de una lectura", x + lado + 8, y + lado / 2 + 5, fuente, TEXTO);
        base(g, x, y + paso, lado, 'C', AZUL_CLARO, AZUL_BORDE, AZUL);
        texto(g, "base complementaria", x + lado + 8, y + paso + lado / 2 + 5, fuente, TEXTO);
        simbolo(g, x, y + 2 * paso, lado, 'T', Estilo.NORMAL);
        texto(g, "símbolo de un patrón", x + lado + 8, y + 2 * paso + lado / 2 + 5, fuente, TEXTO);
        x = ZONA_LEYENDA.x + 192;
        String[][] comodines = { { "R", "R: purina (A o G)" }, { "Y", "Y: pirimidina (C o T)" },
            { "N", "N: cualquier base" } };
        for (int k = 0; k < comodines.length; k++) {
            simbolo(g, x, y + k * paso, lado, comodines[k][0].charAt(0), Estilo.NORMAL);
            texto(g, comodines[k][1], x + lado + 8, y + k * paso + lado / 2 + 5, fuente, TEXTO);
        }
    }

    // ------------------------------------------------------------------------------------
    // Ventana deslizante
    // ------------------------------------------------------------------------------------

    private void dibujarVentana(Graphics2D g, AnimacionFotograma fotograma) throws Exception {
        Ventana ventana = fotograma.ventana();
        String detalle = ventana.lectura() == null ? null
                : ventana.patron() == null ? "lectura " + ventana.idLectura()
                : "sonda " + ventana.nombreSonda() + " · lectura " + ventana.idLectura();
        panel(g, ZONA_VENTANA, "Ventana deslizante", detalle);
        if (fotograma.avanceRapido() != null) {
            etiquetaAvanceRapido(g, fotograma.avanceRapido());
        }
        if (ventana.lectura() == null) {
            texto(g, "(ninguna lectura en análisis)", ZONA_VENTANA.x + 16, Y_LECTURA + LADO / 2 + 5,
                    fuente(SANS, Font.ITALIC, 15), TENUE);
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
            g.setStroke(new BasicStroke(2f));   // continuo: el punteado se reserva para los patrones
            g.draw(banda);
        }

        // Etiquetas de las filas
        Font etiqueta = fuente(SANS, Font.PLAIN, 13);
        etiquetaFila(g, "posición", Y_POSICIONES - 5, fuente(SANS, Font.PLAIN, 12), SECUNDARIO);
        etiquetaFila(g, "lectura " + ventana.idLectura(), Y_LECTURA + LADO / 2, fuente(SANS, Font.BOLD, 13), AZUL);
        etiquetaFila(g, "complementarias", Y_COMPLEMENTARIAS + LADO / 2, etiqueta, hayInverso ? AZUL : TENUE);
        if (hayVentana) {
            etiquetaFila(g, "sentido directo", Y_DIRECTO + LADO / 2, etiqueta, TEXTO);
        }
        if (hayVentana && hayInverso) {
            etiquetaFila(g, "sentido inverso", Y_INVERSO + LADO / 2, etiqueta, AZUL);
        }

        // Posiciones, lectura y complementarias
        Font numero = fuente(MONO, Font.PLAIN, 12);
        for (int p = 0; p < lectura.length(); p++) {
            int x = X_CASILLAS + p * PASO;
            String indice = Integer.toString(p);
            texto(g, indice, x + (LADO - ancho(g, indice, numero)) / 2, Y_POSICIONES, numero, SECUNDARIO);
            base(g, x, Y_LECTURA, LADO, lectura.charAt(p), CASILLA, CASILLA_BORDE, TEXTO);
            char complementaria = Lectura.complementaria(lectura.charAt(p));
            if (hayInverso) {
                base(g, x, Y_COMPLEMENTARIAS, LADO, complementaria, AZUL_CLARO, AZUL_BORDE, AZUL);
            } else {
                base(g, x, Y_COMPLEMENTARIAS, LADO, complementaria, PANEL, GRIS_CLARO, TENUE);
            }
        }
        if (patron != null && ventana.ultimoInicio() >= 0) {
            rangoDeInicio(g, ventana);
        }
        if (!hayVentana) {
            if (patron != null && ventana.notaDirecto() != null) {
                nota(g, null, ventana.notaDirecto(), Y_NOTAS);
            }
            return;
        }

        // Filas del patrón en los dos sentidos
        filaPatron(g, ventana, patron, false);
        if (hayInverso) {
            filaPatron(g, ventana, patron, true);
        }

        // Resultado de cada sentido
        nota(g, "Sentido directo: ", ventana.notaDirecto(), Y_NOTAS);
        nota(g, "Sentido inverso: ", ventana.notaInverso(), Y_NOTAS + 26);
    }

    /** Dibuja el patrón en sentido directo (bajo la lectura) o inverso (bajo las complementarias). */
    private void filaPatron(Graphics2D g, Ventana ventana, String patron, boolean inversa) throws Exception {
        int n = patron.length();
        Casilla[] estados = inversa ? ventana.inverso() : ventana.directo();
        int y = inversa ? Y_INVERSO : Y_DIRECTO;
        int yArriba = inversa ? Y_COMPLEMENTARIAS : Y_LECTURA;
        Font indice = fuente(MONO, Font.PLAIN, 11);
        for (int i = 0; i < n; i++) {
            if (estados[i] == Casilla.COMPARANDO) {
                // Resalta el símbolo y la base (o la complementaria) que se comparan
                int x = X_CASILLAS + (ventana.inicio() + i) * PASO;
                char base = ventana.lectura().charAt(ventana.inicio() + i);
                RoundRectangle2D resalte = new RoundRectangle2D.Double(x - 4, yArriba - 4, LADO + 8, y + LADO - yArriba + 8,
                        12, 12);
                g.setColor(AMBAR_CLARO);
                g.fill(resalte);
                g.setColor(AMBAR_BORDE);
                g.setStroke(new BasicStroke(2.5f));
                g.draw(resalte);
                base(g, x, yArriba, LADO, inversa ? Lectura.complementaria(base) : base, PANEL, AMBAR_BORDE, AMBAR);
            }
        }
        for (int i = 0; i < n; i++) {
            int x = X_CASILLAS + (ventana.inicio() + i) * PASO;
            int posicion = inversa ? n - 1 - i : i;
            Casilla estado = estados[i];
            simbolo(g, x, y, LADO, patron.charAt(posicion), Estilo.de(estado));
            if (estado == Casilla.ENCAJA || estado == Casilla.NO_ENCAJA) {
                insignia(g, x + LADO - 3, y + 1, estado == Casilla.ENCAJA);
            }
            // Índice del símbolo en el patrón
            String texto = Integer.toString(posicion);
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
        Font fuente = fuente(SANS, Font.PLAIN, 12);
        etiquetaFila(g, "inicio: de 0 a " + ventana.ultimoInicio(), Y_INICIOS, fuente, AZUL);
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

    private void nota(Graphics2D g, String titulo, String nota, int y) {
        if (nota == null) {
            return;
        }
        Color color = nota.endsWith(": encaja") ? VERDE : ROJO;
        int x = ZONA_VENTANA.x + 16;
        if (titulo != null) {
            Font negrita = fuente(SANS, Font.BOLD, 14);
            texto(g, titulo, x, y, negrita, color);
            x += ancho(g, titulo, negrita);
        }
        texto(g, nota, x, y, fuente(SANS, Font.PLAIN, 14), color);
    }

    private void etiquetaFila(Graphics2D g, String texto, int centro, Font fuente, Color color) {
        texto(g, texto, X_CASILLAS - 14 - ancho(g, texto, fuente), centro + 5, fuente, color);
    }

    private void etiquetaAvanceRapido(Graphics2D g, String aviso) {
        Font fuente = fuente(SANS, Font.BOLD, 13);
        int ancho = ancho(g, aviso, fuente) + 44;
        int x = ZONA_VENTANA.x + ZONA_VENTANA.width - 16 - ancho;
        int y = ZONA_VENTANA.y + ZONA_VENTANA.height - 38;
        RoundRectangle2D etiqueta = new RoundRectangle2D.Double(x, y, ancho, 26, 26, 26);
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
            triangulo.moveTo(tx, y + 7);
            triangulo.lineTo(tx + 9, y + 13);
            triangulo.lineTo(tx, y + 19);
            triangulo.closePath();
            g.fill(triangulo);
        }
        texto(g, aviso, x + 34, y + 18, fuente, AMBAR);
    }

    // ------------------------------------------------------------------------------------
    // Pila de llamadas
    // ------------------------------------------------------------------------------------

    private void dibujarPila(Graphics2D g, AnimacionFotograma fotograma) {
        panel(g, ZONA_PILA, "Pila de llamadas", "y variables locales");
        List<Llamada> pila = fotograma.pila();
        if (pila.isEmpty()) {
            texto(g, "(ninguna llamada en curso)", ZONA_PILA.x + 16, ZONA_PILA.y + 70, fuente(SANS, Font.ITALIC, 15), TENUE);
            return;
        }
        Font metodo = fuente(MONO, Font.PLAIN, 14);
        Font metodoActual = fuente(MONO, Font.BOLD, 14);
        int altoMetodo = 28;
        int xMax = ZONA_PILA.x + ZONA_PILA.width - 14;
        int y = ZONA_PILA.y + 46;
        int centroAnterior = -1;
        for (int nivel = 0; nivel < pila.size(); nivel++) {
            Llamada llamada = pila.get(nivel);
            boolean actual = nivel == pila.size() - 1;
            int x = ZONA_PILA.x + 18 + nivel * 14;
            int altoVariables = variables(g, llamada.variables(), x + 4, y + altoMetodo, xMax, false);
            if (actual) {
                g.setColor(AZUL_CLARO);
                g.fill(new RoundRectangle2D.Double(ZONA_PILA.x + 8, y + 1, ZONA_PILA.width - 16,
                        altoMetodo + altoVariables - 2, 8, 8));
            }
            int centro = y + altoMetodo / 2;
            if (nivel > 0) {
                // Conector con la llamada que la ha invocado
                g.setColor(TENUE);
                g.setStroke(new BasicStroke(1.5f));
                g.draw(new Line2D.Double(x - 10, centroAnterior + 8, x - 10, centro));
                g.draw(new Line2D.Double(x - 10, centro, x - 3, centro));
            }
            Font fuenteMetodo = actual ? metodoActual : metodo;
            texto(g, llamada.metodo(), x, centro + 5, fuenteMetodo, actual ? TEXTO : SECUNDARIO);
            if (llamada.devuelve() != null) {
                Color color = switch (llamada.devuelve()) {
                    case "true" -> VERDE;
                    case "false", "-1" -> ROJO;
                    default -> AZUL;
                };
                int xf = x + ancho(g, llamada.metodo(), fuenteMetodo) + 10;
                flecha(g, xf, centro, 16, color);
                texto(g, llamada.devuelve(), xf + 22, centro + 5, metodoActual, color);
            }
            variables(g, llamada.variables(), x + 4, y + altoMetodo, xMax, true);
            centroAnterior = centro;
            y += altoMetodo + altoVariables;
        }
    }

    /**
     * Dibuja (o solo mide) las variables de una llamada, en una o más líneas.
     *
     * @return alto que ocupan
     */
    private int variables(Graphics2D g, List<Variable> variables, int x0, int y0, int xMax, boolean dibujar) {
        if (variables.isEmpty()) {
            return 0;
        }
        Font nombre = fuente(MONO, Font.PLAIN, 13);
        Font valor = fuente(MONO, Font.BOLD, 13);
        int altoLinea = 26;
        int x = x0;
        int y = y0;
        for (Variable v : variables) {
            String etiqueta = v.matriz() != null ? v.nombre() + " " : v.nombre() + " = ";
            int anchoValor = v.matriz() != null ? anchoMiniMatriz(v.matriz(), altoLinea - 6) : ancho(g, v.valor(), valor);
            int anchoVariable = ancho(g, etiqueta, nombre) + anchoValor;
            if (x > x0 && x + anchoVariable > xMax) {
                x = x0;
                y += altoLinea;
            }
            if (dibujar) {
                int centro = y + altoLinea / 2 - 2;
                if (v.cambiada()) {
                    // Como en el depurador de Eclipse: las variables que cambian se resaltan
                    RoundRectangle2D resalte = new RoundRectangle2D.Double(x - 4, y, anchoVariable + 8, altoLinea - 4, 8, 8);
                    g.setColor(AMBAR_CLARO);
                    g.fill(resalte);
                    g.setColor(AMBAR_BORDE);
                    g.setStroke(new BasicStroke(1f));
                    g.draw(resalte);
                }
                texto(g, etiqueta, x, centro + 5, nombre, SECUNDARIO);
                int xv = x + ancho(g, etiqueta, nombre);
                if (v.matriz() != null) {
                    miniMatriz(g, v, xv, centro, altoLinea - 6);
                } else {
                    texto(g, v.valor(), xv, centro + 5, valor, TEXTO);
                }
            }
            x += anchoVariable + 16;
        }
        return y + altoLinea - y0 + 2;
    }

    private static final int HUECO_MATRIZ = 2;

    /** Lado de cada punto de una matriz dibujada en la pila. */
    private static int ladoMiniMatriz(boolean[][] matriz, int altoMaximo) {
        int filas = matriz.length;
        return Math.max(4, Math.min(10, (altoMaximo - (filas - 1) * HUECO_MATRIZ) / filas));
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
                    g.setStroke(new BasicStroke(1.8f));
                    g.draw(new Ellipse2D.Double(px - 2, py - 2, lado + 4, lado + 4));
                }
            }
        }
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
        Font fuente = fuente(SANS, Font.PLAIN, 17);
        List<String> lineas = partir(g, mensaje, fuente, ZONA_MENSAJE.width - 44);
        if (lineas.size() > 1) {
            fuente = fuente(SANS, Font.PLAIN, 15);
            lineas = partir(g, mensaje, fuente, ZONA_MENSAJE.width - 44);
        }
        int altoLinea = lineas.size() > 1 ? 19 : 0;
        int y = ZONA_MENSAJE.y + ZONA_MENSAJE.height / 2 + 6 - altoLinea * (lineas.size() - 1) / 2;
        for (String linea : lineas) {
            texto(g, linea, ZONA_MENSAJE.x + 22, y, fuente, TEXTO);
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
    // Bases y símbolos
    // ------------------------------------------------------------------------------------

    /** Dibuja una base de una lectura: letra maciza sobre una casilla de borde fino. */
    private static void base(Graphics2D g, double x, double y, double lado, char letra, Color relleno, Color borde,
            Color colorTexto) {
        RoundRectangle2D forma = new RoundRectangle2D.Double(x, y, lado, lado, lado / 4.5, lado / 4.5);
        g.setColor(relleno);
        g.fill(forma);
        g.setColor(borde);
        g.setStroke(new BasicStroke(1.4f));
        g.draw(forma);
        Font fuente = fuente(MONO, Font.BOLD, (int) Math.round(lado * 0.53));
        letraCentrada(g, String.valueOf(letra), x + lado / 2, y + lado / 2, fuente, colorTexto, null);
    }

    /**
     * Dibuja un símbolo de un patrón: letra hueca en una casilla de borde punteado (el patrón no
     * es una secuencia de bases, sino la plantilla con que se comparan). Los comodines tienen,
     * además, un fondo rayado: R (purina: A o G), con rayas en un sentido; Y (pirimidina: C o T),
     * en el otro; y N (cualquier base, purina o pirimidina), en los dos.
     */
    private static void simbolo(Graphics2D g, double x, double y, double lado, char simbolo, Estilo estilo) {
        Color relleno;
        Color borde;
        Color colorTexto;
        switch (estilo) {
            case PENDIENTE -> {
                relleno = PANEL;
                borde = TENUE;
                colorTexto = TENUE;
            }
            case COMPARANDO -> {
                relleno = AMBAR_CLARO;
                borde = AMBAR_BORDE;
                colorTexto = AMBAR;
            }
            case ENCAJA -> {
                relleno = VERDE_CLARO;
                borde = VERDE_BORDE;
                colorTexto = VERDE;
            }
            case NO_ENCAJA -> {
                relleno = ROJO_CLARO;
                borde = ROJO_BORDE;
                colorTexto = ROJO;
            }
            default -> {
                relleno = PANEL;
                borde = PATRON_BORDE;
                colorTexto = TEXTO;
            }
        }
        RoundRectangle2D forma = new RoundRectangle2D.Double(x, y, lado, lado, lado / 4.5, lado / 4.5);
        g.setColor(relleno);
        g.fill(forma);
        boolean purina = simbolo == 'R' || simbolo == 'N';
        boolean pirimidina = simbolo == 'Y' || simbolo == 'N';
        if (purina || pirimidina) {
            Shape recorte = g.getClip();
            g.clip(forma);
            int alfa = switch (estilo) {
                case NORMAL -> 80;
                case PENDIENTE -> 110;
                default -> 120;
            };
            g.setColor(new Color(borde.getRed(), borde.getGreen(), borde.getBlue(), alfa));
            g.setStroke(new BasicStroke(1.3f));
            double separacion = lado / 5.5;
            for (double d = -lado; d <= lado; d += separacion) {
                if (purina) {
                    g.draw(new Line2D.Double(x + d, y + lado, x + d + lado, y));
                }
                if (pirimidina) {
                    g.draw(new Line2D.Double(x + d, y, x + d + lado, y + lado));
                }
            }
            g.setClip(recorte);
        }
        float punto = lado >= 30 ? 2.4f : 2f;
        g.setColor(borde);
        g.setStroke(new BasicStroke(punto, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
                new float[] { 0.01f, punto * 1.9f }, 0f));
        g.draw(forma);
        Font fuente = fuente(MONO, Font.BOLD, (int) Math.round(lado * 0.66));
        letraCentrada(g, String.valueOf(simbolo), x + lado / 2, y + lado / 2 + 0.5, fuente, colorTexto, relleno);
    }

    /**
     * Dibuja una letra centrada en un punto: maciza o, si se indica el color de su interior,
     * hueca (con el interior y un pequeño margen alrededor de ese color, para que se lea sobre
     * un fondo rayado).
     */
    private static void letraCentrada(Graphics2D g, String letra, double cx, double cy, Font fuente, Color color,
            Color interior) {
        GlyphVector glifos = fuente.createGlyphVector(g.getFontRenderContext(), letra);
        Rectangle2D caja = glifos.getVisualBounds();
        Shape contorno = glifos.getOutline((float) (cx - caja.getCenterX()), (float) (cy - caja.getCenterY()));
        if (interior == null) {
            g.setColor(color);
            g.fill(contorno);
            return;
        }
        g.setColor(interior);
        g.setStroke(new BasicStroke(4.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(contorno);
        g.fill(contorno);
        g.setColor(color);
        g.setStroke(new BasicStroke(fuente.getSize() >= 18 ? 1.5f : 1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(contorno);
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
        Font fuenteTitulo = fuente(SANS, Font.BOLD, 15);
        texto(g, titulo, zona.x + 16, zona.y + 26, fuenteTitulo, TEXTO);
        if (detalle != null) {
            texto(g, detalle, zona.x + 16 + ancho(g, titulo, fuenteTitulo) + 10, zona.y + 26, fuente(SANS, Font.PLAIN, 14),
                    SECUNDARIO);
        }
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
    private static void insignia(Graphics2D g, double cx, double cy, boolean encaja) {
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
