import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Run the application safely on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginAttendance().setVisible(true);
        });
    }
}