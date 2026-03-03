#!/bin/bash
curl -X POST http://127.0.0.1:9090/store/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "label": "sports tennis",
    "categoryTypeId": "MALE"
  }'