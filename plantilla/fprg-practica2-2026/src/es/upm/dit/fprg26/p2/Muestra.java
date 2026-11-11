package es.upm.dit.fprg26.p2;

import module java.base;

/**
 * Una muestra biológica secuenciada: su identificador, la fecha en que se capturó
 * (dato de procedencia) y las lecturas que obtuvo el secuenciador a partir de ella.
 * <p>
 * La fecha de captura se representa con {@link LocalDate}, la clase de la biblioteca
 * de Java para fechas sin hora (por ejemplo, {@code LocalDate.of(2026, 11, 10)}).
 * </p>
 * <p>
 * Una muestra no se puede modificar una vez creada. Como un array sí se puede modificar,
 * el constructor guarda una copia del array de lecturas que recibe, y
 * {@link #getLecturas()} devuelve otra copia: así, nadie puede cambiar desde fuera las
 * lecturas de la muestra.
 * </p>
 */
class Muestra {

    /** Identificador de la muestra. */
    private final String id;

    /** Fecha en que se capturó la muestra. */
    private final LocalDate fechaCaptura;

    /** Lecturas obtenidas al secuenciar la muestra. */
    private final Lectura[] lecturas;

    /**
     * Crea una muestra.
     *
     * @param id identificador de la muestra; no puede ser nulo ni estar en blanco
     * @param fechaCaptura fecha de captura; no puede ser nula ni posterior a la fecha actual
     * @param lecturas lecturas de la muestra; tiene que haber al menos una, y ninguna puede ser nula
     * @throws Exception si alguno de los datos no es válido
     */
    Muestra(String id, LocalDate fechaCaptura, Lectura[] lecturas) throws Exception {
        if (id == null || id.isBlank()) {
            throw new Exception("El identificador de la muestra no puede ser nulo ni estar en blanco");
        }
        if (fechaCaptura == null) {
            throw new Exception("La fecha de captura de la muestra " + id + " no puede ser nula");
        }
        if (fechaCaptura.isAfter(LocalDate.now())) {
            throw new Exception("La fecha de captura de la muestra " + id + " (" + fechaCaptura
                    + ") no puede ser posterior a hoy");
        }
        if (lecturas == null || lecturas.length == 0) {
            throw new Exception("La muestra " + id + " tiene que tener al menos una lectura");
        }
        for (int i = 0; i < lecturas.length; i++) {
            if (lecturas[i] == null) {
                throw new Exception("La lectura " + i + " de la muestra " + id + " es nula");
            }
        }
        this.id = id;
        this.fechaCaptura = fechaCaptura;
        // Se guarda una copia: si quien creó la muestra modifica después su array, la muestra no cambia
        this.lecturas = Arrays.copyOf(lecturas, lecturas.length);
    }

    /**
     * Devuelve el identificador de la muestra.
     *
     * @return identificador de la muestra
     */
    String getId() {
        return id;
    }

    /**
     * Devuelve la fecha en que se capturó la muestra.
     *
     * @return fecha de captura
     */
    LocalDate getFechaCaptura() {
        return fechaCaptura;
    }

    /**
     * Devuelve cuántas lecturas tiene la muestra.
     *
     * @return número de lecturas
     */
    int getNumeroLecturas() {
        return lecturas.length;
    }

    /**
     * Devuelve una de las lecturas de la muestra.
     *
     * @param indice posición de la lectura, empezando a contar en 0
     * @return lectura que ocupa esa posición
     * @throws Exception si el índice está fuera del rango de lecturas
     */
    Lectura getLectura(int indice) throws Exception {
        if (indice < 0 || indice >= lecturas.length) {
            throw new Exception("La muestra " + id + " no tiene lectura " + indice
                    + " (tiene " + lecturas.length + ")");
        }
        return lecturas[indice];
    }

    /**
     * Devuelve todas las lecturas de la muestra, por ejemplo, para recorrerlas con un
     * bucle {@code for} mejorado: {@code for (Lectura lectura : muestra.getLecturas())}.
     * <p>
     * Devuelve una copia del array: modificarla no cambia la muestra.
     * </p>
     *
     * @return copia del array con las lecturas de la muestra
     */
    Lectura[] getLecturas() {
        return Arrays.copyOf(lecturas, lecturas.length);
    }

    /**
     * Devuelve una descripción de la muestra: su identificador, su fecha de captura y
     * su número de lecturas.
     *
     * @return descripción de la muestra, por ejemplo, {@code "M1 (capturada el 2026-11-02, 3 lecturas)"}
     */
    @Override
    public String toString() {
        return id + " (capturada el " + fechaCaptura + ", " + lecturas.length + " lecturas)";
    }
}
