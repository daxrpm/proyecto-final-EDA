package main.model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Representa la red completa como un grafo dirigido y ponderado
 */
public class Network {
    private Map<Integer, Node> nodes;
    private List<Connection> connections;
    private int nextNodeId;
    
    public Network() {
        this.nodes = new HashMap<>();
        this.connections = new ArrayList<>();
        this.nextNodeId = 1;
    }
    
    // Métodos para nodos
    public Node addNode(String name, Node.NodeType type, int x, int y) {
        Node node = new Node(nextNodeId++, name, type, new java.awt.Point(x, y));
        nodes.put(node.getId(), node);
        return node;
    }
    
    public Node addNodeWithId(int id, String name, Node.NodeType type, int x, int y) {
        Node node = new Node(id, name, type, new java.awt.Point(x, y));
        nodes.put(node.getId(), node);
        if (id >= nextNodeId) {
            nextNodeId = id + 1;
        }
        return node;
    }
    
    public Node getNode(int id) {
        return nodes.get(id);
    }
    
    public List<Node> getAllNodes() {
        return new ArrayList<>(nodes.values());
    }
    
    public List<Node> getActiveNodes() {
        return nodes.values().stream()
                .filter(Node::isActive)
                .collect(Collectors.toList());
    }
    
    public boolean removeNode(int nodeId) {
        Node node = nodes.get(nodeId);
        if (node == null) return false;
        
        // Desactivar el nodo en lugar de eliminarlo
        node.setActive(false);
        
        // Desactivar todas las conexiones que involucran este nodo
        connections.stream()
                .filter(conn -> conn.getFromNode().getId() == nodeId || conn.getToNode().getId() == nodeId)
                .forEach(conn -> conn.setActive(false));
        
        return true;
    }
    
    // Métodos para conexiones
    public Connection addConnection(int fromNodeId, int toNodeId, int latency) {
        Node fromNode = nodes.get(fromNodeId);
        Node toNode = nodes.get(toNodeId);
        
        if (fromNode == null || toNode == null) {
            throw new IllegalArgumentException("Nodos no encontrados");
        }
        
        Connection connection = new Connection(fromNode, toNode, latency);
        connections.add(connection);
        return connection;
    }
    
    public List<Connection> getAllConnections() {
        return new ArrayList<>(connections);
    }
    
    public List<Connection> getActiveConnections() {
        return connections.stream()
                .filter(Connection::isActive)
                .collect(Collectors.toList());
    }
    
    public boolean removeConnection(int fromNodeId, int toNodeId) {
        Connection connection = connections.stream()
                .filter(conn -> conn.getFromNode().getId() == fromNodeId && 
                               conn.getToNode().getId() == toNodeId)
                .findFirst()
                .orElse(null);
        
        if (connection != null) {
            connection.setActive(false);
            return true;
        }
        return false;
    }
    
    // Métodos de utilidad
    public int getNodeCount() {
        return nodes.size();
    }
    
    public int getConnectionCount() {
        return connections.size();
    }
    
    public boolean isEmpty() {
        return nodes.isEmpty();
    }
    
    public void clear() {
        nodes.clear();
        connections.clear();
        nextNodeId = 1;
    }
    
    // Obtener conexiones de un nodo específico
    public List<Connection> getConnectionsFrom(int nodeId) {
        return connections.stream()
                .filter(conn -> conn.getFromNode().getId() == nodeId && conn.isActive())
                .collect(Collectors.toList());
    }
    
    public List<Connection> getConnectionsTo(int nodeId) {
        return connections.stream()
                .filter(conn -> conn.getToNode().getId() == nodeId && conn.isActive())
                .collect(Collectors.toList());
    }
    
    // Verificar si existe conexión entre dos nodos
    public boolean hasConnection(int fromNodeId, int toNodeId) {
        return connections.stream()
                .anyMatch(conn -> conn.getFromNode().getId() == fromNodeId && 
                                 conn.getToNode().getId() == toNodeId && 
                                 conn.isActive());
    }
    
    public Connection getConnection(int fromNodeId, int toNodeId) {
        return connections.stream()
                .filter(conn -> conn.getFromNode().getId() == fromNodeId && 
                               conn.getToNode().getId() == toNodeId)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Simula una falla en una conexión específica
     */
    public boolean simulateConnectionFailure(int fromNodeId, int toNodeId) {
        Connection connection = getConnection(fromNodeId, toNodeId);
        if (connection != null && connection.isActive()) {
            connection.setActive(false);
            return true;
        }
        return false;
    }
    
    /**
     * Restaura una conexión fallida
     */
    public boolean restoreConnection(int fromNodeId, int toNodeId) {
        Connection connection = getConnection(fromNodeId, toNodeId);
        if (connection != null && !connection.isActive()) {
            connection.setActive(true);
            return true;
        }
        return false;
    }
    
    /**
     * Obtiene todas las conexiones fallidas
     */
    public List<Connection> getFailedConnections() {
        return connections.stream()
                .filter(conn -> !conn.isActive())
                .collect(Collectors.toList());
    }
    
    /**
     * Verifica si la red está completamente conectada
     */
    public boolean isNetworkConnected() {
        List<Node> activeNodes = getActiveNodes();
        if (activeNodes.size() <= 1) return true;
        
        // Usar BFS para verificar conectividad
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        
        Node startNode = activeNodes.get(0);
        queue.add(startNode.getId());
        visited.add(startNode.getId());
        
        while (!queue.isEmpty()) {
            int currentId = queue.poll();
            Node currentNode = getNode(currentId);
            
            // Obtener todas las conexiones activas desde este nodo
            for (Connection conn : getActiveConnections()) {
                if (conn.getFromNode().getId() == currentId) {
                    int neighborId = conn.getToNode().getId();
                    if (!visited.contains(neighborId)) {
                        visited.add(neighborId);
                        queue.add(neighborId);
                    }
                }
            }
        }
        
        return visited.size() == activeNodes.size();
    }
    
    /**
     * Obtiene los componentes conectados de la red
     */
    public List<List<Node>> getConnectedComponents() {
        List<Node> activeNodes = getActiveNodes();
        Set<Integer> visited = new HashSet<>();
        List<List<Node>> components = new ArrayList<>();
        
        for (Node node : activeNodes) {
            if (!visited.contains(node.getId())) {
                List<Node> component = new ArrayList<>();
                Queue<Integer> queue = new LinkedList<>();
                
                queue.add(node.getId());
                visited.add(node.getId());
                component.add(node);
                
                while (!queue.isEmpty()) {
                    int currentId = queue.poll();
                    
                    // Buscar vecinos conectados
                    for (Connection conn : getActiveConnections()) {
                        if (conn.getFromNode().getId() == currentId) {
                            int neighborId = conn.getToNode().getId();
                            if (!visited.contains(neighborId)) {
                                visited.add(neighborId);
                                queue.add(neighborId);
                                component.add(getNode(neighborId));
                            }
                        }
                    }
                }
                
                components.add(component);
            }
        }
        
        return components;
    }
    
    /**
     * Analiza el impacto de una falla de conexión
     */
    public String analyzeConnectionFailureImpact(int fromNodeId, int toNodeId) {
        Connection connection = getConnection(fromNodeId, toNodeId);
        if (connection == null) {
            return "Conexión no encontrada";
        }
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("Análisis de impacto para: ").append(connection.toString()).append("\n\n");
        
        // Verificar estado actual
        boolean wasActive = connection.isActive();
        if (!wasActive) {
            analysis.append("La conexión ya está fallida.\n");
            return analysis.toString();
        }
        
        // Simular la falla temporalmente
        connection.setActive(false);
        
        // Verificar conectividad
        boolean stillConnected = isNetworkConnected();
        List<List<Node>> components = getConnectedComponents();
        
        analysis.append("Estado de la red después de la falla:\n");
        analysis.append("- Red completamente conectada: ").append(stillConnected ? "Sí" : "No").append("\n");
        analysis.append("- Número de componentes: ").append(components.size()).append("\n");
        
        if (components.size() > 1) {
            analysis.append("- Componentes aislados:\n");
            for (int i = 0; i < components.size(); i++) {
                analysis.append("  Componente ").append(i + 1).append(": ");
                analysis.append(components.get(i).stream()
                        .map(Node::getName)
                        .collect(Collectors.joining(", ")));
                analysis.append("\n");
            }
        }
        
        // Restaurar la conexión
        connection.setActive(true);
        
        return analysis.toString();
    }
} 