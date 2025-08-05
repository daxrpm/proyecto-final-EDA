package main.model;

import java.awt.Point;

/**
 * Representa un nodo/dispositivo en la red
 */
public class Node {
    private int id;
    private String name;
    private NodeType type;
    private Point position;
    private boolean active;
    
    public enum NodeType {
        PC("PC"),
        ROUTER("Router"),
        SWITCH("Switch");
        
        private final String displayName;
        
        NodeType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public Node(int id, String name, NodeType type, Point position) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.position = position;
        this.active = true;
    }
    
    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public NodeType getType() { return type; }
    public void setType(NodeType type) { this.type = type; }
    
    public Point getPosition() { return position; }
    public void setPosition(Point position) { this.position = position; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    @Override
    public String toString() {
        return name + " (" + type.getDisplayName() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return id == node.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
} 