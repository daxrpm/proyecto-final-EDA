package main.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un nodo en el árbol de rutas
 */
public class NodoArbol {
    private Node node;
    private int latency;
    private List<NodoArbol> children;
    private NodoArbol parent;
    private int depth;
    
    public NodoArbol(Node node, int latency) {
        this.node = node;
        this.latency = latency;
        this.children = new ArrayList<>();
        this.depth = 0;
    }
    
    // Getters y setters
    public Node getNode() { return node; }
    public void setNode(Node node) { this.node = node; }
    
    public int getLatency() { return latency; }
    public void setLatency(int latency) { this.latency = latency; }
    
    public List<NodoArbol> getChildren() { return children; }
    public void setChildren(List<NodoArbol> children) { this.children = children; }
    
    public NodoArbol getParent() { return parent; }
    public void setParent(NodoArbol parent) { this.parent = parent; }
    
    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }
    
    /**
     * Agrega un hijo al nodo
     */
    public void addChild(NodoArbol child) {
        child.setParent(this);
        child.setDepth(this.depth + 1);
        this.children.add(child);
    }
    
    /**
     * Verifica si el nodo es una hoja (sin hijos)
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }
    
    /**
     * Obtiene la ruta completa desde la raíz hasta este nodo
     */
    public List<NodoArbol> getPathFromRoot() {
        List<NodoArbol> path = new ArrayList<>();
        NodoArbol current = this;
        
        while (current != null) {
            path.add(0, current);
            current = current.getParent();
        }
        
        return path;
    }
    
    /**
     * Obtiene la latencia total desde la raíz hasta este nodo
     */
    public int getTotalLatency() {
        int total = 0;
        NodoArbol current = this;
        
        while (current != null) {
            total += current.getLatency();
            current = current.getParent();
        }
        
        return total;
    }
    
    /**
     * Imprime el árbol de forma jerárquica
     */
    public String printTree() {
        StringBuilder sb = new StringBuilder();
        printTreeRecursive(this, "", true, sb);
        return sb.toString();
    }
    
    /**
     * Método recursivo para imprimir el árbol
     */
    private void printTreeRecursive(NodoArbol node, String prefix, boolean isLast, StringBuilder sb) {
        // Imprimir el nodo actual
        sb.append(prefix);
        if (isLast) {
            sb.append("└── ");
        } else {
            sb.append("├── ");
        }
        
        sb.append(node.getNode().getName())
          .append(" (")
          .append(node.getLatency())
          .append("ms)");
        
        if (node.isLeaf()) {
            sb.append(" [Total: ").append(node.getTotalLatency()).append("ms]");
        }
        sb.append("\n");
        
        // Imprimir hijos
        List<NodoArbol> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            String newPrefix = prefix + (isLast ? "    " : "│   ");
            printTreeRecursive(children.get(i), newPrefix, i == children.size() - 1, sb);
        }
    }
    
    /**
     * Obtiene todas las rutas (caminos a hojas) en el árbol
     */
    public List<List<NodoArbol>> getAllPaths() {
        List<List<NodoArbol>> paths = new ArrayList<>();
        getAllPathsRecursive(this, new ArrayList<>(), paths);
        return paths;
    }
    
    /**
     * Método recursivo para obtener todas las rutas
     */
    private void getAllPathsRecursive(NodoArbol node, List<NodoArbol> currentPath, List<List<NodoArbol>> allPaths) {
        currentPath.add(node);
        
        if (node.isLeaf()) {
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            for (NodoArbol child : node.getChildren()) {
                getAllPathsRecursive(child, currentPath, allPaths);
            }
        }
        
        currentPath.remove(currentPath.size() - 1);
    }
    
    /**
     * Obtiene información detallada de la ruta
     */
    public String getRouteInfo() {
        List<NodoArbol> path = getPathFromRoot();
        StringBuilder sb = new StringBuilder();
        
        sb.append("Ruta: ");
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getNode().getName());
            if (i < path.size() - 1) {
                sb.append(" -> ");
            }
        }
        sb.append("\n");
        
        sb.append("Latencia total: ").append(getTotalLatency()).append("ms\n");
        sb.append("Número de saltos: ").append(path.size() - 1).append("\n");
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return node.getName() + " (" + latency + "ms)";
    }
} 