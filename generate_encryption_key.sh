#!/usr/bin/env bash
set -euo pipefail

ENV_FILE=".env"
if [ ! -f "$ENV_FILE" ]; then
  echo "${ENV_FILE} no existe. Creando..."
  touch "$ENV_FILE"
fi

if ! command -v openssl >/dev/null 2>&1; then
  echo "Error: openssl no está instalado o no está en PATH." >&2
  exit 1
fi

KEY=$(openssl rand -base64 32)

# Reemplaza o añade la variable ENCRYPTION_KEY de forma segura
awk -v key="$KEY" 'BEGIN{found=0}
  /^ENCRYPTION_KEY=/ {print "ENCRYPTION_KEY=" key; found=1; next}
  {print}
  END{if(!found) print "ENCRYPTION_KEY=" key}' "$ENV_FILE" > "$ENV_FILE.tmp" && mv "$ENV_FILE.tmp" "$ENV_FILE"

# Mensaje resumido sin exponer la clave completa
echo "ENCRYPTION_KEY actualizada en $ENV_FILE"
echo "Clave (primeros 16 chars): ${KEY:0:16}..."
