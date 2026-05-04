#!/bin/sh

PORT=${1:-8080}

echo "Starting High-Availabiltiy Stock Exchange on localhost:$PORT..."
APP_PORT=$PORT docker compose up --build -d

echo "Service is booting up. Check status with 'docker compose ps'"