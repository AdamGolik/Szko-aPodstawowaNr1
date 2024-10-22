import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainWindow extends JFrame {
    private AttendanceManager manager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField pinField;
    private JTextField classField;
    private JTextField diaryNumberField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JPanel manualAddPanel;
    private static final String FILE_PATH = "student_data.csv";

    public MainWindow() {
        manager = new AttendanceManager();
        setTitle("Attendance Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tableModel = new DefaultTableModel(new Object[]{"First Name", "Last Name", "Card Pin", "Class", "Diary Number", "Date"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(4, 1));

        JButton loadButton = new JButton("Load From File");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadStudentsToTable();
            }
        });

        JButton manualAddButton = new JButton("Manual Add");
        manualAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleManualAddPanelVisibility();
            }
        });

        pinField = new JTextField();
        pinField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processCardPinInput();
            }
        });

        JButton removeButton = new JButton("Remove Student");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedStudent();
            }
        });

        actionPanel.add(new JLabel("Card Pin:"));
        actionPanel.add(pinField);
        actionPanel.add(loadButton);
        actionPanel.add(manualAddButton);
        actionPanel.add(removeButton);

        add(actionPanel, BorderLayout.SOUTH);

        manualAddPanel = new JPanel();
        manualAddPanel.setLayout(new GridLayout(6, 2));
        manualAddPanel.setVisible(false);

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameField = new JTextField();
        manualAddPanel.add(firstNameLabel);
        manualAddPanel.add(firstNameField);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameField = new JTextField();
        manualAddPanel.add(lastNameLabel);
        manualAddPanel.add(lastNameField);

        JLabel classLabel = new JLabel("Class:");
        classField = new JTextField();
        manualAddPanel.add(classLabel);
        manualAddPanel.add(classField);

        JLabel diaryNumberLabel = new JLabel("Diary Number:");
        diaryNumberField = new JTextField();
        manualAddPanel.add(diaryNumberLabel);
        manualAddPanel.add(diaryNumberField);

        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudentToTable();
            }
        });

        manualAddPanel.add(addButton);
        add(manualAddPanel, BorderLayout.NORTH);

        loadStudentsFromFile(FILE_PATH);  // Load existing students on startup
    }

    private void toggleManualAddPanelVisibility() {
        manualAddPanel.setVisible(!manualAddPanel.isVisible());
    }

    private void addStudentToTable() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String cardPin = pinField.getText();  // Use the card PIN from the text field
        String classNumber = classField.getText();
        String schoolDiaryNumber = diaryNumberField.getText();

        manager.addStudent(firstName, lastName, cardPin, classNumber, schoolDiaryNumber);
        tableModel.addRow(new Object[]{firstName, lastName, cardPin, classNumber, schoolDiaryNumber});
        clearManualAddFields();
    }

    private void clearManualAddFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        classField.setText("");
        diaryNumberField.setText("");
    }

    private void processCardPinInput() {
        String cardPin = pinField.getText();
        boolean studentFound = false;

        if (!cardPin.isEmpty()) {
            Student student = manager.getStudentByPin(cardPin);
            if (student != null) {
                if (manager.isDuplicateEntryWithinDay(cardPin)) {
                    JOptionPane.showMessageDialog(this, "Warning: " + student.firstName + " " + student.lastName + " already checked in within the last 24 hours.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String currentDate = getCurrentDate();
                    updateTableWithCheckedInStudent(cardPin, currentDate);
                    manager.checkInStudent(cardPin); // Log the check-in
                }
                studentFound = true;
            }
        }

        if (!studentFound) {
            JOptionPane.showMessageDialog(this, "Student with card PIN " + cardPin + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        pinField.setText("");
    }

    private String generateCardPin() {
        return String.valueOf(System.currentTimeMillis() % 10000);
    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    private void updateTableWithCheckedInStudent(String cardPin, String currentDate) {
        Student student = manager.getStudentByPin(cardPin);
        if (student != null && !manager.isDuplicateEntryWithinDay(cardPin)) {
            tableModel.addRow(new Object[]{
                    student.firstName, student.lastName, student.cardPin, student.classNumber, student.schoolDiaryNumber, currentDate
            });
            System.out.println("Updated table with: " + student.firstName + " " + student.lastName);
        }
    }

    private void loadStudentsToTable() {
        tableModel.setRowCount(0);  // Clear existing data in the table
        for (Student student : manager.getStudents().values()) {
            // Use current date for illustration
            String currentDate = getCurrentDate();
            tableModel.addRow(new Object[]{
                    student.firstName, student.lastName, student.cardPin, student.classNumber, student.schoolDiaryNumber, currentDate
            });
            System.out.println("Loaded student: " + student.firstName + " " + student.lastName);
        }
    }

    private void loadStudentsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String firstName = values[0];
                String lastName = values[1];
                String cardPin = values[2];
                String classNumber = values[3];
                String schoolDiaryNumber = values[4];
                manager.addStudent(firstName, lastName, cardPin, classNumber, schoolDiaryNumber);
                tableModel.addRow(new Object[]{firstName, lastName, cardPin, classNumber, schoolDiaryNumber});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String cardPin = (String) tableModel.getValueAt(selectedRow, 2);
            manager.removeStudent(cardPin);
            tableModel.removeRow(selectedRow);
            System.out.println("Student with card PIN " + cardPin + " removed.");
        } else {
            JOptionPane.showMessageDialog(this, "No student selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow window = new MainWindow();
                window.setVisible(true);
            }
        });
    }
}