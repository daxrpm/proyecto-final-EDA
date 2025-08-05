package main.model;

/**
 * Representa una conexión entre dos nodos
 */
public class Connection {
    private Node fromNode;
    private Node toNode;
    private int latency; // en milisegundos
    private boolean active;
    
    public Connection(Node fromNode, Node toNode, int latency) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.latency = latency;
        this.active = true;
    }
    
    // Getters y setters
    public Node getFromNode() { return fromNode; }
    public void setFromNode(Node fromNode) { this.fromNode = fromNode; }
    
    public Node getToNode() { return toNode; }
    public void setToNode(Node toNode) { this.toNode = toNode; }
    
    public int getLatency() { return latency; }
    public void setLatency(int latency) { this.latency = latency; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    @Override
    public String toString() {
        return fromNode.getName() + " -> " + toNode.getName() + " (" + latency + "ms)";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Connection connection = (Connection) obj;
        return fromNode.equals(connection.fromNode) && toNode.equals(connection.toNode);
    }
    
    @Override
    public int hashCode() {
        return fromNode.hashCode() * 31 + toNode.hashCode();
    }
} 