public class Sale {
    
    private String date;
    private String customerName;
    private String modelId;
    private int quantity;
    private double totalPrice;
    private String transactionMethod;
    private String employeeName;
    
    public Sale (String date, String customerName, String modelId, int quantity, double totalPrice, String transactionMethod, String employeeName){
        this.date = date;
        this.customerName = customerName;
        this.modelId = modelId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.transactionMethod = transactionMethod;
        this.employeeName = employeeName;
    }
    
    public String getDate (){ return date;}
    public String getCustomerName (){ return customerName;}
    public String getModelId (){ return modelId;}
    public int getQuantity (){ return quantity;}
    public double getTotalPrice (){ return totalPrice;}
    public String getTransactionMethod (){ return transactionMethod;}
    public String getEmployeeName (){ return employeeName;}
    
    @Override
    public String toString(){
        return "Date: " + date + " | Customer: " + customerName + " | Model: " + modelId + " | Quantity: " + quantity + " | Total Price: " + totalPrice + " | Method: " + transactionMethod + " | Employee: " + employeeName;
    }
    
}
