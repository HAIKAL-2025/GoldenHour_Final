package com.mycompany.aidahtestproject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SalesAnalytics extends JFrame {

    private final Employee currentUser;
    
    // Data Structures for Analytics
    private final Map<String, Double> salesByDay = new TreeMap<>();   // Sorted by Date
    private final Map<String, Double> salesByMonth = new TreeMap<>(); // Sorted by Month
    private final Map<String, Double> salesByYear = new TreeMap<>();  // Sorted by Year
    
    // Key: "yyyy-MM", Value: Map<Model, Qty>
    private final Map<String, Map<String, Integer>> productsByMonth = new TreeMap<>();
    // Key: "yyyy", Value: Map<Model, Qty>
    private final Map<String, Map<String, Integer>> productsByYear = new TreeMap<>();

    private JTabbedPane tabbedPane;

    public SalesAnalytics(Employee user) {
        this.currentUser = user;
        
        setTitle("Business Analytics Dashboard");
        setSize(800, 600); // Made bigger for charts
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Load Data
        loadSalesData();

        // 2. Setup GUI with Tabs
        tabbedPane = new JTabbedPane();

        // Tab 1: Text Summary (Original Report)
        tabbedPane.addTab("📄 Summary Report", createSummaryPanel());

        // Tab 2: Daily Sales Chart
        tabbedPane.addTab("📅 Daily Sales", createChartWrapper(salesByDay, "Daily Revenue (RM)"));

        // Tab 3: Monthly Sales Chart
        tabbedPane.addTab("📊 Monthly Sales", createChartWrapper(salesByMonth, "Monthly Revenue (RM)"));

        // Tab 4: Yearly Sales Chart
        tabbedPane.addTab("📈 Yearly Sales", createChartWrapper(salesByYear, "Yearly Revenue (RM)"));

        // Tab 5: Best Sellers (Text Analysis)
        tabbedPane.addTab("🏆 Best Sellers", createBestSellersPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Navigation Button
        JButton btnBack = new JButton("⬅ Back to Dashboard");
        btnBack.addActionListener(e -> {
            this.dispose();
            if (currentUser != null) new MainDashboard(currentUser).setVisible(true);
        });
        add(btnBack, BorderLayout.SOUTH);
    }
    
    public SalesAnalytics() {
        this(new Employee("ADMIN", "Admin", "Manager", "pass"));
    }

    // ================= DATA LOADING LOGIC =================
    private void loadSalesData() {
        File file = new File("sales.csv");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip Header
            
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");

                String dateStr = ""; 
                String model = ""; 
                int qty = 0; 
                double total = 0.0;

                // Handle CSV formats (New vs Old)
                if (data.length >= 8) { // New Format
                    dateStr = data[1]; 
                    model = data[3];
                    qty = Integer.parseInt(data[4]);
                    total = Double.parseDouble(data[5]);
                } else if (data.length == 7) { // Old Format
                    dateStr = data[0]; 
                    model = data[2];
                    qty = Integer.parseInt(data[3]);
                    total = Double.parseDouble(data[4]);
                } else { continue; }

                // Parse Date Keys
                // dateStr usually "yyyy-MM-dd HH:mm:ss"
                String dayKey = dateStr.length() >= 10 ? dateStr.substring(0, 10) : "Unknown";
                String monthKey = dateStr.length() >= 7 ? dateStr.substring(0, 7) : "Unknown";
                String yearKey = dateStr.length() >= 4 ? dateStr.substring(0, 4) : "Unknown";

                // Aggregate Revenue
                salesByDay.put(dayKey, salesByDay.getOrDefault(dayKey, 0.0) + total);
                salesByMonth.put(monthKey, salesByMonth.getOrDefault(monthKey, 0.0) + total);
                salesByYear.put(yearKey, salesByYear.getOrDefault(yearKey, 0.0) + total);

                // Aggregate Product Counts (Month)
                productsByMonth.putIfAbsent(monthKey, new HashMap<>());
                Map<String, Integer> pMonth = productsByMonth.get(monthKey);
                pMonth.put(model, pMonth.getOrDefault(model, 0) + qty);

                // Aggregate Product Counts (Year)
                productsByYear.putIfAbsent(yearKey, new HashMap<>());
                Map<String, Integer> pYear = productsByYear.get(yearKey);
                pYear.put(model, pYear.getOrDefault(model, 0) + qty);
            }
        } catch (Exception e) {
            System.out.println("Error loading analytics: " + e.getMessage());
        }
    }

    // ================= GUI PANELS =================

    // 1. Text Summary Panel
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        StringBuilder sb = new StringBuilder();
        sb.append("GENERAL SUMMARY\n");
        sb.append("==========================\n");
        double grandTotal = salesByYear.values().stream().mapToDouble(Double::doubleValue).sum();
        sb.append(String.format("Total Lifetime Revenue: RM %.2f\n", grandTotal));
        sb.append("Total Active Days: " + salesByDay.size() + "\n");
        sb.append("Total Active Months: " + salesByMonth.size() + "\n");
        
        area.setText(sb.toString());
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    // 2. Chart Wrapper (Adds ScrollPane to chart)
    private JScrollPane createChartWrapper(Map<String, Double> data, String yAxisLabel) {
        SimpleBarChart chart = new SimpleBarChart(data, yAxisLabel);
        // Calculate preferred width based on number of bars (50px per bar)
        int width = Math.max(800, data.size() * 60 + 100);
        chart.setPreferredSize(new Dimension(width, 400));
        
        return new JScrollPane(chart);
    }

    // 3. Best Sellers Panel (Text List)
    private JPanel createBestSellersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        StringBuilder sb = new StringBuilder();
        
        sb.append("🏆 MOST SOLD PRODUCTS BY MONTH\n");
        sb.append("===================================\n");
        for (String month : productsByMonth.keySet()) {
            Map<String, Integer> products = productsByMonth.get(month);
            String best = getBestProduct(products);
            sb.append(String.format("📅 %s : %s\n", month, best));
        }

        sb.append("\n\n🏆 MOST SOLD PRODUCTS BY YEAR\n");
        sb.append("===================================\n");
        for (String year : productsByYear.keySet()) {
            Map<String, Integer> products = productsByYear.get(year);
            String best = getBestProduct(products);
            sb.append(String.format("📅 %s : %s\n", year, best));
        }

        area.setText(sb.toString());
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private String getBestProduct(Map<String, Integer> map) {
        String best = "None";
        int max = -1;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                best = entry.getKey();
            }
        }
        return best + " (" + max + " units)";
    }

    // ================= CUSTOM CHART COMPONENT =================
    /**
     * A simple inner class to draw bar charts without external libraries.
     */
    static class SimpleBarChart extends JPanel {
        private final Map<String, Double> data;
        private final String yLabel;

        public SimpleBarChart(Map<String, Double> data, String yLabel) {
            this.data = data;
            this.yLabel = yLabel;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data.isEmpty()) {
                g.drawString("No data available to chart.", 20, 20);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 50;
            int graphHeight = height - (2 * padding);
            
            // Find max value for scaling
            double maxValue = data.values().stream().max(Double::compare).orElse(1.0);
            if(maxValue == 0) maxValue = 1;

            // Draw Axes
            g2.drawLine(padding, height - padding, width - padding, height - padding); // X Axis
            g2.drawLine(padding, height - padding, padding, padding); // Y Axis
            
            // Draw Y-Axis Label
            g2.drawString(yLabel, padding + 10, padding - 10);

            // Draw Bars
            int numBars = data.size();
            int barWidth = 40; 
            int gap = 20;
            int x = padding + gap;

            for (Map.Entry<String, Double> entry : data.entrySet()) {
                String key = entry.getKey();
                double value = entry.getValue();

                int barHeight = (int) ((value / maxValue) * graphHeight);
                int y = (height - padding) - barHeight;

                // Draw Bar
                g2.setColor(new Color(100, 149, 237)); // Cornflower Blue
                g2.fillRect(x, y, barWidth, barHeight);
                
                // Draw Border
                g2.setColor(Color.BLACK);
                g2.drawRect(x, y, barWidth, barHeight);

                // Draw Value on top
                g2.drawString(String.format("%.0f", value), x, y - 5);

                // Draw Label (rotated if needed, but keeping simple here)
                g2.drawString(key, x, height - padding + 15);

                x += barWidth + gap;
            }
        }
    }
}