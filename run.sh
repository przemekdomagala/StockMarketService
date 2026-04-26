#!/bin/sh

PORT=${1:-8080}

echo "Building Docker image..."
docker build -t stock-market-service .

echo "Starting Stock Exchange Service on localhost:$PORT..."
docker run -p "$PORT":"$PORT" -e SERVER_PORT="$PORT" --rm stock-market-service