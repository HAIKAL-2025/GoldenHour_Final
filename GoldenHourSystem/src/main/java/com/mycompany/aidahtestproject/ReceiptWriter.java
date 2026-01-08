package com.mycompany.aidahtestproject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ReceiptWriter {
    
    private static final String FOLDER_NAME = "Receipts";

    // Returns the filename if successful, or null if failed
    public static String saveReceiptToFile(Sale sale) {
        
        // 1. Create the folder if it doesn't exist
        File folder = new File(FOLDER_NAME);
        if (!folder.exists()) {
            boolean created = folder.mkdir();
            if (!created) {
                System.err.println("Error: Could not create directory " + FOLDER_NAME);
                return null;
            }
        }

        // 2. Construct the file path inside the folder
        // Format: Receipts/Receipt_Customer_12345678.txt
        String filename = "Receipt_" + sale.getCustomerName().replaceAll("\\s+","") + "_" + System.currentTimeMillis() + ".txt";
        File file = new File(folder, filename);
        
        // 3. Write the file
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.print(sale.generateReceipt());
            
            // Return the full path so we know where it went
            return file.getPath(); 
        } catch (IOException e) {
            System.err.println("Error saving receipt: " + e.getMessage());
            return null;
        }
    }
}