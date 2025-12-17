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

# Generar 64 bytes y base64 (suficiente entropía para HS384/HS512)
SECRET=$(openssl rand -base64 64)

# Reemplaza o añade la variable JWT_SECRET de forma segura
awk -v secret="$SECRET" 'BEGIN{found=0}
  /^JWT_SECRET=/ {print "JWT_SECRET=" secret; found=1; next}
  {print}
  END{if(!found) print "JWT_SECRET=" secret}' "$ENV_FILE" > "$ENV_FILE.tmp" && mv "$ENV_FILE.tmp" "$ENV_FILE"

# Mensaje resumido sin exponer el secreto completo
echo "JWT_SECRET actualizada en $ENV_FILE"
echo "Secret (primeros 16 chars): ${SECRET:0:16}..."
