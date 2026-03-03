#!/bin/bash

# This script tests that a category used in a product cannot be deleted.
# Manual cleanup of the created product and category is required after running this script.

curl -i -X DELETE http://127.0.0.1:9090/store/api/categories/2