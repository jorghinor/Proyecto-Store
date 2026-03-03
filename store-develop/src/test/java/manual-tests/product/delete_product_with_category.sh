#!/bin/bash

# This script tests that a product with a category cannot be deleted.
# Manual cleanup of the created product and category is required after running this script.

# Attempt to delete the product (should fail)
curl -i -X DELETE http://127.0.0.1:9090/store/api/products/1