#include "AttendanceManager.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>

void AttendanceManager::addStudent(const std::string& firstName, const std::string& lastName, const std::string& cardPin, const std::string& classNumber) {
    students[cardPin] = {firstName, lastName, cardPin, classNumber, 0};
}

void AttendanceManager::checkInStudent(const std::string& cardPin) {
    auto it = students.find(cardPin);
    if (it != students.end()) {
        if (isDuplicateCheckIn(it->second)) {
            std::cout << "Warning: " << it->second.firstName << " " << it->second.lastName
                      << " has already checked in within the last 2 hours.\n";
        } else {
            it->second.lastCheckIn = getCurrentTime();
            checkInLog.emplace_back(it->second.lastCheckIn, cardPin);
            std::cout << it->second.firstName << " " << it->second.lastName << " checked in successfully.\n";
        }
    } else {
        std::cout << "Student with card PIN " << cardPin << " not found.\n";
    }
}

std::vector<std::string> AttendanceManager::getAttendanceLog() {
    std::vector<std::string> logEntries;
    for (const auto& [time, pin] : checkInLog) {
        const Student& student = students[pin];
        std::stringstream ss;
        ss << "Time: " << std::put_time(std::localtime(&time), "%F %T")
           << ", Name: " << student.firstName << " " << student.lastName
           << ", PIN: " << student.cardPin << ", Class: " << student.classNumber;
        logEntries.push_back(ss.str());
    }
    return logEntries;
}

void AttendanceManager::loadStudentsFromFile(const std::string& filePath) {
    std::ifstream file(filePath);
    if (!file.is_open()) {
        std::cerr << "Could not open the file!\n";
        return;
    }

    std::string line;
    while (getline(file, line)) {
        std::istringstream ss(line);
        std::string firstName, lastName, cardPin, classNumber;

        getline(ss, firstName, ',');
        getline(ss, lastName, ',');
        getline(ss, cardPin, ',');
        getline(ss, classNumber, ',');

        if (!firstName.empty() && !lastName.empty() && !cardPin.empty() && !classNumber.empty()) {
            addStudent(firstName, lastName, cardPin, classNumber);
        }
    }

    file.close();
}

bool AttendanceManager::isDuplicateCheckIn(const Student& student) {
    const double SECONDS_IN_2_HOURS = 7200.0;
    return difftime(getCurrentTime(), student.lastCheckIn) < SECONDS_IN_2_HOURS;
}

time_t AttendanceManager::getCurrentTime() const {
    return std::time(nullptr);
}