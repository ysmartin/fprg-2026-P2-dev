package es.upm.dit.fprg26.p2.auxiliar;

import module java.desktop;
import java.awt.event.MouseEvent;

import es.upm.dit.fprg26.p2.Microarray;
import es.upm.dit.fprg26.p2.Sonda;

/**
 * Panel que dibuja la cuadrícula de un microarray, con sus puntos iluminados o apagados.
 * <p>
 * Los chips pequeños (de hasta 6 filas y 6 columnas) se dibujan con el nombre y el patrón
 * de cada sonda. Los grandes se dibujan solo con sus puntos, como la imagen de un escáner:
 * el nombre, el patrón y la posición de cada sonda aparecen al dejar el ratón sobre su
 * punto.
 * </p>
 * <p>
 * Forma parte de la interfaz gráfica auxiliar: <strong>no es necesario entender su
 * código, ni mucho menos modificarlo</strong>.
 * </p>
 */
public final class PanelMicroarray extends JPanel {

    private static final long serialVersionUID = 1L;

    /** Número máximo de filas y de columnas de un chip que se dibuja con todo detalle. */
    private static final int MAXIMO_DETALLADO = 6;

    private static final int ANCHO_CELDA = 196;
    private static final int ALTO_CELDA = 116;
    private static final int LADO_COMPACTO = 26;
    private static final int MARGEN = 14;
    private static final int ROTULOS = 22;
    private static final Color FONDO = Color.WHITE;
    private static final Color BORDE = new Color(0xc9ced6);
    private static final Color TEXTO = new Color(0x1f2328);
    private static final Color TEXTO_SECUNDARIO = new Color(0x57606a);
    private static final Color PUNTO_APAGADO = new Color(0xd0d7de);
    private static final Color PUNTO_ENCENDIDO = new Color(0xffd600);
    private static final Color BORDE_ENCENDIDO = new Color(0xb08f00);
    private static final Color CRISTAL = new Color(0x22272e);
    private static final Color PUNTO_APAGADO_EN_CRISTAL = new Color(0x444c56);
    private static final Color SELECCION = new Color(0x58a6ff);

    private transient Microarray microarray;
    private transient boolean[][] iluminados;
    private transient Point celdaBajoRaton;

    /**
     * Crea un panel vacío.
     */
    public PanelMicroarray() {
        setBackground(FONDO);
        setToolTipText("");
        MouseAdapter raton = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent evento) {
                marcar(celdaEn(evento.getX(), evento.getY()));
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                marcar(null);
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
    }

    /**
     * Cambia el microarray que se dibuja y sus puntos iluminados.
     *
     * @param microarray microarray que se dibuja (o {@code null} para no dibujar ninguno)
     * @param iluminados puntos iluminados (o {@code null} si aún no se ha analizado nada)
     */
    public void mostrar(Microarray microarray, boolean[][] iluminados) {
        this.microarray = microarray;
        this.iluminados = iluminados;
        this.celdaBajoRaton = null;
        revalidate();
        repaint();
    }

    /**
     * Indica si el chip que se muestra es grande y se dibuja solo con sus puntos.
     *
     * @return {@code true} si el chip tiene más de 6 filas o más de 6 columnas
     */
    public boolean esCompacto() {
        return microarray != null
                && (microarray.getFilas() > MAXIMO_DETALLADO || microarray.getColumnas() > MAXIMO_DETALLADO);
    }

    @Override
    public Dimension getPreferredSize() {
        if (microarray == null) {
            return new Dimension(ANCHO_CELDA * 3 + 2 * MARGEN, ALTO_CELDA + 2 * MARGEN);
        }
        return new Dimension(origen() + microarray.getColumnas() * anchoCelda() + MARGEN,
                origen() + microarray.getFilas() * altoCelda() + MARGEN);
    }

    @Override
    public String getToolTipText(MouseEvent evento) {
        Point celda = celdaEn(evento.getX(), evento.getY());
        if (celda == null) {
            return null;
        }
        int fila = celda.y;
        int columna = celda.x;
        Sonda sonda = sondaEn(fila, columna);
        if (sonda == null) {
            return null;
        }
        String estado;
        if (iluminados == null) {
            estado = "sin analizar";
        } else if (encendido(fila, columna)) {
            estado = "<b>iluminado</b>";
        } else {
            estado = "apagado";
        }
        return "<html><b>" + html(sonda.getNombre()) + "</b><br><tt>" + html(sonda.getPatron())
                + "</tt><br>fila " + fila + ", columna " + columna + " · " + estado + "</html>";
    }

    @Override
    protected void paintComponent(Graphics graficos) {
        super.paintComponent(graficos);
        if (microarray == null) {
            return;
        }
        Graphics2D g = (Graphics2D) graficos.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (esCompacto()) {
            dibujarCristalYRotulos(g);
        }
        for (int fila = 0; fila < microarray.getFilas(); fila++) {
            for (int columna = 0; columna < microarray.getColumnas(); columna++) {
                int x = origen() + columna * anchoCelda();
                int y = origen() + fila * altoCelda();
                if (esCompacto()) {
                    dibujarPunto(g, x, y, fila, columna);
                } else {
                    dibujarCelda(g, x, y, fila, columna);
                }
            }
        }
        if (celdaBajoRaton != null) {
            g.setColor(SELECCION);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(origen() + celdaBajoRaton.x * anchoCelda() + 1,
                    origen() + celdaBajoRaton.y * altoCelda() + 1, anchoCelda() - 2, altoCelda() - 2, 10, 10);
        }
        g.dispose();
    }

    private void dibujarCristalYRotulos(Graphics2D g) {
        int ancho = microarray.getColumnas() * LADO_COMPACTO;
        int alto = microarray.getFilas() * LADO_COMPACTO;
        g.setColor(CRISTAL);
        g.fillRoundRect(origen() - 4, origen() - 4, ancho + 8, alto + 8, 12, 12);
        g.setColor(TEXTO_SECUNDARIO);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        for (int columna = 0; columna < microarray.getColumnas(); columna++) {
            escribirCentrado(g, String.valueOf(columna), origen() + columna * LADO_COMPACTO + LADO_COMPACTO / 2,
                    origen() - 8);
        }
        for (int fila = 0; fila < microarray.getFilas(); fila++) {
            escribirCentrado(g, String.valueOf(fila), MARGEN + ROTULOS / 2 - 4,
                    origen() + fila * LADO_COMPACTO + LADO_COMPACTO / 2 + 4);
        }
    }

    private void dibujarPunto(Graphics2D g, int x, int y, int fila, int columna) {
        if (sondaEn(fila, columna) == null) {
            return;
        }
        int cx = x + LADO_COMPACTO / 2;
        int cy = y + LADO_COMPACTO / 2;
        if (encendido(fila, columna)) {
            float[] fracciones = { 0f, 0.5f, 1f };
            Color[] colores = { new Color(0xfff59d), new Color(255, 214, 0, 200), new Color(255, 214, 0, 0) };
            g.setPaint(new RadialGradientPaint(cx, cy, 13f, fracciones, colores));
            g.fillOval(cx - 13, cy - 13, 26, 26);
            g.setColor(PUNTO_ENCENDIDO);
        } else {
            g.setColor(PUNTO_APAGADO_EN_CRISTAL);
        }
        g.fillOval(cx - 7, cy - 7, 14, 14);
    }

    private void dibujarCelda(Graphics2D g, int x, int y, int fila, int columna) {
        Sonda sonda = sondaEn(fila, columna);
        if (sonda == null) {
            return;
        }
        boolean encendido = encendido(fila, columna);

        g.setColor(FONDO);
        g.fillRoundRect(x + 4, y + 4, ANCHO_CELDA - 8, ALTO_CELDA - 8, 14, 14);
        g.setColor(encendido ? BORDE_ENCENDIDO : BORDE);
        g.setStroke(new BasicStroke(encendido ? 2f : 1.2f));
        g.drawRoundRect(x + 4, y + 4, ANCHO_CELDA - 8, ALTO_CELDA - 8, 14, 14);

        int cx = x + ANCHO_CELDA / 2;
        int cy = y + 32;
        if (encendido) {
            float[] fracciones = { 0f, 0.45f, 1f };
            Color[] colores = { new Color(0xfff59d), new Color(255, 214, 0, 190), new Color(255, 214, 0, 0) };
            g.setPaint(new RadialGradientPaint(cx, cy, 30f, fracciones, colores));
            g.fillOval(cx - 30, cy - 30, 60, 60);
        }
        g.setColor(encendido ? PUNTO_ENCENDIDO : PUNTO_APAGADO);
        g.fillOval(cx - 11, cy - 11, 22, 22);
        g.setColor(encendido ? BORDE_ENCENDIDO : new Color(0x8c959f));
        g.setStroke(new BasicStroke(2f));
        g.drawOval(cx - 11, cy - 11, 22, 22);

        g.setColor(TEXTO);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        escribirCentrado(g, recortar(g, sonda.getNombre(), ANCHO_CELDA - 20), cx, y + 72);
        g.setColor(TEXTO_SECUNDARIO);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        escribirCentrado(g, recortar(g, sonda.getPatron(), ANCHO_CELDA - 20), cx, y + 93);
    }

    private int anchoCelda() {
        return esCompacto() ? LADO_COMPACTO : ANCHO_CELDA;
    }

    private int altoCelda() {
        return esCompacto() ? LADO_COMPACTO : ALTO_CELDA;
    }

    private int origen() {
        return esCompacto() ? MARGEN + ROTULOS : MARGEN;
    }

    private Point celdaEn(int x, int y) {
        if (microarray == null || x < origen() || y < origen()) {
            return null;
        }
        int columna = (x - origen()) / anchoCelda();
        int fila = (y - origen()) / altoCelda();
        if (fila >= microarray.getFilas() || columna >= microarray.getColumnas()) {
            return null;
        }
        return new Point(columna, fila);
    }

    private void marcar(Point celda) {
        boolean igual = celda == null ? celdaBajoRaton == null : celda.equals(celdaBajoRaton);
        if (!igual) {
            celdaBajoRaton = celda;
            repaint();
        }
    }

    private Sonda sondaEn(int fila, int columna) {
        try {
            return microarray.getSonda(fila, columna);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean encendido(int fila, int columna) {
        return iluminados != null && fila < iluminados.length && iluminados[fila] != null
                && columna < iluminados[fila].length && iluminados[fila][columna];
    }

    private static String html(String texto) {
        if (texto == null) {
            return "(null)";
        }
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static void escribirCentrado(Graphics2D g, String texto, int cx, int y) {
        FontMetrics medidas = g.getFontMetrics();
        g.drawString(texto, cx - medidas.stringWidth(texto) / 2, y);
    }

    private static String recortar(Graphics2D g, String texto, int anchoMaximo) {
        if (texto == null) {
            return "(null)";
        }
        FontMetrics medidas = g.getFontMetrics();
        if (medidas.stringWidth(texto) <= anchoMaximo) {
            return texto;
        }
        String recortado = texto;
        while (!recortado.isEmpty() && medidas.stringWidth(recortado + "…") > anchoMaximo) {
            recortado = recortado.substring(0, recortado.length() - 1);
        }
        return recortado + "…";
    }
}
