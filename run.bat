@echo off
set PORT=%1
if "%PORT%"=="" set PORT=8080

set APP_PORT=%PORT%

echo Starting High-Availability Stock Exchange on localhost:%PORT%...

docker compose up --build -d

echo.
echo Service is booting up.
echo - API will be available at: http://localhost:%PORT%
echo - To view logs, run: docker compose logs -f
echo - To stop everything, run: docker compose down