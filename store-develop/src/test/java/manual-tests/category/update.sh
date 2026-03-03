#!/bin/bash
curl -X PUT http://127.0.0.1:9090/store/api/categories/2 \
  -H "Content-Type: application/json" \
  -d '{
    "label": "knee pads"
  }'
# Uso: ./update.sh <id>