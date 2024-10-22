#include "MainWindow.h"
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QLabel>

MainWindow::MainWindow(QWidget *parent) : QMainWindow(parent) {
    QWidget *centralWidget = new QWidget(this);
    setCentralWidget(centralWidget);

    QVBoxLayout *mainLayout = new QVBoxLayout(centralWidget);

    // Dodanie ucznia
    QHBoxLayout *addLayout = new QHBoxLayout();
    addLayout->addWidget(new QLabel("Imię:"));
    firstNameInput = new QLineEdit();
    addLayout->addWidget(firstNameInput);

    addLayout->addWidget(new QLabel("Nazwisko:"));
    lastNameInput = new QLineEdit();
    addLayout->addWidget(lastNameInput);

    addLayout->addWidget(new QLabel("PIN karty:"));
    cardPinInput = new QLineEdit();
    addLayout->addWidget(cardPinInput);

    addLayout->addWidget(new QLabel("Numer klasy:"));
    classNumberInput = new QLineEdit();
    addLayout->addWidget(classNumberInput);

    QPushButton *addButton = new QPushButton("Dodaj ucznia");
    connect(addButton, &QPushButton::clicked, this, &MainWindow::addStudent);
    addLayout->addWidget(addButton);

    mainLayout->addLayout(addLayout);

    // Rejestracja ucznia
    QHBoxLayout *checkInLayout = new QHBoxLayout();
    checkInLayout->addWidget(new QLabel("PIN karty:"));
    checkInCardPinInput = new QLineEdit();
    checkInLayout->addWidget(checkInCardPinInput);

    QPushButton *checkInButton = new QPushButton("Rejestruj obecność");
    connect(checkInButton, &QPushButton::clicked, this, &MainWindow::checkInStudent);
    checkInLayout->addWidget(checkInButton);

    mainLayout->addLayout(checkInLayout);

    // Przycisk ładowania danych z pliku
    loadButton = new QPushButton("Wczytaj uczniów z pliku");
    connect(loadButton, &QPushButton::clicked, this, &MainWindow::loadStudentsFromFile);
    mainLayout->addWidget(loadButton);

    // Log obecności
    attendanceLog = new QListWidget();
    mainLayout->addWidget(attendanceLog);
}

void MainWindow::addStudent() {
    QString firstName = firstNameInput->text();
    QString lastName = lastNameInput->text();
    QString cardPin = cardPinInput->text();
    QString classNumber = classNumberInput->text();

    if (!firstName.isEmpty() && !lastName.isEmpty() && !cardPin.isEmpty() && !classNumber.isEmpty()) {
        manager.addStudent(firstName.toStdString(), lastName.toStdString(), cardPin.toStdString(), classNumber.toStdString());
        firstNameInput->clear();
        lastNameInput->clear();
        cardPinInput->clear();
        classNumberInput->clear();
    }
}

void MainWindow::checkInStudent() {
    QString cardPin = checkInCardPinInput->text();
    if (!cardPin.isEmpty()) {
        manager.checkInStudent(cardPin.toStdString());

        attendanceLog->clear();
        QStringList logEntries;
        std::vector<std::string> log = manager.getAttendanceLog();
        for (const std::string& entry : log) {
            logEntries << QString::fromStdString(entry);
        }
        attendanceLog->addItems(logEntries);
    }
}

void MainWindow::loadStudentsFromFile() {
    QString fileName = QFileDialog::getOpenFileName(this, "Wybierz plik", "", "Text Files (*.txt);;All Files (*)");
    if (!fileName.isEmpty()) {
        manager.loadStudentsFromFile(fileName.toStdString());
    }
}