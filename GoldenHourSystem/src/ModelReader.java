import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ModelReader {
    public static ArrayList <Model> loadModels (){
        ArrayList <Model> models = new ArrayList <> ();
        
        try {
            Scanner sc =  new Scanner (new File ("models.csv"));
            
            while (sc.hasNextLine()){
                String line = sc.nextLine();
                String data [] = line.split(",");
                
                String modelId = data[0];
                int price = Integer.parseInt(data[1]);
                int[] stockQuantity = new int [10];
                for (int i = 0; i<10; i++){
                    stockQuantity[i] = Integer.parseInt(data[i+2]);
                }
                Model mod = new Model(modelId, price, stockQuantity);
                models.add(mod);
            }
            
            sc.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return models;
    }
    
}
