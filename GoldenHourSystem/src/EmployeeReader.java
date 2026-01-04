import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class EmployeeReader {
    public static ArrayList <Employee> loadEmployees (){
        ArrayList <Employee> employees = new ArrayList <> ();
        
        try {
            Scanner sc = new Scanner (new File ("employees.csv"));
            
            while (sc.hasNextLine()){
                String line = sc.nextLine();
                String [] data = line.split(",");
                
                Employee emp =  new Employee (data[0], data[1], data[2], data[3]);
                employees.add(emp);
            }
            
            sc.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return employees;
    } 
    
}
