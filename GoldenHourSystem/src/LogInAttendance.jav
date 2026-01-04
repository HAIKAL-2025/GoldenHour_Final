import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

// Object-Oriented Data Model 
class Employee {
    String id, name, password, role;

    public Employee(String id, String name, String role, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    // Helper to format data for CSV storage [cite: 200]
    public String toCSV() {
        return id + "," + name + "," + password + "," + role;
    }
}

public class GoldenHourSystem extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private ArrayList<Employee> employeeList = new ArrayList<>();
    private Employee currentUser = null;
    
    // Track clock-in times for calculation [cite: 50]
    private HashMap<String, LocalDateTime> activeSessions = new HashMap<>(); 
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public GoldenHourSystem() {
        loadEmployeeData(); // Data Load State [cite: 209]
        setupGUI();
        
        setTitle("Golden Hour Store Management System");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Load data from storage on startup [cite: 208, 209]
    private void loadEmployeeData() {
    File file = new File("employee.csv");
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        br.readLine(); // Skip header
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length >= 4) {
                // .trim() removes any accidental spaces from the CSV file
                employeeList.add(new Employee(
                    data[0].trim(), 
                    data[1].trim(), 
                    data[2].trim(), 
                    data[3].trim()
                ));
            }
        }
    } catch (IOException e) {
        System.out.println("Could not find employee.csv in " + file.getAbsolutePath());
    }
}

    private void setupGUI() {
        // --- 1. LOGIN PANEL [cite: 26-28] ---
        JPanel loginPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        JTextField idField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        loginPanel.add(new JLabel("Employee ID:"));
        loginPanel.add(idField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passField);
        loginPanel.add(loginBtn);

        // --- 2. MAIN DASHBOARD PANEL ---
        JPanel dashPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        dashPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        JLabel welcomeLabel = new JLabel("Welcome!", SwingConstants.CENTER);
        JButton clockInBtn = new JButton("Clock In");
        JButton clockOutBtn = new JButton("Clock Out");
        JButton regBtn = new JButton("Register New Employee (Manager Only)");
        JButton logoutBtn = new JButton("Logout");
        dashPanel.add(welcomeLabel);
        dashPanel.add(clockInBtn);
        dashPanel.add(clockOutBtn);
        dashPanel.add(regBtn);
        dashPanel.add(logoutBtn);

        // --- 3. REGISTRATION PANEL [cite: 30, 41-46] ---
        JPanel regPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        regPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        JTextField newName = new JTextField();
        JTextField newId = new JTextField();
        JTextField newPass = new JTextField();
        String[] roles = {"Full-time", "Part-time"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        JButton saveUserBtn = new JButton("Save Employee");
        JButton backBtn = new JButton("Back to Dashboard");
        regPanel.add(new JLabel("Name:")); regPanel.add(newName);
        regPanel.add(new JLabel("Employee ID:")); regPanel.add(newId);
        regPanel.add(new JLabel("Set Password:")); regPanel.add(newPass);
        regPanel.add(new JLabel("Role:")); regPanel.add(roleBox);
        regPanel.add(saveUserBtn); regPanel.add(backBtn);

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(dashPanel, "DASH");
        mainPanel.add(regPanel, "REG");
        add(mainPanel);

        // --- BUTTON LOGIC ---
        loginBtn.addActionListener(e -> {
            String id = idField.getText();
            String pass = new String(passField.getPassword());
            for (Employee emp : employeeList) {
                if (emp.id.equals(id) && emp.password.equals(pass)) {
                    currentUser = emp;
                    welcomeLabel.setText("Welcome, " + emp.name + " (" + emp.id + ")");
                    // Registration button only visible to Managers 
                    regBtn.setVisible(emp.role.equalsIgnoreCase("Manager"));
                    cardLayout.show(mainPanel, "DASH");
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Login Failed: Invalid User ID or Password.");
        });

        clockInBtn.addActionListener(e -> {
            activeSessions.put(currentUser.id, LocalDateTime.now());
            recordAttendance("Clock In", null);
        });

        clockOutBtn.addActionListener(e -> {
            if (activeSessions.containsKey(currentUser.id)) {
                LocalDateTime start = activeSessions.get(currentUser.id);
                LocalDateTime end = LocalDateTime.now();
                Duration duration = Duration.between(start, end);
                double hours = duration.toMinutes() / 60.0; // Total Hours calculation 
                recordAttendance("Clock Out", String.format("%.1f", hours));
                activeSessions.remove(currentUser.id);
            } else {
                JOptionPane.showMessageDialog(this, "Error: You must Clock In first.");
            }
        });

        regBtn.addActionListener(e -> cardLayout.show(mainPanel, "REG"));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "DASH"));

        saveUserBtn.addActionListener(e -> {
            Employee newUser = new Employee(newId.getText(), newName.getText(), newPass.getText(), (String)roleBox.getSelectedItem());
            employeeList.add(newUser);
            saveEmployeeToCSV(newUser); // Modify stored data [cite: 201]
            JOptionPane.showMessageDialog(this, "Employee successfully registered!");
            cardLayout.show(mainPanel, "DASH");
        });

        logoutBtn.addActionListener(e -> {
            currentUser = null;
            idField.setText("");
            passField.setText("");
            cardLayout.show(mainPanel, "LOGIN"); // Logout without terminating 
        });
    }

    private void recordAttendance(String type, String hours) {
    File file = new File("attendance.csv");
    // Check if we need to write the header (only if file is new/empty)
    boolean needsHeader = !file.exists() || file.length() == 0;
    
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    
    // Always use 'true' for append mode
    try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
        
        // 1. Write the Header if this is a new file
        if (needsHeader) {
            pw.println("EmployeeID,Name,Action,Timestamp,HoursWorked");
        }
        
        // 2. Format the data row
        String hoursValue = (hours != null) ? hours : "-";
        String row = String.format("%s,%s,%s,%s,%s", 
                        currentUser.id, 
                        currentUser.name, 
                        type, 
                        timestamp, 
                        hoursValue);
        
        // 3. Save the row
        pw.println(row);
        
        JOptionPane.showMessageDialog(this, type + " Successful!");
        
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error saving to attendance.csv");
    }
}

    private void saveEmployeeToCSV(Employee emp) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("employee.csv", true))) {
            pw.println(emp.toCSV());
        } catch (IOException ex) {
            System.out.println("Error saving new employee.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GoldenHourSystem().setVisible(true));
    }
}