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

    public SalesRecord(String date, String customerName, String modelName, int quantity, double totalPrice, String paymentMethod) {
        this.date = date;
        this.customerName = customerName;
        this.modelName = modelName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
    }

    public String getDate() { return date; }
    public String getCustomerName() { return customerName; }
    public String getModelName() { return modelName; }

    public void setCustomerName(String name) { this.customerName = name; }
    public void setModelName(String model) { this.modelName = model; }
    public void setPaymentMethod(String method) { this.paymentMethod = method; }
    public void setTotalPrice(double price) { this.totalPrice = price; }
    public void setQuantity(int qty) { this.quantity = qty; }

    public String toString() {
        return "Date: " + date + "\n" +
               "Customer: " + customerName + "\n" +
               "Item(s): " + modelName + " (Qty: " + quantity + ")\n" +
               "Total: RM" + totalPrice + "\n" +
               "Method: " + paymentMethod + "\n";
    }
}

// --- CLASS 3: DATA MANAGER ---
class DataManager {
    private ArrayList<WatchModel> stockList = new ArrayList<>();
    private ArrayList<SalesRecord> salesList = new ArrayList<>();
    // Helper map to convert "C60" -> "Kuala Lumpur City Centre"
    private Map<String, String> outletMap = new HashMap<>();

    public DataManager() {
        loadOutletData();
        loadStockData();
        loadSalesData();
    }

    // 1. Load Real Outlet Names
    private void loadOutletData() {
        outletMap.put("C60", "Kuala Lumpur City Centre");
        outletMap.put("C61", "MidValley");
        outletMap.put("C62", "Sunway Velocity");
        outletMap.put("C63", "IOI City Mall");
        outletMap.put("C64", "Lalaport");
        outletMap.put("C65", "Kuala Lumpur East Mall");
        outletMap.put("C66", "Nu Sentral");
        outletMap.put("C67", "Pavillion Kuala Lumpur");
        outletMap.put("C68", "1 Utama");
        outletMap.put("C69", "MyTown");
    }

    // 2. Load Real Stock Data
    private void loadStockData() {
        // Helper to quickly create a model with all outlet stocks
        createModel("DW2300-1", 399, new int[]{2,4,3,1,3,3,2,0,2,4});
        createModel("DW2300-2", 399, new int[]{1,1,2,1,2,0,2,2,1,2});
        createModel("DW2300-3", 349, new int[]{0,1,3,0,1,1,2,1,1,1});
        createModel("DW2300-4", 349, new int[]{1,1,0,0,3,1,2,2,0,1});
        createModel("DW2400-1", 599, new int[]{3,2,5,2,4,2,3,3,3,3});
        createModel("DW2400-2", 599, new int[]{0,3,0,2,1,1,2,1,3,2});
        createModel("DW2400-3", 569, new int[]{1,1,2,2,1,2,0,2,1,1});
        createModel("SW2400-1", 789, new int[]{5,2,0,3,5,3,5,0,4,5});
        createModel("SW2400-2", 769, new int[]{5,0,1,1,1,1,0,0,3,3});
        createModel("SW2400-3", 769, new int[]{2,0,2,0,1,1,1,1,5,2});
        createModel("SW2400-4", 729, new int[]{1,1,3,0,1,1,1,1,0,0});
        createModel("SW2500-1", 845, new int[]{4,3,4,2,2,3,2,5,1,1});
        createModel("SW2500-2", 845, new int[]{3,3,2,2,0,2,2,1,4,3});
        createModel("SW2500-3", 845, new int[]{1,3,0,2,1,1,2,1,2,2});
        createModel("SW2500-4", 825, new int[]{2,3,0,2,1,1,2,1,0,1});
    }

    // Helper method to assign array values to C60-C69
    private void createModel(String name, double price, int[] stocks) {
        WatchModel m = new WatchModel(name, price);
        for (int i = 0; i < stocks.length; i++) {
            // Generates C60, C61, etc. based on index
            String outletCode = "C6" + i; 
            m.addStock(outletCode, stocks[i]);
        }
        stockList.add(m);
    }

    // 3. Load Dummy Sales Data
    private void loadSalesData() {
        salesList.add(new SalesRecord("2025-10-13", "Zikri bin Abdullah", "SW2500-1", 1, 845.00, "Credit Card"));
        salesList.add(new SalesRecord("2025-10-14", "Ali Baba", "DW2300-4", 2, 698.00, "Cash"));
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
        System.out.print("\n=== Search Sales Information ===\nSearch keyword (Date/Name/Model): ");
        String keyword = sc.nextLine().toLowerCase();
        
        System.out.println("Searching...");
        boolean found = false;
        
        for (SalesRecord s : salesList) {
            if (s.getDate().contains(keyword) || 
                s.getCustomerName().toLowerCase().contains(keyword) || 
                s.getModelName().toLowerCase().contains(keyword)) {
                
                System.out.println("--- Sales Record Found ---");
                System.out.println(s.toString());
                found = true;
            }
        }
        if (!found) System.out.println("No records found.");
    }

    // --- EDIT STOCK (UPDATED) ---
    public void editStock(Scanner sc) {
        System.out.println("\n=== Edit Stock Information ===");
        System.out.print("Enter Model Name to Edit: ");
        String name = sc.nextLine();

        for (WatchModel m : stockList) {
            if (m.getModelName().equalsIgnoreCase(name)) {
                
                // 1. Show available outlets so user knows what to type
                System.out.println("\n--- Available Outlets ---");
                // Use a TreeMap to sort them by code (C60, C61...) for easier reading
                TreeMap<String, String> sortedOutlets = new TreeMap<>(outletMap);
                for (Map.Entry<String, String> entry : sortedOutlets.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }

                // 2. Ask for Outlet Code
                System.out.print("\nEnter Outlet Code to Edit (e.g., C60): ");
                String outletCode = sc.nextLine().toUpperCase(); // Convert to uppercase to prevent errors

                // 3. Validate Outlet Code
                if (outletMap.containsKey(outletCode)) {
                    System.out.println("Editing stock for: " + outletMap.get(outletCode));
                    System.out.print("Enter New Stock Value: ");
                    
                    try {
                        int newStock = sc.nextInt();
                        sc.nextLine(); // Consume newline
                        m.setStock(outletCode, newStock); 
                        System.out.println("Stock information updated successfully.");
                    } catch (InputMismatchException e) {
                        System.out.println("Error: Please enter a valid number.");
                        sc.nextLine(); 
                    }
                } else {
                    System.out.println("Error: Invalid Outlet Code. Please try again.");
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
            System.out.println("1. Name");
            System.out.println("2. Model");
            System.out.println("3. Quantity");
            System.out.println("4. Total Price");
            System.out.println("5. Transaction Method");
            
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
                default:
                    System.out.println("Invalid choice.");
                    return;
            }
            System.out.println("Sales information updated successfully.");
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
                input.nextLine(); // Consumes the "Enter" key
                
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
                input.nextLine(); // Clear the buffer
            }
        }
    }
}