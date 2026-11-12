package es.upm.dit.fprg26.p2;

import module java.base;

/**
 * Un fotograma de la animación: una instantánea del estado del algoritmo en un momento de
 * su ejecución, como la que mostraría un depurador detenido en un punto de ruptura.
 * <p>
 * Los fotogramas los crea {@link AnimacionTraza} y los dibuja {@link AnimacionLienzo}. Son
 * inmutables: las matrices y los arrays que reciben se copian.
 * </p>
 *
 * @param pila llamadas en curso, de la más externa a la más interna (la que se está ejecutando)
 * @param filaSonda fila de la sonda que se está comprobando, o -1 si no se comprueba ninguna
 * @param columnaSonda columna de la sonda que se está comprobando, o -1
 * @param luces puntos iluminados del chip, o null mientras no haya terminado el análisis
 * @param lectura índice de la lectura de la muestra que se está analizando, o -1
 * @param ventana estado de la ventana deslizante
 * @param mensaje explicación del paso, en una o dos líneas
 * @param avanceRapido aviso de que se omiten pasos (por ejemplo, "un fotograma por posición"),
 *                     o null si se muestran todos
 * @param centesimas duración del fotograma, en centésimas de segundo
 */
record AnimacionFotograma(List<Llamada> pila, int filaSonda, int columnaSonda, boolean[][] luces,
        int lectura, Ventana ventana, String mensaje, String avanceRapido, int centesimas) {

    /** Crea un fotograma, con copias de la pila y de la matriz de puntos iluminados. */
    AnimacionFotograma {
        pila = List.copyOf(pila);
        luces = copiar(luces);
    }

    /**
     * Una llamada de la pila, con sus argumentos y sus variables locales.
     *
     * @param receptor variable del objeto sobre el que se invoca el método (por ejemplo, "s1"),
     *                 o null si se invoca sin él (los métodos estáticos de Sonda, desde la
     *                 propia clase)
     * @param metodo nombre del método
     * @param argumentos argumentos de la llamada, con su tipo
     * @param variables variables locales de la llamada, en el orden en que se declaran
     * @param devuelve valor que devuelve la llamada, si está terminando, o null
     */
    record Llamada(String receptor, String metodo, List<Argumento> argumentos, List<Variable> variables,
            String devuelve) {
        /** Crea una llamada, con copias de sus argumentos y sus variables. */
        Llamada {
            argumentos = List.copyOf(argumentos);
            variables = List.copyOf(variables);
        }

        /**
         * Devuelve la llamada como se escribiría en Java, sin los tipos.
         *
         * @return llamada, por ejemplo, "s1.encajaEn(l1, 2)"
         */
        String texto() {
            StringJoiner valores = new StringJoiner(", ", (receptor == null ? "" : receptor + ".") + metodo + "(", ")");
            for (Argumento argumento : argumentos) {
                valores.add(argumento.valor());
            }
            return valores.toString();
        }
    }

    /**
     * Un argumento de una llamada, con el parámetro que lo recibe.
     *
     * @param tipo tipo del parámetro (por ejemplo, "Lectura" o "int")
     * @param nombre nombre del parámetro (por ejemplo, "lectura" o "inicio")
     * @param valor valor del argumento, tal como se escribe (por ejemplo, "l1" o "2")
     */
    record Argumento(String tipo, String nombre, String valor) {
    }

    /**
     * Una variable local de una llamada.
     *
     * @param tipo tipo de la variable (por ejemplo, "int" o "boolean[][]")
     * @param nombre nombre de la variable
     * @param valor valor, tal como se escribe (si no es una matriz)
     * @param matriz valor, si es una matriz de boolean (se dibuja como una cuadrícula), o null
     * @param filaMarcada fila del elemento de la matriz que se resalta (el de la sonda que se
     *                    comprueba), o -1
     * @param columnaMarcada columna del elemento de la matriz que se resalta, o -1
     * @param cambiada si la variable ha cambiado (o se ha declarado) desde el fotograma
     *                 anterior, dentro de la misma llamada (como en el depurador de Eclipse)
     */
    record Variable(String tipo, String nombre, String valor, boolean[][] matriz, int filaMarcada,
            int columnaMarcada, boolean cambiada) {
        /** Crea una variable, con una copia de la matriz. */
        Variable {
            matriz = copiar(matriz);
        }
    }

    /** Estado de un símbolo del patrón en la ventana deslizante. */
    enum Casilla {
        /** Todavía no se ha comparado (o no se va a comparar, porque ya ha fallado otro). */
        PENDIENTE,
        /** Se está comparando. */
        COMPARANDO,
        /** Se ha comparado y encaja. */
        ENCAJA,
        /** Se ha comparado y no encaja. */
        NO_ENCAJA
    }

    /**
     * Estado de la ventana deslizante: el patrón de una sonda colocado sobre una lectura.
     *
     * @param lectura secuencia de la lectura, o null si no se está analizando ninguna
     * @param idLectura identificador de la lectura
     * @param patron patrón de la sonda, o null si no se está comprobando ninguna
     * @param nombreSonda nombre de la sonda
     * @param inicio posición de la lectura en la que se ha colocado el patrón, o -1
     * @param ultimoInicio última posición en la que cabe el patrón (el límite del bucle de
     *                     encontrarEn), o -1 fuera de encontrarEn
     * @param directo estado de cada símbolo del patrón en sentido directo, o null si no se ha
     *                empezado a comparar
     * @param inverso estado de cada posición de la ventana en sentido inverso (la posición i
     *                tiene el símbolo patron.length() - 1 - i), o null si no se ha empezado a
     *                comparar en este sentido
     * @param notaDirecto resultado de la comparación en sentido directo (o de la búsqueda, si
     *                    el patrón no encaja en ninguna posición), o null
     * @param notaInverso resultado de la comparación en sentido inverso, o null
     */
    record Ventana(String lectura, String idLectura, String patron, String nombreSonda, int inicio,
            int ultimoInicio, Casilla[] directo, Casilla[] inverso, String notaDirecto, String notaInverso) {
        /** Crea el estado de la ventana, con copias de los estados de las casillas. */
        Ventana {
            directo = directo == null ? null : directo.clone();
            inverso = inverso == null ? null : inverso.clone();
        }
    }

    /**
     * Copia una matriz de boolean fila a fila.
     *
     * @param original matriz que se copia, o null
     * @return copia de la matriz, o null si la original es null
     */
    static boolean[][] copiar(boolean[][] original) {
        if (original == null) {
            return null;
        }
        boolean[][] copia = new boolean[original.length][];
        for (int fila = 0; fila < original.length; fila++) {
            copia[fila] = original[fila].clone();
        }
        return copia;
    }
}
