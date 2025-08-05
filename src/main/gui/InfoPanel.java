package main.gui;

import main.model.Network;
import main.model.Node;
import main.model.Connection;
import main.algorithms.FloydWarshall;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Panel de información que muestra estadísticas y rutas
 */
public class InfoPanel extends JPanel {
    private MainWindow mainWindow;
    private Network network;
    private FloydWarshall floydWarshall;
    
    // Componentes
    private JTextArea infoArea;
    private JTextArea routesArea;
    private JScrollPane infoScrollPane;
    private JScrollPane routesScrollPane;
    private JButton refreshButton;
    private JTabbedPane tabbedPane;
    
    public InfoPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.network = mainWindow.getNetwork();
        this.floydWarshall = mainWindow.getFloydWarshall();
        
        setupComponents();
        setupLayout();
        setupEvents();
        updateInfo();
    }
    
    private void setupComponents() {
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        routesArea = new JTextArea();
        routesArea.setEditable(false);
        routesArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        
        infoScrollPane = new JScrollPane(infoArea);
        routesScrollPane = new JScrollPane(routesArea);
        
        refreshButton = new JButton("Actualizar");
        tabbedPane = new JTabbedPane();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Información"));
        setPreferredSize(new Dimension(350, 600));
        
        // Panel superior con botón
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(refreshButton);
        
        // Configurar pestañas
        tabbedPane.addTab("Estadísticas", infoScrollPane);
        tabbedPane.addTab("Rutas", routesScrollPane);
        tabbedPane.addTab("Fallas", createFailuresPanel());
        tabbedPane.addTab("Árbol de Rutas", createRouteTreePanel());
        
        // Layout principal
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void setupEvents() {
        refreshButton.addActionListener(e -> updateInfo());
    }
    
    public void updateInfo() {
        this.network = mainWindow.getNetwork();
        this.floydWarshall = mainWindow.getFloydWarshall();
        
        updateStatistics();
        updateRoutes();
        updateFailures();
        updateRouteTreeInfo();
    }
    
    private void updateStatistics() {
        StringBuilder stats = new StringBuilder();
        
        // Estadísticas generales
        stats.append("=== ESTADÍSTICAS DE LA RED ===\n\n");
        stats.append("Total de nodos: ").append(network.getNodeCount()).append("\n");
        stats.append("Nodos activos: ").append(network.getActiveNodes().size()).append("\n");
        stats.append("Nodos fallidos: ").append(network.getNodeCount() - network.getActiveNodes().size()).append("\n");
        stats.append("Total de conexiones: ").append(network.getConnectionCount()).append("\n");
        stats.append("Conexiones activas: ").append(network.getActiveConnections().size()).append("\n");
        stats.append("Conexiones fallidas: ").append(network.getConnectionCount() - network.getActiveConnections().size()).append("\n\n");
        
        // Información de nodos
        stats.append("=== NODOS ===\n");
        for (Node node : network.getAllNodes()) {
            stats.append(String.format("ID: %d | %s (%s) | %s\n", 
                node.getId(), 
                node.getName(), 
                node.getType().getDisplayName(),
                node.isActive() ? "Activo" : "Fallido"));
        }
        stats.append("\n");
        
        // Información de conexiones
        stats.append("=== CONEXIONES ===\n");
        for (Connection conn : network.getAllConnections()) {
            stats.append(String.format("%s -> %s (%dms) | %s\n",
                conn.getFromNode().getName(),
                conn.getToNode().getName(),
                conn.getLatency(),
                conn.isActive() ? "Activa" : "Fallida"));
        }
        stats.append("\n");
        
        // Nodo más central
        if (!network.getActiveNodes().isEmpty()) {
            Node centralNode = floydWarshall.getMostCentralNode();
            if (centralNode != null) {
                stats.append("Nodo más central: ").append(centralNode.getName()).append("\n");
            }
        }
        
        infoArea.setText(stats.toString());
    }
    
    private void updateRoutes() {
        StringBuilder routes = new StringBuilder();
        
        if (network.getActiveNodes().size() < 2) {
            routes.append("Se necesitan al menos 2 nodos activos\n");
            routes.append("para calcular rutas.");
        } else {
            routes.append("=== RUTAS MÁS CORTAS ===\n\n");
            
            // Mostrar rutas desde cada nodo
            for (Node source : network.getActiveNodes()) {
                routes.append("Desde ").append(source.getName()).append(":\n");
                
                Map<Integer, List<Integer>> paths = floydWarshall.getAllShortestPathsFrom(source.getId());
                
                if (paths.isEmpty()) {
                    routes.append("  No hay rutas disponibles\n");
                } else {
                    for (Map.Entry<Integer, List<Integer>> entry : paths.entrySet()) {
                        Node dest = network.getNode(entry.getKey());
                        List<Integer> path = entry.getValue();
                        double distance = floydWarshall.getShortestDistance(source.getId(), dest.getId());
                        
                        routes.append(String.format("  -> %s: ", dest.getName()));
                        
                        // Mostrar ruta
                        for (int i = 0; i < path.size(); i++) {
                            Node pathNode = network.getNode(path.get(i));
                            routes.append(pathNode.getName());
                            if (i < path.size() - 1) {
                                routes.append(" -> ");
                            }
                        }
                        
                        routes.append(String.format(" (%.0fms)\n", distance));
                    }
                }
                routes.append("\n");
            }
            
            // Mostrar matriz de distancias
            routes.append("=== MATRIZ DE DISTANCIAS ===\n");
            double[][] distances = floydWarshall.getDistanceMatrix();
            
            if (distances.length > 0) {
                // Encabezados
                routes.append("     ");
                for (Node node : network.getActiveNodes()) {
                    routes.append(String.format("%-8s", node.getName()));
                }
                routes.append("\n");
                
                // Filas
                for (int i = 0; i < distances.length; i++) {
                    Node source = network.getActiveNodes().get(i);
                    routes.append(String.format("%-5s", source.getName()));
                    
                    for (int j = 0; j < distances[i].length; j++) {
                        if (distances[i][j] == Double.POSITIVE_INFINITY) {
                            routes.append("∞       ");
                        } else {
                            routes.append(String.format("%-8.0f", distances[i][j]));
                        }
                    }
                    routes.append("\n");
                }
            }
        }
        
        routesArea.setText(routes.toString());
    }
    
    private JPanel createFailuresPanel() {
        JPanel failuresPanel = new JPanel(new BorderLayout());
        JTextArea failuresArea = new JTextArea();
        failuresArea.setEditable(false);
        failuresArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane failuresScrollPane = new JScrollPane(failuresArea);
        failuresPanel.add(failuresScrollPane, BorderLayout.CENTER);
        
        // Guardar referencia para actualizaciones
        this.failuresArea = failuresArea;
        
        return failuresPanel;
    }
    
    private void updateFailures() {
        if (failuresArea == null) return;
        
        StringBuilder failures = new StringBuilder();
        
        failures.append("=== ANÁLISIS DE FALLAS ===\n\n");
        
        // Estado de conectividad
        boolean isConnected = network.isNetworkConnected();
        failures.append("Estado de la red: ").append(isConnected ? "Conectada" : "Desconectada").append("\n\n");
        
        // Componentes conectados
        List<List<Node>> components = network.getConnectedComponents();
        failures.append("Componentes conectados: ").append(components.size()).append("\n");
        
        if (components.size() > 1) {
            failures.append("La red está dividida en ").append(components.size()).append(" componentes:\n\n");
            for (int i = 0; i < components.size(); i++) {
                failures.append("Componente ").append(i + 1).append(":\n");
                for (Node node : components.get(i)) {
                    failures.append("  - ").append(node.getName()).append(" (").append(node.getType().getDisplayName()).append(")\n");
                }
                failures.append("\n");
            }
        } else if (components.size() == 1) {
            failures.append("Todos los nodos están conectados.\n\n");
        }
        
        // Conexiones fallidas
        List<Connection> failedConnections = network.getFailedConnections();
        failures.append("Conexiones fallidas: ").append(failedConnections.size()).append("\n");
        
        if (!failedConnections.isEmpty()) {
            failures.append("\nDetalles de conexiones fallidas:\n");
            for (Connection conn : failedConnections) {
                failures.append("  - ").append(conn.getFromNode().getName())
                        .append(" -> ").append(conn.getToNode().getName())
                        .append(" (").append(conn.getLatency()).append("ms)\n");
            }
        }
        
        // Nodos fallidos
        List<Node> failedNodes = network.getAllNodes().stream()
                .filter(node -> !node.isActive())
                .collect(java.util.stream.Collectors.toList());
        
        failures.append("\nNodos fallidos: ").append(failedNodes.size()).append("\n");
        
        if (!failedNodes.isEmpty()) {
            failures.append("\nDetalles de nodos fallidos:\n");
            for (Node node : failedNodes) {
                failures.append("  - ").append(node.getName())
                        .append(" (").append(node.getType().getDisplayName()).append(")\n");
            }
        }
        
        failuresArea.setText(failures.toString());
    }
    
    // Campo para el área de fallas
    private JTextArea failuresArea;
    
    // Campo para el área del árbol de rutas
    private JTextArea routeTreeArea;
    
    private JPanel createRouteTreePanel() {
        JPanel routeTreePanel = new JPanel(new BorderLayout());
        JTextArea routeTreeArea = new JTextArea();
        routeTreeArea.setEditable(false);
        routeTreeArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane routeTreeScrollPane = new JScrollPane(routeTreeArea);
        routeTreePanel.add(routeTreeScrollPane, BorderLayout.CENTER);
        
        // Guardar referencia para actualizaciones
        this.routeTreeArea = routeTreeArea;
        
        return routeTreePanel;
    }
    
    private void updateRouteTreeInfo() {
        if (routeTreeArea == null) return;
        
        StringBuilder info = new StringBuilder();
        
        info.append("=== ÁRBOL DE RUTAS ===\n\n");
        info.append("Esta funcionalidad permite encontrar TODAS las rutas posibles\n");
        info.append("desde un nodo origen hasta un nodo destino.\n\n");
        
        info.append("Características:\n");
        info.append("- Usa algoritmo DFS para encontrar todas las rutas\n");
        info.append("- Construye un árbol jerárquico de rutas\n");
        info.append("- Muestra latencia de cada conexión\n");
        info.append("- Calcula latencia total de cada ruta\n");
        info.append("- Evita ciclos infinitos\n\n");
        
        info.append("Para usar esta funcionalidad:\n");
        info.append("1. Haga clic en 'Árbol de Rutas' en el panel de control\n");
        info.append("2. Seleccione nodo origen y destino\n");
        info.append("3. Haga clic en 'Encontrar Todas las Rutas'\n");
        info.append("4. Vea el árbol en formato texto o visual\n\n");
        
        info.append("Ejemplo de salida:\n");
        info.append("PC1\n");
        info.append(" └── Switch1 (5ms)\n");
        info.append("      ├── RouterA (10ms)\n");
        info.append("      │     └── RouterB (8ms)\n");
        info.append("      │           └── ServidorX (12ms) [Total: 35ms]\n");
        info.append("      └── ServidorX (30ms) [Total: 35ms]\n");
        
        routeTreeArea.setText(info.toString());
    }
} 