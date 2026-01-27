/******************************************************************************************************************
* File:MiddleFilter.java
* Project: Lab 1
* Copyright:
*   Copyright (c) 2020 University of California, Irvine
*   Copyright (c) 2003 Carnegie Mellon University
* Versions:
*   1.1 January 2020 - Revision for SWE 264P: Distributed Software Architecture, Winter 2020, UC Irvine.
*   1.0 November 2008 - Sample Pipe and Filter code (ajl).
*
* Description:
* This class serves as an example for how to use the FilterRemplate to create a standard filter. This particular
* example is a simple "pass-through" filter that reads data from the filter's input port and writes data out the
* filter's output port.
* Parameters: None
* Internal Methods: None
******************************************************************************************************************/

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

// MiddleFilter filters wild jumps in Altitude
public class MiddleFilter extends FilterFramework {
    final int MeasurementLength = 8;	    // This is the length of all measurements (including time) in bytes
    final int IdLength = 4;					// This is the length of IDs in the byte stream

    public void run() {
        int bytesread = 0;					// Number of bytes read from the input file.
		int byteswritten = 0;				// Number of bytes written to the stream.
		byte databyte = 0;					// The byte of data read from the file

        int id;								// This is the measurement id
        int i;								// This is a loop counter
        long measurement;					// Measurement bits

        // Frame buffering
        long bufTime = 0;
        double bufVel = 0;
        double bufAlt = 0;
        double bufPress = 0;
        double bufTemp = 0;
        double bufPitch = 0;                // Keeping Pitch just in case, though not used in CSV
        boolean frameStarted = false;

        // Altitude history for filtering
        // Altitude history logic moved to class fields

        // CSV Writer for WildPoints
        PrintWriter wildPointsWriter = null;
        SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss:SS");
        Calendar TimeStamp = Calendar.getInstance();

        try {
            // Check if Output directory exists
            File outputDir = new File("Output");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }
            // Open WildPoints.csv
            wildPointsWriter = new PrintWriter(new FileWriter("Output/WildPoints.csv"));
            wildPointsWriter.println("Time,Velocity,Altitude,Pressure,Temperature");
        } catch (IOException e) {
            System.out.println(this.getName() + "::Problem opening output file::" + e);
        }

        System.out.println("\n" + this.getName() + "::Middle Reading ");

        while (true) {
            try {
                id = 0;
                for (i = 0; i < IdLength; i++) {
                    databyte = ReadFilterInputPort();
                    id = id | (databyte & 0xFF);
                    if (i != IdLength - 1) {
                        id = id << 8;
                    }
                    bytesread++;
                }

                measurement = 0;
                for (i = 0; i < MeasurementLength; i++) {
                    databyte = ReadFilterInputPort();
                    measurement = measurement | (databyte & 0xFF);
                    if (i != MeasurementLength - 1) {
                        measurement = measurement << 8;
                    }
                    bytesread++;
                }

                // Logic to handle frames and filtering

                if (id == 0) {
                    // If a frame was already started, print it before starting the new one
                    if (frameStarted) {
                        processFrame(bufTime, bufVel, bufAlt, bufPress, bufTemp, bufPitch, wildPointsWriter, TimeStamp, TimeStampFormat);
                    }

                    // Start new frame
                    bufTime = measurement;
                    frameStarted = true;
                } else if (id == 1) {
                    bufVel = Double.longBitsToDouble(measurement);
                } else if (id == 2) {
                    bufAlt = Double.longBitsToDouble(measurement);
                } else if (id == 3) {
                    bufPress = Double.longBitsToDouble(measurement);
                } else if (id == 4) {
                    bufTemp = Double.longBitsToDouble(measurement);
                } else if (id == 5) {
                    bufPitch = Double.longBitsToDouble(measurement);
                }

            } catch (EndOfStreamException e) {
                // Write the last frame if it exists
                if (frameStarted) {
                    processFrame(bufTime, bufVel, bufAlt, bufPress, bufTemp, bufPitch, wildPointsWriter, TimeStamp, TimeStampFormat);
                }
                if (wildPointsWriter != null) {
                    wildPointsWriter.close();
                }
                ClosePorts();
                System.out.println("\n" + this.getName() + "::Middle Exiting; bytes read: " + bytesread);
                break;
            }
        } // while
    } // run

    // Helper function to process frame, check for wild jumps, and send data
    private double prevAlt = Double.NaN;
    private double prevPrevAlt = Double.NaN;

    private void processFrame(long time, double vel, double alt, double press, double temp, double pitch, PrintWriter writer, Calendar TimeStamp, SimpleDateFormat fmt) {

        boolean isWild = false;
        double newAlt = alt;

        if (!Double.isNaN(prevAlt)) {
            if (Math.abs(alt - prevAlt) > 100) {
                isWild = true;
            }
        }

        if (isWild) {
            // Write original to WildPoints.csv
            TimeStamp.setTimeInMillis(time);
            writer.print(fmt.format(TimeStamp.getTime()));
            writer.print("," + vel);
            writer.print("," + alt);
            writer.print("," + press);
            writer.print("," + temp);
            writer.println();

            // Calculate replacement
            if (!Double.isNaN(prevPrevAlt)) {
                newAlt = (prevAlt + prevPrevAlt) / 2.0;
            } else {
                newAlt = prevAlt;
            }
        }

        // Update history
        prevPrevAlt = prevAlt;
        prevAlt = newAlt; // Use corrected value for history

        // Send data downstream
        sendData(0, time);
        sendData(1, Double.doubleToLongBits(vel));
        if (isWild) {
            sendData(6, Double.doubleToLongBits(newAlt));
        } else {
            sendData(2, Double.doubleToLongBits(newAlt));
        }
        sendData(3, Double.doubleToLongBits(press));
        sendData(4, Double.doubleToLongBits(temp));
        sendData(5, Double.doubleToLongBits(pitch));
    }

    private void sendData(int id, long measurement) {
        byte databyte;

        // Write ID
        for (int i = IdLength - 1; i >= 0; i--) {
            databyte = (byte) ((id >> (8 * i)) & 0xFF);
            WriteFilterOutputPort(databyte);
        }

        // Write Measurement
        for (int i = MeasurementLength - 1; i >= 0; i--) {
            databyte = (byte) ((measurement >> (8 * i)) & 0xFF);
            WriteFilterOutputPort(databyte);
        }
    }
}
