#!/bin/sh

# Substituir variáveis de ambiente nos arquivos JavaScript
# A URL da API pode ser configurada via variável de ambiente
API_URL=${API_URL:-http://localhost:8080}

echo "Configurando API_URL para: $API_URL"

# Encontrar e substituir em todos os arquivos .js do build
find /usr/share/nginx/html -type f -name "*.js" -exec sed -i "s|http://localhost:8080|$API_URL|g" {} \;

# Executar o comando original do Nginx
exec "$@"
