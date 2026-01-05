import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Launches the GUI correctly
        SwingUtilities.invokeLater(() -> {
            new LoginAttendance().setVisible(true);
        });
    }
}