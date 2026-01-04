import java.util.ArrayList;

public class DataSeeder {
    public static void main(String[] args) {
        System.out.println("=== STARTING DATA RESET ===");
        
        // ---- EMPLOYEE RESET ----
        Employee e1  = new Employee ("C6001","Tan Guan Ha","Manager","a2b1c0");
        Employee e2  = new Employee ("C6002","Adam bin Abu","Full-time","d3e4f5");
        Employee e3  = new Employee ("C6003","Jinu Saja","Part-time","g6h7i8");
        Employee e4  = new Employee ("C6004","Wong Benedict","Part-time","j9k0l1");
        Employee e5  = new Employee ("C6005","Dhaarane","Full-time","m2n3o4");
        Employee e6  = new Employee ("C6006","Adriana Nur Zahra binti Helmi","Part-time","p5q6r7");
        Employee e7  = new Employee ("C6007","Kang Rumi","Full-time","s8t9u0");
        Employee e8  = new Employee ("C6008","Li Xinyi","Full-time","v1w2x3");
        Employee e9  = new Employee ("C6009","Megan Mei","Part-time","y4z5a6");
        Employee e10 = new Employee ("C6010","Aminah binti Said","Full-time","b7c8d9");
        Employee e11 = new Employee ("C6011","Jarjit Singh","Part-time","e0f1g2");
        Employee e12 = new Employee ("C6012","Khairul Ariffin bin Muhammad","Part-time","h3i4j5");
        
        // We write them to file (This overwrites existing data if your Writer is set to append=false, 
        // or just adds duplicates if append=true. Ideally, delete the .csv files manually before running this.)
        EmployeeWriter.saveEmployee(e1);
        EmployeeWriter.saveEmployee(e2);
        EmployeeWriter.saveEmployee(e3);
        EmployeeWriter.saveEmployee(e4);
        EmployeeWriter.saveEmployee(e5);
        EmployeeWriter.saveEmployee(e6);
        EmployeeWriter.saveEmployee(e7);
        EmployeeWriter.saveEmployee(e8);
        EmployeeWriter.saveEmployee(e9);
        EmployeeWriter.saveEmployee(e10);
        EmployeeWriter.saveEmployee(e11);
        EmployeeWriter.saveEmployee(e12);
        
        System.out.println("Employees Reset Done.");

        // ---- MODEL (STOCK) RESET ----
        Model m1  = new Model ("DW2300-1",399,new int[]{2,4,3,1,3,3,2,0,2,4});
        Model m2  = new Model ("DW2300-2",399,new int[]{1,1,2,1,2,0,2,2,1,2});
        Model m3  = new Model ("DW2300-3",349,new int[]{0,1,3,0,1,1,2,1,1,1});
        Model m4  = new Model ("DW2300-4",349,new int[]{1,1,0,0,3,1,2,2,0,1});
        Model m5  = new Model ("DW2400-1",599,new int[]{3,2,5,2,4,2,3,3,3,3});
        Model m6  = new Model ("DW2400-2",599,new int[]{0,3,0,2,1,1,2,1,3,2});
        Model m7  = new Model ("DW2400-3",569,new int[]{1,1,2,2,1,2,0,2,1,1});
        Model m8  = new Model ("SW2400-1",789,new int[]{5,2,0,3,5,3,5,0,4,5});
        Model m9  = new Model ("SW2400-2",769,new int[]{5,0,1,1,1,1,0,0,3,3});
        Model m10 = new Model ("SW2400-3",769,new int[]{2,0,2,0,1,1,1,1,5,2});
        Model m11 = new Model ("SW2400-4",729,new int[]{1,1,3,0,1,1,1,1,0,0});
        Model m12 = new Model ("SW2500-1",845,new int[]{4,3,4,2,2,3,2,5,1,1});
        Model m13 = new Model ("SW2500-2",845,new int[]{3,3,2,2,0,2,2,1,4,3});
        Model m14 = new Model ("SW2500-3",845,new int[]{1,3,0,2,1,1,2,1,2,2});
        Model m15 = new Model ("SW2500-4",825,new int[]{2,3,0,2,1,1,2,1,0,1});
        
        ModelWriter.saveModel(m1);
        ModelWriter.saveModel(m2);
        ModelWriter.saveModel(m3);
        ModelWriter.saveModel(m4);
        ModelWriter.saveModel(m5);
        ModelWriter.saveModel(m6);
        ModelWriter.saveModel(m7);
        ModelWriter.saveModel(m8);
        ModelWriter.saveModel(m9);
        ModelWriter.saveModel(m10);
        ModelWriter.saveModel(m11);
        ModelWriter.saveModel(m12);
        ModelWriter.saveModel(m13);
        ModelWriter.saveModel(m14);
        ModelWriter.saveModel(m15);
        
        System.out.println("Models Reset Done.");

        // ---- ATTENDANCE RESET ----
        Attendance a1 = new Attendance ("C6001","10:00","17:10");
        Attendance a2 = new Attendance ("C6002","09:55","17:00");
        Attendance a3 = new Attendance ("C6003","09:45","17:10");
        Attendance a4 = new Attendance ("C6004","09:55","17:01");
        Attendance a5 = new Attendance ("C6005","09:38","17:00");
        Attendance a6 = new Attendance ("C6006","10:09","17:20");
        Attendance a7 = new Attendance ("C6007","09:45","18:24");
        Attendance a8 = new Attendance ("C6008","09:53","18:09");
        Attendance a9 = new Attendance ("C6009","10:26","17:30");
        
        AttendanceWriter.saveAttendance(a1);
        AttendanceWriter.saveAttendance(a2);
        AttendanceWriter.saveAttendance(a3);
        AttendanceWriter.saveAttendance(a4);
        AttendanceWriter.saveAttendance(a5);
        AttendanceWriter.saveAttendance(a6);
        AttendanceWriter.saveAttendance(a7);
        AttendanceWriter.saveAttendance(a8);
        AttendanceWriter.saveAttendance(a9);
        
        System.out.println("Attendance Reset Done.");

        // ---- SALE RESET ----
        Sale s1 = new Sale ("2025-10-13","Ali bin Ahmad","DW2300-1",2,399*2,"Cash","Aminah binti Said");
        Sale s2 = new Sale ("2025-11-23","Zikri bin Abdullah","DW2300-2",1,399,"E-wallet","Tan Guan Han");
        Sale s3 = new Sale ("2025-05-25","Iman bin Adam","SW2400-2",1,769,"Credit Card","Kang Rumi");
        Sale s4 = new Sale ("2025-07-18","Aaron Dylan","SW2500-1",1,845,"Credit Card","Megan Mei");
        
        SaleWriter.saveSale(s1);
        SaleWriter.saveSale(s2);
        SaleWriter.saveSale(s3);
        SaleWriter.saveSale(s4);
        
        System.out.println("Sales Reset Done.");
        System.out.println("=== DATA SEEDING COMPLETE ===");
    }
}