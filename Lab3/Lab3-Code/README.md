# Student Registration System - Lab 3

## Overview

This is a distributed version of the Student Registration System from Lab 2, implemented using **Java Remote Method Invocation (RMI)**. The system runs as 3 separate JVM processes:

- **Database** (port 1099) — Data access layer, reads from `Students.txt` and `Courses.txt`
- **Server** (port 1100) — Instantiates all handler objects and binds them to the registry
- **Client** — CLI user interface with client-side printing and logging

## How to Compile

```bash
cd Lab3-Code
```

```bash
javac *.java
```

## How to Run

Start the following 3 processes in 3 separate terminals, in order:

```bash
java Database
```

```bash
java Server
```

```bash
java Client
```

## How to Test

With Database and Server already running in separate terminals:

```bash
java Client < test.txt
```

## Files

### New Files
- `DBInterface.java` - Remote interface for database operations (`extends java.rmi.Remote`)
- `IActivity.java` - Remote interface with `execute()` method for all handlers (`extends java.rmi.Remote`)
- `Server.java` - Instantiates handlers, looks up DB, binds handlers to registry on port 1100
- `Client.java` - CLI client, looks up handlers, invokes remote methods, logs to `system.log`

### Modified Files (from Lab 2)
- `Student.java` - Added `implements Serializable` for RMI transfer
- `Course.java` - Added `implements Serializable` for RMI transfer
- `Database.java` - Implements `DBInterface`, creates RMI registry on port 1099
- `*Handler.java` - Changed to `extends UnicastRemoteObject implements IActivity`

### Unchanged Files
- `Students.txt` - Student data
- `Courses.txt` - Course data
