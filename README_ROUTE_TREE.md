# Árbol de Rutas - Simulador de Red de Computadoras

## 🌳 Nueva Funcionalidad: Árbol de Rutas

Esta funcionalidad permite encontrar **TODAS las rutas posibles** desde un nodo origen hasta un nodo destino en la red, construyendo un árbol jerárquico que visualiza todas las alternativas de conexión.

## 🎯 Características Principales

### 1. **Búsqueda Completa de Rutas**
- Usa algoritmo **DFS (Depth-First Search)** para encontrar todas las rutas posibles
- No solo la ruta más corta, sino **todas las alternativas**
- Evita ciclos infinitos con límite de profundidad configurable

### 2. **Visualización Jerárquica**
- **Formato de texto**: Muestra el árbol con caracteres ASCII
- **Árbol visual**: Interfaz gráfica con JTree expandible
- **Estadísticas**: Análisis detallado de todas las rutas encontradas

### 3. **Información Detallada**
- Latencia de cada conexión individual
- Latencia total de cada ruta completa
- Número de saltos por ruta
- Estadísticas comparativas entre rutas

## 🚀 Cómo Usar

### Paso 1: Preparar la Red
1. Cargar nodos y conexiones desde archivos CSV
2. O crear la red manualmente usando la interfaz

### Paso 2: Abrir Árbol de Rutas
1. Hacer clic en **"Árbol de Rutas"** en el panel de control
2. Se abrirá una nueva ventana con la interfaz

### Paso 3: Configurar Búsqueda
1. Seleccionar **Nodo Origen** (ej: PC1)
2. Seleccionar **Nodo Destino** (ej: ServidorX)
3. Hacer clic en **"Encontrar Todas las Rutas"**

### Paso 4: Analizar Resultados
- **Pestaña "Árbol de Rutas (Texto)"**: Vista jerárquica en texto
- **Pestaña "Árbol Visual"**: Interfaz gráfica expandible
- **Pestaña "Estadísticas"**: Análisis comparativo de rutas

## 📊 Ejemplo de Salida

```
Árbol de rutas desde PC1 hasta ServidorX:

PC1
 └── Switch1 (5ms)
      ├── RouterA (10ms)
      │     └── RouterB (8ms)
      │           └── ServidorX (12ms) [Total: 35ms]
      └── ServidorX (30ms) [Total: 35ms]
```

## 🔧 Archivos de Ejemplo

### Archivo Complejo de Nodos (`example_complex_nodes.csv`)
```
id,name,type,x,y,status
1,PC1,PC,100,100,ACTIVE
2,Switch1,SWITCH,200,100,ACTIVE
3,RouterA,ROUTER,300,100,ACTIVE
4,RouterB,ROUTER,400,100,ACTIVE
5,ServidorX,PC,500,100,ACTIVE
...
```

### Archivo Complejo de Conexiones (`example_complex_connections.csv`)
```
from_id,to_id,latency,status
1,2,5,ACTIVE
2,3,10,ACTIVE
3,4,8,ACTIVE
4,5,12,ACTIVE
...
```

## 📈 Estadísticas Generadas

- **Número total de rutas** encontradas
- **Latencia mínima** y **máxima**
- **Latencia promedio** de todas las rutas
- **Número de saltos** mínimo y máximo
- **Comparación** entre diferentes rutas

## 🎨 Interfaz de Usuario

### Ventana Principal
- **Selector de origen y destino**: ComboBox con nodos activos
- **Botones de acción**: Encontrar rutas y mostrar estadísticas
- **Indicador de estado**: Muestra el progreso y estado actual

### Pestañas de Visualización
1. **Árbol de Rutas (Texto)**: Vista jerárquica con caracteres ASCII
2. **Árbol Visual**: Interfaz gráfica con JTree expandible
3. **Estadísticas**: Análisis detallado y comparativo

## 🔍 Algoritmo Implementado

### DFS (Depth-First Search)
```java
private void dfsFindPaths(int currentId, int targetId, List<Integer> currentPath, int depth) {
    // Verificar límite de profundidad
    if (depth > maxDepth) return;
    
    // Si llegamos al destino, agregar la ruta
    if (currentId == targetId) {
        allPaths.add(new ArrayList<>(currentPath));
        return;
    }
    
    // Explorar todas las conexiones activas
    for (Connection conn : network.getConnectionsFrom(currentId)) {
        // Evitar ciclos y continuar búsqueda
    }
}
```

### Construcción del Árbol
- **Nodo raíz**: Nodo origen
- **Nodos hijos**: Conexiones directas
- **Hojas**: Nodos destino alcanzados
- **Información**: Latencia y total acumulado

## 🛡️ Características de Seguridad

- **Límite de profundidad**: Evita ciclos infinitos
- **Validación de nodos**: Solo nodos activos
- **Manejo de errores**: Mensajes claros para el usuario
- **Interfaz responsiva**: Cursor de espera durante procesamiento

## 📋 Requisitos

- Mínimo 2 nodos activos en la red
- Conexiones válidas entre nodos
- Suficiente memoria para procesar rutas complejas

## 🎯 Casos de Uso

1. **Análisis de Red**: Ver todas las alternativas de conexión
2. **Planificación**: Evaluar diferentes rutas de comunicación
3. **Troubleshooting**: Identificar puntos de falla
4. **Optimización**: Comparar latencias de diferentes rutas
5. **Educación**: Entender conceptos de routing y árboles

## 🔄 Integración con el Proyecto

Esta funcionalidad se integra perfectamente con:
- **Simulación de fallas**: Ver cómo cambian las rutas disponibles
- **Algoritmo Floyd-Warshall**: Comparar con rutas más cortas
- **Visualización de red**: Complementar la vista gráfica
- **Análisis de conectividad**: Evaluar robustez de la red 