import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AutoEmailAndAnalytics extends JFrame {

    private JTextArea outputArea;
    private double totalSales;
    private String bestProduct;

    // ================= ANALYTICS METHODS =================

    public static double calculateTotalSales(String filePath) {
        double total = 0.0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length < 5) continue;

                try {
                    total += Double.parseDouble(data[4]);
                } catch (NumberFormatException e) {
                    // ignore invalid number
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading sales file.");
        }
        return total;
    }

    public static String findMostSoldProduct(String filePath) {
        Map<String, Integer> productMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length < 5) continue;

                String model = data[2];
                try {
                    int qty = Integer.parseInt(data[3]);
                    productMap.put(model, productMap.getOrDefault(model, 0) + qty);
                } catch (NumberFormatException e) {}
            }
        } catch (IOException e) {}

        String best = "N/A";
        int max = 0;
        for (String model : productMap.keySet()) {
            if (productMap.get(model) > max) {
                max = productMap.get(model);
                best = model;
            }
        }
        return best + " (" + max + " units)";
    }

    public static String sendAutoEmail(String email, double total, String best) {
        return  "=== AUTO EMAIL SIMULATION ===\n"
              + "To: " + email + "\n"
              + "Total Sales: RM " + total + "\n"
              + "Best Seller: " + best + "\n"
              + "============================\n";
    }

    // ================= GUI CONSTRUCTOR =================

    public AutoEmailAndAnalytics() {
        setTitle("Sales Analytics System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JButton analyzeBtn = new JButton("Analyze Sales");
        JButton emailBtn = new JButton("Send Auto Email");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(analyzeBtn);
        buttonPanel.add(emailBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // ===== BUTTON ACTIONS =====

        analyzeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String file = "sales_data.csv";
                totalSales = calculateTotalSales(file);
                bestProduct = findMostSoldProduct(file);

                outputArea.setText("");
                outputArea.append("=== ANALYTICS REPORT ===\n");
                outputArea.append("Total Revenue: RM " + totalSales + "\n");
                outputArea.append("Best Selling Product: " + bestProduct + "\n");
            }
        });

        emailBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputArea.append("\n");
                outputArea.append(sendAutoEmail(
                        "manager@goldenhour.com",
                        totalSales,
                        bestProduct
                ));
            }
        });
    }

    // ================= MAIN METHOD =================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AutoEmailAndAnalytics().setVisible(true);
        });
    }
}
