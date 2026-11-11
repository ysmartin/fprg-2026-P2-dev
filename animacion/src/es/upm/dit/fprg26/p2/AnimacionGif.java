package es.upm.dit.fprg26.p2;

import module java.base;
import module java.desktop;

/**
 * Escribe un GIF animado, fotograma a fotograma, con javax.imageio (sin bibliotecas externas).
 * <p>
 * Cada fotograma tiene su propia duración, y la animación se repite indefinidamente.
 * </p>
 */
final class AnimacionGif implements Closeable {

    private final ImageOutputStream salida;
    private final ImageWriter escritor;
    private boolean primero = true;

    /**
     * Crea el fichero GIF.
     *
     * @param fichero fichero en el que se escribe la animación
     * @throws IOException si no se puede crear el fichero
     */
    AnimacionGif(Path fichero) throws IOException {
        Iterator<ImageWriter> escritores = ImageIO.getImageWritersByFormatName("gif");
        if (!escritores.hasNext()) {
            throw new IOException("Esta instalación de Java no puede escribir GIF");
        }
        Files.deleteIfExists(fichero);
        salida = ImageIO.createImageOutputStream(fichero.toFile());
        if (salida == null) {
            throw new IOException("No se puede escribir en " + fichero);
        }
        escritor = escritores.next();
        escritor.setOutput(salida);
        escritor.prepareWriteSequence(null);
    }

    /**
     * Añade un fotograma.
     *
     * @param imagen imagen del fotograma
     * @param centesimas duración del fotograma, en centésimas de segundo
     * @throws IOException si no se puede escribir
     */
    void escribir(BufferedImage imagen, int centesimas) throws IOException {
        ImageWriteParam parametros = escritor.getDefaultWriteParam();
        IIOMetadata metadatos = escritor.getDefaultImageMetadata(ImageTypeSpecifier.createFromRenderedImage(imagen),
                parametros);
        String formato = metadatos.getNativeMetadataFormatName();
        IIOMetadataNode raiz = (IIOMetadataNode) metadatos.getAsTree(formato);

        IIOMetadataNode control = nodo(raiz, "GraphicControlExtension");
        control.setAttribute("disposalMethod", "none");
        control.setAttribute("userInputFlag", "FALSE");
        control.setAttribute("transparentColorFlag", "FALSE");
        control.setAttribute("delayTime", Integer.toString(Math.max(1, centesimas)));
        control.setAttribute("transparentColorIndex", "0");

        if (primero) {
            // Extensión NETSCAPE 2.0: repetir la animación indefinidamente
            IIOMetadataNode extension = new IIOMetadataNode("ApplicationExtension");
            extension.setAttribute("applicationID", "NETSCAPE");
            extension.setAttribute("authenticationCode", "2.0");
            extension.setUserObject(new byte[] { 1, 0, 0 });
            nodo(raiz, "ApplicationExtensions").appendChild(extension);
            primero = false;
        }
        metadatos.setFromTree(formato, raiz);
        escritor.writeToSequence(new IIOImage(imagen, null, metadatos), parametros);
    }

    /** Devuelve el primer nodo hijo con ese nombre, creándolo si no existe. */
    private static IIOMetadataNode nodo(IIOMetadataNode raiz, String nombre) {
        for (int i = 0; i < raiz.getLength(); i++) {
            if (raiz.item(i).getNodeName().equalsIgnoreCase(nombre)) {
                return (IIOMetadataNode) raiz.item(i);
            }
        }
        IIOMetadataNode nodo = new IIOMetadataNode(nombre);
        raiz.appendChild(nodo);
        return nodo;
    }

    /**
     * Termina la animación y cierra el fichero.
     *
     * @throws IOException si no se puede escribir
     */
    @Override
    public void close() throws IOException {
        try {
            escritor.endWriteSequence();
        } finally {
            escritor.dispose();
            salida.close();
        }
    }
}
