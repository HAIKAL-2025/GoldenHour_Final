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

            // Format: Date,Model,Quantity,TotalPrice
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                double price = Double.parseDouble(data[3]);
                total += price;
            }

        } catch (IOException e) {
            System.out.println("Error reading sales file.");
        }

        return total;
    }

    // Find most sold product model
    public static String findMostSoldProduct(String filePath) {
        Map<String, Integer> productMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String model = data[1];
                int quantity = Integer.parseInt(data[2]);

                productMap.put(model,
                        productMap.getOrDefault(model, 0) + quantity);
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

        System.out.println("\n=== AUTO EMAIL SENT ===");
        System.out.println("To: " + receiverEmail);
        System.out.println("Subject: Daily Sales Report - " + reportDate);
        System.out.println("Body:");
        System.out.println("Total Sales: RM " + totalSales);
        System.out.println("Most Sold Product: " + bestProduct);
        System.out.println("Attachment: " + attachmentPath);
        System.out.println("======================");
    }

    /* ===============================
       MAIN METHOD (TESTING)
       =============================== */

    public static void main(String[] args) {

        // Sample file path (MUST EXIST)
        String salesFile = "sales_2025-10-13.txt";
        String reportDate = "2025-10-13";
        String email = "your_email@gmail.com";

        double totalSales = calculateTotalSales(salesFile);
        String bestProduct = findMostSoldProduct(salesFile);

        System.out.println("=== DAILY SALES ANALYTICS ===");
        System.out.println("Total Sales: RM " + totalSales);
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
