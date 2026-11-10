#!/usr/bin/env bash
# Genera la documentación HTML (Javadoc) que se entrega con la plantilla, en
# plantilla/fprg-practica2-2026/doc (o en la carpeta que se indique), con un JDK 25.
#
# Documenta el paquete es.upm.dit.fprg26.p2 de la plantilla (carpetas src y auxiliar),
# incluidos los miembros sin modificador de acceso (-package). Hay que volver a ejecutarlo cada vez que cambie el Javadoc de la plantilla
# (por ejemplo, después de scripts/derivar-plantilla.py); scripts/verificar.sh comprueba
# que la carpeta doc está al día.
#
# Uso: scripts/generar-doc.sh [carpeta_destino]
set -euo pipefail

RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
BIN="${JAVA_HOME:+$JAVA_HOME/bin/}"
DESTINO="${1:-$RAIZ/plantilla/fprg-practica2-2026/doc}"

rm -rf "$DESTINO"
"${BIN}javadoc" -quiet -notimestamp --no-fonts -Xdoclint:all,-missing \
    -encoding UTF-8 -docencoding UTF-8 -charset UTF-8 \
    -package -sourcepath "$RAIZ/plantilla/fprg-practica2-2026/src:$RAIZ/plantilla/fprg-practica2-2026/auxiliar" \
    -d "$DESTINO" \
    -doctitle "FPRG 2026-27 · Práctica 2" -windowtitle "FPRG 2026-27 · Práctica 2" \
    es.upm.dit.fprg26.p2
