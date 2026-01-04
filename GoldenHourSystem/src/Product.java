public class Product {
    private String modelName;
    private double price;
    private int stockC60; // Assuming your store is C60 (KLCC)

    public Product(String modelName, double price, int stockC60) {
        this.modelName = modelName;
        this.price = price;
        this.stockC60 = stockC60;
    }

    // Getters and Setters
    public String getModelName() { return modelName; }
    public double getPrice() { return price; }
    public int getStock() { return stockC60; }
    
    public void decreaseStock(int amount) {
        this.stockC60 -= amount;
    }
}