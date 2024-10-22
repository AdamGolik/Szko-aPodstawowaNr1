#pragma once

#include <QMainWindow>
#include <QPushButton>
#include <QLineEdit>
#include <QListWidget>
#include <QFileDialog>
#include "AttendanceManager.h"

class MainWindow : public QMainWindow {
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);

private slots:
    void addStudent();
    void checkInStudent();
    void loadStudentsFromFile(); // Nowy slot

private:
    AttendanceManager manager;

    QLineEdit *firstNameInput;
    QLineEdit *lastNameInput;
    QLineEdit *cardPinInput;
    QLineEdit *classNumberInput;
    QLineEdit *checkInCardPinInput;
    QListWidget *attendanceLog;
    QPushButton *loadButton; // Nowy przycisk
};