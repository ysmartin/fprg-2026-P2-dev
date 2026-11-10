package es.upm.dit.fprg26.p2.auxiliar;

import module java.desktop;

import es.upm.dit.fprg26.p2.Microarray;
import es.upm.dit.fprg26.p2.Sonda;

/**
 * Panel que dibuja la cuadrícula de un microarray, con sus puntos iluminados o apagados.
 * <p>
 * Forma parte de la interfaz gráfica auxiliar: <strong>no es necesario entender su
 * código, ni mucho menos modificarlo</strong>.
 * </p>
 */
public final class PanelMicroarray extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int ANCHO_CELDA = 196;
    private static final int ALTO_CELDA = 116;
    private static final int MARGEN = 14;
    private static final Color FONDO = Color.WHITE;
    private static final Color BORDE = new Color(0xc9ced6);
    private static final Color TEXTO = new Color(0x1f2328);
    private static final Color TEXTO_SECUNDARIO = new Color(0x57606a);
    private static final Color PUNTO_APAGADO = new Color(0xd0d7de);
    private static final Color PUNTO_ENCENDIDO = new Color(0xffd600);
    private static final Color BORDE_ENCENDIDO = new Color(0xb08f00);

    private transient Microarray microarray;
    private transient boolean[][] iluminados;

    /**
     * Crea un panel vacío.
     */
    public PanelMicroarray() {
        setBackground(FONDO);
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
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (microarray == null) {
            return new Dimension(ANCHO_CELDA * 3 + 2 * MARGEN, ALTO_CELDA + 2 * MARGEN);
        }
        return new Dimension(microarray.getColumnas() * ANCHO_CELDA + 2 * MARGEN,
                microarray.getFilas() * ALTO_CELDA + 2 * MARGEN);
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
        for (int fila = 0; fila < microarray.getFilas(); fila++) {
            for (int columna = 0; columna < microarray.getColumnas(); columna++) {
                int x = MARGEN + columna * ANCHO_CELDA;
                int y = MARGEN + fila * ALTO_CELDA;
                dibujarCelda(g, x, y, fila, columna);
            }
        }
        g.dispose();
    }

    private void dibujarCelda(Graphics2D g, int x, int y, int fila, int columna) {
        Sonda sonda;
        try {
            sonda = microarray.getSonda(fila, columna);
        } catch (Exception e) {
            return;
        }
        boolean encendido = iluminados != null && fila < iluminados.length
                && iluminados[fila] != null && columna < iluminados[fila].length && iluminados[fila][columna];

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
