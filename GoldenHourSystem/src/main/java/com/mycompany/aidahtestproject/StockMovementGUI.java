package com.mycompany.aidahtestproject;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StockMovementGUI extends JFrame {
    private final JRadioButton rbIn, rbOut;
    private final JLabel lblFromTo, lblFixedPlace, lblCurrentOutletName;
    private final JTextArea logArea;
    private final JButton btnSubmit;
    private final JButton btnBack; 
    
    private final JComboBox<String> comboSupplierOutlet; 
    private final String HOME_OUTLET = "C60 - Kuala Lumpur City Centre";
    
    // CHANGED: Targeting model.csv now
    private final String MODEL_FILE = "model.csv"; 

    public StockMovementGUI() {
        setTitle("Stock Movement System - C60");
        setSize(550, 600); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- TOP: Movement Selection ---
        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createTitledBorder("Movement Type"));
        rbIn = new JRadioButton("Stock In (Receive)", true);
        rbOut = new JRadioButton("Stock Out (Transfer)");
        ButtonGroup group = new ButtonGroup();
        group.add(rbIn); group.add(rbOut);
        topPanel.add(rbIn); topPanel.add(rbOut);

        // --- CENTER: Input Form ---
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 1. Current Outlet
        formPanel.add(new JLabel("Your Outlet:"));
        lblCurrentOutletName = new JLabel(HOME_OUTLET);
        lblCurrentOutletName.setFont(new Font("Arial", Font.BOLD, 12));
        lblCurrentOutletName.setForeground(new Color(0, 102, 204));
        formPanel.add(lblCurrentOutletName);

        // 2. The Dynamic Dropdown
        lblFromTo = new JLabel("From (Source):");
        comboSupplierOutlet = new JComboBox<>(loadSupplierAndOutlets());
        formPanel.add(lblFromTo);
        formPanel.add(comboSupplierOutlet);

        // 3. Target Label
        lblFixedPlace = new JLabel("To:");
        formPanel.add(lblFixedPlace);
        formPanel.add(new JLabel("This Outlet (C60)"));

        // Row 4: Submit Button
        btnSubmit = new JButton("Process Movement");
        formPanel.add(new JLabel("")); 
        formPanel.add(btnSubmit);
        
        // Row 5: Back Button
        btnBack = new JButton("← Back to Dashboard");
        btnBack.setBackground(new Color(240, 240, 240));
        formPanel.add(new JLabel("")); 
        formPanel.add(btnBack);

        // --- BOTTOM: Log Area ---
        logArea = new JTextArea(8, 30);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Transaction Log"));

        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);

        // --- LOGIC ---
        rbIn.addActionListener(e -> updateLabels());
        rbOut.addActionListener(e -> updateLabels());
        btnSubmit.addActionListener(e -> processMovement());
        
        btnBack.addActionListener(e -> {
            this.dispose(); 
            new MainDashboard().setVisible(true); 
        });
        
        setLocationRelativeTo(null); 
    }

    // --- HELPER METHODS ---

    private String[] loadSupplierAndOutlets() {
        List<String> list = new ArrayList<>();
        list.add("MAIN WAREHOUSE");
        list.add("GLOBAL DISTRIBUTOR A");
        
        try (BufferedReader br = new BufferedReader(new FileReader("outlet.csv"))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && !parts[0].equals("C60")) {
                    list.add(parts[0] + " - " + parts[1]);
                }
            }
        } catch (IOException e) { }
        return list.toArray(new String[0]);
    }

    // LOAD MODELS from model.csv
    private String[] loadModels() {
        List<String> models = new ArrayList<>();
        File file = new File(MODEL_FILE);
        if (!file.exists()) return new String[]{}; 

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Assuming format: ModelName, Price, Quantity
                if (line.contains(",")) {
                    models.add(line.split(",")[0].trim()); 
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return models.toArray(new String[0]);
    }

    private void updateLabels() {
        if (rbIn.isSelected()) {
            lblFromTo.setText("From (Source):");
            lblFixedPlace.setText("To:");
        } else {
            lblFromTo.setText("To (Destination):");
            lblFixedPlace.setText("From:");
        }
    }

    // UPDATED: Modifies model.csv directly
    private void updateStockFile(String model, int qty, boolean isStockIn) {
        List<String> lines = new ArrayList<>();
        boolean found = false;
        File file = new File(MODEL_FILE);

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    
                    // Check if this line is the model we are updating
                    if (parts.length >= 2 && parts[0].equalsIgnoreCase(model)) {
                        
                        // Assuming Format: Model, Price, Quantity (Quantity is last)
                        int qtyIndex = parts.length - 1; 
                        String currentPrice = parts.length > 2 ? parts[1] : "0.0"; // Preserve Price if exists
                        
                        try {
                            int currentQty = Integer.parseInt(parts[qtyIndex]);
                            int newQty = isStockIn ? (currentQty + qty) : (currentQty - qty);
                            
                            if (newQty < 0) newQty = 0; // Prevent negatives
                            
                            // Rebuild the line with new quantity
                            lines.add(parts[0] + "," + currentPrice + "," + newQty);
                            found = true;
                        } catch (NumberFormatException e) {
                            // If quantity was corrupt, just reset it
                            lines.add(parts[0] + "," + currentPrice + "," + qty);
                            found = true;
                        }
                    } else {
                        lines.add(line); // Keep other lines as is
                    }
                }
            } catch (IOException e) {
                logArea.append("Error reading model file.\n");
            }
        }

        // If New Item (Stock In Only)
        if (!found && isStockIn) {
            // Add new line: Model, 0.0 (Default Price), Quantity
            lines.add(model + ",0.0," + qty);
        } else if (!found && !isStockIn) {
            logArea.append("Warning: Cannot remove stock for '" + model + "' (Not Found).\n");
            return;
        }

        // Write changes back to model.csv
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String l : lines) {
                pw.println(l);
            }
        } catch (IOException e) {
            logArea.append("Error saving stock updates.\n");
        }
    }

    private void processMovement() {
        String externalParty = (String) comboSupplierOutlet.getSelectedItem();
        boolean isStockIn = rbIn.isSelected();
        String type = isStockIn ? "STOCK IN" : "STOCK OUT";
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String countStr = JOptionPane.showInputDialog(this, "How many different models are you processing?", "Batch Quantity", JOptionPane.QUESTION_MESSAGE);
        if (countStr == null || countStr.trim().isEmpty()) return;

        int modelCount;
        try {
            modelCount = Integer.parseInt(countStr);
            if (modelCount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number entered!");
            return;
        }

        StringBuilder batchDetails = new StringBuilder();
        int totalItems = 0;

        for (int i = 1; i <= modelCount; i++) {
            
            // ComboBox with models from CSV
            JComboBox<String> comboModel = new JComboBox<>(loadModels());
            comboModel.setEditable(true); 
            JTextField txtQty = new JTextField();

            JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            inputPanel.add(new JLabel("Select/Type Model:"));
            inputPanel.add(comboModel);
            inputPanel.add(new JLabel("Quantity:"));
            inputPanel.add(txtQty);

            int result = JOptionPane.showConfirmDialog(this, inputPanel, "Entry " + i + " of " + modelCount, JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                logArea.append("[CANCELLED] Process stopped.\n");
                return;
            }

            String model = (String) comboModel.getSelectedItem();
            String qtyStr = txtQty.getText().trim();

            if (model == null || model.trim().isEmpty() || qtyStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty!");
                i--; continue;
            }

            try {
                int qty = Integer.parseInt(qtyStr);
                
                // Update model.csv
                updateStockFile(model, qty, isStockIn);

                batchDetails.append(String.format("   - %s | Qty: %d\n", model, qty));
                totalItems += qty;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantity must be a number!");
                i--;
            }
        }

        StringBuilder entry = new StringBuilder();
        entry.append("--- ").append(type).append(" ---\n");
        entry.append("Time: ").append(time).append("\n");
        if (isStockIn) {
            entry.append("Source: ").append(externalParty).append("\nDest:   ").append(HOME_OUTLET).append("\n");
        } else {
            entry.append("Source: ").append(HOME_OUTLET).append("\nDest:   ").append(externalParty).append("\n");
        }
        entry.append("Total Items: ").append(totalItems).append("\n");
        entry.append("Details:\n").append(batchDetails);
        entry.append("Status: COMPLETED (model.csv Updated)\n--------------------------\n");
        
        logArea.append(entry.toString());
        JOptionPane.showMessageDialog(this, "Stock updated successfully in model.csv!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockMovementGUI().setVisible(true));
    }
}