package main.gui;

import main.model.Network;
import main.model.Node;
import main.model.NodoArbol;
import main.algorithms.RouteTreeFinder;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Ventana para mostrar el árbol de rutas desde un origen hasta un destino
 */
public class RouteTreeWindow extends JFrame {
    private MainWindow mainWindow;
    private Network network;
    private RouteTreeFinder routeFinder;
    
    // Componentes
    private JComboBox<Node> sourceCombo;
    private JComboBox<Node> targetCombo;
    private JButton findRoutesButton;
    private JButton showStatisticsButton;
    private JTextArea treeTextArea;
    private JTextArea statisticsArea;
    private JTree routeTree;
    private JTabbedPane tabbedPane;
    private JLabel statusLabel;
    
    public RouteTreeWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.network = mainWindow.getNetwork();
        this.routeFinder = new RouteTreeFinder(network);
        
        setupWindow();
        setupComponents();
        setupLayout();
        setupEvents();
        updateNodeLists();
    }
    
    private void setupWindow() {
        setTitle("Árbol de Rutas - Todas las Rutas Posibles");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(mainWindow);
        setMinimumSize(new Dimension(600, 400));
    }
    
    private void setupComponents() {
        // ComboBoxes para selección de nodos
        sourceCombo = new JComboBox<>();
        targetCombo = new JComboBox<>();
        
        // Botones
        findRoutesButton = new JButton("Encontrar Todas las Rutas");
        showStatisticsButton = new JButton("Mostrar Estadísticas");
        
        // Áreas de texto
        treeTextArea = new JTextArea();
        treeTextArea.setEditable(false);
        treeTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        statisticsArea = new JTextArea();
        statisticsArea.setEditable(false);
        statisticsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Árbol visual
        routeTree = new JTree();
        routeTree.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Panel con pestañas
        tabbedPane = new JTabbedPane();
        
        // Label de estado
        statusLabel = new JLabel("Seleccione origen y destino para encontrar rutas");
        statusLabel.setForeground(Color.GRAY);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel superior para controles
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Configuración"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Primera fila
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Nodo Origen:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        controlPanel.add(sourceCombo, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        controlPanel.add(new JLabel("Nodo Destino:"), gbc);
        
        gbc.gridx = 3; gbc.gridy = 0;
        controlPanel.add(targetCombo, gbc);
        
        // Segunda fila
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        controlPanel.add(findRoutesButton, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2;
        controlPanel.add(showStatisticsButton, gbc);
        
        // Tercera fila
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        controlPanel.add(statusLabel, gbc);
        
        // Configurar pestañas
        JScrollPane treeScrollPane = new JScrollPane(treeTextArea);
        JScrollPane statisticsScrollPane = new JScrollPane(statisticsArea);
        JScrollPane visualTreeScrollPane = new JScrollPane(routeTree);
        
        tabbedPane.addTab("Árbol de Rutas (Texto)", treeScrollPane);
        tabbedPane.addTab("Árbol Visual", visualTreeScrollPane);
        tabbedPane.addTab("Estadísticas", statisticsScrollPane);
        
        // Layout principal
        add(controlPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void setupEvents() {
        findRoutesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findRoutes();
            }
        });
        
        showStatisticsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStatistics();
            }
        });
        
        // Evento para actualizar cuando cambian los nodos
        sourceCombo.addActionListener(e -> updateStatus());
        targetCombo.addActionListener(e -> updateStatus());
    }
    
    private void updateNodeLists() {
        List<Node> activeNodes = network.getActiveNodes();
        
        sourceCombo.removeAllItems();
        targetCombo.removeAllItems();
        
        for (Node node : activeNodes) {
            sourceCombo.addItem(node);
            targetCombo.addItem(node);
        }
        
        updateStatus();
    }
    
    private void updateStatus() {
        Node source = (Node) sourceCombo.getSelectedItem();
        Node target = (Node) targetCombo.getSelectedItem();
        
        if (source != null && target != null) {
            if (source.equals(target)) {
                statusLabel.setText("Origen y destino no pueden ser el mismo nodo");
                statusLabel.setForeground(Color.RED);
            } else {
                statusLabel.setText("Listo para encontrar rutas de " + source.getName() + " a " + target.getName());
                statusLabel.setForeground(Color.BLACK);
            }
        } else {
            statusLabel.setText("Seleccione origen y destino para encontrar rutas");
            statusLabel.setForeground(Color.GRAY);
        }
    }
    
    private void findRoutes() {
        Node source = (Node) sourceCombo.getSelectedItem();
        Node target = (Node) targetCombo.getSelectedItem();
        
        if (source == null || target == null) {
            JOptionPane.showMessageDialog(this, "Seleccione origen y destino", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (source.equals(target)) {
            JOptionPane.showMessageDialog(this, "Origen y destino no pueden ser el mismo nodo", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Mostrar cursor de espera
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {
            // Construir árbol de rutas
            NodoArbol routeTree = routeFinder.buildRouteTree(source.getId(), target.getId());
            
            if (routeTree == null) {
                treeTextArea.setText("No se encontraron rutas entre " + source.getName() + " y " + target.getName());
                this.routeTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("No hay rutas")));
                statisticsArea.setText("No hay rutas para mostrar estadísticas");
            } else {
                // Mostrar árbol en texto
                String treeText = "Árbol de rutas desde " + source.getName() + " hasta " + target.getName() + ":\n\n";
                treeText += routeTree.printTree();
                treeTextArea.setText(treeText);
                
                // Mostrar árbol visual
                DefaultMutableTreeNode rootNode = buildVisualTree(routeTree);
                this.routeTree.setModel(new DefaultTreeModel(rootNode));
                
                // Expandir todos los nodos
                expandAllNodes(this.routeTree, new TreePath(rootNode));
                
                // Mostrar estadísticas
                showStatistics();
            }
            
            statusLabel.setText("Rutas encontradas de " + source.getName() + " a " + target.getName());
            statusLabel.setForeground(Color.GREEN);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al encontrar rutas: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Error al procesar rutas");
            statusLabel.setForeground(Color.RED);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    private DefaultMutableTreeNode buildVisualTree(NodoArbol routeNode) {
        String nodeText = routeNode.getNode().getName();
        if (routeNode.getLatency() > 0) {
            nodeText += " (" + routeNode.getLatency() + "ms)";
        }
        if (routeNode.isLeaf()) {
            nodeText += " [Total: " + routeNode.getTotalLatency() + "ms]";
        }
        
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(nodeText);
        
        for (NodoArbol child : routeNode.getChildren()) {
            treeNode.add(buildVisualTree(child));
        }
        
        return treeNode;
    }
    
    private void expandAllNodes(JTree tree, TreePath parent) {
        tree.expandPath(parent);
        Object node = parent.getLastPathComponent();
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
        
        for (int i = 0; i < treeNode.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) treeNode.getChildAt(i);
            TreePath path = parent.pathByAddingChild(child);
            expandAllNodes(tree, path);
        }
    }
    
    private void showStatistics() {
        Node source = (Node) sourceCombo.getSelectedItem();
        Node target = (Node) targetCombo.getSelectedItem();
        
        if (source == null || target == null || source.equals(target)) {
            statisticsArea.setText("Seleccione origen y destino válidos para ver estadísticas");
            return;
        }
        
        String stats = routeFinder.getRouteStatistics(source.getId(), target.getId());
        statisticsArea.setText(stats);
        
        // Cambiar a la pestaña de estadísticas
        tabbedPane.setSelectedIndex(2);
    }
    
    /**
     * Actualiza la lista de nodos cuando cambia la red
     */
    public void updateNetwork() {
        this.network = mainWindow.getNetwork();
        this.routeFinder = new RouteTreeFinder(network);
        updateNodeLists();
    }
} 