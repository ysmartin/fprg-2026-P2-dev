#!/usr/bin/env bash
# Genera la animación del algoritmo de la P2 (por omisión, animacion/hibridacion.gif) con un
# JDK 25 o posterior. Compila las clases de animacion/src junto con las de la solución.
#
# Uso: [JAVA_HOME=...] animacion/generar-animacion.sh [fichero.gif] [--escala 1.5] [--png carpeta]
#   --escala  tamaño de las imágenes respecto a 1280x720 (1.5 da 1920x1080)
#   --png     guarda además cada fotograma como PNG, con su duración en duraciones.txt
set -euo pipefail

ANIMACION="$(cd "$(dirname "$0")" && pwd)"
RAIZ="$(dirname "$ANIMACION")"
BIN="${JAVA_HOME:+$JAVA_HOME/bin/}"

version=$("${BIN}javac" -version 2>&1 | grep -o 'javac [0-9]*' | grep -o '[0-9]*$' || true)
if [ "${version:-0}" -lt 25 ]; then
    echo "Se necesita un JDK 25 o posterior (javac encontrado: ${version:-ninguno})" >&2
    exit 1
fi

# Si no se indica el fichero GIF, se genera animacion/hibridacion.gif
argumentos=("$@")
hay_gif=no
i=0
while [ "$i" -lt "${#argumentos[@]}" ]; do
    case "${argumentos[$i]}" in
        --escala|--png) i=$((i + 2)) ;;
        *) hay_gif=si; i=$((i + 1)) ;;
    esac
done
[ "$hay_gif" = si ] || argumentos=("$ANIMACION/hibridacion.gif" ${argumentos[@]+"${argumentos[@]}"})

CLASES="$(mktemp -d)"
trap 'rm -rf "$CLASES"' EXIT
# Se compila desde la raíz del repositorio, con rutas relativas (sin espacios)
# shellcheck disable=SC2046
(cd "$RAIZ" && "${BIN}javac" -Xlint:all -Werror -encoding UTF-8 -d "$CLASES" \
    $(find animacion/src solucion/fprg-practica2-2026/src solucion/fprg-practica2-2026/auxiliar -name '*.java'))
"${BIN}java" -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp "$CLASES" \
    es.upm.dit.fprg26.p2.AnimacionHibridacion ${argumentos[@]+"${argumentos[@]}"}
