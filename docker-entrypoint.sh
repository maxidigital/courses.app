#!/bin/sh
set -e

# Write Google credentials JSON from env var
echo "$GOOGLE_CREDENTIALS_JSON" > /app/credentials.json

# Write config.properties from env var (full content)
echo "$CONFIG_PROPERTIES" > /app/config.properties

exec java -jar app.jar
