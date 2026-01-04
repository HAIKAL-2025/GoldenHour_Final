public class Employee {
    
    private final String id;
    private String name;
    private String role;
    private String password;
    
    public Employee (String id, String name, String role, String password){
        this.id = id;
        this.name = name;
        this.role = role;
        this.password = password;
    }
    
    public String getId (){
        return id;
    }
    
    public String getName (){
        return name;
    }
    
    public String getRole (){
        return role;
    }
    
    public String getPassword (){
        return password;
    }
    
    //Setters (update private fields if needed)
    public void setName (String name){
        this.name = name;
    }
    
    public void setRole (String role){
        this.role = role;
    }
    
    public void setPassword (String password){
        this.password = password;
    }
    
    @Override
    public String toString (){
        return id + "-" + name + " (" + role + ")";
    }
}
