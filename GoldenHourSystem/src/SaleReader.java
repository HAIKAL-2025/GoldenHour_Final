import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class SaleReader {
    public static ArrayList <Sale> loadSales (){
        ArrayList <Sale> sales = new ArrayList <> ();
        
        try { 
            Scanner sc = new Scanner (new File ("sales.csv"));
            
            while (sc.hasNextLine()){
                String line = sc.nextLine();
                String data [] = line.split(",");
                
                String date = data[0];
                String customerName = data[1];
                String modelId = data[2];
                int quantity = Integer.parseInt(data[3]);
                double totalPrice = Double.parseDouble(data[4]);
                String transactionMethod = data[5];
                String employeeName = data[6];
                
                Sale sale = new Sale (date, customerName, modelId, quantity, totalPrice, transactionMethod, employeeName);
                sales.add(sale);
            }
            sc.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return sales;
    }
}
