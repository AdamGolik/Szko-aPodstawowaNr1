# Użycie oficjalnego obrazu Qt5 z systemem operacyjnym Ubuntu
FROM ubuntu:20.04

# Ustawienia środowiska
ENV DEBIAN_FRONTEND=noninteractive

# Aktualizacja systemu i instalacja niezbędnych pakietów
RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    qt5-default \
    qtbase5-dev \
    qtbase5-dev-tools \
    qttools5-dev-tools \
    libqt5widgets5 \
    git

# Skopiowanie plików projektu do obrazu
WORKDIR /app
COPY . /app

# Kompilacja projektu
RUN mkdir build && cd build && cmake .. && make

# Określienie komendy startowej
CMD ["./build/AttendanceSystem"]