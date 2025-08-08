#!/bin/bash
set -e

AXWAY_DIR="${AXWAY_HOME:-/opt/Axway}"
JAR_FILE=$(ls -1 build/libs/*.jar 2>/dev/null | head -1)
EXT_LIB_DIR="$AXWAY_DIR/apigateway/groups/group-2/instance-1/ext/lib"

echo "=== Policy Loop Filter Installation (Linux) ==="
echo "Axway dir: $AXWAY_DIR"
echo "Jar: ${JAR_FILE:-<not found>}"

if [ -z "$JAR_FILE" ] || [ ! -f "$JAR_FILE" ]; then
  echo "JAR não encontrado. Rode ./gradlew build"; exit 1; fi

if [ ! -d "$AXWAY_DIR" ]; then
  echo "Diretório Axway não encontrado: $AXWAY_DIR"; exit 1; fi

mkdir -p "$EXT_LIB_DIR"
cp "$JAR_FILE" "$EXT_LIB_DIR/"
echo "✅ Copiado para $EXT_LIB_DIR"

