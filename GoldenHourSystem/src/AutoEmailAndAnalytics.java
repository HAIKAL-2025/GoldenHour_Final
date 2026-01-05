import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * Module: Auto Email & Data Analytics
 * Description:
 * - Reads the official 'sales.csv' file
 * - Calculates total revenue
 * - Finds the most popular watch model
 * - Simulates sending a report to the Manager
 */

public class AutoEmailAndAnalytics {

    /* ===============================
       DATA ANALYTICS
       =============================== */

    // Calculate total sales amount
    public static double calculateTotalSales(String filePath) {
        double total = 0.0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                // 1. SKIP EMPTY LINES
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");

                // 2. CHECK COLUMN COUNT
                // sales.csv format: Date,Customer,Model,Qty,Total,Method,Employee (7 cols)
                // We need at least up to Total (Index 4), so length >= 5 is safe
                if (data.length < 5) {
                    continue; 
                }

                try {
                    // 3. GET TOTAL PRICE (Column 4)
                    // Data: [0]Date, [1]Name, [2]Model, [3]Qty, [4]Total, ...
                    double price = Double.parseDouble(data[4]);
                    total += price;
                } catch (NumberFormatException e) {
                    // Skip if data is corrupted
                    continue;
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading sales file: " + e.getMessage());
        }

        return total;
    }

    // Find most sold product model
    public static String findMostSoldProduct(String filePath) {
        Map<String, Integer> productMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");
                if (data.length < 5) continue;

                // GET MODEL (Index 2) and QUANTITY (Index 3)
                String model = data[2]; 
                
                try {
                    int quantity = Integer.parseInt(data[3]);
                    productMap.put(model, productMap.getOrDefault(model, 0) + quantity);
                } catch (NumberFormatException e) {
                    continue;
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading sales file.");
        }

        String bestProduct = "N/A";
        int maxQty = 0;

        for (String model : productMap.keySet()) {
            if (productMap.get(model) > maxQty) {
                maxQty = productMap.get(model);
                bestProduct = model;
            }
        }

        return bestProduct + " (" + maxQty + " units)";
    }

    /* ===============================
       AUTO EMAIL (SIMULATION)
       =============================== */

    public static void sendAutoEmail(
            String receiverEmail,
            String reportDate,
            double totalSales,
            String bestProduct,
            String attachmentPath) {

        System.out.