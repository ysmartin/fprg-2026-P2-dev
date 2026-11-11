package es.upm.dit.fprg26.p2;

import module java.base;
import module java.desktop;

import java.util.List;

import es.upm.dit.fprg26.p2.AnimacionTraza.Detalle;

/**
 * Genera una animación que muestra, como un depurador, cómo analiza un microarray una muestra:
 * las vueltas de cada bucle y las llamadas a hibridar(Muestra), hibridar(Lectura),
 * hibridaCon, encontrarEn, encajaEn, encaja y encajaInverso, con la ventana deslizante en los
 * dos sentidos.
 * <p>
 * El ejemplo usa un chip de 2x2 sondas y una muestra de dos lecturas, elegidos para que
 * aparezcan todos los casos: un patrón con un comodín que encaja en sentido directo, otro que
 * encaja en sentido inverso, uno que no encaja en ninguna posición y un punto que sigue
 * iluminado aunque su sonda no hibride con la segunda lectura.
 * </p>
 * <p>
 * Uso (desde la carpeta animacion):
 * </p>
 * <pre>
 * java ... es.upm.dit.fprg26.p2.AnimacionHibridacion [fichero.gif] [--escala 1.5] [--png carpeta]
 * </pre>
 * <ul>
 *   <li>fichero.gif: fichero de salida (por omisión, hibridacion.gif);</li>
 *   <li>--escala: tamaño de las imágenes respecto a 1280x720 (1.5 da 1920x1080);</li>
 *   <li>--png: además, guarda cada fotograma como PNG en esa carpeta, con un fichero
 *       duraciones.txt (útil para montar un vídeo sin la pérdida de color del GIF).</li>
 * </ul>
 */
final class AnimacionHibridacion {

    private AnimacionHibridacion() {
        // No se instancia: solo contiene métodos estáticos
    }

    /**
     * Devuelve el chip de la animación: cuatro sondas de patrones cortos.
     *
     * @return chip de 2x2
     * @throws Exception si alguna sonda no se puede crear
     */
    static Microarray chip() throws Exception {
        return new Microarray("chip", new Sonda[][] {
            { new Sonda("s1", "TRC"), new Sonda("s2", "GTA") },
            { new Sonda("s3", "CYT"), new Sonda("s4", "CGC") }
        });
    }

    /**
     * Devuelve la muestra de la animación. Con el chip de {@link #chip()}:
     * <ul>
     *   <li>l1 (la lectura de los ejemplos del enunciado) ilumina s1 (TRC encaja en sentido
     *       directo en la posición 2, la R con una A) y s2 (GTA encaja en sentido inverso en la
     *       posición 2);</li>
     *   <li>l2 ilumina s3 (CYT encaja en la posición 0, la Y con una C); s1 y s2 no hibridan
     *       con l2, pero siguen iluminados;</li>
     *   <li>s4 no hibrida con ninguna lectura.</li>
     * </ul>
     *
     * @return muestra de dos lecturas
     * @throws Exception si la muestra no se puede crear
     */
    static Muestra muestra() throws Exception {
        return new Muestra("m1", LocalDate.now(), new Lectura[] {
            new Lectura("l1", "GATACATGCA"),
            new Lectura("l2", "CCTAGTTCGA")
        });
    }

    /**
     * Nivel de detalle de cada sonda con cada lectura: con la primera lectura, la primera sonda
     * se muestra paso a paso, la segunda comparación a comparación y las demás posición a
     * posición; con la segunda lectura, solo el resultado de cada sonda.
     *
     * @param lectura índice de la lectura
     * @param fila fila de la sonda
     * @param columna columna de la sonda
     * @return nivel de detalle
     */
    static Detalle detalle(int lectura, int fila, int columna) {
        if (lectura > 0) {
            return Detalle.RESUMEN;
        }
        if (fila == 0) {
            return columna == 0 ? Detalle.PASO_A_PASO : Detalle.COMPARACIONES;
        }
        return Detalle.POSICIONES;
    }

    /**
     * Genera la animación.
     *
     * @param args argumentos (ver la documentación de la clase)
     * @throws Exception si la animación no se puede generar
     */
    static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        Path gif = Path.of("hibridacion.gif");
        double escala = 1;
        Path carpetaPng = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--escala" -> escala = Double.parseDouble(argumento(args, ++i));
                case "--png" -> carpetaPng = Path.of(argumento(args, ++i));
                default -> gif = Path.of(args[i]);
            }
        }

        Microarray microarray = chip();
        Muestra muestra = muestra();
        List<AnimacionFotograma> fotogramas = AnimacionTraza.generar(microarray, muestra, AnimacionHibridacion::detalle);
        AnimacionLienzo lienzo = new AnimacionLienzo(microarray, muestra, escala);

        if (gif.toAbsolutePath().getParent() != null) {
            Files.createDirectories(gif.toAbsolutePath().getParent());
        }
        if (carpetaPng != null) {
            Files.createDirectories(carpetaPng);
        }
        StringBuilder duraciones = new StringBuilder();
        int total = 0;
        try (AnimacionGif salida = new AnimacionGif(gif)) {
            for (int i = 0; i < fotogramas.size(); i++) {
                AnimacionFotograma fotograma = fotogramas.get(i);
                BufferedImage imagen = lienzo.dibujar(fotograma);
                salida.escribir(imagen, fotograma.centesimas());
                total += fotograma.centesimas();
                if (carpetaPng != null) {
                    String nombre = String.format("fotograma-%03d.png", i + 1);
                    ImageIO.write(imagen, "png", carpetaPng.resolve(nombre).toFile());
                    duraciones.append(nombre).append(' ').append(fotograma.centesimas() / 100.0).append('\n');
                }
            }
        }
        if (carpetaPng != null) {
            Files.writeString(carpetaPng.resolve("duraciones.txt"), duraciones);
        }
        System.out.printf("%s: %d fotogramas, %d:%02d minutos%n", gif.toAbsolutePath(), fotogramas.size(),
                total / 6000, total / 100 % 60);
    }

    private static String argumento(String[] args, int i) {
        if (i >= args.length) {
            throw new IllegalArgumentException("Falta el valor de " + args[i - 1]);
        }
        return args[i];
    }
}
