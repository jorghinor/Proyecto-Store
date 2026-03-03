#!/bin/bash
curl -X POST http://127.0.0.1:9090/store/api/products/1/categories \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 2
  }'