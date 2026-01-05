import java.io.*; // Needed for File Reading
import java.util.*;

// --- CLASS 1: WATCH MODEL ---
class WatchModel {
    private String modelName;
    private double price;
    // Maps Outlet Name (e.g., "C60") to Stock Quantity
    private Map<String, Integer> outletStock; 

    public WatchModel(String modelName, double price) {
        this.modelName = modelName;
        this.price = price;
        this.outletStock = new HashMap<>();
    }

    public void addStock(String outletCode, int quantity) {
        outletStock.put(outletCode, quantity);
    }

    public String getModelName() { return modelName; }
    
    // Helper to get total stock across all outlets
    public int getTotalStock() {
        int total = 0;
        for (int qty : outletStock.values()) {
            total += qty;
        }
        return total;
    }
    
    // Updates stock for a specific outlet
    public void setStock(String outletCode, int stock) { 
        outletStock.put(outletCode, stock);
    }
    
    public void displayInfo(Map<String, String> outletNames) {
        System.out.println("Model: " + modelName);
        System.out.println("Unit Price: RM" + price);
        System.out.println("Total Stock (All Outlets): " + getTotalStock());
        System.out.println("Stock Breakdown:");
        
        // Sort keys for neat display
        TreeMap<String, Integer> sortedStock = new TreeMap<>(outletStock);
        
        for (Map.Entry<String, Integer> entry : sortedStock.entrySet()) {
            String code = entry.getKey();
            int qty = entry.getValue();
            // Get full name if available, otherwise use code
            String fullName = outletNames.getOrDefault(code, code);
            System.out.println("  - " + fullName + " (" + code + "): " + qty);
        }
    }
}

// --- CLASS 2: SALES RECORD ---
class SalesRecord {
    private String date;
    private String customerName;
    private String modelName;
    private int quantity;
    private double totalPrice;
    private String paymentMethod;
    private String sellerName; 

    public SalesRecord(String date, String customerName, String modelName, int quantity, double totalPrice, String paymentMethod, String sellerName) {
        this.date = date;
        this.customerName = customerName;
        this.modelName = modelName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.sellerName = sellerName;
    }

    // Getters
    public String getDate() { return date; }
    public String getCustomerName() { return customerName; }
    public String getModelName() { return modelName; }
    public String getSellerName() { return sellerName; }

    // Setters
    public void setCustomerName(String name) { this.customerName = name; }
    public void setModelName(String model) { this.modelName = model; }
    public void setPaymentMethod(String method) { this.paymentMethod = method; }
    public void setTotalPrice(double price) { this.totalPrice = price; }
    public void setQuantity(int qty) { this.quantity = qty; }
    public void setSellerName(String seller) { this.sellerName = seller; }

    public String toString() {
        return "Date: " + date + "\n" +
               "Customer: " + customerName + "\n" +
               "Item(s): " + modelName + " (Qty: " + quantity + ")\n" +
               "Total: RM" + totalPrice + "\n" +
               "Method: " + paymentMethod + "\n" +
               "Seller: " + sellerName + "\n";
    }
}

// --- CLASS 3: DATA MANAGER ---
class DataManager {
    private ArrayList<WatchModel> stockList = new ArrayList<>();
    private ArrayList<SalesRecord> salesList = new ArrayList<>();
    private Map<String, String> outletMap = new HashMap<>();

    public DataManager() {
        // UPDATED PATHS: Now pointing to the subdirectory
        String folder = "GoldenHourSystem/";
        
        loadOutletData(folder + "outlets.csv");
        loadStockData(folder + "models.csv");
        loadSalesData(folder + "sales.csv");
    }

    // 1. Load Outlet Names from CSV
    private void loadOutletData(String fileName) {
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            if (fileScanner.hasNextLine()) fileScanner.nextLine(); // Skip Header
            
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] data = line.split(","); // Split by comma
                if (data.length >= 2) {
                    outletMap.put(data[0].trim(), data[1].trim());
                }
            }
            System.out.println("Loaded " + outletMap.size() + " outlets.");
        } catch (FileNotFoundException e) {
            System.out.println("Error: Could not find " + fileName);
        }
    }

    // 2. Load Stock Data from CSV
    private void loadStockData(String fileName) {
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            if (fileScanner.hasNextLine()) fileScanner.nextLine(); // Skip Header
            
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] data = line.split(",");
                
                // Expected format: Model, Price, C60, C61... C69 (Total 12 columns)
                if (data.length >= 12) {
                    String name = data[0].trim();
                    double price = Double.parseDouble(data[1].trim());
                    
                    WatchModel m = new WatchModel(name, price);
                    
                    // Loop through columns 2 to 11 (C60 to C69)
                    for (int i = 0; i < 10; i++) {
                        String outletCode = "C6" + i;
                        int stock = Integer.parseInt(data[i + 2].trim());
                        m.addStock(outletCode, stock);
                    }
                    stockList.add(m);
                }
            }
            System.out.println("Loaded " + stockList.size() + " models.");
        } catch (FileNotFoundException e) {
            System.out.println("Error: Could not find " + fileName);
        } catch (Exception e) {
            System.out.println("Error reading models.csv: " + e.getMessage());
        }
    }

    // 3. Load Sales Data from CSV
    private void loadSalesData(String fileName) {
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            if (fileScanner.hasNextLine()) fileScanner.nextLine(); // Skip Header
            
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] data = line.split(",");
                
                // Now expecting 7 columns including Seller Name
                if (data.length >= 7) {
                    salesList.add(new SalesRecord(
                        data[0].trim(), // Date
                        data[1].trim(), // Customer
                        data[2].trim(), // Model
                        Integer.parseInt(data[3].trim()), // Qty
                        Double.parseDouble(data[4].trim()), // Total
                        data[5].trim(), // Method
                        data[6].trim()  // Seller Name
                    ));
                }
            }
            System.out.println("Loaded " + salesList.size() + " sales records.");
        } catch (FileNotFoundException e) {
            System.out.println("Error: Could not find " + fileName);
        }
    }

    // --- SEARCH STOCK ---
    public void searchStock(Scanner sc) {
        System.out.print("\n=== Search Stock Information ===\nSearch Model Name: ");
        String query = sc.nextLine();
        
        boolean found = false;
        System.out.println("Searching...");
        
        for (WatchModel m : stockList) {
            if (m.getModelName().equalsIgnoreCase(query)) {
                m.displayInfo(outletMap); 
                found = true;
            }
        }
        if (!found) System.out.println("Model not found.");
    }

    // --- SEARCH SALES ---
    public void searchSales(Scanner sc) {
        System.out.print("\n=== Search Sales Information ===\nSearch keyword (Date/Name/Model/Seller): ");
        String keyword = sc.nextLine().toLowerCase();
        
        System.out.println("Searching...");
        boolean found = false;
        
        for (SalesRecord s : salesList) {
            if (s.getDate().contains(keyword) || 
                s.getCustomerName().toLowerCase().contains(keyword) || 
                s.getModelName().toLowerCase().contains(keyword) ||
                s.getSellerName().toLowerCase().contains(keyword)) {
                
                System.out.println("--- Sales Record Found ---");
                System.out.println(s.toString());
                found = true;
            }
        }
        if (!found) System.out.println("No records found.");
    }

    // --- EDIT STOCK ---
    public void editStock(Scanner sc) {
        System.out.println("\n=== Edit Stock Information ===");
        System.out.print("Enter Model Name to Edit: ");
        String name = sc.nextLine();

        for (WatchModel m : stockList) {
            if (m.getModelName().equalsIgnoreCase(name)) {
                
                System.out.println("\n--- Available Outlets ---");
                TreeMap<String, String> sortedOutlets = new TreeMap<>(outletMap);
                for (Map.Entry<String, String> entry : sortedOutlets.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }

                System.out.print("\nEnter Outlet Code to Edit (e.g., C60): ");
                String outletCode = sc.nextLine().toUpperCase();

                if (outletMap.containsKey(outletCode)) {
                    System.out.println("Editing stock for: " + outletMap.get(outletCode));
                    System.out.print("Enter New Stock Value: ");
                    
                    try {
                        int newStock = sc.nextInt();
                        sc.nextLine(); 
                        m.setStock(outletCode, newStock); 
                        System.out.println("Stock information updated (in memory).");
                    } catch (InputMismatchException e) {
                        System.out.println("Error: Please enter a valid number.");
                        sc.nextLine(); 
                    }
                } else {
                    System.out.println("Error: Invalid Outlet Code.");
                }
                return;
            }
        }
        System.out.println("Model not found.");
    }

    // --- EDIT SALES ---
    public void editSales(Scanner sc) {
        System.out.println("\n=== Edit Sales Information ===");
        System.out.print("Enter Customer Name to search: ");
        String name = sc.nextLine();

        SalesRecord targetSale = null;
        for (SalesRecord s : salesList) {
            if (s.getCustomerName().equalsIgnoreCase(name)) {
                targetSale = s;
                break;
            }
        }

        if (targetSale != null) {
            System.out.println("\nRecord Found:");
            System.out.println(targetSale.toString());
            
            System.out.println("Select number to edit:");
            System.out.println("1. Customer Name");
            System.out.println("2. Model Name");
            System.out.println("3. Quantity");
            System.out.println("4. Total Price");
            System.out.println("5. Transaction Method");
            System.out.println("6. Seller Name");
            
            System.out.print("> ");
            int choice = -1;
            try {
                choice = sc.nextInt();
                sc.nextLine(); 
            } catch (InputMismatchException e) {
                System.out.println("Invalid input.");
                sc.nextLine();
                return;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter New Name: ");
                    targetSale.setCustomerName(sc.nextLine());
                    break;
                case 2:
                    System.out.print("Enter New Model: ");
                    targetSale.setModelName(sc.nextLine());
                    break;
                case 3:
                    System.out.print("Enter New Quantity: ");
                    targetSale.setQuantity(sc.nextInt());
                    sc.nextLine(); 
                    break;
                case 4:
                    System.out.print("Enter New Total: ");
                    targetSale.setTotalPrice(sc.nextDouble());
                    sc.nextLine(); 
                    break;
                case 5:
                    System.out.print("Enter New Transaction Method: ");
                    targetSale.setPaymentMethod(sc.nextLine());
                    break;
                case 6:
                    System.out.print("Enter New Seller Name: ");
                    targetSale.setSellerName(sc.nextLine());
                    break;
                default:
                    System.out.println("Invalid choice.");
                    return;
            }
            System.out.println("Sales information updated (in memory).");
        } else {
            System.out.println("Sales record not found.");
        }
    }
}

// --- CLASS 4: MAIN SYSTEM ---
public class StoreSystem {
    public static void main(String[] args) {
        DataManager manager = new DataManager();
        Scanner input = new Scanner(System.in); 
        
        while (true) {
            System.out.println("\n===========================================");
            System.out.println("   GOLDENHOUR MANAGEMENT SYSTEM (FARIST)   ");
            System.out.println("===========================================");
            System.out.println("1. Search Stock Information");
            System.out.println("2. Search Sales Information");
            System.out.println("3. Edit Stock Information");
            System.out.println("4. Edit Sales Information");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");
            
            try {
                int choice = input.nextInt();
                input.nextLine(); 
                
                switch (choice) {
                    case 1: manager.searchStock(input); break;
                    case 2: manager.searchSales(input); break;
                    case 3: manager.editStock(input); break;
                    case 4: manager.editSales(input); break;
                    case 5: 
                        System.out.println("Exiting system. Goodbye!"); 
                        return;
                    default: 
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                input.nextLine(); 
            }
        }
    }
}