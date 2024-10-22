import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainWindow extends JFrame {
    private AttendanceManager manager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField pinField;
    private JTextField classFieldManual;
    private JTextField diaryNumberFieldManual;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField classFieldStudent;
    private JTextField diaryNumberFieldStudent;
    private JTextField cardPinFieldStudent;
    private Timer timer;
    private String studentsString;

    public MainWindow() {
        super("Attendance Manager");

        manager = new AttendanceManager();

        setLayout(new BorderLayout());

        // Table
        String[] columns = {"Date", "Card Pin", "First Name", "Last Name", "Class", "Diary Number"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(700, 200));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.NORTH);

        // Pin field and buttons
        JPanel topPanel = new JPanel(new GridLayout(1, 7));
        pinField = new JTextField();
        pinField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        processCardPinInput();
                    }
                }, 200);
            }
        });

        topPanel.add(new JLabel("Card PIN"));
        topPanel.add(pinField);

        JButton addButton = new JButton("Add student");
        JButton manualAddButton = new JButton("Manual add");
        JButton checkInButton = new JButton("Check In");
        JButton loadButton = new JButton("Load from file");
        JButton removeButton = new JButton("Remove selected");

        topPanel.add(addButton);
        topPanel.add(manualAddButton);
        topPanel.add(checkInButton);
        topPanel.add(loadButton);
        topPanel.add(removeButton);

        add(topPanel, BorderLayout.CENTER);

        // Manual add panel
        JPanel manualAddPanel = new JPanel(new GridLayout(3, 2));
        manualAddPanel.add(new JLabel("Class"));
        classFieldManual = new JTextField();
        manualAddPanel.add(classFieldManual);

        manualAddPanel.add(new JLabel("Diary Number"));
        diaryNumberFieldManual = new JTextField();
        manualAddPanel.add(diaryNumberFieldManual);

        JButton manualAddToTableButton = new JButton("ADD to table");
        manualAddPanel.add(manualAddToTableButton);

        manualAddPanel.setVisible(false);
        add(manualAddPanel, BorderLayout.SOUTH);

        // Add student panel
        JPanel addStudentPanel = new JPanel(new GridLayout(6, 2));

        addStudentPanel.add(new JLabel("First Name"));
        firstNameField = new JTextField();
        addStudentPanel.add(firstNameField);

        addStudentPanel.add(new JLabel("Last Name"));
        lastNameField = new JTextField();
        addStudentPanel.add(lastNameField);

        addStudentPanel.add(new JLabel("Class"));
        classFieldStudent = new JTextField();
        addStudentPanel.add(classFieldStudent);

        addStudentPanel.add(new JLabel("Diary Number"));
        diaryNumberFieldStudent = new JTextField();
        addStudentPanel.add(diaryNumberFieldStudent);

        addStudentPanel.add(new JLabel("Card Pin"));
        cardPinFieldStudent = new JTextField();
        addStudentPanel.add(cardPinFieldStudent);

        JButton addStudentToFileButton = new JButton("Add to file");
        addStudentPanel.add(addStudentToFileButton);

        addStudentPanel.setVisible(false);
        add(addStudentPanel, BorderLayout.SOUTH);

        // Event listeners
        manualAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manualAddPanel.setVisible(true);
                addStudentPanel.setVisible(false);
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manualAddPanel.setVisible(false);
                addStudentPanel.setVisible(true);
            }
        });

        manualAddToTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processManualAdd();
            }
        });

        addStudentToFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processAddStudentToFile();
            }
        });

        checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processCardPinInput();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadStudentsFromFile();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedRow();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);

        // Load students from file and print to terminal
        loadStudentsFromFile();
        System.out.println(studentsString);
    }

    private void processCardPinInput() {
        String cardPin = pinField.getText();
        if (!cardPin.trim().isEmpty()) {
            manager.checkInStudent(cardPin);
            updateTableWithCheckedInStudent(cardPin);
            pinField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a valid card pin.");
        }
    }

    private void processManualAdd() {
        String classNumber = classFieldManual.getText();
        String diaryNumber = diaryNumberFieldManual.getText();

        String defaultFirstName = "Default";
        String defaultLastName = "Student";
        String defaultCardPin = "1";

        manager.addStudent(defaultFirstName, defaultLastName, defaultCardPin, classNumber, diaryNumber);
        updateTableWithCheckedInStudent(defaultCardPin);
    }

    private void processAddStudentToFile() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String classNumber = classFieldStudent.getText();
        String diaryNumber = diaryNumberFieldStudent.getText();
        String cardPin = cardPinFieldStudent.getText();

        manager.addStudent(firstName, lastName, cardPin, classNumber, diaryNumber);

        updateTableWithCheckedInStudent(cardPin);
    }

    private void updateTableWithCheckedInStudent(String cardPin) {
        Student student = manager.getStudents().get(cardPin);
        if (student != null) {
            tableModel.addRow(new Object[]{
                    getCurrentDate(), student.cardPin, student.firstName, student.lastName, student.classNumber, student.schoolDiaryNumber
            });
        } else {
            System.out.println("Student with card PIN " + cardPin + " not found.");
        }
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss").format(new Date());
    }

    public void loadStudentsFromFile() {
        manager.loadStudentsFromFile("student_data.csv");

        // Initialize studentsString with student data
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Student> entry : manager.getStudents().entrySet()) {
            Student student = entry.getValue();
            sb.append(String.format("Student: %s %s, Card Pin: %s, Class: %s, Diary Number: %s\n",
                    student.firstName, student.lastName, student.cardPin, student.classNumber, student.schoolDiaryNumber));
        }
        studentsString = sb.toString();
        System.out.println("Students loaded from file successfully.");
    }

    private void removeSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to remove.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}