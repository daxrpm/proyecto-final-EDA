# 🖥️ Simulador de Red de Computadoras con Rutas de Paquetes

Un simulador completo de redes de computadoras que implementa el algoritmo Floyd-Warshall para encontrar las rutas más cortas entre dispositivos, con una interfaz gráfica moderna y simulación visual avanzada.

## 🚀 Características Principales

### 📊 **Gestión de Red Intuitiva**
- **Modo Cisco Packet Tracer**: Click para agregar nodos y conexiones
- **Drag & Drop**: Mover nodos arrastrándolos
- **Eliminación fácil**: Botones para eliminar nodos y conexiones
- **Sin mensajes molestos**: Interfaz limpia sin confirmaciones excesivas

### 🧮 **Algoritmo Floyd-Warshall**
- **Cálculo automático** de rutas más cortas
- **Ventana dedicada** para matrices con tablas organizadas y colores
- **Matriz de distancias** con latencias mínimas
- **Matriz de siguiente nodo** para reconstruir rutas

### 🎮 **Simulación Visual Avanzada**
- **Carta/Paquete animado**: Se mueve suavemente siguiendo la ruta
- **Ruta punteada**: Muestra el camino completo con flechas
- **Animación fluida**: Movimiento suave entre nodos
- **Log detallado**: Información paso a paso de la simulación

### 📁 **Importar/Exportar CSV**
- **Archivos CSV** para nodos y conexiones
- **Plantillas de ejemplo** incluidas
- **Validación** de datos importados

### 🎨 **Interfaz Moderna**
- **FlatLaf** para diseño moderno
- **Flechas claras** que muestran dirección del grafo
- **Colores diferenciados** por tipo de dispositivo

## 🛠️ Instalación y Uso

### Requisitos
- Java 8 o superior
- Sistema operativo: Windows, macOS, Linux

### Ejecución
```bash
# Dar permisos de ejecución
chmod +x run.sh

# Ejecutar la aplicación
./run.sh
```

## 📋 Guía de Uso

### 1. **Crear una Red**
- **Agregar Nodos**: Selecciona el tipo y haz click donde quieras colocarlo
- **Crear Conexiones**: Selecciona "Agregar Conexión" y haz click en origen y destino
- **Configurar Latencia**: Establece la latencia en milisegundos

### 2. **Calcular y Ver Rutas**
- Presiona "Calcular Rutas" para ejecutar Floyd-Warshall
- Usa "Ver Matrices" para ver las tablas organizadas

### 3. **Simular Paquetes**
- Selecciona "Simular Paquete"
- Elige origen y destino
- **Observa la carta animada** siguiendo la ruta más corta
- Revisa el log detallado de cada paso

### 4. **Gestionar la Red**
- **Importar**: Carga redes desde archivos CSV
- **Exportar**: Guarda el estado actual en CSV
- **Simular Fallas**: Desactiva nodos para ver cambios
- **Limpiar**: Reinicia la red completamente

## 🎯 **Simulación Visual Mejorada**

La simulación ahora incluye:
- **Carta/Paquete realista**: Dibujado como un sobre con sello
- **Movimiento suave**: Animación fluida entre nodos
- **Ruta visual**: Línea punteada con flechas que muestra el camino
- **Efectos visuales**: Sombra y brillo en el paquete

## 🏗️ Estructura del Proyecto

```
src/
├── main/
│   ├── gui/                    # Interfaz gráfica
│   │   ├── MainWindow.java     # Ventana principal
│   │   ├── NetworkPanel.java   # Visualización de red
│   │   ├── ControlPanel.java   # Panel de controles
│   │   ├── InfoPanel.java      # Información y rutas
│   │   ├── MatricesWindow.java # Ventana de matrices
│   │   └── PacketSimulationWindow.java # Simulación de paquetes
│   ├── model/                  # Modelo de datos
│   │   ├── Network.java        # Grafo principal
│   │   ├── Node.java           # Nodo/dispositivo
│   │   └── Connection.java     # Conexión/arista
│   ├── algorithms/             # Algoritmos
│   │   └── FloydWarshall.java  # Algoritmo de rutas
│   └── utils/                  # Utilidades
│       └── CSVManager.java     # Importar/exportar CSV
└── App.java                    # Punto de entrada
```

## 📊 Formato de Archivos CSV

### nodes.csv
```csv
id,name,type,x,y,status
1,Router_Central,ROUTER,100,100,ACTIVE
2,PC_Oficina1,PC,200,150,ACTIVE
```

### connections.csv
```csv
from_id,to_id,latency,status
1,2,15,ACTIVE
2,3,10,ACTIVE
```

## 🔧 Tecnologías Utilizadas

- **Java Swing**: Interfaz gráfica
- **FlatLaf**: Tema moderno
- **Algoritmo Floyd-Warshall**: Cálculo de rutas más cortas
- **Grafos dirigidos ponderados**: Modelo de red
- **Animación 2D**: Movimiento suave de paquetes

## 📝 Notas del Proyecto

Este proyecto demuestra la aplicación práctica de estructuras de datos (grafos) y algoritmos (Floyd-Warshall) en un contexto real de redes de computadoras. La implementación incluye:

- **Grafos dirigidos** para representar conexiones unidireccionales
- **Ponderación** con latencias reales
- **Algoritmo eficiente** para rutas más cortas
- **Interfaz visual** para mejor comprensión
- **Simulación realista** como herramientas profesionales

## 🚀 Próximas Mejoras

- [ ] Múltiples tipos de paquetes
- [ ] Estadísticas de tráfico
- [ ] Exportación a formatos adicionales
- [ ] Simulación de congestión de red
- [ ] Animaciones más complejas # proyecto-final-EDA
