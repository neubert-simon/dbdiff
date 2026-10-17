#!/bin/bash
set -e

printf '\n%s\n' "Creates PostgreSQL, MySQL and SQLite (if the file doesn't already exist) databases."

# Pull up db containers
docker-compose up -d

# Create sqlite db
if [ ! -f ./data/test.db ]; then
    mkdir -p ./data
    sqlite3 ./data/test.db <<EOF
.mode csv
.import --skip 1 ./data/csv/users.csv users
EOF
fi

printf '\n%s\n' "You can now connect via the configuration files in the conf directory."
printf '%s\n' "Remember to run docker-compose down after you've finished testing."