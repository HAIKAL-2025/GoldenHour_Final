import java.util.Arrays;

public class Model {
    
    private String modelId;
    private int price;
    private int[] stockQuantity;
    
    public Model (String modelId, int price, int[] stockQuantity){
        this.modelId = modelId;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
    
    public String getModelId (){ return modelId; }
    public int getPrice (){ return price; }
    public int[] getStockQuantity (){ return stockQuantity; }
    
    //Setters (to update private fields if needed
    public void setStockQuantity (){
        this.stockQuantity = stockQuantity;
    }
    
    @Override
    public String toString (){
        return modelId + " | Price: " + price + " | Stock Quantity: " + Arrays.toString(stockQuantity);
    }
}
