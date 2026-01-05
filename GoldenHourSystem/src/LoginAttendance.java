import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class LoginAttendance extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private ArrayList<Employee> employeeList = new ArrayList<>();
    private Employee currentUser = null;
    
    private HashMap<String, LocalDateTime> activeSessions = new HashMap<>(); 

    public LoginAttendance() {
        // 1. Load Employees
        employeeList = EmployeeReader.loadEmployees();
        
        setupGUI();
        setTitle("GoldenHour Store Management System");
        setSize(600, 600); // Made it slightly bigger
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupGUI() {
        // --- LOGIN PANEL ---
        JPanel loginPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        JTextField idField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        loginPanel.add(new JLabel("Employee ID (e.g., C6002):")); loginPanel.add(idField);
        loginPanel.add(new JLabel("Password (e.g., d3e4f5):")); loginPanel.add(passField);
        loginPanel.add(loginBtn);

        // --- DASHBOARD PANEL ---
        JPanel dashPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        dashPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel welcomeLabel = new JLabel("Welcome!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Attendance Buttons
        JButton clockInBtn = new JButton("Clock In");
        JButton clockOutBtn = new JButton("Clock Out");
        
        // Operation Buttons
        JButton salesBtn = new JButton("💰 Open Sales System");
        JButton stockCountBtn = new JButton("📋 Morning Stock Count");
        JButton stockMoveBtn = new JButton("📦 Stock In / Stock Out");
        
        // Manager Only Buttons
        JButton analyticsBtn = new JButton("📈 View Analytics Report");
        JButton regBtn = new JButton("👤 Register New Employee");
        
        JButton logoutBtn = new JButton("Logout");
        
        dashPanel.add(welcomeLabel);
        dashPanel.add(new JSeparator());
        dashPanel.add(new JLabel("Attendance:", SwingConstants.CENTER));
        dashPanel.add(clockInBtn);
        dashPanel.add(clockOutBtn);
        dashPanel.add(new JSeparator());
        dashPanel.add(new JLabel("Operations:", SwingConstants.CENTER));
        dashPanel.add(salesBtn);
        dashPanel.add(stockCountBtn);
        dashPanel.add(stockMoveBtn);
        dashPanel.add(analyticsBtn); // Visible to everyone for now, or hide for manager
        dashPanel.add(regBtn);
        dashPanel.add(logoutBtn);

        // --- REGISTRATION PANEL ---
        JPanel regPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        regPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        JTextField newName = new JTextField();
        JTextField newId = new JTextField();
        JTextField newPass = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Full-time", "Part-time"});
        JButton saveUserBtn = new JButton("Save Employee");
        JButton backBtn = new JButton("Back");
        regPanel.add(new JLabel("Name:")); regPanel.add(newName);
        regPanel.add(new JLabel("ID:")); regPanel.add(newId);
        regPanel.add(new JLabel("Password:")); regPanel.add(newPass);
        regPanel.add(new JLabel("Role:")); regPanel.add(roleBox);
        regPanel.add(saveUserBtn); regPanel.add(backBtn);

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(dashPanel, "DASH");
        mainPanel.add(regPanel, "REG");
        add(mainPanel);

        // --- LOGIC ---
        loginBtn.addActionListener(e -> {
            String id = idField.getText();
            String pass = new String(passField.getPassword());
            boolean found = false;
            
            for (Employee emp : employeeList) {
                if (emp.getId().equals(id) && emp.getPassword().equals(pass)) {
                    currentUser = emp;
                    welcomeLabel.setText("Welcome, " + emp.getName());
                    
                    // Manager Features
                    boolean isManager = emp.getRole().equalsIgnoreCase("Manager");
                    regBtn.setVisible(isManager);
                    analyticsBtn.setVisible(isManager); // Only Manager sees analytics
                    
                    cardLayout.show(mainPanel, "DASH");
                    found = true;
                    break;
                }
            }
            if (!found) JOptionPane.showMessageDialog(this, "Invalid ID or Password!");
        });

        clockInBtn.addActionListener(e -> {
            activeSessions.put(currentUser.getId(), LocalDateTime.now());
            AttendanceWriter.saveAttendance(new Attendance(currentUser.getId(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), "00:00"));
            JOptionPane.showMessageDialog(this, "Clocked In Successfully!");
        });

        clockOutBtn.addActionListener(e -> {
            if (activeSessions.containsKey(currentUser.getId())) {
                String inTime = activeSessions.get(currentUser.getId()).format(DateTimeFormatter.ofPattern("HH:mm"));
                String outTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                AttendanceWriter.saveAttendance(new Attendance(currentUser.getId(), inTime, outTime));
                activeSessions.remove(currentUser.getId());
                JOptionPane.showMessageDialog(this, "Clocked Out Successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Please Clock In first!");
            }
        });
        
        // --- THREADED LAUNCHERS (Important!) ---
        // These run in a separate thread so the GUI doesn't freeze
        
        salesBtn.addActionListener(e -> {
             SalesSystem.currentUser = currentUser;
             JOptionPane.showMessageDialog(this, "Check the Output Console below to use the Sales System.");
             new Thread(() -> SalesSystem.startSale()).start();
        });
        
        stockCountBtn.addActionListener(e -> {
             JOptionPane.showMessageDialog(this, "Check the Output Console below to start Stock Count.");
             new Thread(() -> StockCount.main(null)).start();
        });
        
        stockMoveBtn.addActionListener(e -> {
             JOptionPane.showMessageDialog(this, "Check the Output Console below for Stock Movement.");
             new Thread(() -> StockInStockOut.main(null)).start();
        });
        
        analyticsBtn.addActionListener(e -> {
             JOptionPane.showMessageDialog(this, "Generating Analytics Report in Console...");
             new Thread(() -> AutoEmailAndAnalytics.main(null)).start();
        });

        regBtn.addActionListener(e -> cardLayout.show(mainPanel, "REG"));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "DASH"));
        
        saveUserBtn.addActionListener(e -> {
            Employee newEmp = new Employee(newId.getText(), newName.getText(), (String)roleBox.getSelectedItem(), newPass.getText());
            employeeList.add(newEmp);
            EmployeeWriter.saveEmployee(newEmp);
            JOptionPane.showMessageDialog(this, "Employee Saved!");
            cardLayout.show(mainPanel, "DASH");
        });

        logoutBtn.addActionListener(e -> {
            currentUser = null;
            idField.setText(""); passField.setText("");
            cardLayout.show(mainPanel, "LOGIN");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginAttendance().setVisible(true));
    }
}