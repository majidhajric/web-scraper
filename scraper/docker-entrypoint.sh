#!/bin/bash

# Set database config from Heroku DATABASE_URL
if [[ "$DATABASE_URL" != "" ]]; then
    echo "Found database configuration in DATABASE_URL=$DATABASE_URL"

    regex='^postgres://([a-zA-Z0-9_-]+):([a-zA-Z0-9]+)@([a-z0-9.-]+):([[:digit:]]+)/([a-zA-Z0-9_-]+)$'
    if [[ ${DATABASE_URL} =~ $regex ]]; then
        export DB_ADDR=${BASH_REMATCH[3]}
        export DB_PORT=${BASH_REMATCH[4]}
        export DB_DATABASE=${BASH_REMATCH[5]}
        export DB_USER=${BASH_REMATCH[1]}
        export DB_PASSWORD=${BASH_REMATCH[2]}

        echo "DB_ADDR=$DB_ADDR, DB_PORT=$DB_PORT, DB_DATABASE=$DB_DATABASE, DB_USER=$DB_USER, DB_PASSWORD=$DB_PASSWORD"
        export DB_VENDOR=postgres
        export SPRING_DATASOURCE_URL="jdbc:postgresql://$DB_ADDR:$DB_PORT/$DB_DATABASE?sslmode=require"
        export SPRING_DATASOURCE_USERNAME="$DB_USER"
        export SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD"
    fi

fi

env
exec java -jar /opt/spring/app.jar
