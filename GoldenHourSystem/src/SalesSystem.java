import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class SalesSystem {
    // We now use your team's "Model" class instead of "Product"
    private static ArrayList<Model> inventory = new ArrayList<>();
    private static final String MODEL_FILE = "model.csv";
    
    // 1. LOAD DATA
    public static void loadInventory() {
        inventory.clear(); 
        try (BufferedReader br = new BufferedReader(new FileReader(MODEL_FILE))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // CSV Format: Model(0), Price(1), C60(2), C61(3)...
                if (values.length > 2) {
                    String name = values[0];
                    int price = Integer.parseInt(values[1]);
                    
                    // Grab all the stock numbers for all outlets
                    int[] stocks = new int[values.length - 2];
                    for (int i = 2; i < values.length; i++) {
                        stocks[i - 2] = Integer.parseInt(values[i]);
                    }
                    
                    // Create the Model object
                    inventory.add(new Model(name, price, stocks));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading stock: " + e.getMessage());
        }
    }

    // 2. SAVE DATA
    public static void updateStockFile() {
        try {
            // Read all lines first to keep the header safe
            ArrayList<String> allLines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(MODEL_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    allLines.add(line);
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(MODEL_FILE))) {
                // Write the original header (don't change it!)
                bw.write(allLines.get(0)); 
                bw.newLine();
                
                // Write the updated data
                for (Model m : inventory) {
                    // Rebuild the CSV line: Name,Price,Stock1,Stock2,Stock3...
                    StringBuilder line = new StringBuilder();
                    line.append(m.getModelId()).append(",");
                    line.append(m.getPrice());
                    
                    for (int s : m.getStockQuantity()) {
                        line.append(",").append(s);
                    }
                    
                    bw.write(line.toString());
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
        
        Model selectedModel = null;
        for (Model m : inventory) {
            if (m.getModelId().equalsIgnoreCase(modelCode)) {
                selectedModel = m;
                break;
            }
        }
        
        if (selectedModel == null) {
            System.out.println("Error: Model not found!");
            return;
        }

        // We assume we are Outlet C60 (Index 0)
        int currentStock = selectedModel.getStockQuantity()[0];

        System.out.print("Enter Quantity (Current Stock: " + currentStock + "): ");
        int qty = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        if (qty > currentStock) {
            System.out.println("Error: Insufficient stock!");
            return;
        }

        System.out.print("Payment Method (Cash/Card/E-wallet): ");
        String payment = scanner.nextLine();

        double total = selectedModel.getPrice() * qty;
        System.out.println("Total Price: RM" + total);
        System.out.print("Confirm Pay? (Y/N): ");
        
        if (scanner.next().equalsIgnoreCase("Y")) {
            // A. Update Stock in Memory (Decrease C60 stock only)
            selectedModel.getStockQuantity()[0] -= qty;
            
            // B. Save to File
            updateStockFile(); 
            saveSaleRecord(customer, modelCode, qty, total, payment);
            
            System.out.println("Sale Recorded & Stock Updated!");
        } else {
            System.out.println("Transaction Cancelled.");
        }
    }

    public static void saveSaleRecord(String customer, String model, int qty, double total, String payment) {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String filename = "sales_" + today + ".txt"; // Using .txt as per assignment
        
        // Also save to a main "sales_data.csv" for analytics
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("sales_data.csv", true))) {
             String line = String.format("%s,%s,%s,%d,%.2f", today, customer, model, qty, total);
             bw.write(line);
             bw.newLine();
        } catch (IOException e) { }

        // Generate the Daily Receipt
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
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