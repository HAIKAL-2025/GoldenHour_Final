import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class EmployeeWriter {
    public static void saveEmployee (Employee emp){
        try {
            PrintWriter pw = new PrintWriter (new FileWriter ("employees.csv", true));
            
            pw.println (emp.getId() + "," + emp.getName() + "," + emp.getRole() + ","+ emp.getPassword());
            
            pw.close();
            System.out.println("Employee saved: " + emp);          
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
