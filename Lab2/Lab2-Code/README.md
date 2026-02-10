# Student Registration System - Lab 2

## Overview

This is a student course registration system implemented using the **Implicit-Invocation (Publish-Subscribe)** architectural pattern. The system has been modified to include three enhancements:

- **Modification A (Logging):** A `LoggingHandler` component subscribes to display events and writes all system output to a log file (`system.log`).
- **Modification B (Overbooking Notification):** An `OverbookingHandler` component monitors successful registrations and announces a warning when a course has more than 3 students registered.
- **Modification C (Conflict Checking Extraction):** The course-conflict checking logic has been extracted from `RegisterStudentHandler` into a standalone `ConflictCheckHandler` component. The external behavior remains unchanged.

## How to Compile

Open a terminal, navigate to the directory containing the source files, and type:

```
javac *.java
```

## How to Run

```
java SystemMain Students.txt Courses.txt
```

## How to Test

```
java SystemMain Students.txt Courses.txt < test.txt
```

## Files

### New Files
- `LoggingHandler.java` - Modification A: Logs all output to `system.log`
- `OverbookingHandler.java` - Modification B: Overbooking notification
- `ConflictCheckHandler.java` - Modification C: Extracted conflict checking

### Modified Files
- `EventBus.java` - Added `EV_CHECK_CONFLICT` and `EV_REGISTRATION_SUCCESS` events
- `SystemMain.java` - Instantiates new components
- `ClientInput.java` - Registration requests now go through `EV_CHECK_CONFLICT`
- `RegisterStudentHandler.java` - Conflict logic removed; announces `EV_REGISTRATION_SUCCESS`
