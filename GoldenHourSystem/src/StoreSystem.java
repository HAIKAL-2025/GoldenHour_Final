import java.util.*;

// --- CLASS 1: MODEL (Represents a Watch/Item) ---
class Model {
    private String modelName;
    private double price;
    private int currentStock;
    // Maps Outlet Name to Stock Quantity (e.g., "KLCC" -> 2)
    private Map<String, Integer> outletStock; 

    public Model(String modelName, double price, int currentStock) {
        this.modelName = modelName;
        this.price = price;
        this.currentStock = currentStock;
        this.outletStock = new HashMap<>();
        
        // Dummy data for other outlets to match the PDF search example
        outletStock.put("KLCC", currentStock); 
        outletStock.put("MidValley", 1);
        outletStock.put("Pavilion", 2);
        outletStock.put("Nu Sentral", 2);
    }

    // Getters and Setters
    public String getModelName() { return modelName; }
    public int getCurrentStock() { return currentStock; }
    public void setCurrentStock(int stock) { 
        this.currentStock = stock; 
        // Update the KLCC record in the map as well to keep them synced
        outletStock.put("KLCC", stock);
    }
    public double getPrice() { return price; }
    
    // Display method for Search results (Matches PDF Page 8 format)
    public void displayInfo() {
        System.out.println("Model: " + modelName);
        System.out.println("Unit Price: RM" + price);
        System.out.println("Stock by Outlet: " + outletStock);
    }
}

// --- CLASS 2: SALE (Represents a Transaction) ---
class Sale {
    private String date;
    private String customerName;
    private String modelName;
    private int quantity;
    private double totalPrice;
    private String paymentMethod;

    public Sale(String date, String customerName, String modelName, int quantity, double totalPrice, String paymentMethod) {
        this.date = date;
        this.customerName = customerName;
        this.modelName = modelName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
    }

    // Getters
    public String getDate() { return date; }
    public String getCustomerName() { return customerName; }
    public String getModelName() { return modelName; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotalPrice() { return totalPrice; }
    public int getQuantity() { return quantity; }

    // Setters for Editing (Matches PDF Page 9 requirements)
    public void setCustomerName(String name) { this.customerName = name; }
    public void setModelName(String model) { this.modelName = model; }
    public void setPaymentMethod(String method) { this.paymentMethod = method; }
    public void setTotalPrice(double price) { this.totalPrice = price; }
    public void setQuantity(int qty) { this.quantity = qty; }

    // Format output to look like the PDF Search Sales result
    public String toString() {
        return "Date: " + date + "\n" +
               "Customer: " + customerName + "\n" +
               "Item(s): " + modelName + " (Qty: " + quantity + ")\n" +
               "Total: RM" + totalPrice + "\n" +
               "Method: " + paymentMethod + "\n";
    }
}

// --- CLASS 3: DATA MANAGER (Contains your Search & Edit Logic) ---
class DataManager {
    // specific lists to hold data
    private ArrayList<Model> stockList = new ArrayList<>();
    private ArrayList<Sale> salesList = new ArrayList<>();

    // Constructor to load dummy data so you can test immediately
    public DataManager() {
        // Data based on PDF examples
        stockList.add(new Model("DW2300-4", 349.00, 1));
        stockList.add(new Model("SW2500-1", 845.00, 5));
        
        salesList.add(new Sale("2025-10-13", "Zikri bin Abdullah", "SW2500-1", 1, 845.00, "Credit Card"));
        salesList.add(new Sale("2025-10-14", "Ali Baba", "DW2300-4", 2, 698.00, "Cash"));
    }

    // --- REQUIREMENT: SEARCH STOCK (PDF Page 8) ---
    [cite_start]// "Employees can search by model name to view current stock" [cite: 149]
    public void searchStock() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\n=== Search Stock Information ===\nSearch Model Name: ");
        String query = sc.nextLine();
        
        boolean found = false;
        System.out.println("Searching...");
        
        for (Model m : stockList) {
            if (m.getModelName().equalsIgnoreCase(query)) {
                m.displayInfo(); 
                found = true;
            }
        }
        if (!found) System.out.println("Model not found.");
    }

    // --- REQUIREMENT: SEARCH SALES (PDF Page 8) ---
    [cite_start]// "Search sales records by date, customer name, or model name" [cite: 151]
    public void searchSales() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\n=== Search Sales Information ===\nSearch keyword (Date/Name/Model): ");
        String keyword = sc.nextLine().toLowerCase();
        
        System.out.println("Searching...");
        boolean found = false;
        
        for (Sale s : salesList) {
            // Check if keyword matches date OR name OR model
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

    // --- REQUIREMENT: EDIT STOCK (PDF Page 9) ---
    [cite_start]// "Editable fields include: Stock-related data" [cite: 178]
    public void editStock() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n=== Edit Stock Information ===");
        System.out.print("Enter Model Name to Edit: ");
        String name = sc.nextLine();

        for (Model m : stockList) {
            if (m.getModelName().equalsIgnoreCase(name)) {
                System.out.println("Current Stock: " + m.getCurrentStock());
                System.out.print("Enter New Stock Value: ");
                
                try {
                    int newStock = sc.nextInt();
                    m.setCurrentStock(newStock); 
                    System.out.println("Stock information updated successfully.");
                } catch (InputMismatchException e) {
                    System.out.println("Error: Please enter a valid number.");
                }
                return;
            }
        }
        System.out.println("Model not found.");
    }

    // --- REQUIREMENT: EDIT SALES (PDF Page 9) ---
    [cite_start]// "Editable fields include: Sales information" [cite: 179]
    public void editSales() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n=== Edit Sales Information ===");
        System.out.print("Enter Customer Name to search: ");
        String name = sc.nextLine();

        // Find the sale object
        Sale targetSale = null;
        for (Sale s : salesList) {
            if (s.getCustomerName().equalsIgnoreCase(name)) {
                targetSale = s;
                break;
            }
        }

        if (targetSale != null) {
            System.out.println("\nRecord Found:");
            System.out.println(targetSale.toString());
            
            [cite_start]// Menu based on PDF Page 9 screenshot [cite: 192-194]
            System.out.println("Select number to edit:");
            System.out.println("1. Name");
            System.out.println("2. Model");
            System.out.println("3. Quantity");
            System.out.println("4. Total Price");
            System.out.println("5. Transaction Method");
            
            System.out.print("> ");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

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
                    break;
                case 4:
                    System.out.print("Enter New Total: ");
                    targetSale.setTotalPrice(sc.nextDouble());
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

// --- CLASS 4: MAIN SYSTEM (Run this one) ---
public class StoreSystem {
    public static void main(String[] args) {
        // Initialize the manager (which loads the dummy data)
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
                
                switch (choice) {
                    case 1: manager.searchStock(); break;
                    case 2: manager.searchSales(); break;
                    case 3: manager.editStock(); break;
                    case 4: manager.editSales(); break;
                    case 5: 
                        System.out.println("Exiting system. Goodbye!"); 
                        return;
                    default: 
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                input.nextLine(); // clear the buffer
            }
        }
    }
}