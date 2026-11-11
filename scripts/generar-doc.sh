#!/usr/bin/env bash
# Genera la documentación HTML (Javadoc) que se entrega con cada proyecto, en
# plantilla/fprg-practica2-2026/doc y solucion/fprg-practica2-2026/doc, con un JDK 25.
#
# Documenta el paquete es.upm.dit.fprg26.p2 de cada proyecto (carpetas src y auxiliar),
# incluidos los miembros sin modificador de acceso (-package). Hay que volver a ejecutarlo
# cada vez que cambie el Javadoc (por ejemplo, después de scripts/derivar-plantilla.py);
# scripts/verificar.sh comprueba que las dos carpetas doc están al día.
#
# Uso: scripts/generar-doc.sh                        (genera las dos)
#      scripts/generar-doc.sh plantilla|solucion DIR  (genera la de un proyecto en DIR)
set -euo pipefail

RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
BIN="${JAVA_HOME:+$JAVA_HOME/bin/}"

generar() {
    local proyecto="$RAIZ/$1/fprg-practica2-2026" destino="$2"
    # Solo se borra una carpeta de Javadoc anterior (con su element-list), nunca otra cosa
    if [ -e "$destino" ] && [ ! -f "$destino/element-list" ]; then
        echo "$destino existe y no es una carpeta de Javadoc: no se borra" >&2
        exit 1
    fi
    rm -rf "$destino"
    "${BIN}javadoc" -quiet -notimestamp --no-fonts -Xdoclint:all,-missing \
        -encoding UTF-8 -docencoding UTF-8 -charset UTF-8 \
        -package -sourcepath "$proyecto/src:$proyecto/auxiliar" \
        -d "$destino" \
        -doctitle "FPRG 2026-27 · Práctica 2" -windowtitle "FPRG 2026-27 · Práctica 2" \
        es.upm.dit.fprg26.p2
}

if [ $# -eq 2 ] && { [ "$1" = plantilla ] || [ "$1" = solucion ]; }; then
    generar "$1" "$2"
elif [ $# -eq 0 ]; then
    generar plantilla "$RAIZ/plantilla/fprg-practica2-2026/doc"
    generar solucion "$RAIZ/solucion/fprg-practica2-2026/doc"
else
    echo "Uso: $0  |  $0 plantilla|solucion DIR" >&2
    exit 1
fi
