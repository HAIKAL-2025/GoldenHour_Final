import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * Module: Auto Email & Data Analytics
 * Developer: M. Afifi Furqan
 * Description:
 * - Reads daily sales receipt file
 * - Calculates total sales
 * - Finds most sold product
 * - Simulates auto email sending
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

            // Loop through every line in the file
            while ((line = br.readLine()) != null) {
                // 1. SKIP EMPTY LINES (Fixes the crash)
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");

                // 2. CHECK COLUMN COUNT (Safety Check)
                // Expected format: Date,Customer,Model,Qty,Total (5 columns)
                if (data.length < 5) {
                    continue; 
                }

                try {
                    // 3. GET TOTAL PRICE (Column 4, since counting starts at 0)
                    double price = Double.parseDouble(data[4]);
                    total += price;
                } catch (NumberFormatException e) {
                    // Skip if the price is not a number
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
                // 1. SKIP EMPTY LINES
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");

                // 2. CHECK COLUMN COUNT
                if (data.length < 5) {
                    continue;
                }

                // 3. GET MODEL & QTY (Indices adjusted for your format)
                // Date[0], Customer[1], Model[2], Qty[3], Total[4]
                String model = data[2]; 
                
                try {
                    int quantity = Integer.parseInt(data[3]);
                    productMap.put(model, productMap.getOrDefault(model, 0) + quantity);
                } catch (NumberFormatException e) {
                    continue;
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading sales file: " + e.getMessage());
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

        System.out.println("\n=== AUTO EMAIL SENT ===");
        System.out.println("To: " + receiverEmail);
        System.out.println("Subject: Daily Sales Report - " + reportDate);
        System.out.println("Body:");
        System.out.println("Total Sales: RM " + String.format("%.2f", totalSales));
        System.out.println("Most Sold Product: " + bestProduct);
        System.out.println("Attachment: " + attachmentPath);
        System.out.println("======================");
    }

    /* ===============================
       MAIN METHOD (TESTING)
       =============================== */

    public static void main(String[] args) {

        // CHANGE THIS to match the actual file name in your project folder
        String salesFile = "sales_data.csv"; 
        
        String reportDate = "2025-10-13"; // You can automate this date too
        String email = "your_email@gmail.com";

        // Check if file exists before running to avoid confusion
        java.io.File file = new java.io.File(salesFile);
        if (!file.exists()) {
            System.out.println("Error: File '" + salesFile + "' not found. Run SalesSystem first!");
            return;
        }

        double totalSales = calculateTotalSales(salesFile);
        String bestProduct = findMostSoldProduct(salesFile);

        System.out.println("=== DAILY SALES ANALYTICS ===");
        System.out.println("Total Sales: RM " + String.format("%.2f", totalSales));
        System.out.println("Most Sold Product: " + bestProduct);

        sendAutoEmail(
                email,
                reportDate,
                totalSales,
                bestProduct,
                salesFile
        );
    }
}