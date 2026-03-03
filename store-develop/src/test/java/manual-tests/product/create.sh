#!/bin/bash
curl -X POST http://127.0.0.1:9090/store/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "sports tennis"
  }'
