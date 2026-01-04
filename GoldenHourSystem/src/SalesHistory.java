import java.io.*;
import java.util.*;

public class SalesHistory {
    
    static class SaleRecord {
        String date, time, customer, model, payment;
        int qty;
        double total;

        public SaleRecord(String d, String t, String c, String m, int q, double tot, String p) {
            this.date = d; this.time = t; this.customer = c; this.model = m; 
            this.qty = q; this.total = tot; this.payment = p;
        }
        
        @Override
        public String toString() {
            // Pretty table format
            return String.format("%-11s %-9s %-15s %-10s %-4d RM%-9.2f %s", 
                    date, time, customer, model, qty, total, payment);
        }
    }

    // Load ALL sales files (or just one for simplicity)
    public static ArrayList<SaleRecord> loadAllSales() {
        ArrayList<SaleRecord> records = new ArrayList<>();
        File folder = new File("."); // Current folder
        File[] files = folder.listFiles((dir, name) -> name.startsWith("sales_") && name.endsWith(".csv"));
        
        if (files != null) {
            for (File f : files) {
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] p = line.split(",");
                        if (p.length >= 7) {
                            records.add(new SaleRecord(p[0], p[1], p[2], p[3], 
                                    Integer.parseInt(p[4]), Double.parseDouble(p[5]), p[6]));
                        }
                    }
                } catch (IOException e) { System.out.println("Error reading " + f.getName()); }
            }
        }
        return records;
    }

    public static void viewHistory() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<SaleRecord> allData = loadAllSales();
        
        if (allData.isEmpty()) {
            System.out.println("No sales records found.");
            return;
        }

        System.out.println("\n=== SALES ANALYTICS ===");
        System.out.println("1. View All & Sort");
        System.out.println("2. Filter by Date Range");
        System.out.print("Choose: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        ArrayList<SaleRecord> displayList = new ArrayList<>(allData);

        // FILTER LOGIC
        if (choice == 2) {
            System.out.print("Enter Start Date (YYYY-MM-DD): ");
            String start = scanner.nextLine();
            System.out.print("Enter End Date (YYYY-MM-DD): ");
            String end = scanner.nextLine();
            
            displayList.clear();
            for (SaleRecord r : allData) {
                // String comparison works for ISO dates (2025-10-13 >= 2025-10-01)
                if (r.date.compareTo(start) >= 0 && r.date.compareTo(end) <= 0) {
                    displayList.add(r);
                }
            }
        }

        // SORT LOGIC
        System.out.println("\nSort by: 1. Date (Newest)  2. Amount (High->Low)  3. Customer (A-Z)");
        int sortOpt = scanner.nextInt();
        
        if (sortOpt == 1) displayList.sort((r1, r2) -> r2.date.compareTo(r1.date));
        if (sortOpt == 2) displayList.sort((r1, r2) -> Double.compare(r2.total, r1.total));
        if (sortOpt == 3) displayList.sort((r1, r2) -> r1.customer.compareToIgnoreCase(r2.customer));

        // DISPLAY TABLE
        System.out.println("\nDate        Time      Customer        Model      Qty  Total       Payment");
        System.out.println("-------------------------------------------------------------------------");
        double grandTotal = 0;
        for (SaleRecord r : displayList) {
            System.out.println(r);
            grandTotal += r.total;
        }
        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("TOTAL SALES IN THIS VIEW: RM%.2f\n", grandTotal);
    }
    
    public static void main(String[] args) {
        viewHistory();
    }
}