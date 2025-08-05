package main.gui;

import main.model.Network;
import main.model.Node;
import main.algorithms.FloydWarshall;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Ventana para simular el envío de paquetes paso a paso
 */
public class PacketSimulationWindow extends JFrame {
    private MainWindow mainWindow;
    private Network network;
    private FloydWarshall floydWarshall;
    private Node sourceNode;
    private Node destinationNode;
    
    // Componentes
    private JTextArea logArea;
    private JScrollPane logScrollPane;
    private JButton nextStepButton;
    private JButton autoPlayButton;
    private JButton stopButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    
    // Variables de simulación
    private List<Integer> path;
    private int currentStep = 0;
    private Timer autoPlayTimer;
    private boolean isAutoPlaying = false;
    
    public PacketSimulationWindow(MainWindow mainWindow, Node source, Node destination) {
        this.mainWindow = mainWindow;
        this.network = mainWindow.getNetwork();
        this.floydWarshall = mainWindow.getFloydWarshall();
        this.sourceNode = source;
        this.destinationNode = destination;
        
        setupWindow();
        setupComponents();
        setupLayout();
        setupEvents();
        
        // Inicializar simulación
        initializeSimulation();
    }
    
    private void setupWindow() {
        setTitle("Simulación de Paquete: " + sourceNode.getName() + " → " + destinationNode.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(mainWindow);
        setResizable(false);
    }
    
    private void setupComponents() {
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        logScrollPane = new JScrollPane(logArea);
        
        nextStepButton = new JButton("Siguiente Paso");
        autoPlayButton = new JButton("Auto Play");
        stopButton = new JButton("Detener");
        stopButton.setEnabled(false);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        
        statusLabel = new JLabel("Preparando simulación...");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Información de Ruta"));
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 2));
        infoPanel.add(new JLabel("Origen: " + sourceNode.getName()));
        infoPanel.add(new JLabel("Destino: " + destinationNode.getName()));
        infoPanel.add(new JLabel("Tipo Origen: " + sourceNode.getType().getDisplayName()));
        infoPanel.add(new JLabel("Tipo Destino: " + destinationNode.getType().getDisplayName()));
        
        topPanel.add(infoPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        
        // Panel central - Log
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Log de Simulación"));
        centerPanel.add(logScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        // Panel inferior - Controles
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Controles"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(nextStepButton);
        buttonPanel.add(autoPlayButton);
        buttonPanel.add(stopButton);
        
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(statusLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(progressPanel, BorderLayout.CENTER);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEvents() {
        nextStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeNextStep();
            }
        });
        
        autoPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isAutoPlaying) {
                    stopAutoPlay();
                } else {
                    startAutoPlay();
                }
            }
        });
        
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSimulation();
            }
        });
    }
    
    private void initializeSimulation() {
        // Obtener ruta usando Floyd-Warshall
        path = floydWarshall.getShortestPath(sourceNode.getId(), destinationNode.getId());
        
        if (path == null) {
            logArea.append("❌ ERROR: No existe ruta entre " + sourceNode.getName() + " y " + destinationNode.getName() + "\n");
            logArea.append("La red puede estar desconectada o los nodos no son alcanzables.\n");
            nextStepButton.setEnabled(false);
            autoPlayButton.setEnabled(false);
            statusLabel.setText("Sin ruta disponible");
            return;
        }
        
        // Mostrar información inicial
        logArea.append("🚀 INICIANDO SIMULACIÓN DE PAQUETE\n");
        logArea.append("=====================================\n");
        logArea.append("Origen: " + sourceNode.getName() + " (ID: " + sourceNode.getId() + ")\n");
        logArea.append("Destino: " + destinationNode.getName() + " (ID: " + destinationNode.getId() + ")\n");
        logArea.append("Ruta calculada: ");
        
        for (int i = 0; i < path.size(); i++) {
            Node node = network.getNode(path.get(i));
            logArea.append(node.getName());
            if (i < path.size() - 1) {
                logArea.append(" → ");
            }
        }
        
        double totalDistance = floydWarshall.getShortestDistance(sourceNode.getId(), destinationNode.getId());
        logArea.append("\nDistancia total: " + totalDistance + "ms\n");
        logArea.append("Número de saltos: " + (path.size() - 1) + "\n\n");
        
        logArea.append("📋 MATRIZ DE SIGUIENTE NODO (Floyd-Warshall):\n");
        int[][] nextMatrix = floydWarshall.getNextMatrix();
        List<Node> activeNodes = network.getActiveNodes();
        
        // Mostrar encabezados
        logArea.append("     ");
        for (Node node : activeNodes) {
            logArea.append(String.format("%-8s", node.getName()));
        }
        logArea.append("\n");
        
        // Mostrar matriz
        for (int i = 0; i < nextMatrix.length; i++) {
            Node source = activeNodes.get(i);
            logArea.append(String.format("%-5s", source.getName()));
            
            for (int j = 0; j < nextMatrix[i].length; j++) {
                if (nextMatrix[i][j] == -1) {
                    logArea.append("X       ");
                } else {
                    Node next = activeNodes.get(nextMatrix[i][j]);
                    logArea.append(String.format("%-8s", next.getName()));
                }
            }
            logArea.append("\n");
        }
        
        logArea.append("\n🎯 PREPARADO PARA SIMULACIÓN\n");
        logArea.append("Presione 'Siguiente Paso' para comenzar...\n\n");
        
        statusLabel.setText("Listo para simular");
        progressBar.setValue(0);
    }
    
    private void executeNextStep() {
        if (path == null || currentStep >= path.size()) {
            return;
        }
        
        Node currentNode = network.getNode(path.get(currentStep));
        
        if (currentStep == 0) {
            // Primer paso - origen
            logArea.append("📍 PASO " + (currentStep + 1) + ": PAQUETE EN ORIGEN\n");
            logArea.append("   Nodo: " + currentNode.getName() + " (ID: " + currentNode.getId() + ")\n");
            logArea.append("   Acción: Paquete creado y listo para envío\n");
            logArea.append("   Estado: En cola de salida\n\n");
            
            // Iniciar simulación visual
            mainWindow.getNetworkPanel().startVisualSimulation(path);
            
        } else if (currentStep == path.size() - 1) {
            // Último paso - destino
            logArea.append("🎯 PASO " + (currentStep + 1) + ": PAQUETE EN DESTINO\n");
            logArea.append("   Nodo: " + currentNode.getName() + " (ID: " + currentNode.getId() + ")\n");
            logArea.append("   Acción: Paquete recibido exitosamente\n");
            logArea.append("   Estado: Entrega completada ✓\n\n");
            
            logArea.append("✅ SIMULACIÓN COMPLETADA\n");
            logArea.append("El paquete ha llegado a su destino siguiendo la ruta más corta.\n");
            
            nextStepButton.setEnabled(false);
            autoPlayButton.setEnabled(false);
            statusLabel.setText("Simulación completada");
            
        } else {
            // Pasos intermedios
            Node previousNode = network.getNode(path.get(currentStep - 1));
            Node nextNode = network.getNode(path.get(currentStep + 1));
            
            logArea.append("🔄 PASO " + (currentStep + 1) + ": PAQUETE EN TRANSITO\n");
            logArea.append("   Nodo actual: " + currentNode.getName() + " (ID: " + currentNode.getId() + ")\n");
            logArea.append("   Nodo anterior: " + previousNode.getName() + "\n");
            logArea.append("   Próximo nodo: " + nextNode.getName() + "\n");
            
            // Obtener latencia de la conexión
            double latency = floydWarshall.getShortestDistance(previousNode.getId(), currentNode.getId());
            logArea.append("   Latencia de llegada: " + latency + "ms\n");
            logArea.append("   Acción: Reenviando paquete al siguiente nodo\n");
            logArea.append("   Estado: En tránsito\n\n");
        }
        
        currentStep++;
        progressBar.setValue((currentStep * 100) / path.size());
        
        if (currentStep >= path.size()) {
            nextStepButton.setEnabled(false);
            autoPlayButton.setEnabled(false);
            // Detener simulación visual
            mainWindow.getNetworkPanel().stopVisualSimulation();
        }
    }
    
    private void startAutoPlay() {
        if (isAutoPlaying) return;
        
        isAutoPlaying = true;
        autoPlayButton.setText("Pausar");
        stopButton.setEnabled(true);
        
        autoPlayTimer = new Timer();
        autoPlayTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (currentStep < path.size()) {
                        executeNextStep();
                    } else {
                        stopAutoPlay();
                    }
                });
            }
        }, 0, 2000); // 2 segundos entre pasos
    }
    
    private void stopAutoPlay() {
        if (!isAutoPlaying) return;
        
        isAutoPlaying = false;
        autoPlayButton.setText("Auto Play");
        stopButton.setEnabled(false);
        
        if (autoPlayTimer != null) {
            autoPlayTimer.cancel();
            autoPlayTimer = null;
        }
    }
    
    private void stopSimulation() {
        stopAutoPlay();
        dispose();
    }
    
    @Override
    public void dispose() {
        stopAutoPlay();
        // Detener simulación visual
        mainWindow.getNetworkPanel().stopVisualSimulation();
        super.dispose();
    }
} 