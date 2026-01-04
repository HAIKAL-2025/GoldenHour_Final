import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ModelWriter {
    public static void saveModel (Model mod){
        try {
            PrintWriter pw = new PrintWriter (new FileWriter ("models.csv", true));
            
            pw.print(mod.getModelId() + "," + mod.getPrice());
            for (int stock : mod.getStockQuantity()){
                pw.print("," + stock);
            }
            pw.println();
            pw.close();
            System.out.println("Model saved: " + mod);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}