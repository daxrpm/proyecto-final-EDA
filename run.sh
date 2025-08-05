#!/bin/bash

echo "🔄 Compilando proyecto..."

# Crear directorio de clases si no existe
mkdir -p classes

# Compilar todas las clases Java
javac -cp "lib/*" -d classes src/main/gui/*.java src/main/model/*.java src/main/algorithms/*.java src/main/utils/*.java src/App.java

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
    echo "🚀 Ejecutando aplicación..."
    java -cp "lib/*:classes" App
else
    echo "❌ Error en la compilación"
    exit 1
fi 