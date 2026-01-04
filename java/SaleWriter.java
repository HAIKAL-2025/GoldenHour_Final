import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SaleWriter {
    public static void saveSale (Sale sale){
        try {
            PrintWriter pw = new PrintWriter (new FileWriter ("sales.csv",true));
            
            pw.println (sale.getDate() + "," + sale.getCustomerName() + "," + sale.getModelId() + "," + sale.getQuantity() + "," + sale.getTotalPrice() + "," + sale.getTransactionMethod() + "," + sale.getEmployeeName());
            
            pw.close();
            System.out.println("Sale saved: " + sale);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
}
