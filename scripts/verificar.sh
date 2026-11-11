#!/usr/bin/env bash
# Verifica la plantilla y la solución de la P2 con un JDK 25 (o posterior):
#   - la plantilla está al día respecto a la solución (scripts/derivar-plantilla.py),
#   - ambas compilan sin avisos (javac -Xlint:all -Werror) y, si se indica ECJ_JAR, también
#     con el compilador de Eclipse y la configuración de .settings. La plantilla, a propósito,
#     no compila hasta que se añade el try/catch de Microarray.hibridar(Lectura): se comprueba
#     que da exactamente ese error, y el resto se verifica con una copia que ya lo tiene,
#   - en la solución, las clases de prueba no dan ningún ✗; en la plantilla se ejecutan
#     sin excepciones,
#   - el Javadoc no produce avisos (en la plantilla, sin el grupo «missing»),
#   - la documentación HTML de la plantilla y de la solución (carpetas doc) está al día
#     (scripts/generar-doc.sh),
#   - la solución supera también las comprobaciones del profesorado (docs/pruebas-profesorado),
#   - la solución con las ampliaciones (docs/material-adicional) compila y supera sus pruebas
#     y las de la versión base.
#
# Uso: [ECJ_JAR=/ruta/ecj-3.44.0.jar] scripts/verificar.sh   (usa javac/java/javadoc del PATH o de JAVA_HOME)
set -euo pipefail

RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
BIN="${JAVA_HOME:+$JAVA_HOME/bin/}"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

version=$("${BIN}javac" -version 2>&1 | grep -o 'javac [0-9]*' | grep -o '[0-9]*$')
if [ "${version:-0}" -lt 25 ]; then
    echo "Se necesita un JDK 25 o posterior (javac encontrado: ${version:-ninguno})" >&2
    exit 1
fi

fallos=0
PRUEBAS="PruebaSonda PruebaMicroarray"

echo "== plantilla derivada de la solución"
cp -r "$RAIZ/scripts" "$RAIZ/solucion" "$TMP/"
mkdir -p "$TMP/plantilla"
python3 "$TMP/scripts/derivar-plantilla.py" >/dev/null
if diff -r -q -x doc "$TMP/plantilla/fprg-practica2-2026" "$RAIZ/plantilla/fprg-practica2-2026" >/dev/null; then
    echo "   la plantilla está al día"
else
    echo "   ✗ la plantilla no está al día (ejecute scripts/derivar-plantilla.py)"; fallos=$((fallos + 1))
fi

# comprobar <nombre> <es_solucion> [<carpeta del proyecto>] [<pruebas adicionales>]
comprobar() {
    local nombre="$1" es_solucion="$2" proyecto="${3:-$RAIZ/$1/fprg-practica2-2026}" pruebas="$PRUEBAS ${4:-}"
    echo "== $nombre"
    local fuentes
    fuentes=$(find "$proyecto/src" "$proyecto/auxiliar" -name '*.java')
    # shellcheck disable=SC2086
    if ! "${BIN}javac" -Xlint:all -Werror -encoding UTF-8 -d "$TMP/clases-$nombre" $fuentes; then
        echo "   ✗ no compila sin avisos con javac"; fallos=$((fallos + 1)); return
    fi
    if [ -n "${ECJ_JAR:-}" ]; then
        local ecj
        ecj=$("${BIN}java" -jar "$ECJ_JAR" -25 -encoding UTF-8 -properties "$proyecto/.settings/org.eclipse.jdt.core.prefs" \
            -d "$TMP/ecj-$nombre" "$proyecto/src" "$proyecto/auxiliar" 2>&1 | grep -v JAVA_TOOL || true)
        if [ -n "$ecj" ]; then
            echo "   ✗ el compilador de Eclipse da avisos o errores:"; echo "$ecj" | tail -5; fallos=$((fallos + 1))
        else
            echo "   compila sin avisos con javac y con ECJ"
        fi
    fi
    for prueba in $pruebas; do
        local salida
        salida=$("${BIN}java" -Dstdout.encoding=UTF-8 -cp "$TMP/clases-$nombre" "es.upm.dit.fprg26.p2.$prueba" 2>&1) || {
            echo "   ✗ $prueba termina con una excepción"; echo "$salida" | tail -5; fallos=$((fallos + 1)); continue; }
        echo "   $prueba · $(echo "$salida" | grep '^Resumen')"
        if [ "$es_solucion" = si ] && echo "$salida" | grep -q '✗'; then
            echo "   ✗ la solución no supera todas las comprobaciones"; fallos=$((fallos + 1))
        fi
    done
    local doclint=all avisos
    [ "$es_solucion" = si ] || doclint=all,-missing
    # Las clases Prueba* tienen un main de instancia (void main(), como en la P1), que necesita el
    # constructor por defecto: se ignora el aviso de que ese constructor no tiene comentario.
    avisos=$("${BIN}javadoc" -quiet -package -Xdoclint:$doclint -encoding UTF-8 -d "$TMP/doc-$nombre" \
        -sourcepath "$proyecto/src:$proyecto/auxiliar" es.upm.dit.fprg26.p2 2>&1 \
        | grep -v 'use of default constructor' | grep -c ': warning:\|: error:' || true)
    echo "   Javadoc: $avisos avisos"
    [ "$avisos" -eq 0 ] || fallos=$((fallos + 1))
}

# Solución, con las comprobaciones del profesorado (docs/pruebas-profesorado), que no se entregan
SOLUCION="$TMP/solucion-profesorado/fprg-practica2-2026"
mkdir -p "$TMP/solucion-profesorado"
cp -r "$RAIZ/solucion/fprg-practica2-2026" "$SOLUCION"
cp "$RAIZ"/docs/pruebas-profesorado/*.java "$SOLUCION/src/es/upm/dit/fprg26/p2/"
comprobar solucion si "$SOLUCION" PruebaProfesorado

# La plantilla no compila hasta que el alumnado añade el try/catch de hibridar(Lectura)
echo "== plantilla, tal como se entrega"
errores=$("${BIN}javac" -encoding UTF-8 -d "$TMP/clases-plantilla-original" \
    $(find "$RAIZ/plantilla/fprg-practica2-2026/src" "$RAIZ/plantilla/fprg-practica2-2026/auxiliar" -name '*.java') 2>&1 \
    | grep 'error:' || true)
if [ "$(echo "$errores" | grep -c 'Microarray.java.*unreported exception')" -eq 1 ] && [ "$(echo "$errores" | grep -c 'error:')" -eq 1 ]; then
    echo "   no compila solo por la excepción sin capturar de hibridar(Lectura), como se espera"
else
    echo "   ✗ se esperaba un único error (la excepción sin capturar de hibridar(Lectura)):"; echo "$errores"
    fallos=$((fallos + 1))
fi
COMPLETADA="$TMP/plantilla-completada/fprg-practica2-2026"
mkdir -p "$TMP/plantilla-completada"
cp -r "$RAIZ/plantilla/fprg-practica2-2026" "$COMPLETADA"
python3 - "$RAIZ/solucion/fprg-practica2-2026/src/es/upm/dit/fprg26/p2/Microarray.java" \
    "$COMPLETADA/src/es/upm/dit/fprg26/p2/Microarray.java" <<'FIN'
import sys
def metodo(texto):
    i = texto.index('    boolean[][] hibridar(Lectura lectura) {')
    return i, texto.index('\n    }\n', i) + 7
solucion = open(sys.argv[1], encoding='utf-8').read()
plantilla = open(sys.argv[2], encoding='utf-8').read()
a, b = metodo(solucion)
c, d = metodo(plantilla)
open(sys.argv[2], 'w', encoding='utf-8').write(plantilla[:c] + solucion[a:b] + plantilla[d:])
FIN
comprobar "plantilla (con el try/catch de hibridar(Lectura))" no "$COMPLETADA"

# Solución con las ampliaciones: la de referencia, con las clases ampliadas y sus pruebas
AMPLIADA="$TMP/ampliada/fprg-practica2-2026"
mkdir -p "$TMP/ampliada"
cp -r "$RAIZ/solucion/fprg-practica2-2026" "$AMPLIADA"
cp "$RAIZ"/docs/material-adicional/solucion/*.java "$RAIZ/docs/material-adicional/PruebaAmpliaciones.java" \
    "$AMPLIADA/src/es/upm/dit/fprg26/p2/"
cp "$RAIZ"/docs/pruebas-profesorado/*.java "$AMPLIADA/src/es/upm/dit/fprg26/p2/"
comprobar "solucion con ampliaciones" si "$AMPLIADA" "PruebaAmpliaciones PruebaProfesorado"

echo "== doc"
for proyecto in plantilla solucion; do
    if "$RAIZ/scripts/generar-doc.sh" "$proyecto" "$TMP/doc-$proyecto-generada" >/dev/null 2>&1 \
            && diff -r -q -I 'Generated by javadoc' "$TMP/doc-$proyecto-generada" "$RAIZ/$proyecto/fprg-practica2-2026/doc" >/dev/null; then
        echo "   la carpeta doc de $proyecto está al día"
    else
        echo "   ✗ la carpeta doc de $proyecto no está al día (ejecute scripts/generar-doc.sh)"; fallos=$((fallos + 1))
    fi
done

if [ "$fallos" -eq 0 ]; then echo "OK"; else echo "$fallos problema(s)"; exit 1; fi
