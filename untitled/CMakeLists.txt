cmake_minimum_required(VERSION 3.5)

project(AttendanceSystem LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(Qt5 COMPONENTS Widgets REQUIRED)

set(SOURCES
        main.cpp
        AttendanceManager.cpp
        MainWindow.cpp
)

set(HEADERS
        AttendanceManager.h
        MainWindow.h
)

add_executable(AttendanceSystem
        ${SOURCES}
        ${HEADERS}
)

target_link_libraries(AttendanceSystem Qt5::Widgets)