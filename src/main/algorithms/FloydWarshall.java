package main.algorithms;

import main.model.Network;
import main.model.Node;
import main.model.Connection;

import java.util.*;

/**
 * Implementación del algoritmo Floyd-Warshall para encontrar rutas más cortas
 */
public class FloydWarshall {
    private Network network;
    private double[][] distances;
    private int[][] next;
    private Map<Integer, Integer> nodeToIndex;
    private Map<Integer, Integer> indexToNode;
    
    public FloydWarshall(Network network) {
        this.network = network;
        initialize();
    }
    
    /**
     * Inicializa las matrices para el algoritmo
     */
    private void initialize() {
        List<Node> activeNodes = network.getActiveNodes();
        int n = activeNodes.size();
        
        // Crear mapeo de IDs a índices
        nodeToIndex = new HashMap<>();
        indexToNode = new HashMap<>();
        for (int i = 0; i < activeNodes.size(); i++) {
            Node node = activeNodes.get(i);
            nodeToIndex.put(node.getId(), i);
            indexToNode.put(i, node.getId());
        }
        
        // Inicializar matrices
        distances = new double[n][n];
        next = new int[n][n];
        
        // Inicializar con infinito
        for (int i = 0; i < n; i++) {
            Arrays.fill(distances[i], Double.POSITIVE_INFINITY);
            Arrays.fill(next[i], -1);
        }
        
        // Distancia de un nodo a sí mismo es 0
        for (int i = 0; i < n; i++) {
            distances[i][i] = 0;
        }
        
        // Llenar con las conexiones existentes
        for (Connection conn : network.getActiveConnections()) {
            int fromIndex = nodeToIndex.get(conn.getFromNode().getId());
            int toIndex = nodeToIndex.get(conn.getToNode().getId());
            distances[fromIndex][toIndex] = conn.getLatency();
            next[fromIndex][toIndex] = toIndex;
        }
    }
    
    /**
     * Ejecuta el algoritmo Floyd-Warshall
     */
    public void execute() {
        int n = distances.length;
        
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (distances[i][k] + distances[k][j] < distances[i][j]) {
                        distances[i][j] = distances[i][k] + distances[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
    }
    
    /**
     * Obtiene la ruta más corta entre dos nodos
     */
    public List<Integer> getShortestPath(int fromNodeId, int toNodeId) {
        if (!nodeToIndex.containsKey(fromNodeId) || !nodeToIndex.containsKey(toNodeId)) {
            return null;
        }
        
        int fromIndex = nodeToIndex.get(fromNodeId);
        int toIndex = nodeToIndex.get(toNodeId);
        
        if (next[fromIndex][toIndex] == -1) {
            return null; // No hay camino
        }
        
        List<Integer> path = new ArrayList<>();
        path.add(fromNodeId);
        
        int current = fromIndex;
        while (current != toIndex) {
            current = next[current][toIndex];
            path.add(indexToNode.get(current));
        }
        
        return path;
    }
    
    /**
     * Obtiene la distancia más corta entre dos nodos
     */
    public double getShortestDistance(int fromNodeId, int toNodeId) {
        if (!nodeToIndex.containsKey(fromNodeId) || !nodeToIndex.containsKey(toNodeId)) {
            return Double.POSITIVE_INFINITY;
        }
        
        int fromIndex = nodeToIndex.get(fromNodeId);
        int toIndex = nodeToIndex.get(toNodeId);
        
        return distances[fromIndex][toIndex];
    }
    
    /**
     * Verifica si existe un camino entre dos nodos
     */
    public boolean hasPath(int fromNodeId, int toNodeId) {
        return getShortestDistance(fromNodeId, toNodeId) != Double.POSITIVE_INFINITY;
    }
    
    /**
     * Obtiene todas las rutas más cortas desde un nodo origen
     */
    public Map<Integer, List<Integer>> getAllShortestPathsFrom(int fromNodeId) {
        Map<Integer, List<Integer>> paths = new HashMap<>();
        
        for (Node node : network.getActiveNodes()) {
            if (node.getId() != fromNodeId) {
                List<Integer> path = getShortestPath(fromNodeId, node.getId());
                if (path != null) {
                    paths.put(node.getId(), path);
                }
            }
        }
        
        return paths;
    }
    
    /**
     * Obtiene la matriz de distancias
     */
    public double[][] getDistanceMatrix() {
        return distances;
    }
    
    /**
     * Obtiene la matriz de siguiente nodo
     */
    public int[][] getNextMatrix() {
        return next;
    }
    
    /**
     * Imprime la matriz de distancias (para debugging)
     */
    public void printDistanceMatrix() {
        System.out.println("Matriz de distancias:");
        for (int i = 0; i < distances.length; i++) {
            for (int j = 0; j < distances[i].length; j++) {
                if (distances[i][j] == Double.POSITIVE_INFINITY) {
                    System.out.print("∞ ");
                } else {
                    System.out.printf("%.0f ", distances[i][j]);
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Obtiene el nodo más central (menor suma de distancias a todos los demás)
     */
    public Node getMostCentralNode() {
        double minSum = Double.POSITIVE_INFINITY;
        Node mostCentral = null;
        
        for (Node node : network.getActiveNodes()) {
            double sum = 0;
            for (Node other : network.getActiveNodes()) {
                if (node.getId() != other.getId()) {
                    sum += getShortestDistance(node.getId(), other.getId());
                }
            }
            
            if (sum < minSum) {
                minSum = sum;
                mostCentral = node;
            }
        }
        
        return mostCentral;
    }
} 