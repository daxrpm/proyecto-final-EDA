package main.gui;

import main.model.Network;
import main.model.Node;
import main.model.Connection;
import main.utils.CSVManager;
import main.gui.RouteTreeWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Panel de control con botones para manejar la red
 */
public class ControlPanel extends JPanel {
    private MainWindow mainWindow;
    
    // Componentes
    private JButton addNodeButton;
    private JButton addConnectionButton;
    private JButton deleteNodeButton;
    private JButton deleteConnectionButton;
    private JButton importButton;
    private JButton exportButton;
    private JButton calculateRoutesButton;
    private JButton showMatricesButton;
    private JButton simulateFailureButton;
    private JButton restoreConnectionButton;
    private JButton routeTreeButton;
    private JButton packetSimulationButton;
    private JButton clearButton;
    private JButton cancelEditButton;
    
    // Selectores
    private JComboBox<Node.NodeType> nodeTypeCombo;
    private JSpinner latencySpinner;
    
    public ControlPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setupComponents();
        setupLayout();
        setupEvents();
    }
    
    private void setupComponents() {
        // Botones
        addNodeButton = new JButton("Agregar Nodo");
        addConnectionButton = new JButton("Agregar Conexión");
        deleteNodeButton = new JButton("Eliminar Nodo");
        deleteConnectionButton = new JButton("Eliminar Conexión");
        importButton = new JButton("Importar CSV");
        exportButton = new JButton("Exportar CSV");
        calculateRoutesButton = new JButton("Calcular Rutas");
        showMatricesButton = new JButton("Ver Matrices");
        simulateFailureButton = new JButton("Simular Falla");
        restoreConnectionButton = new JButton("Restaurar Conexión");
        routeTreeButton = new JButton("Árbol de Rutas");
        packetSimulationButton = new JButton("Simular Paquete");
        clearButton = new JButton("Limpiar Red");
        
        // Botón para cancelar modo de edición
        cancelEditButton = new JButton("Cancelar Edición");
        cancelEditButton.addActionListener(e -> {
            NetworkPanel networkPanel = mainWindow.getNetworkPanel();
            networkPanel.setAddNodeMode(false, null);
            networkPanel.setAddConnectionMode(false, 0);
            networkPanel.setDeleteMode(false);
        });
        
        // Selectores
        nodeTypeCombo = new JComboBox<>(Node.NodeType.values());
        latencySpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
    }
    
    private void setupLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Controles"));
        setPreferredSize(new Dimension(200, 600));
        
        // Panel de agregar nodo
        JPanel addNodePanel = new JPanel();
        addNodePanel.setLayout(new GridLayout(3, 1));
        addNodePanel.setBorder(BorderFactory.createTitledBorder("Agregar Nodo"));
        
        addNodePanel.add(new JLabel("Tipo:"));
        addNodePanel.add(nodeTypeCombo);
        addNodePanel.add(addNodeButton);
        
        // Panel de agregar conexión
        JPanel addConnectionPanel = new JPanel();
        addConnectionPanel.setLayout(new GridLayout(3, 1));
        addConnectionPanel.setBorder(BorderFactory.createTitledBorder("Agregar Conexión"));
        
        addConnectionPanel.add(new JLabel("Latencia (ms):"));
        addConnectionPanel.add(latencySpinner);
        addConnectionPanel.add(addConnectionButton);
        
        // Panel de eliminación
        JPanel deletePanel = new JPanel();
        deletePanel.setLayout(new GridLayout(2, 1));
        deletePanel.setBorder(BorderFactory.createTitledBorder("Eliminar"));
        
        deletePanel.add(deleteNodeButton);
        deletePanel.add(deleteConnectionButton);
        
        // Panel de archivos
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new GridLayout(2, 1));
        filePanel.setBorder(BorderFactory.createTitledBorder("Archivos"));
        
        filePanel.add(importButton);
        filePanel.add(exportButton);
        
        // Panel de algoritmos
        JPanel algorithmPanel = new JPanel();
        algorithmPanel.setLayout(new GridLayout(6, 1));
        algorithmPanel.setBorder(BorderFactory.createTitledBorder("Algoritmos"));
        
        algorithmPanel.add(calculateRoutesButton);
        algorithmPanel.add(showMatricesButton);
        algorithmPanel.add(simulateFailureButton);
        algorithmPanel.add(restoreConnectionButton);
        algorithmPanel.add(routeTreeButton);
        algorithmPanel.add(packetSimulationButton);
        
        // Panel de utilidades
        JPanel utilityPanel = new JPanel();
        utilityPanel.setLayout(new GridLayout(2, 1));
        utilityPanel.setBorder(BorderFactory.createTitledBorder("Utilidades"));
        
        utilityPanel.add(clearButton);
        utilityPanel.add(cancelEditButton);
        
        // Agregar todos los paneles
        add(addNodePanel);
        add(Box.createVerticalStrut(10));
        add(addConnectionPanel);
        add(Box.createVerticalStrut(10));
        add(deletePanel);
        add(Box.createVerticalStrut(10));
        add(filePanel);
        add(Box.createVerticalStrut(10));
        add(algorithmPanel);
        add(Box.createVerticalStrut(10));
        add(utilityPanel);
        add(Box.createVerticalGlue());
    }
    
    private void setupEvents() {
        addNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNode();
            }
        });
        
        addConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addConnection();
            }
        });
        
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importFromCSV();
            }
        });
        
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToCSV();
            }
        });
        
        calculateRoutesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateRoutes();
            }
        });
        
        showMatricesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMatrices();
            }
        });
        
        simulateFailureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulateFailure();
            }
        });
        
        deleteNodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteNode();
            }
        });
        
        deleteConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteConnection();
            }
        });
        
        restoreConnectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restoreConnection();
            }
        });
        
        routeTreeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRouteTree();
            }
        });
        
        packetSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulatePacket();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearNetwork();
            }
        });
    }
    
    private void addNode() {
        Node.NodeType type = (Node.NodeType) nodeTypeCombo.getSelectedItem();
        NetworkPanel networkPanel = mainWindow.getNetworkPanel();
        
        // Activar modo de agregar nodo
        networkPanel.setAddNodeMode(true, type);
    }
    
    private void addConnection() {
        Network network = mainWindow.getNetwork();
        if (network.getActiveNodes().size() < 2) {
            mainWindow.showError("Se necesitan al menos 2 nodos para crear una conexión");
            return;
        }
        
        int latency = (Integer) latencySpinner.getValue();
        NetworkPanel networkPanel = mainWindow.getNetworkPanel();
        
        // Activar modo de agregar conexión
        networkPanel.setAddConnectionMode(true, latency);
    }
    
    private void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo de nodos");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        
        if (fileChooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            File nodesFile = fileChooser.getSelectedFile();
            
            fileChooser.setDialogTitle("Seleccionar archivo de conexiones");
            if (fileChooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
                File connectionsFile = fileChooser.getSelectedFile();
                
                try {
                    Network importedNetwork = CSVManager.importFromCSV(nodesFile.getPath(), connectionsFile.getPath());
                    Network currentNetwork = mainWindow.getNetwork();
                    
                    // Limpiar red actual
                    currentNetwork.clear();
                    
                    // Copiar nodos
                    for (Node node : importedNetwork.getAllNodes()) {
                        currentNetwork.addNodeWithId(node.getId(), node.getName(), node.getType(), 
                                                   node.getPosition().x, node.getPosition().y);
                        if (!node.isActive()) {
                            currentNetwork.getNode(node.getId()).setActive(false);
                        }
                    }
                    
                    // Copiar conexiones
                    for (Connection conn : importedNetwork.getAllConnections()) {
                        currentNetwork.addConnection(conn.getFromNode().getId(), conn.getToNode().getId(), conn.getLatency());
                        if (!conn.isActive()) {
                            currentNetwork.getConnection(conn.getFromNode().getId(), conn.getToNode().getId()).setActive(false);
                        }
                    }
                    
                    mainWindow.updateFloydWarshall();
                    mainWindow.showInfo("Red importada exitosamente");
                } catch (IOException ex) {
                    mainWindow.showError("Error al importar: " + ex.getMessage());
                }
            }
        }
    }
    
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar archivo de nodos");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        
        if (fileChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
            File nodesFile = fileChooser.getSelectedFile();
            
            fileChooser.setDialogTitle("Guardar archivo de conexiones");
            if (fileChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
                File connectionsFile = fileChooser.getSelectedFile();
                
                try {
                    CSVManager.exportToCSV(mainWindow.getNetwork(), nodesFile.getPath(), connectionsFile.getPath());
                    mainWindow.showInfo("Red exportada exitosamente");
                } catch (IOException ex) {
                    mainWindow.showError("Error al exportar: " + ex.getMessage());
                }
            }
        }
    }
    
    private void calculateRoutes() {
        mainWindow.updateFloydWarshall();
        mainWindow.showInfo("Rutas calculadas usando Floyd-Warshall");
    }
    
    private void showMatrices() {
        MatricesWindow matricesWindow = new MatricesWindow(mainWindow);
        matricesWindow.setVisible(true);
    }
    
    private void simulateFailure() {
        // Crear diálogo para seleccionar tipo de falla
        String[] options = {"Falla de Nodo", "Falla de Conexión", "Análisis de Impacto"};
        int choice = JOptionPane.showOptionDialog(mainWindow,
            "Seleccionar tipo de falla a simular:", "Simular Falla",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
        
        switch (choice) {
            case 0:
                simulateNodeFailure();
                break;
            case 1:
                simulateConnectionFailure();
                break;
            case 2:
                analyzeConnectionFailure();
                break;
        }
    }
    
    private void simulateNodeFailure() {
        Network network = mainWindow.getNetwork();
        if (network.getActiveNodes().isEmpty()) {
            mainWindow.showError("No hay nodos activos para simular falla");
            return;
        }
        
        Node[] nodes = network.getActiveNodes().toArray(new Node[0]);
        Node selectedNode = (Node) JOptionPane.showInputDialog(mainWindow,
            "Seleccionar nodo para simular falla:", "Simular Falla de Nodo",
            JOptionPane.QUESTION_MESSAGE, null, nodes, nodes[0]);
        
        if (selectedNode != null) {
            network.removeNode(selectedNode.getId());
            mainWindow.updateFloydWarshall();
            mainWindow.showInfo("Falla simulada en nodo: " + selectedNode.getName());
        }
    }
    
    private void simulateConnectionFailure() {
        Network network = mainWindow.getNetwork();
        if (network.getActiveConnections().isEmpty()) {
            mainWindow.showError("No hay conexiones activas para simular falla");
            return;
        }
        
        Connection[] connections = network.getActiveConnections().toArray(new Connection[0]);
        Connection selectedConnection = (Connection) JOptionPane.showInputDialog(mainWindow,
            "Seleccionar conexión para simular falla:", "Simular Falla de Conexión",
            JOptionPane.QUESTION_MESSAGE, null, connections, connections[0]);
        
        if (selectedConnection != null) {
            boolean success = network.simulateConnectionFailure(
                selectedConnection.getFromNode().getId(), 
                selectedConnection.getToNode().getId()
            );
            
            if (success) {
                mainWindow.updateFloydWarshall();
                mainWindow.showInfo("Falla simulada en conexión: " + 
                    selectedConnection.getFromNode().getName() + " -> " + 
                    selectedConnection.getToNode().getName());
            } else {
                mainWindow.showError("No se pudo simular la falla en la conexión");
            }
        }
    }
    
    private void analyzeConnectionFailure() {
        Network network = mainWindow.getNetwork();
        if (network.getActiveConnections().isEmpty()) {
            mainWindow.showError("No hay conexiones activas para analizar");
            return;
        }
        
        Connection[] connections = network.getActiveConnections().toArray(new Connection[0]);
        Connection selectedConnection = (Connection) JOptionPane.showInputDialog(mainWindow,
            "Seleccionar conexión para analizar impacto:", "Análisis de Impacto",
            JOptionPane.QUESTION_MESSAGE, null, connections, connections[0]);
        
        if (selectedConnection != null) {
            String analysis = network.analyzeConnectionFailureImpact(
                selectedConnection.getFromNode().getId(), 
                selectedConnection.getToNode().getId()
            );
            
            // Mostrar análisis en un diálogo con scroll
            JTextArea textArea = new JTextArea(analysis);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));
            
            JOptionPane.showMessageDialog(mainWindow, scrollPane, 
                "Análisis de Impacto - " + selectedConnection.toString(),
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void restoreConnection() {
        Network network = mainWindow.getNetwork();
        if (network.getFailedConnections().isEmpty()) {
            mainWindow.showError("No hay conexiones fallidas para restaurar");
            return;
        }
        
        Connection[] failedConnections = network.getFailedConnections().toArray(new Connection[0]);
        Connection selectedConnection = (Connection) JOptionPane.showInputDialog(mainWindow,
            "Seleccionar conexión para restaurar:", "Restaurar Conexión",
            JOptionPane.QUESTION_MESSAGE, null, failedConnections, failedConnections[0]);
        
        if (selectedConnection != null) {
            boolean success = network.restoreConnection(
                selectedConnection.getFromNode().getId(), 
                selectedConnection.getToNode().getId()
            );
            
            if (success) {
                mainWindow.updateFloydWarshall();
                mainWindow.showInfo("Conexión restaurada: " + 
                    selectedConnection.getFromNode().getName() + " -> " + 
                    selectedConnection.getToNode().getName());
            } else {
                mainWindow.showError("No se pudo restaurar la conexión");
            }
        }
    }
    
    private void showRouteTree() {
        Network network = mainWindow.getNetwork();
        if (network.getActiveNodes().size() < 2) {
            mainWindow.showError("Se necesitan al menos 2 nodos activos para mostrar el árbol de rutas");
            return;
        }
        
        // Crear y mostrar la ventana del árbol de rutas
        RouteTreeWindow routeTreeWindow = new RouteTreeWindow(mainWindow);
        routeTreeWindow.setVisible(true);
    }
    
    private void clearNetwork() {
        int result = JOptionPane.showConfirmDialog(mainWindow,
            "¿Está seguro de que desea limpiar toda la red?",
            "Limpiar Red", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            mainWindow.getNetwork().clear();
            mainWindow.updateFloydWarshall();
        }
    }
    
    private void deleteNode() {
        Network network = mainWindow.getNetwork();
        if (network.getActiveNodes().isEmpty()) {
            mainWindow.showError("No hay nodos activos para eliminar");
            return;
        }
        
        Node[] nodes = network.getActiveNodes().toArray(new Node[0]);
        Node selectedNode = (Node) JOptionPane.showInputDialog(mainWindow,
            "Seleccionar nodo para eliminar:", "Eliminar Nodo",
            JOptionPane.QUESTION_MESSAGE, null, nodes, nodes[0]);
        
        if (selectedNode != null) {
            int result = JOptionPane.showConfirmDialog(mainWindow,
                "¿Está seguro de que desea eliminar el nodo '" + selectedNode.getName() + "'?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            
                    if (result == JOptionPane.YES_OPTION) {
            network.removeNode(selectedNode.getId());
            mainWindow.updateFloydWarshall();
        }
        }
    }
    
    private void deleteConnection() {
        Network network = mainWindow.getNetwork();
        if (network.getActiveConnections().isEmpty()) {
            mainWindow.showError("No hay conexiones activas para eliminar");
            return;
        }
        
        Connection[] connections = network.getActiveConnections().toArray(new Connection[0]);
        Connection selectedConnection = (Connection) JOptionPane.showInputDialog(mainWindow,
            "Seleccionar conexión para eliminar:", "Eliminar Conexión",
            JOptionPane.QUESTION_MESSAGE, null, connections, connections[0]);
        
        if (selectedConnection != null) {
            int result = JOptionPane.showConfirmDialog(mainWindow,
                "¿Está seguro de que desea eliminar la conexión '" + 
                selectedConnection.getFromNode().getName() + " -> " + 
                selectedConnection.getToNode().getName() + "'?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                network.removeConnection(selectedConnection.getFromNode().getId(), 
                                       selectedConnection.getToNode().getId());
                mainWindow.updateFloydWarshall();
            }
        }
    }
    
    private void simulatePacket() {
        Network network = mainWindow.getNetwork();
        if (network.getActiveNodes().size() < 2) {
            mainWindow.showError("Se necesitan al menos 2 nodos activos para simular paquete");
            return;
        }
        
        // Crear diálogo para seleccionar origen y destino
        JDialog dialog = new JDialog(mainWindow, "Simular Envío de Paquete", true);
        dialog.setLayout(new GridLayout(4, 2));
        
        JComboBox<Node> fromCombo = new JComboBox<>(network.getActiveNodes().toArray(new Node[0]));
        JComboBox<Node> toCombo = new JComboBox<>(network.getActiveNodes().toArray(new Node[0]));
        
        dialog.add(new JLabel("Origen:"));
        dialog.add(fromCombo);
        dialog.add(new JLabel("Destino:"));
        dialog.add(toCombo);
        
        JButton okButton = new JButton("Simular");
        JButton cancelButton = new JButton("Cancelar");
        
        okButton.addActionListener(e -> {
            Node from = (Node) fromCombo.getSelectedItem();
            Node to = (Node) toCombo.getSelectedItem();
            
            if (from != null && to != null && from != to) {
                dialog.dispose();
                startPacketSimulation(from, to);
            } else {
                mainWindow.showError("Seleccione nodos diferentes");
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(okButton);
        dialog.add(cancelButton);
        
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
    }
    
    private void startPacketSimulation(Node from, Node to) {
        // Crear ventana de simulación
        PacketSimulationWindow simulationWindow = new PacketSimulationWindow(mainWindow, from, to);
        simulationWindow.setVisible(true);
    }
} 