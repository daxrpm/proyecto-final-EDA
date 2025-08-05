package main.gui;

import com.formdev.flatlaf.FlatLightLaf;
import main.model.Network;
import main.algorithms.FloydWarshall;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal de la aplicación
 */
public class MainWindow extends JFrame {
    private Network network;
    private FloydWarshall floydWarshall;
    
    // Paneles principales
    private ControlPanel controlPanel;
    private NetworkPanel networkPanel;
    private InfoPanel infoPanel;
    
    public MainWindow() {
        // Configurar FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Error al configurar FlatLaf: " + e.getMessage());
        }
        
        // Inicializar modelo
        network = new Network();
        floydWarshall = new FloydWarshall(network);
        
        // Configurar ventana
        setupWindow();
        setupComponents();
        setupLayout();
        
        // Configurar eventos
        setupEvents();
    }
    
    private void setupWindow() {
        setTitle("Simulador de Red de Computadoras");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
    }
    
    private void setupComponents() {
        // Crear paneles
        controlPanel = new ControlPanel(this);
        networkPanel = new NetworkPanel(this);
        infoPanel = new InfoPanel(this);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel izquierdo - Controles
        add(controlPanel, BorderLayout.WEST);
        
        // Panel central - Visualización de red
        add(networkPanel, BorderLayout.CENTER);
        
        // Panel derecho - Información
        add(infoPanel, BorderLayout.EAST);
    }
    
    private void setupEvents() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Guardar configuración si es necesario
                System.exit(0);
            }
        });
    }
    
    // Getters para acceder a los componentes
    public Network getNetwork() {
        return network;
    }
    
    public FloydWarshall getFloydWarshall() {
        return floydWarshall;
    }
    
    public NetworkPanel getNetworkPanel() {
        return networkPanel;
    }
    
    public InfoPanel getInfoPanel() {
        return infoPanel;
    }
    
    /**
     * Actualiza el algoritmo Floyd-Warshall cuando cambia la red
     */
    public void updateFloydWarshall() {
        floydWarshall = new FloydWarshall(network);
        floydWarshall.execute();
        
        // Actualizar paneles
        networkPanel.repaint();
        infoPanel.updateInfo();
    }
    
    /**
     * Muestra un mensaje de información
     */
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    /**
     * Muestra un mensaje de error
     */
    public void showError(String message) {
        showMessage(message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Muestra un mensaje de información
     */
    public void showInfo(String message) {
        showMessage(message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Método principal para ejecutar la aplicación
     */
    public static void main(String[] args) {
        // Configurar FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Error al configurar FlatLaf: " + e.getMessage());
        }
        
        // Ejecutar en EDT
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);
        });
    }
} 