#!/usr/bin/env python3
"""Deriva la plantilla del alumnado a partir de la solución de referencia.

Copia el proyecto solucion/fprg-practica2-2026 en plantilla/fprg-practica2-2026 y
sustituye el cuerpo de los métodos que debe programar el alumnado por un comentario
TODO y un valor de retorno provisional. Los atributos de las clases que se completan
dejan de ser final, para que la plantilla compile con los constructores vacíos.

Uso (desde la raíz del repositorio): python3 scripts/derivar-plantilla.py
La carpeta doc/ de la plantilla se regenera después con scripts/generar-doc.sh.
"""
import os
import shutil

RAIZ = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..')
SOLUCION = os.path.join(RAIZ, 'solucion', 'fprg-practica2-2026')
PLANTILLA = os.path.join(RAIZ, 'plantilla', 'fprg-practica2-2026')
PAQUETE = os.path.join('src', 'es', 'upm', 'dit', 'fprg26', 'p2')

TODO = '// TODO: desarrollar'

# fichero -> lista de (inicio de la cabecera del método, líneas del cuerpo provisional)
METODOS = {
    'Lectura.java': [
        ('public int contarIndeterminadas()', [TODO, 'return 0;']),
    ],
    'Sonda.java': [
        ('public Sonda(String nombre, String patron)', [TODO]),
        ('public String getNombre()', [TODO, 'return null;']),
        ('public String getPatron()', [TODO, 'return null;']),
        ('public int getLongitud()', [TODO, 'return 0;']),
        ('public static boolean encaja(char simbolo, char base)', [TODO, 'return false;']),
        ('public boolean encajaEn(Lectura lectura, int inicio)',
         [TODO + ' (use try/catch: si el patrón se sale de la lectura, no encaja)', 'return false;']),
        ('public int buscarEn(Lectura lectura)', [TODO, 'return -1;']),
        ('public boolean hibridaCon(Lectura lectura)', [TODO, 'return false;']),
    ],
    'Microarray.java': [
        ('public boolean[][] hibridar(Lectura lectura)', [TODO, 'return null;']),
        ('public boolean[][] hibridar(Muestra muestra)', [TODO, 'return null;']),
    ],
    'Analisis.java': [
        ('public Analisis(Muestra muestra, Microarray microarray)', [TODO]),
        ('public Muestra getMuestra()', [TODO, 'return null;']),
        ('public Microarray getMicroarray()', [TODO, 'return null;']),
        ('public LocalDate getFechaAnalisis()', [TODO, 'return null;']),
        ('public boolean[][] getIluminados()', [TODO, 'return null;']),
        ('public long getDiasDesdeCaptura()', [TODO, 'return 0;']),
        ('public int contarIluminados()', [TODO, 'return 0;']),
    ],
}

# Clases cuyos atributos deja de ser final la plantilla (sus constructores están vacíos)
SIN_FINAL = ['Sonda.java', 'Analisis.java']


def sustituir_cuerpo(lineas, cabecera, cuerpo):
    for i, linea in enumerate(lineas):
        if linea.strip().startswith(cabecera) and linea.rstrip().endswith('{'):
            sangria = linea[:len(linea) - len(linea.lstrip())] + '    '
            profundidad = 0
            for j in range(i, len(lineas)):
                profundidad += lineas[j].count('{') - lineas[j].count('}')
                if profundidad == 0:
                    return lineas[:i + 1] + [sangria + c for c in cuerpo] + lineas[j:]
    raise ValueError('No se encuentra el método: ' + cabecera)


def main():
    if os.path.exists(PLANTILLA):
        shutil.rmtree(PLANTILLA)
    shutil.copytree(SOLUCION, PLANTILLA)
    proyecto = os.path.join(PLANTILLA, '.project')
    with open(proyecto, encoding='utf-8') as f:
        texto = f.read()
    with open(proyecto, 'w', encoding='utf-8') as f:
        f.write(texto.replace('<name>Practica2-solucion</name>', '<name>Practica2</name>'))
    for fichero, metodos in METODOS.items():
        ruta = os.path.join(PLANTILLA, PAQUETE, fichero)
        with open(ruta, encoding='utf-8') as f:
            lineas = f.read().split('\n')
        for cabecera, cuerpo in metodos:
            lineas = sustituir_cuerpo(lineas, cabecera, cuerpo)
        texto = '\n'.join(lineas)
        if fichero in SIN_FINAL:
            texto = texto.replace('    private final ', '    private ')
        with open(ruta, 'w', encoding='utf-8') as f:
            f.write(texto)
    print('Plantilla derivada en', os.path.relpath(PLANTILLA, RAIZ))


if __name__ == '__main__':
    main()
