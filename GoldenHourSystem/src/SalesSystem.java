import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class SalesSystem {
    private static ArrayList<Product> inventory = new ArrayList<>();
    private static final String MODEL_FILE = "model.csv";
    
    // 1. LOAD DATA
    public static void loadInventory() {
        inventory.clear(); // Clear old data to avoid duplicates
        try (BufferedReader br = new BufferedReader(new FileReader(MODEL_FILE))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // Model(0), Price(1), C60 Stock(2)
                if (values.length > 2) {
                    inventory.add(new Product(values[0], Double.parseDouble(values[1]), Integer.parseInt(values[2])));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading stock: " + e.getMessage());
        }
    }

    // 2. SAVE DATA (Crucial for Storage Marks!)
    public static void updateStockFile() {
        try {
            // Read all lines first to keep the other outlet columns safe
            ArrayList<String> allLines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(MODEL_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    allLines.add(line);
                }
            }

            // Rewrite the file with updated C60 numbers
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(MODEL_FILE))) {
                // Write header
                bw.write(allLines.get(0)); 
                bw.newLine();
                
                // Write data rows
                for (int i = 1; i < allLines.size(); i++) {
                    String[] parts = allLines.get(i).split(",");
                    String currentModel = parts[0];
                    
                    // Find the updated stock for this model in our memory
                    for (Product p : inventory) {
                        if (p.getModelName().equals(currentModel)) {
                            parts[2] = String.valueOf(p.getStock()); // Update Column C60
                        }
                    }
                    // Join it back together
                    bw.write(String.join(",", parts));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error updating stock file: " + e.getMessage());
        }
    }

    // 3. THE SALE PROCESS
    public static void startSale() {
        Scanner scanner = new Scanner(System.in);
        loadInventory();
        
        System.out.println("\n=== NEW SALE TRANSACTION ===");
        System.out.print("Customer Name: ");
        String customer = scanner.nextLine();
        
        System.out.print("Enter Model Name (e.g. DW2300-1): ");
        String modelCode = scanner.nextLine();
        
        Product selectedProduct = null;
        for (Product p : inventory) {
            if (p.getModelName().equalsIgnoreCase(modelCode)) {
                selectedProduct = p;
                break;
            }
        }
        
        if (selectedProduct == null) {
            System.out.println("Error: Model not found!");
            return;
        }

        System.out.print("Enter Quantity (Current Stock: " + selectedProduct.getStock() + "): ");
        int qty = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        if (qty > selectedProduct.getStock()) {
            System.out.println("Error: Insufficient stock!");
            return;
        }

        System.out.print("Payment Method (Cash/Card/E-wallet): ");
        String payment = scanner.nextLine();

        double total = selectedProduct.getPrice() * qty;
        System.out.println("Total Price: RM" + total);
        System.out.print("Confirm Pay? (Y/N): ");
        
        if (scanner.next().equalsIgnoreCase("Y")) {
            // A. Update Stock Memory & File
            selectedProduct.decreaseStock(qty);
            updateStockFile(); 
            
            // B. Save Receipt
            saveSaleRecord(customer, modelCode, qty, total, payment);
            System.out.println("Sale Recorded & Stock Updated!");
        } else {
            System.out.println("Transaction Cancelled.");
        }
    }

    public static void saveSaleRecord(String customer, String model, int qty, double total, String payment) {
        // File name changes daily: sales_2025-10-13.csv
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String filename = "sales_" + today + ".csv";
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            // Format: Date,Time,Customer,Model,Qty,Total,Payment
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String line = String.format("%s,%s,%s,%s,%d,%.2f,%s", 
                    today, time, customer, model, qty, total, payment);
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error saving receipt: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        startSale();
    }
}