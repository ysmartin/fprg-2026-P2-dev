#!/usr/bin/env python3
"""Genera las figuras SVG de la ventana deslizante que usa el enunciado (carpeta img/).

Cada figura muestra una lectura con sus posiciones y el patrón colocado en varias
posiciones sucesivas, con flechas que indican cómo avanza. En cada posición se colorean
los símbolos comparados (verde si encajan, rojo el primero que no encaja) y en gris los
que ya no se comparan.

En las figuras en sentido inverso se añade la fila de las bases complementarias de la
lectura, y el patrón se dibuja leído de derecha a izquierda.

También genera la figura de las dos hebras de un fragmento de ADN (img/dos-hebras.svg) y
las miniaturas de las letras de la palabra «ADN» del chip de cribado (img/letra-*.svg).

Uso (desde la raíz del repositorio): python3 scripts/generar-figuras.py
"""
import os
import re

RAIZ = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..')
COMPLEMENTARIA = {'A': 'T', 'T': 'A', 'C': 'G', 'G': 'C', 'N': 'N'}

C = 36       # lado de cada casilla
X0 = 190     # x de la posición 0
SEP = 64     # separación vertical entre las filas del patrón
MONO = "font-family=\"'DejaVu Sans Mono', Menlo, Consolas, monospace\""
SANS = "font-family=\"'Segoe UI', 'DejaVu Sans', Helvetica, Arial, sans-serif\""
AZUL = '#0969da'


def encaja(simbolo, base):
    return {'N': True, 'R': base in 'AG', 'Y': base in 'CT'}.get(simbolo, simbolo == base)


def casilla(x, y, letra, relleno, borde, color_texto, grosor=1.5):
    return (f'<rect x="{x}" y="{y}" width="{C}" height="{C}" rx="5" fill="{relleno}" '
            f'stroke="{borde}" stroke-width="{grosor}"/>'
            f'<text x="{x + C / 2}" y="{y + C / 2 + 6}" text-anchor="middle" font-size="18" '
            f'font-weight="bold" {MONO} fill="{color_texto}">{letra}</text>')


def texto(x, y, contenido, tamano=14, color='#1f2328', ancla='start', negrita=False, fuente=SANS):
    peso = ' font-weight="bold"' if negrita else ''
    return (f'<text x="{x}" y="{y}" text-anchor="{ancla}" font-size="{tamano}"{peso} {fuente} '
            f'fill="{color}">{contenido}</text>')


def figura(lectura, patron, inicios, inverso=False):
    comparado = patron[::-1] if inverso else patron
    y = 18
    partes = []
    if inverso:
        inversa = ''.join(COMPLEMENTARIA.get(c, c) for c in reversed(patron))
        partes.append(texto(24, y + 14, f'Sentido inverso: el patrón {patron}, leído de derecha a izquierda '
                                        f'({comparado}), se compara con las complementarias de la lectura.',
                            color='#57606a'))
        partes.append(texto(24, y + 36, f'Es lo mismo que buscar en la lectura la complementaria inversa del '
                                        f'patrón, {inversa} (recuadrada en verde).', color='#57606a'))
        y += 52
    y_indices = y + 22
    y_lectura = y_indices + 10
    partes.append(texto(X0 - 16, y_indices - 6, 'posición', 13, '#57606a', 'end'))
    partes.append(texto(X0 - 16, y_lectura + C / 2 + 5, 'lectura', 15, ancla='end', negrita=True))
    for i, base in enumerate(lectura):
        x = X0 + i * C
        partes.append(texto(x + C / 2, y_indices - 6, i, 13, '#57606a', 'middle', fuente=MONO))
        partes.append(casilla(x, y_lectura, base, '#f6f8fa', '#8c959f', '#1f2328'))
    fila_bases = lectura
    y_filas = y_lectura + C + 28
    if inverso:
        fila_bases = ''.join(COMPLEMENTARIA[b] for b in lectura)
        y_comp = y_lectura + C + 10
        partes.append(texto(X0 - 16, y_comp + C / 2 + 5, 'complementarias', 14, AZUL, 'end'))
        for i, base in enumerate(fila_bases):
            partes.append(casilla(X0 + i * C, y_comp, base, '#ddf4ff', '#54aeff', AZUL))
        y_filas = y_comp + C + 28

    notas = []
    filas_y = [y_filas + k * SEP for k in range(len(inicios))]
    for y_fila, inicio in zip(filas_y, inicios):
        partes.append(texto(X0 - 16, y_fila + C / 2 + 5, f'inicio = {inicio}', ancla='end'))
        fallo = None
        for k, simbolo in enumerate(comparado):
            x = X0 + (inicio + k) * C
            base = fila_bases[inicio + k]
            if fallo is not None:
                partes.append(casilla(x, y_fila, simbolo, '#ffffff', '#d0d7de', '#afb8c1', 1.2))
            elif encaja(simbolo, base):
                partes.append(casilla(x, y_fila, simbolo, '#dafbe1', '#1a7f37', '#116329'))
            else:
                fallo = (simbolo, base, lectura[inicio + k])
                partes.append(casilla(x, y_fila, simbolo, '#ffebe9', '#cf222e', '#a40e26'))
        if fallo is None:
            pares = ', '.join(f'{s} con {fila_bases[inicio + k]}' for k, s in enumerate(comparado))
            notas.append((y_fila, f'{pares}: encaja, devuelve {inicio}', '#116329'))
        elif inverso:
            notas.append((y_fila, f'{fallo[0]} no encaja con {fallo[1]} (complementaria de {fallo[2]})', '#a40e26'))
        else:
            notas.append((y_fila, f'{fallo[0]} no encaja con {fallo[1]}: se desplaza el patrón', '#a40e26'))
    if inverso:
        encaja_en = inicios[-1]
        partes.append(f'<rect x="{X0 + encaja_en * C - 4}" y="{y_lectura - 4}" width="{len(patron) * C + 8}" '
                      f'height="{y_comp + C - y_lectura + 8}" rx="8" fill="none" stroke="#1a7f37" stroke-width="2.5"/>')
    x_notas = X0 + len(lectura) * C + 28
    for y_fila, contenido, color in notas:
        partes.append(texto(x_notas, y_fila + C / 2 + 5, contenido, color=color))

    for k in range(len(inicios) - 1):
        xa = X0 + inicios[k] * C + C / 2
        ya = filas_y[k] + C + 3
        xb = X0 + inicios[k + 1] * C + C / 2
        yb = filas_y[k + 1] - 4
        partes.append(f'<path d="M {xa} {ya} C {xa} {ya + 16}, {xb} {yb - 16}, {xb} {yb}" fill="none" '
                      f'stroke="{AZUL}" stroke-width="2" marker-end="url(#punta)"/>')
        partes.append(texto(xa - 8, (ya + yb) / 2 + 4, '+1', 13, AZUL, 'end', True))

    y_flecha = filas_y[-1] + C + 30
    partes.append(f'<line x1="{X0}" y1="{y_flecha}" x2="{X0 + len(lectura) * C - 4}" y2="{y_flecha}" '
                  f'stroke="{AZUL}" stroke-width="2" marker-end="url(#punta)"/>')
    partes.append(texto(X0 + len(lectura) * C + 10, y_flecha + 5, 'el patrón avanza una posición cada vez', 13, AZUL))

    # Ancho aproximado del texto más largo (unos 7,8 píxeles por carácter a 14 puntos)
    largo_notas = max(len(contenido) for _, contenido, _ in notas)
    ancho = int(max(920, x_notas + largo_notas * 7.8 + 30))
    alto = y_flecha + 24
    cabecera = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{ancho}" height="{alto}" viewBox="0 0 {ancho} {alto}">',
                '<defs><marker id="punta" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="7" markerHeight="7" '
                f'orient="auto-start-reverse"><path d="M 0 0 L 10 5 L 0 10 z" fill="{AZUL}"/></marker></defs>',
                f'<rect x="1" y="1" width="{ancho - 2}" height="{alto - 2}" rx="10" fill="#ffffff" stroke="#d0d7de"/>']
    return '\n'.join(cabecera + partes + ['</svg>']) + '\n'


def figura_dos_hebras(hebra, inicio_diana, largo_diana):
    """Fragmento de ADN con sus dos hebras: la diana está en la primera, y la segunda, leída
    en su sentido (de derecha a izquierda), es la lectura que contiene su complementaria inversa."""
    n = len(hebra)
    complementaria = ''.join(COMPLEMENTARIA[b] for b in hebra)
    lectura = complementaria[::-1]
    diana = hebra[inicio_diana:inicio_diana + largo_diana]
    inversa = lectura[n - inicio_diana - largo_diana:n - inicio_diana]
    x0 = 230
    y1, y2 = 30, 30 + C + 18
    y_indices = y2 + C + 96
    y3 = y_indices + 10
    partes = []

    def fila(y, bases, resaltadas, relleno, borde, color):
        for i, b in enumerate(bases):
            if i in resaltadas:
                partes.append(casilla(x0 + i * C, y, b, relleno, borde, color))
            else:
                partes.append(casilla(x0 + i * C, y, b, '#f6f8fa', '#8c959f', '#1f2328'))

    en_diana = range(inicio_diana, inicio_diana + largo_diana)
    en_lectura = range(n - inicio_diana - largo_diana, n - inicio_diana)
    partes.append(texto(x0 - 16, y1 + C / 2 + 5, 'hebra con la diana', 14, ancla='end', negrita=True))
    fila(y1, hebra, en_diana, '#dafbe1', '#1a7f37', '#116329')
    for i in range(n):
        x = x0 + i * C + C / 2
        partes.append(f'<line x1="{x}" y1="{y1 + C}" x2="{x}" y2="{y2}" stroke="#8c959f" stroke-width="2"/>')
    partes.append(texto(x0 - 16, y2 + C / 2 + 5, 'hebra complementaria', 14, ancla='end', negrita=True))
    fila(y2, complementaria, en_diana, '#ddf4ff', '#54aeff', AZUL)
    x_notas = x0 + n * C + 24
    partes.append(texto(x_notas, y1 + C / 2 + 5, f'→  se lee de izquierda a derecha; contiene la diana, {diana}'))
    partes.append(texto(x_notas, y2 + C / 2 + 5, '←  se lee de derecha a izquierda', color=AZUL))
    # Correspondencia entre la hebra complementaria y la lectura (se invierte el orden)
    for k in en_diana:
        xa = x0 + k * C + C / 2
        xb = x0 + (n - 1 - k) * C + C / 2
        partes.append(f'<line x1="{xa}" y1="{y2 + C + 4}" x2="{xb}" y2="{y_indices - 22}" stroke="{AZUL}" '
                      f'stroke-width="2" stroke-dasharray="5 4" marker-end="url(#punta)"/>')
    partes.append(texto(x_notas, (y2 + C + y_indices) / 2 + 4,
                        'la hebra complementaria, leída de derecha a izquierda,', color=AZUL))
    partes.append(texto(x_notas, (y2 + C + y_indices) / 2 + 24, 'es la lectura que obtiene el secuenciador', color=AZUL))
    partes.append(texto(x0 - 16, y_indices - 6, 'posición', 13, '#57606a', 'end'))
    for i in range(n):
        partes.append(texto(x0 + i * C + C / 2, y_indices - 6, i, 13, '#57606a', 'middle', fuente=MONO))
    partes.append(texto(x0 - 16, y3 + C / 2 + 5, 'lectura', 15, ancla='end', negrita=True))
    fila(y3, lectura, en_lectura, '#dafbe1', '#1a7f37', '#116329')
    partes.append(texto(x_notas, y3 + C / 2 + 5, f'contiene {inversa}, la complementaria inversa de {diana}',
                        color='#116329'))
    largo = max(len(f'→  se lee de izquierda a derecha; contiene la diana, {diana}'),
                len('la hebra complementaria, leída de derecha a izquierda,'))
    ancho = int(x_notas + largo * 7.8 + 30)
    alto = y3 + C + 24
    cabecera = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{ancho}" height="{alto}" viewBox="0 0 {ancho} {alto}">',
                '<defs><marker id="punta" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="7" markerHeight="7" '
                f'orient="auto-start-reverse"><path d="M 0 0 L 10 5 L 0 10 z" fill="{AZUL}"/></marker></defs>',
                f'<rect x="1" y="1" width="{ancho - 2}" height="{alto - 2}" rx="10" fill="#ffffff" stroke="#d0d7de"/>']
    return '\n'.join(cabecera + partes + ['</svg>']) + '\n'


def formas_de_las_letras():
    """Lee de DatosEjemplo el dibujo de la palabra «ADN» y devuelve las filas de cada letra."""
    ruta = os.path.join(RAIZ, 'solucion', 'fprg-practica2-2026', 'auxiliar', 'es', 'upm', 'dit', 'fprg26', 'p2',
                        'DatosEjemplo.java')
    with open(ruta, encoding='utf-8') as f:
        codigo = f.read()
    filas = re.findall(r'"([.#]{2,})"', codigo)
    columna_d = int(re.search(r'COLUMNA_D = (\d+);', codigo).group(1))
    columna_n = int(re.search(r'COLUMNA_N = (\d+);', codigo).group(1))
    con_puntos = [f for f in filas if '#' in f]
    letras = {}
    for letra, desde, hasta in [('a', 0, columna_d), ('d', columna_d, columna_n), ('n', columna_n, len(filas[0]))]:
        # Las columnas de la letra: de la primera a la última con algún punto encendido
        columnas = [c for c in range(desde, hasta) if any(f[c] == '#' for f in con_puntos)]
        letras[letra] = [f[columnas[0]:columnas[-1] + 1] for f in con_puntos]
    return letras


def miniatura_letra(filas):
    """Una letra dibujada con los puntos del chip de cribado: encendidos en amarillo y apagados en gris."""
    paso, margen = 10, 6
    ancho = len(filas[0]) * paso + 2 * margen
    alto = len(filas) * paso + 2 * margen
    partes = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{ancho}" height="{alto}" viewBox="0 0 {ancho} {alto}">',
              f'<rect width="{ancho}" height="{alto}" rx="5" fill="#22272e"/>']
    for f, linea in enumerate(filas):
        for c, punto in enumerate(linea):
            color = '#ffd600' if punto == '#' else '#444c56'
            partes.append(f'<circle cx="{margen + c * paso + paso / 2}" cy="{margen + f * paso + paso / 2}" r="3.6" '
                          f'fill="{color}"/>')
    return '\n'.join(partes + ['</svg>']) + '\n'


FIGURAS = {
    'ventana-sin-comodines.svg': ('GATACATGCA', 'TAC', [0, 1, 2], False),
    'ventana-deslizante.svg': ('GATACATGCA', 'TRC', [0, 1, 2], False),
    'ventana-inversa.svg': ('GATACATGCA', 'GTA', [0, 1, 2], True),
}


def guardar(nombre, contenido):
    ruta = os.path.join(RAIZ, 'img', nombre)
    with open(ruta, 'w', encoding='utf-8') as f:
        f.write(contenido)
    print('Generada', os.path.relpath(ruta, RAIZ))


def main():
    for nombre, (lectura, patron, inicios, inverso) in FIGURAS.items():
        guardar(nombre, figura(lectura, patron, inicios, inverso))
    # La lectura de las figuras anteriores (GATACATGCA) es la hebra complementaria de este fragmento
    guardar('dos-hebras.svg', figura_dos_hebras('TGCATGTATC', 5, 3))
    for letra, filas in formas_de_las_letras().items():
        guardar(f'letra-{letra}.svg', miniatura_letra(filas))


if __name__ == '__main__':
    main()
