import java.util.Scanner;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class StockInStockOut {

    public static void displayDateTime(){
        LocalTime currentTime = LocalTime.now();
        System.out.println("Current Time: " + currentTime);
    }

    public static void main(String[] args){
        Scanner userInput = new Scanner(System.in);
        String stockMovementChoice = "";
        boolean hasValidInput = false;
        
        while(!hasValidInput){
            System.out.print("Stock Movement (In/Out): ");
            stockMovementChoice = userInput.next();
            if(stockMovementChoice.equalsIgnoreCase("In") || stockMovementChoice.equalsIgnoreCase("Out"))
                hasValidInput = true;
            else
                System.out.println("Invalid input. Type 'In' or 'Out'.");
        }
        
        // Read models.csv to check availability
        List<List<String>> records = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader("models.csv"))){
            String line;
            while((line = br.readLine()) != null){ 
                String[] values = line.split(",");
                records.add(Arrays.asList(values));  
            }
        } catch(IOException e){
            System.out.println("Error reading models.csv");
            return;
        }

        if(stockMovementChoice.equalsIgnoreCase("In")){
            System.out.println("\n=== Stock In (Receiving) ===");
            displayDateTime();
            
            System.out.print("From (Supplier/Outlet): ");
            String placeInInput = userInput.next();
            System.out.println("To: Outlet C60");
            System.out.println("(Note: This is a simulation. To update actual stock, use Sales System or Edit function.)");
        }
        else{
            System.out.println("\n=== Stock Out (Transfer) ===");
            displayDateTime();
            
            System.out.println("From: Outlet C60");
            System.out.print("To (Outlet Code): ");
            String placeOutOutput = userInput.next();
            System.out.println("Transfer recorded to " + placeOutOutput);
        }
    }
}