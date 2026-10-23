#!/bin/bash
set -e

printf '\n%s\n' "Creates PostgreSQL, MySQL and SQLite databases."
printf '%s\n' "To overwrite an existing SQLite file, pass any argument to this script."

# Pull up db containers
docker-compose up -d

# Create sqlite db
if [[ ! -e ./data/test.db || $# -gt 0 ]]; then
    sqlite3 ./data/test.db <<EOF
.mode csv
.import ./data/csv/users.csv users
EOF
fi

printf '\n%s\n' "You can now connect via the configuration files in the conf directory."
printf '%s\n' "Remember to run docker-compose down after you've finished testing."