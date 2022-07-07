
/**
 * Filename: App.java
 * 
 * Project: Homework3
 * 
 * Author: Kevin Gyorick
 * 
 * Date: 10/09/2021
 * 
 * File Purpose:    This file serves as an entry point to the program.
 *                  This file asks the user for an input file and processes it.
 *                  This file prints the finialized schedule and paychecks of the mechanics.
 * 
 * Program Purpose: The program schedules appointments for a mechanic shop. 
 *                  Appointments are scheduled first come, first served as 
 *                  long as the job can be completed the same day it is started 
 *                  given the current schedule. Mechanics with lower bay numbers 
 *                  assigned have higher priority when multiple mechanics are available
 */

import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.nio.file.Files;
import java.sql.SQLException;

//built with 'javac -d ../build *.java' within the src directory
//run with 'java -classpath ".;sqlite-jdbc-3.36.0.3.jar" App' within the build directory

public class App {
    static final String FILE_DELIMITER = "\t"; // the input file's delimiter
    static final boolean DEBUG_OUTPUT = false; // wether or not to display debug output of the entity arrays
    static final String OUTPUT_FORMAT = "%-10s%15s%25s%35s%30s%30s"; // The format string for the output schedules
    static final Object[] HEADERS = new Object[] { "Bay Number", "Customer Name", "Vehicle Description", "Service",
            "Start Date & Time", "End Date & Time" }; // the headers of the output schedule

    static Mechanic[] arrMechanics; // array for all mechanics in the database
    static Customer[] arrCustomers; // array for all customers in the database
    static Vehicle[] arrVehicles; // array for all vehicles in the database
    static Service[] arrServices; // array for all services in the database
    static Bay[] arrBays; // array for all bays in the database
    static Schedule[] arrSchedules; // array for all schedules in the database

    // the entry point of the program
    // creates the database if it doesnt exist
    // and processes a input file for creating customers, vehicles, and appointments
    public static void main(String[] args) {
        // Scanner for taking console input
        Scanner input = new java.util.Scanner(System.in);

        try {
            // populate the arrays using the database
            arrMechanics = Mechanic.getMechanics();
            arrCustomers = Customer.getCustomers();
            arrVehicles = Vehicle.getVehicles();
            arrServices = Service.getServices();
            arrBays = Bay.getBays();

            // on start up populate the allMechanicSchedule with the existing schedule
            arrSchedules = Schedule.prepopulateSchedules(arrCustomers, arrVehicles, arrServices, arrBays, arrMechanics);

            // if debug output print the database arrays
            if (DEBUG_OUTPUT)
                Entity.printEntity(
                        new Entity[][] { arrMechanics, arrCustomers, arrVehicles, arrServices, arrBays, arrSchedules });

            // ask the user if they will be providing a new input file
            System.out.println("Would you like to import a new file? (Yes/No)");
            // if yes ask for the file path and process the input of the file
            if (input.nextLine().toLowerCase().equals("yes")) {
                // The input file
                File inputFile;
                // while the input file does not exist
                while (true) {
                    // prompt the user for the input file
                    System.out.println("What is the path to the input file?");
                    // get the input
                    String strFilePath = input.nextLine();
                    // check if the input file exist
                    inputFile = new File(strFilePath);
                    if (inputFile.exists())
                        break;
                    else
                        // if not show a error
                        System.out.println("That file does not exist!");
                }
                // store the lines of the file in a array
                String[] arrFileLines = readFile(inputFile);
                // process the lines of the file
                processFileLines(arrFileLines, input);
            }
            // display the ouput schedules
            if (arrSchedules.length > 0)
                displaySchedule();
            else
                System.out.println("No appointments booked");

            // if debug out display the database arrays
            if (DEBUG_OUTPUT)
                Entity.printEntity(
                        new Entity[][] { arrMechanics, arrCustomers, arrVehicles, arrServices, arrBays, arrSchedules });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // prompt the user for databse removal
            System.out.println("Would you like to drop the database? (Yes/No)");
            // if yes remove the database
            if (input.nextLine().toLowerCase().equals("yes")) {
                Entity.dropDatabase();
                System.out.println("Database dropped");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // close the scanner
        input.close();
    }

    /**
     * Reads all lines of the provided file
     * 
     * @param inputFile the file path to read
     * @return the lines of the file in a string array
     * @throws IOException file not found exception
     */
    public static String[] readFile(File inputFile) throws IOException {
        // return the lines of the input file in a string array
        return Files.readAllLines(inputFile.toPath()).toArray(new String[0]);
    }

    /**
     * Processes each line of the file Running one of three functions for each line
     * depending on the function character of the line Can add a new customers,
     * vehicles, and/or appointments from the string array
     * 
     * @param arrFileLines a string array of input lines representing a new
     *                     customer, vehicle or appointment
     * @param input        the scanner for accepting command line input
     * @throws SQLException throws a SQL exception if there is a error with a
     *                      database query and the user does not want to continue
     */
    public static void processFileLines(String[] arrFileLines, Scanner input) throws SQLException {
        // foreach line of the file
        for (String strLine : arrFileLines) {
            // split the line by the delimiter
            String[] arrSplit = strLine.split(FILE_DELIMITER);
            // get the action from the line
            final String ACTION = arrSplit[0];
            try {
                // if C action
                if (ACTION.equals("C"))
                    // create a new customer with the line
                    arrCustomers = Customer.createCustomer(arrSplit[1], arrCustomers);
                else if (ACTION.equals("V"))
                    // create a new vehicle with the line
                    arrVehicles = Vehicle.createVehicle(arrSplit[1], arrSplit[2], arrCustomers, arrVehicles);
                else if (ACTION.equals("S"))
                    // create a new appointment with the line
                    arrSchedules = Schedule.createAppointment(-1, arrSplit[1], arrSplit[2], arrSplit[3], arrCustomers,
                            arrVehicles, arrServices, arrBays, arrMechanics);
                else
                    // action not recognized
                    System.out.println("Error parsing line: " + strLine);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                // If error ask the user if they would like to ignore it
                System.out.println("Would you like to continue? (Yes/No)");
                // if no rethrow the error so the program can exit
                if (input.nextLine().toLowerCase().equals("no")) {
                    throw ex;
                }
            }
        }
    }

    /**
     * Displays the appointment schedule on the command line
     * 
     * @throws SQLException throws a SQL exception if there is an error with a
     *                      database query
     */
    public static void displaySchedule() throws SQLException {
        // print the schedule
        System.out.println("Appointment Schedule");
        // the mechanics paycheck were the indexes are mechanic tuid - 1 and week number
        // starting at 0
        double[][] paychecks = Schedule.getPaychecks(arrMechanics, arrServices);
        // foreach mechanic
        for (Mechanic currMechanic : arrMechanics) {
            // print schedule header
            System.out.println(currMechanic.strName + "'s Schedule");
            System.out.println(String.format(OUTPUT_FORMAT, HEADERS));
            // get the mechanic's bay id
            final int BAY_TUID = Bay.getMechanicsBay(currMechanic).getTUID();
            // foreach schedule
            for (Schedule currSchedule : arrSchedules) {
                // if the schedule belong to the bay/mechanic
                if (currSchedule.Bays_TUID == BAY_TUID) {
                    // get the appointment vehicle
                    Vehicle currVehicle = Vehicle.getVehicle(currSchedule.Vehicles_TUID, arrVehicles);
                    // get the customer of the appointment
                    Customer vehicleOwner = currVehicle.getOwner(arrCustomers);
                    // get the appointment service
                    Service currService = Service.getService(currSchedule.Service_TUID, arrServices);
                    // print the appointment and relevant info
                    System.out.println(String.format(OUTPUT_FORMAT, Integer.toString(BAY_TUID), vehicleOwner.strName,
                            currVehicle.strVehicle_Description, currService.strService_Name,
                            currSchedule.Appointment_Start_Time, currSchedule.Appointment_End_Time));
                }
            }
            // display paychecks for the weeks in the system
            System.out.println("Paychecks: ");
            // foreach paycheck belonging to the current mechanic
            for (int i = 0; i < paychecks[currMechanic.getTUID() - 1].length; i++) {
                // get the current paycheck
                double currPaycheck = paychecks[currMechanic.getTUID() - 1][i];
                // if nonzero print the paycheck amount
                if (currPaycheck != 0)
                    System.out.println(String.format("Week %d: $%,.2f", i + 1, currPaycheck));
            }
            // print new line
            System.out.println();

            // if debug out print the mechanic schedules data structure
            if (DEBUG_OUTPUT) {
                Schedule.printScheduleDataStructure();
            }
        }
    }
}