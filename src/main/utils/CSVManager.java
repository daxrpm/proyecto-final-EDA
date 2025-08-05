package main.utils;

import main.model.Network;
import main.model.Node;
import main.model.Connection;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Maneja la importación y exportación de redes desde archivos CSV
 */
public class CSVManager {
    
    /**
     * Importa una red desde archivos CSV
     */
    public static Network importFromCSV(String nodesFile, String connectionsFile) throws IOException {
        Network network = new Network();
        
        // Importar nodos
        importNodes(network, nodesFile);
        
        // Importar conexiones
        importConnections(network, connectionsFile);
        
        return network;
    }
    
    /**
     * Exporta una red a archivos CSV
     */
    public static void exportToCSV(Network network, String nodesFile, String connectionsFile) throws IOException {
        // Exportar nodos
        exportNodes(network, nodesFile);
        
        // Exportar conexiones
        exportConnections(network, connectionsFile);
    }
    
    private static void importNodes(Network network, String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // Saltar encabezado
            if (line == null || !line.startsWith("id,name,type,x,y")) {
                throw new IOException("Formato de archivo de nodos inválido");
            }
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    Node.NodeType type = Node.NodeType.valueOf(parts[2].trim().toUpperCase());
                    int x = Integer.parseInt(parts[3].trim());
                    int y = Integer.parseInt(parts[4].trim());
                    
                    // Crear nodo manualmente para mantener el ID original
                    Node node = new Node(id, name, type, new java.awt.Point(x, y));
                    // Agregar a la red (esto requiere modificar Network para aceptar nodos con ID específico)
                    addNodeWithId(network, node);
                }
            }
        }
    }
    
    private static void importConnections(Network network, String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // Saltar encabezado
            if (line == null || !line.startsWith("from_id,to_id,latency")) {
                throw new IOException("Formato de archivo de conexiones inválido");
            }
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    int fromId = Integer.parseInt(parts[0].trim());
                    int toId = Integer.parseInt(parts[1].trim());
                    int latency = Integer.parseInt(parts[2].trim());
                    
                    network.addConnection(fromId, toId, latency);
                }
            }
        }
    }
    
    private static void exportNodes(Network network, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("id,name,type,x,y,status");
            
            for (Node node : network.getAllNodes()) {
                writer.printf("%d,%s,%s,%d,%d,%s%n",
                    node.getId(),
                    node.getName(),
                    node.getType().name(),
                    node.getPosition().x,
                    node.getPosition().y,
                    node.isActive() ? "ACTIVE" : "FAILED"
                );
            }
        }
    }
    
    private static void exportConnections(Network network, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("from_id,to_id,latency,status");
            
            for (Connection connection : network.getAllConnections()) {
                writer.printf("%d,%d,%d,%s%n",
                    connection.getFromNode().getId(),
                    connection.getToNode().getId(),
                    connection.getLatency(),
                    connection.isActive() ? "ACTIVE" : "FAILED"
                );
            }
        }
    }
    
    // Método auxiliar para agregar nodo con ID específico
    private static void addNodeWithId(Network network, Node node) {
        network.addNodeWithId(node.getId(), node.getName(), node.getType(), 
                             node.getPosition().x, node.getPosition().y);
    }
    
    /**
     * Crea archivos CSV de ejemplo
     */
    public static void createExampleFiles() throws IOException {
        // Crear ejemplo de nodos
        try (PrintWriter writer = new PrintWriter(new FileWriter("example_nodes.csv"))) {
            writer.println("id,name,type,x,y,status");
            writer.println("1,Router_Central,ROUTER,100,100,ACTIVE");
            writer.println("2,PC_Oficina1,PC,200,150,ACTIVE");
            writer.println("3,Switch_Piso1,SWITCH,150,200,ACTIVE");
            writer.println("4,PC_Oficina2,PC,250,200,ACTIVE");
        }
        
        // Crear ejemplo de conexiones
        try (PrintWriter writer = new PrintWriter(new FileWriter("example_connections.csv"))) {
            writer.println("from_id,to_id,latency,status");
            writer.println("1,2,15,ACTIVE");
            writer.println("1,3,10,ACTIVE");
            writer.println("3,4,8,ACTIVE");
            writer.println("2,4,25,ACTIVE");
        }
    }
} 