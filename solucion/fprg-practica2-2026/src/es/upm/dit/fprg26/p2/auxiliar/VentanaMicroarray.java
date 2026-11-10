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
 * Permite elegir un microarray y una muestra (o escribir lecturas a mano) y una fecha de
 * análisis. Al pulsar «Analizar», crea un {@link Analisis} con las clases de la práctica,
 * dibuja el chip con sus puntos iluminados y muestra, para cada lectura, en qué posición
 * encaja cada sonda.
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
    private JTextField campoFecha;
    private PanelMicroarray panel;
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
        campoFecha = new JTextField(LocalDate.now().toString(), 9);
        campoFecha.setToolTipText("Fecha del análisis, con el formato AAAA-MM-DD");
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
        colocar(controles, c, 3, 0, campoFecha);
        colocar(controles, c, 0, 1, new JLabel("Muestra:"));
        colocar(controles, c, 1, 1, selectorMuestra);
        colocar(controles, c, 2, 1, new JLabel("Lecturas:"));
        colocar(controles, c, 3, 1, campoLecturas);
        colocar(controles, c, 4, 1, botonAnalizar);

        panel = new PanelMicroarray();
        detalle = new JTextArea(14, 80);
        detalle.setEditable(false);
        detalle.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(panel), new JScrollPane(detalle));
        division.setResizeWeight(0.55);

        ventana.getContentPane().add(controles, BorderLayout.NORTH);
        ventana.getContentPane().add(division, BorderLayout.CENTER);
        actualizarCampoLecturas();
        mostrarChipSinAnalizar();
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
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
        detalle.setText("Elija una muestra y pulse «Analizar».");
    }

    private void analizar() {
        Microarray microarray = microarrays[selectorMicroarray.getSelectedIndex()];
        panel.mostrar(microarray, null);
        try {
            Muestra muestra = muestraElegida();
            LocalDate fecha = leerFecha();
            Analisis analisis = new Analisis(muestra, microarray, fecha);
            boolean[][] iluminados = analisis.getIluminados();
            String aviso = comprobarDimensiones(microarray, iluminados);
            panel.mostrar(microarray, aviso == null ? iluminados : null);
            detalle.setText(describir(analisis, muestra, microarray, aviso));
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

    private LocalDate leerFecha() throws Exception {
        try {
            return LocalDate.parse(campoFecha.getText().trim());
        } catch (DateTimeParseException e) {
            throw new Exception("La fecha del análisis «" + campoFecha.getText()
                    + "» no tiene el formato AAAA-MM-DD (por ejemplo, 2026-11-10).");
        }
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
                .append(" (").append(analisis.getDiasDesdeCaptura()).append(" días después de la captura)\n");
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
