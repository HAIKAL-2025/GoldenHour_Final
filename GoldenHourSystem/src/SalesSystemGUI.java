import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SalesSystemGUI extends JFrame {
    private JComboBox<String> modelBox;
    private JTextField customerField, qtyField, priceField, totalField;
    private JComboBox<String> paymentBox;
    private JLabel stockLabel;
    
    private ArrayList<Model> inventory;
    private Employee currentUser;

    public SalesSystemGUI(Employee user) {
        this.currentUser = user;
        
        setTitle("GoldenHour Cash Register - Cashier: " + user.getName());
        setSize(500, 450);
        setLayout(new GridLayout(8, 2, 10, 10));
        setLocationRelativeTo(null);
        
        // 1. Load Inventory
        inventory = ModelReader.loadModels();
        
        // 2. Setup Components
        add(new JLabel("Customer Name:"));
        customerField = new JTextField();
        add(customerField);

        add(new JLabel("Select Watch Model:"));
        modelBox = new JComboBox<>();
        for (Model m : inventory) {
            modelBox.addItem(m.getModelId());
        }
        add(modelBox);

        add(new JLabel("Current Stock (C60):"));
        stockLabel = new JLabel("-");
        add(stockLabel);

        add(new JLabel("Unit Price (RM):"));
        priceField = new JTextField();
        priceField.setEditable(false); // Read-only
        add(priceField);

        add(new JLabel("Quantity:"));
        qtyField = new JTextField();
        add(qtyField);
        
        add(new JLabel("Payment Method:"));
        String[] payments = {"Cash", "Credit Card", "E-wallet"};
        paymentBox = new JComboBox<>(payments);
        add(paymentBox);

        add(new JLabel("Total Price (RM):"));
        totalField = new JTextField();
        totalField.setEditable(false);
        add(totalField);

        JButton calcBtn = new JButton("Calculate Total");
        JButton payBtn = new JButton("CONFIRM SALE");
        payBtn.setBackground(Color.GREEN);
        
        add(calcBtn);
        add(payBtn);
        
        // 3. Logic: Update Price/Stock when Model changes
        modelBox.addActionListener(e -> updateModelDetails());
        updateModelDetails(); // Run once at start

        // 4. Logic: Calculate Button
        calcBtn.addActionListener(e -> calculateTotal());

        // 5. Logic: Pay Button
        payBtn.addActionListener(e -> processSale());
    }
    
    private void updateModelDetails() {
        String selected = (String) modelBox.getSelectedItem();
        for (Model m : inventory) {
            if (m.getModelId().equals(selected)) {
                priceField.setText(String.valueOf(m.getPrice()));
                // Assuming C60 is index 0
                stockLabel.setText(String.valueOf(m.getStockQuantity()[0]));
                break;
            }
        }
    }
    
    private void calculateTotal() {
        try {
            double price = Double.parseDouble(priceField.getText());
            int qty = Integer.parseInt(qtyField.getText());
            totalField.setText(String.format("%.2f", price * qty));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity!");
        }
    }

    private void processSale() {
        calculateTotal(); // Ensure total is updated
        
        String customer = customerField.getText();
        String modelName = (String) modelBox.getSelectedItem();
        int qty = Integer.parseInt(qtyField.getText());
        double total = Double.parseDouble(totalField.getText());
        String payment = (String) paymentBox.getSelectedItem();
        
        // Find model to deduct stock
        Model selectedModel = null;
        for (Model m : inventory) {
            if (m.getModelId().equals(modelName)) {
                selectedModel = m;
                break;
            }
        }
        
        if (selectedModel != null) {
            int currentStock = selectedModel.getStockQuantity()[0];
            if (qty > currentStock) {
                JOptionPane.showMessageDialog(this, "Error: Not enough stock!");
                return;
            }
            
            // 1. Deduct Stock
            selectedModel.getStockQuantity()[0] -= qty;
            
            // 2. Save Stock to File (Re-using SalesSystem logic)
            SalesSystem.inventory = inventory; // Pass list to SalesSystem
            SalesSystem.updateStockFile(); 
            
            // 3. Save Sale to File
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Sale newSale = new Sale(date, customer, modelName, qty, total, payment, currentUser.getName());
            SaleWriter.saveSale(newSale);
            
            JOptionPane.showMessageDialog(this, "Sale Successful! Receipt Saved.");
            dispose(); // Close window
        }
    }
}