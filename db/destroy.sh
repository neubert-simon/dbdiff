#!/bin/bash
set -e

# Pull down db containers and delete volumes
docker-compose down -v

if [ -e ./data/test.db ]; then
    rm ./data/test.db
fi