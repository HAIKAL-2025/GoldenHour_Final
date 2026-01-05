import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class SalesHistoryGUI extends JFrame {
    
    public SalesHistoryGUI() {
        setTitle("Sales History & Reports");
        setSize(800, 500);
        setLocationRelativeTo(null);
        
        // 1. Setup Table Columns
        String[] columns = {"Date", "Customer", "Model", "Qty", "Total (RM)", "Method", "Employee"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        // 2. Load Data using SaleReader
        ArrayList<Sale> sales = SaleReader.loadSales();
        double grandTotal = 0;
        
        for (Sale s : sales) {
            Object[] row = {
                s.getDate(),
                s.getCustomerName(),
                s.getModelId(),
                s.getQuantity(),
                s.getTotalPrice(),
                s.getTransactionMethod(),
                s.getEmployeeName()
            };
            model.addRow(row);
            grandTotal += s.getTotalPrice();
        }
        
        // 3. Layout
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Total Revenue: RM " + String.format("%.2f", grandTotal)));
        JButton refreshBtn = new JButton("Refresh");
        bottomPanel.add(refreshBtn);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Refresh Button Logic (Just re-opens the window)
        refreshBtn.addActionListener(e -> {
            dispose();
            new SalesHistoryGUI().setVisible(true);
        });
    }
}