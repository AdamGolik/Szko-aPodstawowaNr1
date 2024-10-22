#pragma once
#include <string>
#include <unordered_map>
#include <vector>
#include <ctime>

struct Student {
    std::string firstName;
    std::string lastName;
    std::string cardPin;
    std::string classNumber;
    time_t lastCheckIn;
};

class AttendanceManager {
public:
    void addStudent(const std::string& firstName, const std::string& lastName, const std::string& cardPin, const std::string& classNumber);
    void checkInStudent(const std::string& cardPin);
    std::vector<std::string> getAttendanceLog();
    void loadStudentsFromFile(const std::string& filePath); // Nowa metoda

private:
    std::unordered_map<std::string, Student> students;
    std::vector<std::pair<time_t, std::string>> checkInLog;
    bool isDuplicateCheckIn(const Student& student);

    // Helper function to get current time
    time_t getCurrentTime() const;
};