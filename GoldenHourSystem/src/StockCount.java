/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectproject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
/**
 *
 * @author harisdanial
 */
public class StockCount {
    public static boolean isSameWithStockRecord(ArrayList<Integer> n){
        
      return false;  
    }
    
    public static void main(String[] args){
        Scanner userInput = new Scanner(System.in);
        
        List<List<String>> records = new ArrayList<>(); //sets up dynamic multidimensional array to store strings of value
        try(BufferedReader br = new BufferedReader(new FileReader("model.csv"))){
            String line;
            while((line = br.readLine()) != null){ //each value in each line is separated by comma into array of strings
                String[] values = line.split(",");
                records.add(Arrays.asList(values));  
            }
            
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String textTime = currentTime.format(formatter);
            
            System.out.println("=== Morning Stock Count ===");
            System.out.println("Date: " + LocalDate.now());
            System.out.println("Time: " + textTime);
            System.out.println();
            
            for(int i = 1; i < records.size();i++){
                System.out.print("Model: " + records.get(i).get(0) + " - Counted : ");
                int count = userInput.nextInt();
                System.out.println("Store Record: " + records.get(i).get(2));
                if(count == Integer.parseInt(records.get(i).get(2))){
                    System.out.println("\u001B[32m" + "Stock tally correct" + "\u001B[0m"); 
                }
                else{
                    System.out.println("\u001B[31m" +"! Mismatch detected (" + (abs(count - Integer.parseInt(records.get(i).get(2)))) + " unit difference)" + "\u001B[0m");
                }
                System.out.println();
            }
           
        }catch(FileNotFoundException e){
            e.getLocalizedMessage();
        }catch(IOException e){
            e.getLocalizedMessage();
        }
    }

    
}
