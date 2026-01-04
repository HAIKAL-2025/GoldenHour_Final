import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class AttendanceWriter {
    public static void saveAttendance (Attendance att){
        try {
            PrintWriter pw = new PrintWriter (new FileWriter ("attendance.csv", true));
            
            String formattedHours = String.format("%.2f", att.getHoursWorked());
            
            pw.println (att.getEmployeeId() + "," + att.getClockInTime() + "," + att.getClockOutTime() + "," + formattedHours);
            
            pw.close();
            System.out.println("Attendance saved: " + att);
        }catch (IOException e){
            e.printStackTrace();
        }
    }    
}
    
