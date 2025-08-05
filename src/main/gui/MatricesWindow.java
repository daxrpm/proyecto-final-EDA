package main.gui;

import main.model.Network;
import main.model.Node;
import main.algorithms.FloydWarshall;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ventana para mostrar las matrices de Floyd-Warshall de forma clara
 */
public class MatricesWindow extends JFrame {
    private MainWindow mainWindow;
    private Network network;
    private FloydWarshall floydWarshall;
    
    // Componentes
    private JTabbedPane tabbedPane;
    private JTable distancesTable;
    private JTable nextTable;
    private JTextArea explanationArea;
    
    public MatricesWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.network = mainWindow.getNetwork();
        this.floydWarshall = mainWindow.getFloydWarshall();
        
        setupWindow();
        setupComponents();
        setupLayout();
        updateMatrices();
    }
    
    private void setupWindow() {
        setTitle("Matrices de Floyd-Warshall");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(mainWindow);
        setResizable(true);
    }
    
    private void setupComponents() {
        tabbedPane = new JTabbedPane();
        
        // Tabla de distancias
        distancesTable = new JTable();
        distancesTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        distancesTable.setRowHeight(25);
        
        // Tabla de siguiente nodo
        nextTable = new JTable();
        nextTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        nextTable.setRowHeight(25);
        
        // Área de explicación
        explanationArea = new JTextArea();
        explanationArea.setEditable(false);
        explanationArea.setFont(new Font("Arial", Font.PLAIN, 12));
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Configurar pestañas
        JScrollPane distancesScrollPane = new JScrollPane(distancesTable);
        JScrollPane nextScrollPane = new JScrollPane(nextTable);
        JScrollPane explanationScrollPane = new JScrollPane(explanationArea);
        
        tabbedPane.addTab("Matriz de Distancias", new ImageIcon(), distancesScrollPane, "Muestra las distancias mínimas entre nodos");
        tabbedPane.addTab("Matriz de Siguiente Nodo", new ImageIcon(), nextScrollPane, "Muestra el siguiente nodo en la ruta más corta");
        tabbedPane.addTab("Explicación", new ImageIcon(), explanationScrollPane, "Explicación de las matrices");
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    public void updateMatrices() {
        this.network = mainWindow.getNetwork();
        this.floydWarshall = mainWindow.getFloydWarshall();
        
        if (network.getActiveNodes().size() < 2) {
            showNoDataMessage();
            return;
        }
        
        updateDistancesTable();
        updateNextTable();
        updateExplanation();
    }
    
    private void updateDistancesTable() {
        List<Node> activeNodes = network.getActiveNodes();
        double[][] distances = floydWarshall.getDistanceMatrix();
        
        // Crear modelo de tabla
        String[] columnNames = new String[activeNodes.size() + 1];
        columnNames[0] = "Desde/Hacia";
        for (int i = 0; i < activeNodes.size(); i++) {
            columnNames[i + 1] = activeNodes.get(i).getName();
        }
        
        Object[][] data = new Object[activeNodes.size()][activeNodes.size() + 1];
        for (int i = 0; i < activeNodes.size(); i++) {
            data[i][0] = activeNodes.get(i).getName();
            for (int j = 0; j < activeNodes.size(); j++) {
                if (distances[i][j] == Double.POSITIVE_INFINITY) {
                    data[i][j + 1] = "∞";
                } else {
                    data[i][j + 1] = String.format("%.0f", distances[i][j]);
                }
            }
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        distancesTable.setModel(model);
        
        // Configurar colores
        distancesTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 0) {
                    // Columna de nombres
                    setBackground(new Color(240, 240, 240));
                    setForeground(Color.BLACK);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (row == column - 1) {
                    // Diagonal principal
                    setBackground(new Color(255, 255, 200));
                    setForeground(Color.BLACK);
                } else if ("∞".equals(value)) {
                    // Sin conexión
                    setBackground(new Color(255, 200, 200));
                    setForeground(Color.RED);
                } else {
                    // Conexión normal
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
    }
    
    private void updateNextTable() {
        List<Node> activeNodes = network.getActiveNodes();
        int[][] next = floydWarshall.getNextMatrix();
        
        // Crear modelo de tabla
        String[] columnNames = new String[activeNodes.size() + 1];
        columnNames[0] = "Desde/Hacia";
        for (int i = 0; i < activeNodes.size(); i++) {
            columnNames[i + 1] = activeNodes.get(i).getName();
        }
        
        Object[][] data = new Object[activeNodes.size()][activeNodes.size() + 1];
        for (int i = 0; i < activeNodes.size(); i++) {
            data[i][0] = activeNodes.get(i).getName();
            for (int j = 0; j < activeNodes.size(); j++) {
                if (next[i][j] == -1) {
                    data[i][j + 1] = "-";
                } else {
                    data[i][j + 1] = activeNodes.get(next[i][j]).getName();
                }
            }
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        nextTable.setModel(model);
        
        // Configurar colores
        nextTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 0) {
                    // Columna de nombres
                    setBackground(new Color(240, 240, 240));
                    setForeground(Color.BLACK);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (row == column - 1) {
                    // Diagonal principal
                    setBackground(new Color(255, 255, 200));
                    setForeground(Color.BLACK);
                } else if ("-".equals(value)) {
                    // Sin conexión
                    setBackground(new Color(255, 200, 200));
                    setForeground(Color.RED);
                } else {
                    // Conexión normal
                    setBackground(new Color(200, 255, 200));
                    setForeground(Color.BLACK);
                }
                
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
    }
    
    private void updateExplanation() {
        StringBuilder explanation = new StringBuilder();
        
        explanation.append("📊 EXPLICACIÓN DE LAS MATRICES DE FLOYD-WARSHALL\n");
        explanation.append("================================================\n\n");
        
        explanation.append("🎯 MATRIZ DE DISTANCIAS:\n");
        explanation.append("• Muestra la latencia mínima (en milisegundos) entre cada par de nodos\n");
        explanation.append("• Los valores en la diagonal principal son siempre 0 (nodo a sí mismo)\n");
        explanation.append("• ∞ significa que no existe ruta entre esos nodos\n");
        explanation.append("• Los valores representan la suma de latencias de la ruta más corta\n\n");
        
        explanation.append("🔄 MATRIZ DE SIGUIENTE NODO:\n");
        explanation.append("• Indica cuál es el siguiente nodo en la ruta más corta\n");
        explanation.append("• Se usa para reconstruir la ruta completa\n");
        explanation.append("• '-' significa que no hay siguiente nodo (mismo nodo o sin ruta)\n");
        explanation.append("• Para ir de A a B, sigue la cadena: A → siguiente(A,B) → ... → B\n\n");
        
        explanation.append("🔍 EJEMPLO DE USO:\n");
        explanation.append("Si quieres ir de Router_Central a PC_Oficina3:\n");
        explanation.append("1. Mira en la matriz de distancias: Router_Central → PC_Oficina3 = 32ms\n");
        explanation.append("2. Mira en la matriz de siguiente nodo: Router_Central → Router_Secundario\n");
        explanation.append("3. Luego: Router_Secundario → PC_Oficina3\n");
        explanation.append("4. Ruta completa: Router_Central → Router_Secundario → PC_Oficina3\n\n");
        
        explanation.append("💡 COLORES:\n");
        explanation.append("• Amarillo: Diagonal principal (nodo a sí mismo)\n");
        explanation.append("• Verde: Conexiones válidas\n");
        explanation.append("• Rojo: Sin conexión disponible\n");
        explanation.append("• Gris: Nombres de nodos\n");
        
        explanationArea.setText(explanation.toString());
    }
    
    private void showNoDataMessage() {
        // Limpiar tablas
        distancesTable.setModel(new DefaultTableModel());
        nextTable.setModel(new DefaultTableModel());
        
        // Mostrar mensaje
        explanationArea.setText("Se necesitan al menos 2 nodos activos para mostrar las matrices de Floyd-Warshall.\n\n" +
                               "Agrega algunos nodos y conexiones, luego ejecuta 'Calcular Rutas' para ver las matrices.");
    }
} 