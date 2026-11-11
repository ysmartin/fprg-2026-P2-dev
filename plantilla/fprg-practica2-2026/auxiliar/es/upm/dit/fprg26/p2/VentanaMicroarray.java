package es.upm.dit.fprg26.p2;

import module java.base;
import module java.desktop;

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
class VentanaMicroarray {

    private static final String A_MANO = "Lecturas escritas a mano…";

    private final Microarray[] microarrays;
    private final Muestra[] muestras;

    private JComboBox<String> selectorMicroarray;
    private JComboBox<String> selectorMuestra;
    private JTextField campoLecturas;
    private JLabel etiquetaFecha;
    private PanelMicroarray panel;
    private JSplitPane division;
    private JTextArea detalle;

    /**
     * Prepara la ventana con los microarrays y las muestras que se podrán elegir.
     *
     * @param microarrays microarrays disponibles (al menos uno)
     * @param muestras muestras disponibles (puede estar vacío: siempre se pueden escribir lecturas a mano)
     */
    VentanaMicroarray(Microarray[] microarrays, Muestra[] muestras) {
        this.microarrays = microarrays;
        this.muestras = muestras;
    }

    /**
     * Muestra la ventana.
     */
    void mostrar() {
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
        selectorMuestra.addActionListener(_ -> {
            actualizarCampoLecturas();
            mostrarChipSinAnalizar();
        });
        botonAnalizar.addActionListener(_ -> analizar());
        campoLecturas.addActionListener(_ -> analizar());

        JPanel controles = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        colocar(controles, c, 0, 0, new JLabel("Microarray:"));
        colocar(controles, c, 1, 0, selectorMicroarray);
        colocar(controles, c, 2, 0, new JLabel("Fecha del análisis:"));
        etiquetaFecha = new JLabel();
        colocar(controles, c, 3, 0, etiquetaFecha);
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
        actualizarFecha();
        panel.mostrar(microarrays[selectorMicroarray.getSelectedIndex()], null);
        detalle.setText(avisoChipGrande() + "Elija una muestra y pulse «Analizar».");
        ajustarDivision();
    }

    private void analizar() {
        actualizarFecha();
        Microarray microarray = microarrays[selectorMicroarray.getSelectedIndex()];
        panel.mostrar(microarray, null);
        try {
            Muestra muestra = muestraElegida();
            Analisis analisis = new Analisis(muestra, microarray);
            panel.mostrar(microarray, analisis.getIluminados());
            detalle.setText(avisoChipGrande() + describir(analisis, muestra, microarray));
            detalle.setCaretPosition(0);
        } catch (RuntimeException e) {
            detalle.setText("Su código ha producido un error inesperado:\n" + e
                    + "\nen " + (e.getStackTrace().length > 0 ? e.getStackTrace()[0] : "(desconocido)"));
        } catch (Exception e) {
            detalle.setText("No se ha podido hacer el análisis:\n" + e.getMessage());
        } catch (Error e) {
            detalle.setText("Su proyecto todavía tiene errores de compilación (o se ha producido un error grave):\n"
                    + e.getMessage());
        }
    }

    private Muestra muestraElegida() throws Exception {
        int indice = selectorMuestra.getSelectedIndex();
        if (indice < muestras.length) {
            return muestras[indice];
        }
        // Las lecturas pueden ir separadas por espacios, comas o puntos y coma
        String texto = campoLecturas.getText().toUpperCase().replaceAll("[\\s,;]+", " ").trim();
        if (texto.isEmpty()) {
            throw new Exception("Escriba al menos una lectura en el campo «Lecturas».");
        }
        String[] textos = texto.split(" ");
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

    /** La fecha del análisis es la de hoy: se actualiza por si la ventana sigue abierta al cambiar el día. */
    private void actualizarFecha() {
        etiquetaFecha.setText(LocalDate.now() + " (hoy)");
    }

    private String avisoChipGrande() {
        if (!panel.esCompacto()) {
            return "";
        }
        return "(Este chip es grande: deje el ratón sobre un punto para ver su sonda.)\n\n";
    }

    private static String describir(Analisis analisis, Muestra muestra, Microarray microarray) {
        StringBuilder texto = new StringBuilder();
        texto.append("Muestra: ").append(muestra).append('\n');
        texto.append("Microarray: ").append(microarray).append('\n');
        texto.append("Fecha del análisis: ").append(analisis.getFechaAnalisis())
                .append(" (días desde la captura: ").append(analisis.getDiasDesdeCaptura()).append(")\n");
        texto.append("Puntos iluminados: ").append(analisis.contarIluminados())
                .append(" de ").append(microarray.getFilas() * microarray.getColumnas()).append("\n\n");
        texto.append("Dónde encaja cada sonda en cada lectura (según encontrarEn):\n");
        int excepciones = 0;
        for (Lectura lectura : muestra.getLecturas()) {
            String secuencia = lectura.getSecuencia();
            int indeterminadas = secuencia.length() - secuencia.replace("N", "").length();
            texto.append("\n@").append(lectura.getId()).append("   (").append(indeterminadas)
                    .append(indeterminadas == 1 ? " base indeterminada)\n" : " bases indeterminadas)\n");
            texto.append(lectura.getSecuencia()).append('\n');
            boolean alguna = false;
            for (int fila = 0; fila < microarray.getFilas(); fila++) {
                for (int columna = 0; columna < microarray.getColumnas(); columna++) {
                    Sonda sonda = sondaEn(microarray, fila, columna);
                    int posicion;
                    try {
                        posicion = sonda.encontrarEn(lectura);
                    } catch (Exception e) {
                        alguna = true;
                        excepciones++;
                        texto.append("(").append(sonda.getNombre()).append(": encontrarEn lanza una excepción: ")
                                .append(e instanceof RuntimeException ? e.toString() : e.getMessage()).append(")\n");
                        continue;
                    }
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
        if (excepciones > 0) {
            return "AVISO: encontrarEn ha lanzado " + excepciones + (excepciones == 1 ? " excepción" : " excepciones")
                    + " (las verá más abajo).\n"
                    + "Con sondas y lecturas válidas no debería lanzar ninguna: revise su código.\n"
                    + "Los puntos de esas sondas aparecen apagados porque hibridar(Lectura) captura las\n"
                    + "excepciones, pero eso no significa que la sonda no encaje.\n\n" + texto;
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
