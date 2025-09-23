#!/bin/bash

# Navigate to docker directory
echo "Navigating to docker directory..."
cd docker

# Build and start
echo "Building and starting The-Cat-API-Project..."
docker-compose up --build -d

echo "Waiting for service to start..."
sleep 30

# Check if service is running
echo "Checking service status..."
docker-compose ps

echo "The Cat API Project is running!"
echo "API: http://localhost:8080"