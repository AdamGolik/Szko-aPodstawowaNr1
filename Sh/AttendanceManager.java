import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class Student {
    String firstName;
    String lastName;
    String cardPin;
    String classNumber;
    String schoolDiaryNumber;
    long lastCheckIn;

    Student(String firstName, String lastName, String cardPin, String classNumber, String schoolDiaryNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cardPin = cardPin;
        this.classNumber = classNumber;
        this.schoolDiaryNumber = schoolDiaryNumber;
        this.lastCheckIn = 0;
    }
}

public class AttendanceManager {
    private Map<String, Student> students = new HashMap<>();
    private ArrayList<String[]> checkInLog = new ArrayList<>();

    public void addStudent(String firstName, String lastName, String cardPin, String classNumber, String schoolDiaryNumber) {
        students.put(cardPin, new Student(firstName, lastName, cardPin, classNumber, schoolDiaryNumber));
        System.out.println("Student added: " + firstName + " " + lastName + " with pin " + cardPin);
    }

    public void checkInStudent(String cardPin) {
        Student student = students.get(cardPin);
        if (student != null) {
            if (!isDuplicateCheckIn(student)) {
                student.lastCheckIn = getCurrentTime();
                checkInLog.add(new String[]{
                        new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss").format(new Date(student.lastCheckIn)),
                        student.cardPin, student.firstName, student.lastName, student.classNumber, student.schoolDiaryNumber
                });
                System.out.println(student.firstName + " " + student.lastName + " checked in successfully.");
            } else {
                System.out.println("Warning: " + student.firstName + " " + student.lastName
                        + " already checked in within the last 24 hours.");
            }
        } else {
            System.out.println("Student with card PIN " + cardPin + " not found.");
        }
    }

    public boolean isDuplicateEntryWithinDay(String cardPin) {
        for (String[] logEntry : checkInLog) {
            if (logEntry[1].equals(cardPin)) {
                // Check if the log entry is from today
                String logDate = logEntry[0].split(" ")[0];
                String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
                if (logDate.equals(currentDate)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeStudent(String cardPin) {
        if (students.containsKey(cardPin)) {
            students.remove(cardPin);
            System.out.println("Removed student with pin: " + cardPin);
        }
    }

    public Student getStudentByPin(String cardPin) {
        return students.get(cardPin);
    }

    public ArrayList<String[]> getAttendanceLog() {
        return checkInLog;
    }

    public Map<String, Student> getStudents() {
        return students;
    }

    public void loadStudentsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 4) {
                    addStudent(values[0], values[1], values[2], values[3].substring(0, values[3].length() - 2), values[3].substring(values[3].length() - 2));
                } else if (values.length == 5) {
                    addStudent(values[0], values[1], values[2], values[3], values[4]);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not open the file: " + e.getMessage());
        }
    }

    private boolean isDuplicateCheckIn(Student student) {
        final long TWENTY_FOUR_HOURS_IN_MILLIS = 86400000L;
        return System.currentTimeMillis() - student.lastCheckIn < TWENTY_FOUR_HOURS_IN_MILLIS;
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }
}