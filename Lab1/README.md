# Lab 1: Pipe-and-Filter System

## Overview
This project implements a pipe-and-filter architecture to process flight data. 
It consists of two systems, **System A** and **System B**, both built upon a common framework.

## Directory Structure
- `SystemA/`: Source code for System A.
    - `DataSets/`: Contains input data.
    - `Output/`: Contains output files.
- `SystemB/`: Source code for System B.
    - `DataSets/`: Contains input data.
    - `Output/`: Contains output files.
- `Lab1/`: Original lab files and templates.

```bash
$ tree SystemA -L 2 && tree SystemB -L 2

SystemA
├── DataSets
│   └── FlightData.dat
├── FilterFramework.java
├── MiddleFilter.java
├── Output
│   └── OutputA.csv
├── Plumber.java
├── SinkFilter.java
└── SourceFilter.java

3 directories, 7 files
SystemB
├── DataSets
│   └── FlightData.dat
├── FilterFramework.java
├── MiddleFilter.java
├── Output
│   ├── OutputB.csv
│   └── WildPoints.csv
├── Plumber.java
├── SinkFilter.java
└── SourceFilter.java

3 directories, 8 files
```

## Build and Execution

### Prerequisites
- Java SE 8 or higher.

### System A
System A reads the binary flight data and converts it into a CSV format and stores it in `Output/OutputA.csv`.

**Steps:**

1. Navigate to the `SystemA` directory:
   ```bash
   cd ./SystemA
   ```
2. Reset compile environment and output directory:
   ```bash
   rm -rf *.class; rm -rf Output
   ```
3. Compile the Java files:
   ```bash
   javac *.java
   ```
4. Run the Plumber:
   ```bash
   java Plumber
   ```
5. Output will be generated in `Output/OutputA.csv`.

*Optional*: To specify a custom input file, add the input file into the `DataSets` directory and change the file name to `FlightData.dat` or change the code in `SourceFilter.java` to read from the new file.

### System B
System B extends System A by adding a `MiddleFilter` that detects and corrects "wild jumps" in altitude (replacing them with the average of previous values). It produces an annotated CSV output and a separate log of wild jumps.

**Steps:**
1. Navigate to the `SystemB` directory:
   ```bash
   cd ./SystemB
   ```
2. Reset compile environment and output directory:
   ```bash
   rm -rf *.class; rm -rf Output
   ```
3. Compile the Java files:
   ```bash
   javac *.java
   ```
4. Run the Plumber:
   ```bash
   java Plumber
   ```
5. Output will be generated in `Output/OutputB.csv` (corrected data with asterisks `*` on replaced values).
6. Wild jump records will be logged in `Output/WildPoints.csv`.

*Optional*: To specify a custom input file, add the input file into the `DataSets` directory and change the file name to `FlightData.dat` or change the code in `SourceFilter.java` to read from the new file.

## Implementation Details
- **System A**:
    - `SourceFilter`: Reads `DataSets/FlightData.dat`.
    - `MiddleFilter`: Pass-through filter.
    - `SinkFilter`: Formats dataframes into CSV line `Time,Velocity,Altitude,Pressure,Temperature`.
- **System B**:
    - `MiddleFilter`: Buffers frames, checks for altitude jumps (>100ft variation), filters them, and logs original values to `Output/WildPoints.csv`. Corrected altitudes are flagged with ID 6 in the stream.
    - `SinkFilter`: Handles ID 6 by appending `*` to the altitude value in the CSV output.
