@echo off
title Lanzador AdoptaMe App

cd /d "%~dp0"

echo [1/2] Arrancando Motor (Backend) en ventana separada...
start "Backend AdoptaMe" /min java -jar "backend-adoptame\target\backend.jar"

echo Esperando 25 segundos a que el motor caliente...
timeout /t 25

echo [2/2] Iniciando Interfaz (Frontend)...
javaw -jar "frontend-adoptame\target\frontend.jar"

echo.
echo Si la app no se ha abierto, revisa los errores arriba.
pause