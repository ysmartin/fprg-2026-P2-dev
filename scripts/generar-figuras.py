#!/usr/bin/env python3
"""Genera las figuras SVG de la ventana deslizante que usa el enunciado (carpeta img/).

Cada figura muestra una lectura con sus posiciones y el patrón colocado en varias
posiciones sucesivas, con flechas que indican cómo avanza. En cada posición se colorean
los símbolos comparados (verde si encajan, rojo el primero que no encaja) y en gris los
que ya no se comparan.

En las figuras en sentido inverso se añade la fila de las bases complementarias de la
lectura, y el patrón se dibuja leído de derecha a izquierda.

Uso (desde la raíz del repositorio): python3 scripts/generar-figuras.py
"""
import os

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
        partes.append(texto(24, y + 14, f'Sentido inverso: el patrón {patron}, leído de derecha a izquierda '
                                        f'({comparado}), se compara con las complementarias de la lectura',
                            color='#57606a'))
        y += 30
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


FIGURAS = {
    'ventana-sin-comodines.svg': ('GATACATGCA', 'TAC', [0, 1, 2], False),
    'ventana-deslizante.svg': ('GATACATGCA', 'TRC', [0, 1, 2], False),
    'ventana-inversa.svg': ('GATACATGCA', 'GTA', [0, 1, 2], True),
}


def main():
    for nombre, (lectura, patron, inicios, inverso) in FIGURAS.items():
        ruta = os.path.join(RAIZ, 'img', nombre)
        with open(ruta, 'w', encoding='utf-8') as f:
            f.write(figura(lectura, patron, inicios, inverso))
        print('Generada', os.path.relpath(ruta, RAIZ))


if __name__ == '__main__':
    main()
