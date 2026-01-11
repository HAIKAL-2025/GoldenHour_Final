package com.mycompany.aidahtestproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;

public class SalesSystemGUI extends JFrame {
    
    // Data structures
    private static ArrayList<Model> inventory = new ArrayList<>();
    
    // Specific to this window instance
    private final Employee currentUser; 

    // GUI Components
    private final JTextField txtCustomer, txtQuantity;
    private final JComboBox<String> comboModelCode;
    private final JComboBox<String> comboPayment;
    private final JButton btnProcess, btnBack;
    private final JLabel lblStaff;
    
    private final JTable inventoryTable;
    private final DefaultTableModel tableModel;

    // Helper to launch window requires the User object now
    public static void startSale(Employee user) {
        SwingUtilities.invokeLater(() -> new SalesSystemGUI(user).setVisible(true));
    }

    public SalesSystemGUI(Employee user) {
        this.currentUser = user; // Capture the logged-in user

        // 1. Load Data
        inventory = ModelReader.loadModels();

        // 2. Window Setup
        setTitle("Sales System - Logged in as: " + user.getName());
        setSize(900, 650); // Slightly wider to fit everything nicely
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- LEFT PANEL: Input Form ---
        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("New Sale"));
        inputPanel.setPreferredSize(new Dimension(300, 0));

        // Display Current Staff
        inputPanel.add(new JLabel("Staff In-Charge:"));
        lblStaff = new JLabel(currentUser.getName() + " (" + currentUser.getId() + ")");
        lblStaff.setForeground(Color.BLUE);
        lblStaff.setFont(new Font("Arial", Font.BOLD, 12));
        inputPanel.add(lblStaff);

        // Customer Name
        inputPanel.add(new JLabel("Customer Name:"));
        txtCustomer = new JTextField();
        inputPanel.add(txtCustomer);

        // Model Selection
        inputPanel.add(new JLabel("Select Model:"));
        comboModelCode = new JComboBox<>();
        if (inventory != null) inventory.forEach(m -> comboModelCode.addItem(m.getModelId()));
        inputPanel.add(comboModelCode);

        // Quantity
        inputPanel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        inputPanel.add(txtQuantity);

        // Payment Method
        inputPanel.add(new JLabel("Payment:"));
        comboPayment = new JComboBox<>(new String[]{"Cash", "Card", "E-wallet"});
        inputPanel.add(comboPayment);

        // Buttons
        btnProcess = new JButton("Process Sale");
        btnProcess.setBackground(new Color(144, 238, 144)); 
        inputPanel.add(new JLabel("")); 
        inputPanel.add(btnProcess);

        inputPanel.add(new JLabel("")); inputPanel.add(new JLabel(""));

        btnBack = new JButton("← Back to Dashboard");
        btnBack.setBackground(new Color(220, 220, 220));
        inputPanel.add(new JLabel("")); 
        inputPanel.add(btnBack);

        // --- RIGHT PANEL: Inventory Table ---
        String[] columnNames = {"Model ID", "Price (RM)", "Stock (C60)"};
        tableModel = new DefaultTableModel(columnNames, 0);
        inventoryTable = new JTable(tableModel);
        refreshTableData(); 

        JScrollPane tableScroll = new JScrollPane(inventoryTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Current Inventory"));

        add(inputPanel, BorderLayout.WEST);
        add(tableScroll, BorderLayout.CENTER);

        // --- ACTION LISTENERS ---
        btnProcess.addActionListener(e -> handleSale());
        btnBack.addActionListener(e -> {
            this.dispose();
            new MainDashboard().setVisible(true); 
        });

        // --- CENTER THE WINDOW ---
        setLocationRelativeTo(null); // This command centers the GUI
    }

    SalesSystemGUI() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private void handleSale() {
        try {
            // Validation
            String customer = txtCustomer.getText();
            String modelCode = (String) comboModelCode.getSelectedItem(); 
            String qtyText = txtQuantity.getText();
            String payment = (String) comboPayment.getSelectedItem();

            if (customer.isEmpty() || qtyText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }
            int qty = Integer.parseInt(qtyText);
            
            Model selectedModel = null;
            for (Model m : inventory) {
                if (m.getModelId().equals(modelCode)) { selectedModel = m; break; }
            }
            
            if (selectedModel == null) return;
            if (qty > selectedModel.getStockQuantity()[0]) {
                JOptionPane.showMessageDialog(this, "Insufficient stock!");
                return;
            }

            double total = selectedModel.getPrice() * qty;
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Confirm sale by staff: " + currentUser.getName() + "?\nTotal: RM " + String.format("%.2f", total), 
                "Confirm", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                selectedModel.getStockQuantity()[0] -= qty;
                updateStockFile();

                String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                
                // Create Sale Object
                Sale newSale = new Sale(date, customer, modelCode, qty, total, payment, currentUser.getName());
                
                SaleWriter.saveSale(newSale);

                // --- RECEIPT GENERATION ---
                String receiptText = newSale.generateReceipt();
                JTextArea textArea = new JTextArea(receiptText);
                
                // IMPORTANT: Set Monospaced font so receipt columns align perfectly
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
                textArea.setEditable(false);
                
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(350, 450));

                Object[] options = {"Print & Save", "Close"};
                int choice = JOptionPane.showOptionDialog(this, scrollPane, "Receipt Preview",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                if (choice == JOptionPane.YES_OPTION) {
                    String fileSaved = ReceiptWriter.saveReceiptToFile(newSale);
                    if (fileSaved != null) JOptionPane.showMessageDialog(this, "Saved: " + fileSaved);
                }

                refreshTableData();
                clearFields();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.");
        }
    }
    
    private void clearFields() {
        txtCustomer.setText("");
        txtQuantity.setText("");
        comboPayment.setSelectedIndex(0);
    }
    
    private void refreshTableData() {
        tableModel.setRowCount(0);
        if (inventory == null) return;
        for (Model m : inventory) {
            tableModel.addRow(new Object[]{m.getModelId(), String.format("%.2f", m.getPrice()), m.getStockQuantity()[0]});
        }
    }

    public static void updateStockFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("model.csv"))) {
            pw.println("Model,Price,C60,C61,C62,C63,C64,C65,C66,C67,C68,C69"); 
            for (Model m : inventory) {
                StringBuilder line = new StringBuilder();
                line.append(m.getModelId()).append(",").append(m.getPrice());
                for (int s : m.getStockQuantity()) {
                    line.append(",").append(s);
                }
                pw.println(line.toString());
            }
        } catch (IOException e) {
            System.err.println("File Update Error: " + e.getMessage());
        }
    }
}