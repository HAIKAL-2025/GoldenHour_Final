import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class AttendanceReader {
    public static ArrayList <Attendance> loadAttendance (){
        ArrayList <Attendance> records = new ArrayList <> ();
        
        try {
            Scanner sc = new Scanner (new File ("attendance.csv"));
            
            while (sc.hasNextLine()){
                String line = sc.nextLine();
                String data [] = line.split(",");
                
                Attendance att = new Attendance(data[0], data[1], data[2]);
                att.setHoursWorked(Double.parseDouble(data[3]));
                records.add(att);
            }
            sc.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return records;
    }
    
}
