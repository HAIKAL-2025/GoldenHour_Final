import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class SalesSystem {
    public static Employee currentUser; // So we know who is selling
    private static ArrayList<Model> inventory = new ArrayList<>();
    
    public static void startSale() {
        Scanner scanner = new Scanner(System.in);
        // 1. USE TEAM READER
        inventory = ModelReader.loadModels();
        
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

        // Assume Outlet C60 (Index 0)
        int currentStock = selectedModel.getStockQuantity()[0];
        System.out.print("Enter Quantity (Current Stock: " + currentStock + "): ");
        int qty = scanner.nextInt();
        scanner.nextLine(); 
        
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
            // A. Update Stock
            selectedModel.getStockQuantity()[0] -= qty;
            updateStockFile(); 
            
            // B. USE TEAM WRITER for Sales
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String empName = (currentUser != null) ? currentUser.getName() : "Unknown";
            
            Sale newSale = new Sale(date, customer, modelCode, qty, total, payment, empName);
            SaleWriter.saveSale(newSale);
            
            System.out.println("Sale Recorded!");
        } else {
            System.out.println("Transaction Cancelled.");
        }
    }

    // We still need this manual update because ModelWriter only appends (doesn't update)
    public static void updateStockFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("models.csv"))) {
            // Re-write the whole file
            for (Model m : inventory) {
                pw.print(m.getModelId() + "," + m.getPrice());
                for (int s : m.getStockQuantity()) {
                    pw.print("," + s);
                }
                pw.println();
            }
        } catch (IOException e) {
            System.out.println("Error updating stock.");
        }
    }
}