package main.gui;

import main.model.Network;
import main.model.Node;
import main.model.Connection;
import main.algorithms.FloydWarshall;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Panel para visualizar la red gráficamente
 */
public class NetworkPanel extends JPanel {
    private MainWindow mainWindow;
    private Network network;
    private FloydWarshall floydWarshall;
    
    // Variables para interacción
    private Node selectedNode;
    private Node draggedNode;
    private Point dragOffset;
    private boolean isDragging = false;
    
    // Variables para modo de edición
    private boolean isAddNodeMode = false;
    private boolean isAddConnectionMode = false;
    private Node.NodeType selectedNodeType = Node.NodeType.PC;
    private Node connectionStartNode = null;
    private int connectionLatency = 10;
    
    // Variables para simulación visual
    private boolean isSimulating = false;
    private List<Integer> simulationPath = null;
    private int currentSimulationStep = 0;
    private Timer simulationTimer;
    private Color packetColor = new Color(255, 100, 100);
    private Point packetPosition = null;
    private Point packetTarget = null;
    private double packetProgress = 0.0;
    private int animationSpeed = 50; // ms entre frames
    
    // Colores
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color GRID_COLOR = new Color(233, 236, 239);
    private static final Color NODE_COLOR = new Color(52, 152, 219);
    private static final Color NODE_SELECTED_COLOR = new Color(231, 76, 60);
    private static final Color NODE_FAILED_COLOR = new Color(128, 128, 128);
    private static final Color CONNECTION_COLOR = new Color(149, 165, 166);
    private static final Color CONNECTION_ACTIVE_COLOR = new Color(46, 204, 113);
    private static final Color CONNECTION_FAILED_COLOR = new Color(231, 76, 60);
    
    public NetworkPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.network = mainWindow.getNetwork();
        this.floydWarshall = mainWindow.getFloydWarshall();
        
        setupPanel();
        setupEvents();
    }
    
    private void setupPanel() {
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(600, 600));
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }
    
    private void setupEvents() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClicked(e);
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
    }
    
    private void handleMousePressed(MouseEvent e) {
        Point p = e.getPoint();
        Node node = findNodeAt(p);
        
        if (node != null) {
            selectedNode = node;
            draggedNode = node;
            dragOffset = new Point(p.x - node.getPosition().x, p.y - node.getPosition().y);
            isDragging = true;
            repaint();
        }
    }
    
    private void handleMouseReleased(MouseEvent e) {
        if (isDragging) {
            isDragging = false;
            draggedNode = null;
            mainWindow.updateFloydWarshall();
        }
    }
    
    private void handleMouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        
        if (isAddNodeMode) {
            // Modo agregar nodo
            addNodeAt(p);
        } else if (isAddConnectionMode) {
            // Modo agregar conexión
            handleConnectionClick(p);
        } else {
            // Modo normal
            if (e.getClickCount() == 2) {
                Node node = findNodeAt(p);
                if (node != null) {
                    showNodeInfo(node);
                }
            }
        }
    }
    
    private void addNodeAt(Point p) {
        String name = JOptionPane.showInputDialog(mainWindow, 
            "Nombre del nodo:", "Agregar " + selectedNodeType.getDisplayName(), 
            JOptionPane.QUESTION_MESSAGE);
        
        if (name != null && !name.trim().isEmpty()) {
            Network network = mainWindow.getNetwork();
            Node node = network.addNode(name.trim(), selectedNodeType, p.x, p.y);
            mainWindow.updateFloydWarshall();
            
            // Salir del modo de edición
            setAddNodeMode(false, null);
        }
    }
    
    private void handleConnectionClick(Point p) {
        Node clickedNode = findNodeAt(p);
        
        if (clickedNode == null) {
            // Click en espacio vacío - cancelar conexión
            connectionStartNode = null;
            return;
        }
        
        if (connectionStartNode == null) {
            // Primer click - seleccionar nodo origen
            connectionStartNode = clickedNode;
        } else if (connectionStartNode != clickedNode) {
            // Segundo click - crear conexión
            try {
                Network network = mainWindow.getNetwork();
                network.addConnection(connectionStartNode.getId(), clickedNode.getId(), connectionLatency);
                mainWindow.updateFloydWarshall();
                
                // Salir del modo de edición
                setAddConnectionMode(false, 0);
            } catch (IllegalArgumentException ex) {
                mainWindow.showError("Error al crear conexión: " + ex.getMessage());
            }
        } else {
            // Click en el mismo nodo - cancelar
            connectionStartNode = null;
        }
    }
    
    private void handleMouseDragged(MouseEvent e) {
        if (isDragging && draggedNode != null) {
            Point p = e.getPoint();
            Point newPos = new Point(p.x - dragOffset.x, p.y - dragOffset.y);
            
            // Mantener el nodo dentro del panel
            newPos.x = Math.max(20, Math.min(newPos.x, getWidth() - 40));
            newPos.y = Math.max(20, Math.min(newPos.y, getHeight() - 40));
            
            draggedNode.setPosition(newPos);
            repaint();
        }
    }
    
    private Node findNodeAt(Point p) {
        for (Node node : network.getAllNodes()) {
            if (node.isActive() && isPointInNode(p, node)) {
                return node;
            }
        }
        return null;
    }
    
    private boolean isPointInNode(Point p, Node node) {
        Point pos = node.getPosition();
        int radius = 20;
        return Math.sqrt(Math.pow(p.x - pos.x, 2) + Math.pow(p.y - pos.y, 2)) <= radius;
    }
    
    private void showNodeInfo(Node node) {
        StringBuilder info = new StringBuilder();
        info.append("Nodo: ").append(node.getName()).append("\n");
        info.append("Tipo: ").append(node.getType().getDisplayName()).append("\n");
        info.append("ID: ").append(node.getId()).append("\n");
        info.append("Estado: ").append(node.isActive() ? "Activo" : "Fallido").append("\n");
        info.append("Posición: (").append(node.getPosition().x).append(", ").append(node.getPosition().y).append(")");
        
        JOptionPane.showMessageDialog(this, info.toString(), "Información del Nodo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Configurar antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Dibujar grid
        drawGrid(g2d);
        
        // Dibujar conexiones
        drawConnections(g2d);
        
        // Dibujar simulación si está activa
        if (isSimulating && simulationPath != null) {
            drawSimulation(g2d);
        }
        
        // Dibujar nodos
        drawNodes(g2d);
    }
    
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(new BasicStroke(1));
        
        int gridSize = 20;
        for (int x = 0; x < getWidth(); x += gridSize) {
            g2d.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += gridSize) {
            g2d.drawLine(0, y, getWidth(), y);
        }
    }
    
    private void drawConnections(Graphics2D g2d) {
        for (Connection connection : network.getAllConnections()) {
            Node from = connection.getFromNode();
            Node to = connection.getToNode();
            
            // Solo dibujar conexiones si ambos nodos están activos
            if (!from.isActive() || !to.isActive()) continue;
            
            Point fromPos = from.getPosition();
            Point toPos = to.getPosition();
            
            // Color y estilo de la conexión
            if (connection.isActive()) {
                g2d.setColor(CONNECTION_ACTIVE_COLOR);
                g2d.setStroke(new BasicStroke(2));
            } else {
                g2d.setColor(CONNECTION_FAILED_COLOR);
                // Línea punteada para conexiones fallidas
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{8, 4}, 0));
            }
            
            g2d.drawLine(fromPos.x, fromPos.y, toPos.x, toPos.y);
            
            // Dibujar flecha solo para conexiones activas
            if (connection.isActive()) {
                drawArrow(g2d, fromPos, toPos);
            }
            
            // Dibujar latencia
            drawLatency(g2d, fromPos, toPos, connection.getLatency());
        }
    }
    
    private void drawArrow(Graphics2D g2d, Point from, Point to) {
        // Calcular ángulo
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        
        // Ajustar el punto final para que la flecha no se superponga con el nodo
        int nodeRadius = 20;
        int endX = to.x - (int) (nodeRadius * Math.cos(angle));
        int endY = to.y - (int) (nodeRadius * Math.sin(angle));
        
        // Dibujar línea principal
        g2d.drawLine(from.x, from.y, endX, endY);
        
        // Dibujar flecha en el extremo
        int arrowLength = 15;
        int arrowAngle = 25;
        
        int x1 = endX - (int) (arrowLength * Math.cos(angle - Math.toRadians(arrowAngle)));
        int y1 = endY - (int) (arrowLength * Math.sin(angle - Math.toRadians(arrowAngle)));
        int x2 = endX - (int) (arrowLength * Math.cos(angle + Math.toRadians(arrowAngle)));
        int y2 = endY - (int) (arrowLength * Math.sin(angle + Math.toRadians(arrowAngle)));
        
        // Dibujar flecha con relleno
        int[] xPoints = {endX, x1, x2};
        int[] yPoints = {endY, y1, y2};
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    private void drawLatency(Graphics2D g2d, Point from, Point to, int latency) {
        // Punto medio de la línea
        int midX = (from.x + to.x) / 2;
        int midY = (from.y + to.y) / 2;
        
        // Fondo para el texto
        String text = latency + "ms";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(midX - textWidth/2 - 2, midY - textHeight/2 - 2, textWidth + 4, textHeight + 4);
        
        // Texto
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, midX - textWidth/2, midY + textHeight/2 - 2);
    }
    
    private void drawNodes(Graphics2D g2d) {
        for (Node node : network.getAllNodes()) {
            Point pos = node.getPosition();
            int radius = 20;
            
            // Color del nodo
            if (!node.isActive()) {
                g2d.setColor(NODE_FAILED_COLOR);
            } else if (node == selectedNode) {
                g2d.setColor(NODE_SELECTED_COLOR);
            } else {
                g2d.setColor(NODE_COLOR);
            }
            
            // Dibujar círculo
            g2d.fillOval(pos.x - radius, pos.y - radius, radius * 2, radius * 2);
            
            // Borde
            if (!node.isActive()) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{4, 2}, 0));
            } else {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
            }
            g2d.drawOval(pos.x - radius, pos.y - radius, radius * 2, radius * 2);
            
            // Dibujar texto
            if (!node.isActive()) {
                g2d.setColor(Color.LIGHT_GRAY);
            } else {
                g2d.setColor(Color.WHITE);
            }
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            
            String text = node.getName();
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            
            g2d.drawString(text, pos.x - textWidth/2, pos.y + textHeight/2 - 2);
            
            // Dibujar ID
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            String idText = String.valueOf(node.getId());
            fm = g2d.getFontMetrics();
            textWidth = fm.stringWidth(idText);
            
            g2d.drawString(idText, pos.x - textWidth/2, pos.y - radius - 5);
        }
    }
    
    private void drawSimulation(Graphics2D g2d) {
        if (simulationPath == null || currentSimulationStep >= simulationPath.size()) {
            return;
        }
        
        // Dibujar ruta completa con línea punteada y flecha
        g2d.setColor(new Color(255, 100, 100, 150));
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{15, 8}, 0));
        
        for (int i = 0; i < simulationPath.size() - 1; i++) {
            Node from = network.getNode(simulationPath.get(i));
            Node to = network.getNode(simulationPath.get(i + 1));
            
            if (from != null && to != null) {
                Point fromPos = from.getPosition();
                Point toPos = to.getPosition();
                
                // Dibujar línea punteada
                g2d.drawLine(fromPos.x, fromPos.y, toPos.x, toPos.y);
                
                // Dibujar flecha en el medio de la línea
                drawRouteArrow(g2d, fromPos, toPos);
            }
        }
        
        // Dibujar paquete/carta animado
        if (packetPosition != null) {
            drawPacket(g2d, packetPosition);
        }
        
        // Restaurar stroke normal
        g2d.setStroke(new BasicStroke(2));
    }
    
    private void drawRouteArrow(Graphics2D g2d, Point from, Point to) {
        // Calcular punto medio
        int midX = (from.x + to.x) / 2;
        int midY = (from.y + to.y) / 2;
        
        // Calcular ángulo
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        
        // Dibujar flecha
        int arrowLength = 12;
        int arrowAngle = 20;
        
        int x1 = midX - (int) (arrowLength * Math.cos(angle - Math.toRadians(arrowAngle)));
        int y1 = midY - (int) (arrowLength * Math.sin(angle - Math.toRadians(arrowAngle)));
        int x2 = midX - (int) (arrowLength * Math.cos(angle + Math.toRadians(arrowAngle)));
        int y2 = midY - (int) (arrowLength * Math.sin(angle + Math.toRadians(arrowAngle)));
        
        // Rellenar flecha
        g2d.setColor(new Color(255, 100, 100, 200));
        int[] xPoints = {midX, x1, x2};
        int[] yPoints = {midY, y1, y2};
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    private void drawPacket(Graphics2D g2d, Point pos) {
        // Dibujar carta/paquete como en Cisco Packet Tracer
        int width = 24;
        int height = 16;
        
        // Sombra
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRect(pos.x - width/2 + 2, pos.y - height/2 + 2, width, height);
        
        // Cuerpo de la carta
        g2d.setColor(new Color(255, 255, 200));
        g2d.fillRect(pos.x - width/2, pos.y - height/2, width, height);
        
        // Borde de la carta
        g2d.setColor(new Color(139, 69, 19));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(pos.x - width/2, pos.y - height/2, width, height);
        
        // Líneas de la carta (como un sobre)
        g2d.setColor(new Color(139, 69, 19));
        g2d.drawLine(pos.x - width/2 + 4, pos.y - height/2 + 4, pos.x + width/2 - 4, pos.y - height/2 + 4);
        g2d.drawLine(pos.x - width/2 + 4, pos.y - height/2 + 4, pos.x - width/2 + 4, pos.y + height/2 - 4);
        g2d.drawLine(pos.x + width/2 - 4, pos.y - height/2 + 4, pos.x + width/2 - 4, pos.y + height/2 - 4);
        g2d.drawLine(pos.x - width/2 + 4, pos.y + height/2 - 4, pos.x + width/2 - 4, pos.y + height/2 - 4);
        
        // Sello/logo en el centro
        g2d.setColor(new Color(255, 0, 0));
        g2d.fillOval(pos.x - 3, pos.y - 3, 6, 6);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        g2d.drawString("P", pos.x - 2, pos.y + 2);
        
        // Efecto de brillo
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillRect(pos.x - width/2 + 1, pos.y - height/2 + 1, width - 2, 3);
    }
    
    public void updateNetwork() {
        this.network = mainWindow.getNetwork();
        this.floydWarshall = mainWindow.getFloydWarshall();
        repaint();
    }
    
    // Métodos para modo de edición
    public void setAddNodeMode(boolean enabled, Node.NodeType nodeType) {
        this.isAddNodeMode = enabled;
        this.isAddConnectionMode = false;
        this.selectedNodeType = nodeType;
        this.connectionStartNode = null;
        
        if (enabled) {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    public void setAddConnectionMode(boolean enabled, int latency) {
        this.isAddConnectionMode = enabled;
        this.isAddNodeMode = false;
        this.connectionLatency = latency;
        this.connectionStartNode = null;
        
        if (enabled) {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    public void setDeleteMode(boolean enabled) {
        this.isAddNodeMode = false;
        this.isAddConnectionMode = false;
        this.connectionStartNode = null;
        
        if (enabled) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    public boolean isInEditMode() {
        return isAddNodeMode || isAddConnectionMode;
    }
    
    // Métodos para simulación visual
    public void startVisualSimulation(List<Integer> path) {
        this.simulationPath = path;
        this.currentSimulationStep = 0;
        this.isSimulating = true;
        this.packetProgress = 0.0;
        
        if (path != null && !path.isEmpty()) {
            Node startNode = network.getNode(path.get(0));
            if (startNode != null) {
                this.packetPosition = new Point(startNode.getPosition());
                this.packetTarget = new Point(startNode.getPosition());
            }
        }
        
        // Crear timer para animación suave
        simulationTimer = new Timer(animationSpeed, e -> {
            updatePacketAnimation();
        });
        
        simulationTimer.start();
        repaint();
    }
    
    private void updatePacketAnimation() {
        if (simulationPath == null || currentSimulationStep >= simulationPath.size()) {
            stopVisualSimulation();
            return;
        }
        
        // Actualizar progreso del paquete
        packetProgress += 0.02; // Incremento suave
        
        if (packetProgress >= 1.0) {
            // Llegamos al nodo destino, pasar al siguiente
            currentSimulationStep++;
            packetProgress = 0.0;
            
            if (currentSimulationStep < simulationPath.size()) {
                // Actualizar posición inicial y destino
                Node currentNode = network.getNode(simulationPath.get(currentSimulationStep - 1));
                Node nextNode = network.getNode(simulationPath.get(currentSimulationStep));
                
                if (currentNode != null && nextNode != null) {
                    packetPosition = new Point(currentNode.getPosition());
                    packetTarget = new Point(nextNode.getPosition());
                }
            }
        } else {
            // Interpolar posición entre nodos
            if (packetPosition != null && packetTarget != null) {
                int x = (int) (packetPosition.x + (packetTarget.x - packetPosition.x) * packetProgress);
                int y = (int) (packetPosition.y + (packetTarget.y - packetPosition.y) * packetProgress);
                packetPosition = new Point(x, y);
            }
        }
        
        repaint();
    }
    
    public void stopVisualSimulation() {
        isSimulating = false;
        if (simulationTimer != null) {
            simulationTimer.stop();
            simulationTimer = null;
        }
        packetPosition = null;
        packetTarget = null;
        packetProgress = 0.0;
        repaint();
    }
    
    public boolean isSimulating() {
        return isSimulating;
    }
} 