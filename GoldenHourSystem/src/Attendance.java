import java.time.LocalTime;
import java.time.Duration;

public class Attendance {
    
    private final String employeeId;
    private LocalTime clockInTime;
    private LocalTime clockOutTime;
    private double hoursWorked;
    
    public Attendance (String employeeId, String clockInTime, String clockOutTime){
        this.employeeId = employeeId;
        this.clockInTime = LocalTime.parse(clockInTime);
        this.clockOutTime = LocalTime.parse(clockOutTime);
        this.hoursWorked = calculateHoursWorked();
    }
    
    public Attendance (String employeeId, String clockInTime, String clockOutTime, double hoursWorked){
        this.employeeId = employeeId;
        this.clockInTime = LocalTime.parse(clockInTime);
        this.clockOutTime = LocalTime.parse(clockOutTime);
        this.hoursWorked = hoursWorked;
    }
    
    private double calculateHoursWorked(){
        Duration duration = Duration.between(clockInTime, clockOutTime);
        return duration.toMinutes() / 60.0;
    }
    
    public String getEmployeeId (){ return employeeId;}
    public LocalTime getClockInTime (){ return clockInTime;}
    public LocalTime getClockOutTime (){ return clockOutTime;}
    public double getHoursWorked (){ return hoursWorked;}
    
    //Setters
    public void setClockOutTime (String clockOutTime){
        this.clockOutTime = LocalTime.parse(clockOutTime);
        this.hoursWorked = calculateHoursWorked();
    }
    public void setHoursWorked (double hoursWorked){
        this.hoursWorked = hoursWorked;
    }
    
    @Override
    public String toString (){
        return employeeId + " | In: " + clockInTime + " | Out: " + clockOutTime + " | Hours: " + String.format("%.2f",hoursWorked);
    }
    
}
