import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AutoEmailAndAnalytics {

    public static double calculateTotalSales(String filePath) {
        double total = 0.0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length < 5) continue; 
                try {
                    // Date,Cust,Model,Qty,Total -> Total is index 4
                    total += Double.parseDouble(data[4]);
                } catch (NumberFormatException e) { continue; }
            }
        } catch (IOException e) {
            System.out.println("Error reading sales file.");
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
                // Date[0], Cust[1], Model[2], Qty[3]
                String model = data[2];
                try {
                    int quantity = Integer.parseInt(data[3]);
                    productMap.put(model, productMap.getOrDefault(model, 0) + quantity);
                } catch (Exception e) {}
            }
        } catch (IOException e) {}

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

    public static void sendAutoEmail(String email, double total, String best) {
        System.out.println("\n=== AUTO EMAIL SIMULATION ===");
        System.out.println("To: " + email);
        System.out.println("Total Sales: RM" + total);
        System.out.println("Best Seller: " + best);
        System.out.println("=============================");
    }

    public static void main(String[] args) {
        String file = "sales_data.csv";
        System.out.println("=== ANALYTICS REPORT ===");
        double total = calculateTotalSales(file);
        String best = findMostSoldProduct(file);
        System.out.println("Total Revenue: RM " + total);
        System.out.println("Best Selling: " + best);
        
        sendAutoEmail("manager@goldenhour.com", total, best);
    }
}