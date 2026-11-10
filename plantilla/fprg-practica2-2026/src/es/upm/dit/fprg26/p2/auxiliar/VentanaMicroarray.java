package es.upm.dit.fprg26.p2.auxiliar;

import module java.base;
import module java.desktop;

import es.upm.dit.fprg26.p2.Analisis;
import es.upm.dit.fprg26.p2.Lectura;
import es.upm.dit.fprg26.p2.Microarray;
import es.upm.dit.fprg26.p2.Muestra;
import es.upm.dit.fprg26.p2.Sonda;

/**
 * Ventana para analizar muestras con microarrays y ver qué puntos del chip se iluminan.
 * <p>
 * Permite elegir un microarray y una muestra (o escribir lecturas a mano). Al pulsar
 * «Analizar», crea un {@link Analisis} con las clases de la práctica (con la fecha actual),
 * dibuja el chip con sus puntos iluminados y muestra, para cada lectura, en qué posición
 * encaja cada sonda. En los chips grandes solo se dibujan los puntos: los datos de cada
 * sonda aparecen al dejar el ratón sobre su punto.
 * </p>
 * <p>
 * Forma parte de la interfaz gráfica auxiliar: <strong>no es necesario entender su
 * código, ni mucho menos modificarlo</strong>.
 * </p>
 */
public class VentanaMicroarray {

    private static final String A_MANO = "Lecturas escritas a mano…";

    private final Microarray[] microarrays;
    private final Muestra[] muestras;

    private JComboBox<String> selectorMicroarray;
    private JComboBox<String> selectorMuestra;
    private JTextField campoLecturas;
    private PanelMicroarray panel;
    private JSplitPane division;
    private JTextArea detalle;

    /**
     * Prepara la ventana con los microarrays y las muestras que se podrán elegir.
     *
     * @param microarrays microarrays disponibles (al menos uno)
     * @param muestras muestras disponibles (puede estar vacío: siempre se pueden escribir lecturas a mano)
     */
    public VentanaMicroarray(Microarray[] microarrays, Muestra[] muestras) {
        this.microarrays = microarrays;
        this.muestras = muestras;
    }

    /**
     * Muestra la ventana.
     */
    public void mostrar() {
        SwingUtilities.invokeLater(this::crearVentana);
    }

    private void crearVentana() {
        JFrame ventana = new JFrame("FPRG 2026-27 · Práctica 2 · Microarray virtual");
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ToolTipManager.sharedInstance().setInitialDelay(150);
        ToolTipManager.sharedInstance().setDismissDelay(30_000);

        String[] nombresMicroarrays = new String[microarrays.length];
        for (int i = 0; i < microarrays.length; i++) {
            nombresMicroarrays[i] = String.valueOf(microarrays[i]);
        }
        String[] nombresMuestras = new String[muestras.length + 1];
        for (int i = 0; i < muestras.length; i++) {
            nombresMuestras[i] = String.valueOf(muestras[i]);
        }
        nombresMuestras[muestras.length] = A_MANO;

        selectorMicroarray = new JComboBox<>(nombresMicroarrays);
        selectorMuestra = new JComboBox<>(nombresMuestras);
        campoLecturas = new JTextField(32);
        campoLecturas.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        campoLecturas.setToolTipText("Una o varias lecturas separadas por espacios (A, C, G, T y N)");
        JButton botonAnalizar = new JButton("Analizar");

        selectorMicroarray.addActionListener(_ -> mostrarChipSinAnalizar());
        selectorMuestra.addActionListener(_ -> actualizarCampoLecturas());
        botonAnalizar.addActionListener(_ -> analizar());
        campoLecturas.addActionListener(_ -> analizar());

        JPanel controles = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        colocar(controles, c, 0, 0, new JLabel("Microarray:"));
        colocar(controles, c, 1, 0, selectorMicroarray);
        colocar(controles, c, 2, 0, new JLabel("Fecha del análisis:"));
        colocar(controles, c, 3, 0, new JLabel(LocalDate.now() + " (hoy)"));
        colocar(controles, c, 0, 1, new JLabel("Muestra:"));
        colocar(controles, c, 1, 1, selectorMuestra);
        colocar(controles, c, 2, 1, new JLabel("Lecturas:"));
        colocar(controles, c, 3, 1, campoLecturas);
        colocar(controles, c, 4, 1, botonAnalizar);

        panel = new PanelMicroarray();
        detalle = new JTextArea(10, 80);
        detalle.setEditable(false);
        detalle.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JScrollPane desplazamientoChip = new JScrollPane(panel);
        desplazamientoChip.setPreferredSize(tamanoParaTodosLosChips());
        division = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                desplazamientoChip, new JScrollPane(detalle));
        division.setResizeWeight(0.55);

        ventana.getContentPane().add(controles, BorderLayout.NORTH);
        ventana.getContentPane().add(division, BorderLayout.CENTER);
        actualizarCampoLecturas();
        mostrarChipSinAnalizar();
        ventana.pack();
        Rectangle pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        ventana.setSize(Math.min(ventana.getWidth(), pantalla.width), Math.min(ventana.getHeight(), pantalla.height));
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
        SwingUtilities.invokeLater(this::ajustarDivision);
    }

    /** Tamaño que necesita el panel para mostrar entero el mayor de los chips (más las barras de desplazamiento). */
    private Dimension tamanoParaTodosLosChips() {
        int ancho = 0;
        int alto = 0;
        for (Microarray microarray : microarrays) {
            panel.mostrar(microarray, null);
            ancho = Math.max(ancho, panel.getPreferredSize().width);
            alto = Math.max(alto, panel.getPreferredSize().height);
        }
        return new Dimension(ancho + 4, alto + 4);
    }

    private static void colocar(JPanel contenedor, GridBagConstraints c, int x, int y, JComponent componente) {
        c.gridx = x;
        c.gridy = y;
        contenedor.add(componente, c);
    }

    private void actualizarCampoLecturas() {
        campoLecturas.setEnabled(selectorMuestra.getSelectedIndex() == muestras.length);
    }

    private void mostrarChipSinAnalizar() {
        panel.mostrar(microarrays[selectorMicroarray.getSelectedIndex()], null);
        detalle.setText(avisoChipGrande() + "Elija una muestra y pulse «Analizar».");
        ajustarDivision();
    }

    private void analizar() {
        Microarray microarray = microarrays[selectorMicroarray.getSelectedIndex()];
        panel.mostrar(microarray, null);
        try {
            Muestra muestra = muestraElegida();
            Analisis analisis = new Analisis(muestra, microarray);
            boolean[][] iluminados = analisis.getIluminados();
            String aviso = comprobarDimensiones(microarray, iluminados);
            panel.mostrar(microarray, aviso == null ? iluminados : null);
            detalle.setText(avisoChipGrande() + describir(analisis, muestra, microarray, aviso));
            detalle.setCaretPosition(0);
        } catch (RuntimeException e) {
            detalle.setText("Su código ha producido un error inesperado:\n" + e
                    + "\nen " + (e.getStackTrace().length > 0 ? e.getStackTrace()[0] : "(desconocido)"));
        } catch (Exception e) {
            detalle.setText("No se ha podido hacer el análisis:\n" + e.getMessage());
        }
    }

    private Muestra muestraElegida() throws Exception {
        int indice = selectorMuestra.getSelectedIndex();
        if (indice < muestras.length) {
            return muestras[indice];
        }
        String[] textos = campoLecturas.getText().trim().toUpperCase().split("[\\s,;]+");
        if (textos.length == 0 || textos[0].isEmpty()) {
            throw new Exception("Escriba al menos una lectura en el campo «Lecturas».");
        }
        Lectura[] lecturas = new Lectura[textos.length];
        for (int i = 0; i < textos.length; i++) {
            lecturas[i] = new Lectura("A mano " + (i + 1), textos[i]);
        }
        return new Muestra("Lecturas escritas a mano", LocalDate.now(), lecturas);
    }

    /** Deja para el chip el espacio que necesita (si cabe) y el resto para el texto. */
    private void ajustarDivision() {
        int disponible = division.getHeight() - division.getDividerSize();
        if (disponible > 0) {
            division.setDividerLocation(Math.max(0, Math.min(panel.getPreferredSize().height + 4, disponible - 160)));
        }
    }

    private String avisoChipGrande() {
        if (!panel.esCompacto()) {
            return "";
        }
        return "(Este chip es grande: deje el ratón sobre un punto para ver su sonda.)\n\n";
    }

    private static String comprobarDimensiones(Microarray microarray, boolean[][] iluminados) {
        if (iluminados == null) {
            return "getIluminados() devuelve null: ¿está completo el constructor de Analisis?";
        }
        if (iluminados.length != microarray.getFilas()) {
            return "La matriz de puntos iluminados tiene " + iluminados.length + " filas, pero el chip tiene "
                    + microarray.getFilas();
        }
        for (boolean[] fila : iluminados) {
            if (fila == null || fila.length != microarray.getColumnas()) {
                return "Alguna fila de la matriz de puntos iluminados no tiene " + microarray.getColumnas()
                        + " columnas, como el chip";
            }
        }
        return null;
    }

    private static String describir(Analisis analisis, Muestra muestra, Microarray microarray, String aviso) {
        StringBuilder texto = new StringBuilder();
        if (aviso != null) {
            texto.append("AVISO: ").append(aviso).append("\n\n");
        }
        texto.append("Muestra: ").append(muestra).append('\n');
        texto.append("Microarray: ").append(microarray).append('\n');
        texto.append("Fecha del análisis: ").append(analisis.getFechaAnalisis())
                .append(" (días desde la captura: ").append(analisis.getDiasDesdeCaptura()).append(")\n");
        texto.append("Puntos iluminados: ").append(analisis.contarIluminados())
                .append(" de ").append(microarray.getFilas() * microarray.getColumnas()).append("\n\n");
        texto.append("Dónde encaja cada sonda en cada lectura (según buscarEn):\n");
        for (Lectura lectura : muestra.getLecturas()) {
            int indeterminadas = lectura.contarIndeterminadas();
            texto.append("\n@").append(lectura.getId()).append("   (").append(indeterminadas)
                    .append(indeterminadas == 1 ? " base indeterminada)\n" : " bases indeterminadas)\n");
            texto.append(lectura.getSecuencia()).append('\n');
            boolean alguna = false;
            for (int fila = 0; fila < microarray.getFilas(); fila++) {
                for (int columna = 0; columna < microarray.getColumnas(); columna++) {
                    Sonda sonda = sondaEn(microarray, fila, columna);
                    int posicion = sonda.buscarEn(lectura);
                    if (posicion >= 0) {
                        alguna = true;
                        texto.append(" ".repeat(posicion)).append("^".repeat(sonda.getLongitud()))
                                .append(" ").append(sonda.getNombre()).append(" (posición ")
                                .append(posicion).append(")\n");
                    }
                }
            }
            if (!alguna) {
                texto.append("(no encaja ninguna sonda)\n");
            }
        }
        return texto.toString();
    }

    private static Sonda sondaEn(Microarray microarray, int fila, int columna) {
        try {
            return microarray.getSonda(fila, columna);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
