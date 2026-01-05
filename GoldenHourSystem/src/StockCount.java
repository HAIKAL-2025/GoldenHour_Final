import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class StockCount {
    
    public static void main(String[] args){
        Scanner userInput = new Scanner(System.in);
        
        List<List<String>> records = new ArrayList<>(); 
        // FIXED: Pointing to "models.csv" instead of the old file
        try(BufferedReader br = new BufferedReader(new FileReader("models.csv"))){
            String line;
            while((line = br.readLine()) != null){ 
                String[] values = line.split(",");
                records.add(Arrays.asList(values));  
            }
            
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String textTime = currentTime.format(formatter);
            
            System.out.println("=== Morning Stock Count (Outlet C60) ===");
            System.out.println("Date: " + LocalDate.now());
            System.out.println("Time: " + textTime);
            System.out.println("--------------------------------");
            
            // Loop starts at 0 if no header, or 1 if header exists. 
            // models.csv usually doesn't have a header in your team's code, but let's be safe.
            for(int i = 0; i < records.size(); i++){
                // Check if it's a valid data row (must have at least 3 columns)
                if (records.get(i).size() < 3) continue;

                String modelName = records.get(i).get(0);
                
                // Skip empty lines or headers
                if(modelName.trim().isEmpty() || modelName.equalsIgnoreCase("Model")) continue;

                System.out.print("Model: " + modelName + " - Counted : ");
                // Validation to ensure integer input
                while (!userInput.hasNextInt()) {
                    System.out.println("Please enter a valid number.");
                    userInput.next();
                }
                int count = userInput.nextInt();
                
                // Index 2 is the C60 Column in models.csv
                int systemStock = Integer.parseInt(records.get(i).get(2));
                
                System.out.println("System Record: " + systemStock);
                
                if(count == systemStock){
                    System.out.println("Status: [MATCH]"); 
                } else {
                    int diff = abs(count - systemStock);
                    System.out.println("Status: [MISMATCH] (Difference: " + diff + ")");
                }
                System.out.println("--------------------------------");
            }
           
        } catch(FileNotFoundException e){
            System.out.println("Error: models.csv not found! Run DataSeeder first.");
        } catch(IOException e){
            System.out.println("Error reading file.");
        }
    }
}