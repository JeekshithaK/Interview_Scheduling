import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class interviewschedule extends JFrame {
    JTextField nameField, emailField, dateField, timeField, interviewerField, positionField;
    JTextArea displayArea;

    public interviewschedule() {
        setTitle("Interview Scheduler");
        setSize(700, 500);  // Decreased size
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));  // Add spacing


        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10)); // reduced columns to fit better
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding

        nameField = new JTextField();
        emailField = new JTextField();
        dateField = new JTextField(); // yyyy-mm-dd
        timeField = new JTextField(); // HH:mm:ss
        interviewerField = new JTextField();
        positionField = new JTextField();

        formPanel.add(new JLabel("Candidate Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Interview Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Interview Time (HH:MM:SS):"));
        formPanel.add(timeField);
        formPanel.add(new JLabel("Interviewer:"));
        formPanel.add(interviewerField);
        formPanel.add(new JLabel("Position:"));
        formPanel.add(positionField);

        JButton submit = new JButton("Schedule");


        displayArea = new JTextArea(12, 60); // Reduced row size
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        add(formPanel, BorderLayout.NORTH);
        add(submit, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);


        submit.addActionListener(e -> saveInterview());


        loadInterviews();
        setVisible(true);
    }

    private void saveInterview() {
        try (Connection conn = DBconnection.getConnection()) {
            String sql = "INSERT INTO interviews (candidate_name, email, interview_date, interview_time, interviewer, position) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nameField.getText());
            ps.setString(2, emailField.getText());
            ps.setString(3, dateField.getText());
            ps.setString(4, timeField.getText());
            ps.setString(5, interviewerField.getText());
            ps.setString(6, positionField.getText());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Interview Scheduled!");

            clearFields();
            loadInterviews(); // Auto-refresh display
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    private void loadInterviews() {
    displayArea.setText("");

    try (Connection conn = DBconnection.getConnection()) {

        if (conn == null) {
            displayArea.setText("Error: Database connection failed.");
            return;
        }

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM interviews ORDER BY interview_date, interview_time");

        displayArea.append(String.format("%-20s %-25s %-12s %-10s %-20s %-15s\n",
                "Candidate", "Email", "Date", "Time", "Interviewer", "Position"));
        displayArea.append("----------------------------------------------------------------------------------------------\n");

        while (rs.next()) {
            displayArea.append(String.format("%-20s %-25s %-12s %-10s %-20s %-15s\n",
                    rs.getString("candidate_name"),
                    rs.getString("email"),
                    rs.getDate("interview_date"),
                    rs.getTime("interview_time"),
                    rs.getString("interviewer"),
                    rs.getString("position")));
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        displayArea.setText("Error loading data: " + ex.getMessage());
    }
}
    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        dateField.setText("");
        timeField.setText("");
        interviewerField.setText("");
        positionField.setText("");
    }
}
    // Main method to run the program
