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
    private String id, name, password, role;

    public Employee(String id, String name, String role, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    // --- GETTERS (The Public Doors) ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // Helper to format data for CSV storage
    public String toCSV() {
        return id + "," + name + "," + role + "," + password;
    }
}

public class LoginAttendance extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private ArrayList<Employee> employeeList = new ArrayList<>();
    private Employee currentUser = null;
    
    // Track clock-in times for calculation
    private HashMap<String, LocalDateTime> activeSessions = new HashMap<>(); 
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LoginAttendance() {
        loadEmployeeData(); 
        setupGUI();
        
        setTitle("Golden Hour Store Management System");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Load data from storage on startup
    private void loadEmployeeData() {
        File file = new File("employee.csv");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    // .trim() removes any accidental spaces
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
        // --- 1. LOGIN PANEL ---
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

        // --- 3. REGISTRATION PANEL ---
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

        // --- BUTTON LOGIC (Fixed Access Modifiers) ---
        loginBtn.addActionListener(e -> {
            String id = idField.getText();
            String pass = new String(passField.getPassword());
            boolean found = false;
            
            for (Employee emp : employeeList) {
                // FIXED: Using getters instead of direct field access
                if (emp.getId().equals(id) && emp.getPassword().equals(pass)) {
                    currentUser = emp;
                    welcomeLabel.setText("Welcome, " + emp.getName() + " (" + emp.getId() + ")");
                    
                    // Registration button only visible to Managers
                    regBtn.setVisible(emp.getRole().equalsIgnoreCase("Manager"));
                    
                    cardLayout.show(mainPanel, "DASH");
                    found = true;
                    break;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(this, "Login Failed: Invalid User ID or Password.");
            }
        });

        clockInBtn.addActionListener(e -> {
            activeSessions.put(currentUser.getId(), LocalDateTime.now());
            recordAttendance("Clock In", null);
        });

        clockOutBtn.addActionListener(e -> {
            if (activeSessions.containsKey(currentUser.getId())) {
                LocalDateTime start = activeSessions.get(currentUser.getId());
                LocalDateTime end = LocalDateTime.now();
                Duration duration = Duration.between(start, end);
                double hours = duration.toMinutes() / 60.0; 
                recordAttendance("Clock Out", String.format("%.1f", hours));
                activeSessions.remove(currentUser.getId());
            } else {
                JOptionPane.showMessageDialog(this, "Error: You must Clock In first.");
            }
        });

        regBtn.addActionListener(e -> cardLayout.show(mainPanel, "REG"));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "DASH"));

        saveUserBtn.addActionListener(e -> {
            Employee newUser = new Employee(newId.getText(), newName.getText(), (String)roleBox.getSelectedItem(), newPass.getText());
            employeeList.add(newUser);
            saveEmployeeToCSV(newUser);
            JOptionPane.showMessageDialog(this, "Employee successfully registered!");
            cardLayout.show(mainPanel, "DASH");
        });

        logoutBtn.addActionListener(e -> {
            currentUser = null;
            idField.setText("");
            passField.setText("");
            cardLayout.show(mainPanel, "LOGIN"); 
        });
    }

    private void recordAttendance(String type, String hours) {
        File file = new File("attendance.csv");
        boolean needsHeader = !file.exists() || file.length() == 0;
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            if (needsHeader) {
                pw.println("EmployeeID,Name,Action,Timestamp,HoursWorked");
            }
            
            String hoursValue = (hours != null) ? hours : "-";
            // Fixed: Using getters here too
            String row = String.format("%s,%s,%s,%s,%s", 
                            currentUser.getId(), 
                            currentUser.getName(), 
                            type, 
                            timestamp, 
                            hoursValue);
            
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
        SwingUtilities.invokeLater(() -> new LoginAttendance().setVisible(true));
    }
}