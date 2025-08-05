package main.algorithms;

import main.model.Network;
import main.model.Node;
import main.model.Connection;
import main.model.NodoArbol;

import java.util.*;

/**
 * Algoritmo para encontrar todas las rutas posibles desde un origen hasta un destino
 * y construir un árbol de rutas
 */
public class RouteTreeFinder {
    private Network network;
    private Set<Integer> visited;
    private List<List<Integer>> allPaths;
    private int maxDepth;
    
    public RouteTreeFinder(Network network) {
        this.network = network;
        this.maxDepth = 10; // Evitar ciclos infinitos
    }
    
    /**
     * Encuentra todas las rutas posibles desde origen hasta destino
     */
    public List<List<Integer>> findAllPaths(int sourceId, int targetId) {
        visited = new HashSet<>();
        allPaths = new ArrayList<>();
        
        // Verificar que los nodos existen y están activos
        Node source = network.getNode(sourceId);
        Node target = network.getNode(targetId);
        
        if (source == null || target == null) {
            return allPaths;
        }
        
        if (!source.isActive() || !target.isActive()) {
            return allPaths;
        }
        
        // Buscar todas las rutas usando DFS
        List<Integer> currentPath = new ArrayList<>();
        currentPath.add(sourceId);
        visited.add(sourceId);
        
        dfsFindPaths(sourceId, targetId, currentPath, 0);
        
        return allPaths;
    }
    
    /**
     * Algoritmo DFS para encontrar todas las rutas
     */
    private void dfsFindPaths(int currentId, int targetId, List<Integer> currentPath, int depth) {
        // Verificar límite de profundidad para evitar ciclos infinitos
        if (depth > maxDepth) {
            return;
        }
        
        // Si llegamos al destino, agregar la ruta
        if (currentId == targetId) {
            allPaths.add(new ArrayList<>(currentPath));
            return;
        }
        
        // Obtener todas las conexiones activas desde el nodo actual
        List<Connection> connections = network.getConnectionsFrom(currentId);
        
        for (Connection conn : connections) {
            int nextId = conn.getToNode().getId();
            
            // Verificar que el nodo destino esté activo
            if (!conn.getToNode().isActive()) {
                continue;
            }
            
            // Evitar ciclos (no visitar el mismo nodo dos veces en la misma ruta)
            if (!visited.contains(nextId)) {
                visited.add(nextId);
                currentPath.add(nextId);
                
                dfsFindPaths(nextId, targetId, currentPath, depth + 1);
                
                // Backtracking
                currentPath.remove(currentPath.size() - 1);
                visited.remove(nextId);
            }
        }
    }
    
    /**
     * Construye un árbol de rutas desde las rutas encontradas
     */
    public NodoArbol buildRouteTree(int sourceId, int targetId) {
        List<List<Integer>> paths = findAllPaths(sourceId, targetId);
        
        if (paths.isEmpty()) {
            return null; // No hay rutas
        }
        
        Node sourceNode = network.getNode(sourceId);
        NodoArbol root = new NodoArbol(sourceNode, 0); // La raíz no tiene latencia
        
        // Construir el árbol a partir de todas las rutas
        for (List<Integer> path : paths) {
            addPathToTree(root, path);
        }
        
        return root;
    }
    
    /**
     * Agrega una ruta al árbol
     */
    private void addPathToTree(NodoArbol root, List<Integer> path) {
        NodoArbol current = root;
        
        for (int i = 1; i < path.size(); i++) {
            int nodeId = path.get(i);
            Node node = network.getNode(nodeId);
            
            // Buscar si ya existe un hijo con este nodo
            NodoArbol existingChild = findChildByNodeId(current, nodeId);
            
            if (existingChild != null) {
                // El nodo ya existe en este nivel, continuar
                current = existingChild;
            } else {
                // Crear nuevo nodo hijo
                int latency = getLatencyBetweenNodes(path.get(i-1), nodeId);
                NodoArbol newChild = new NodoArbol(node, latency);
                current.addChild(newChild);
                current = newChild;
            }
        }
    }
    
    /**
     * Busca un hijo por ID de nodo
     */
    private NodoArbol findChildByNodeId(NodoArbol parent, int nodeId) {
        for (NodoArbol child : parent.getChildren()) {
            if (child.getNode().getId() == nodeId) {
                return child;
            }
        }
        return null;
    }
    
    /**
     * Obtiene la latencia entre dos nodos
     */
    private int getLatencyBetweenNodes(int fromId, int toId) {
        Connection conn = network.getConnection(fromId, toId);
        return conn != null ? conn.getLatency() : 0;
    }
    
    /**
     * Encuentra todas las rutas y las ordena por latencia total
     */
    public List<List<Integer>> findAllPathsSorted(int sourceId, int targetId) {
        List<List<Integer>> paths = findAllPaths(sourceId, targetId);
        
        // Ordenar por latencia total
        paths.sort((path1, path2) -> {
            int latency1 = calculateTotalLatency(path1);
            int latency2 = calculateTotalLatency(path2);
            return Integer.compare(latency1, latency2);
        });
        
        return paths;
    }
    
    /**
     * Calcula la latencia total de una ruta
     */
    private int calculateTotalLatency(List<Integer> path) {
        int total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Connection conn = network.getConnection(path.get(i), path.get(i + 1));
            if (conn != null) {
                total += conn.getLatency();
            }
        }
        return total;
    }
    
    /**
     * Obtiene estadísticas de las rutas encontradas
     */
    public String getRouteStatistics(int sourceId, int targetId) {
        List<List<Integer>> paths = findAllPaths(sourceId, targetId);
        
        if (paths.isEmpty()) {
            return "No se encontraron rutas entre los nodos especificados.";
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== ESTADÍSTICAS DE RUTAS ===\n\n");
        stats.append("Número total de rutas: ").append(paths.size()).append("\n\n");
        
        // Calcular estadísticas
        int minLatency = Integer.MAX_VALUE;
        int maxLatency = 0;
        int totalLatency = 0;
        int minHops = Integer.MAX_VALUE;
        int maxHops = 0;
        
        for (List<Integer> path : paths) {
            int latency = calculateTotalLatency(path);
            int hops = path.size() - 1;
            
            minLatency = Math.min(minLatency, latency);
            maxLatency = Math.max(maxLatency, latency);
            totalLatency += latency;
            minHops = Math.min(minHops, hops);
            maxHops = Math.max(maxHops, hops);
        }
        
        double avgLatency = (double) totalLatency / paths.size();
        
        stats.append("Latencia mínima: ").append(minLatency).append("ms\n");
        stats.append("Latencia máxima: ").append(maxLatency).append("ms\n");
        stats.append("Latencia promedio: ").append(String.format("%.1f", avgLatency)).append("ms\n");
        stats.append("Saltos mínimos: ").append(minHops).append("\n");
        stats.append("Saltos máximos: ").append(maxHops).append("\n");
        
        return stats.toString();
    }
    
    /**
     * Establece el límite máximo de profundidad para evitar ciclos infinitos
     */
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
} 