/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectproject;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class StockInStockOut {

private static void messWithTheFile(String stockMovementChoice){
    Scanner userInput = new Scanner(System.in);
    
    List<List<String>> records = new ArrayList<>();
    try(BufferedReader modelFile = new BufferedReader(new FileReader("model.csv"))){
        String line;
        while((line = modelFile.readLine()) != null){ //each value in each line is separated by comma into array of strings
            String[] values = line.split(",");
            records.add(Arrays.asList(values));  
        }
        
        
    }catch(FileNotFoundException e){
        
    }catch(IOException e){
        
    }
}
    
public static void displayDateTime(){
    LocalTime currentTime = LocalTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    String textTime = currentTime.format(formatter);
    
    System.out.println("Date: " + LocalDate.now());
    System.out.println("Time: " + textTime);
    System.out.println();
}   

    public static void main(String[] args){
        Scanner userInput = new Scanner(System.in);
        String stockMovementChoice = new String();
        boolean hasValidInput = false;
        
        while(!hasValidInput){
            System.out.print("Stock Movement (In/Out): ");
            stockMovementChoice = userInput.next();
            if(stockMovementChoice.equalsIgnoreCase("In") || stockMovementChoice.equalsIgnoreCase("Out"))
                hasValidInput = true;
            else
                System.out.println("\u001B[31m" + "Invalid output" + "\u001B[0m");
        }
        
        List<String> model = new ArrayList<>();
        if(stockMovementChoice.equalsIgnoreCase("In")){//there are like methods reused in both choice
            System.out.println("=== Stock In ===");
            displayDateTime();
            
            System.out.print("From: ");
            String placeInInput = userInput.next();
            System.out.println("To: C60");
            
            System.out.println("Models Received:");
            
            
            
        }
        else{
            System.out.println("=== Stock Out ===");
            displayDateTime();
            
            System.out.println("From: C60");
            System.out.print("To: ");
            String placeOutOutput = userInput.next();
            
            System.out.println("Models Sent: ");
            
        }
    }
}
